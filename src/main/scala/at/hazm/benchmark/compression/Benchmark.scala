package at.hazm.benchmark.compression

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Random

import at.hazm.benchmark.compression.Main.PreferredBinarySize

/**
  * Trait to create sample binary for benchmark.
  */
trait Benchmark {

  /**
    * Return title for specified sampling size.
    *
    * @param sampleBinarySize claimed sample binary size
    * @return benchmark title
    */
  def title(sampleBinarySize:Int):String

  /**
    * Return sample binary to benchmark compression. The binary of the return value need not to the same as specified size.
    *
    * @param sampleBinarySize claimed sample binary size
    * @return sample binary for this benchmark
    */
  def newSample(sampleBinarySize:Int):Array[Byte]

  /**
    * Describe how to the sample elements filled.
    *
    * @return pseudo code
    */
  def sampleCode():String
}

object Benchmark {

  /**
    * Array with all byte values zero.
    */
  object ZeroFilledByteArray extends Benchmark {
    def title(sampleBinarySize:Int):String = f"Zero-filled `Byte[$sampleBinarySize%,d]` Array"

    def newSample(sampleBinarySize:Int):Array[Byte] = new Array[Byte](sampleBinarySize)

    def sampleCode():String = "0"
  }

  /**
    * Array with uniform-random 0x00 - 0xFF binary.
    */
  object UniformRandomByteArray extends Benchmark {
    def title(sampleBinarySize:Int):String = f"Uniform Random filled `Byte[$sampleBinarySize%,d]` Array"

    def newSample(sampleBinarySize:Int):Array[Byte] = {
      val random = new Array[Byte](sampleBinarySize)
      newRandom().nextBytes(random)
      random
    }

    def sampleCode():String = "random.nextByte()"
  }

  /**
    * INT32 array with gaussian-random binary.
    */
  case class GaussianRandomIntArray(mu:Int = 0, sigma2:Int = 1) extends Benchmark {
    def title(sampleBinarySize:Int):String = f"Norm(μ=$mu%d,σ<sup>2</sup>=$sigma2%d) Random `Int[${sampleBinarySize / 4}%,d]` Array"

    def newSample(sampleBinarySize:Int):Array[Byte] = {
      val sigma = math.sqrt(sigma2)
      randomBinary(PreferredBinarySize, 4) { (buffer, random) =>
        buffer.putInt((random.nextGaussian() * sigma + mu).toInt)
      }
    }

    def sampleCode():String = "μ + random.nextGaussian() × σ"
  }

  /**
    * FLOAT64 array with gaussian-random binary.
    */
  case class GaussianRandomDoubleArray(mu:Int = 0, sigma2:Int = 1) extends Benchmark {
    def title(sampleBinarySize:Int):String = f"Norm(μ=$mu%d,σ<sup>2</sup>=$sigma2%d) Random `Double[${sampleBinarySize / 8}%,d]` Array"

    def newSample(sampleBinarySize:Int):Array[Byte] = {
      val sigma = math.sqrt(sigma2)
      randomBinary(PreferredBinarySize, 8) { (buffer, random) =>
        buffer.putDouble(random.nextGaussian() * sigma + mu)
      }
    }

    def sampleCode():String = "μ + random.nextGaussian() × σ"
  }

  /**
    * Random CSV text.
    */
  object RandomDoubleCSV extends Benchmark {
    def title(sampleBinarySize:Int):String = f"CSV Formatted Text of Random Floating-Point Values, ASCII Text"

    def newSample(sampleBinarySize:Int):Array[Byte] = {
      val random = newRandom()
      val buffer = new StringBuilder()
      while(buffer.length < sampleBinarySize) {
        if(buffer.nonEmpty) buffer.append(',')
        buffer.append(random.nextDouble())
      }
      buffer.toString().take(sampleBinarySize).getBytes(StandardCharsets.US_ASCII)
    }

    def sampleCode():String = {
      val random = newRandom()
      (0 to 5).map { _ => random.nextDouble().toString }.mkString(",").take(50) + "..."
    }
  }

  /**
    * A representative sentence of US-ASCII: The unanimous declaration of the thirteen United States of America
    */
  object USDeclarationOfIndependenceText extends Benchmark {
    def title(sampleBinarySize:Int):String = f"United States Declaration of Independence, ASCII Text"

    def newSample(sampleBinarySize:Int):Array[Byte] = {
      loadText("the-unanimous-declaration-of-the-thirteen-USA.txt").getBytes(StandardCharsets.US_ASCII)
    }

    def sampleCode():String = loadText("the-unanimous-declaration-of-the-thirteen-USA.txt").take(50) + "..."
  }

  /**
    * A representative sentence of CJK UTF-8: Japanese novel "Kokoro (こころ)", full text.
    */
  object JapaneseNovelKokoro extends Benchmark {
    def title(sampleBinarySize:Int):String = f"夏目漱石 こころ, Japanese UTF-8 Text"

    def newSample(sampleBinarySize:Int):Array[Byte] = {
      loadText("soseki-kokoro.txt").getBytes(StandardCharsets.UTF_8)
    }

    def sampleCode():String = loadText("soseki-kokoro.txt").take(25) + "..."
  }

  /**
    * Generate a byte-array resulted from ByteBuffer operation
    *
    * @param length      byte-array length to create
    * @param elementSize element size
    * @param append      the operation to put some value to buffer based on RNG.
    * @return byte-array
    */
  private[this] def randomBinary(length:Int, elementSize:Int)(append:(ByteBuffer, Random) => Unit):Array[Byte] = {
    val random = newRandom()
    val buffer = ByteBuffer.allocate(length / elementSize * elementSize)
    for(_ <- 0 until length / elementSize) {
      append(buffer, random)
    }
    buffer.array()
  }

  /**
    * Load text resource from claspath.
    *
    * @param name resource file name
    * @return resource text
    */
  private[this] def loadText(name:String):String = {
    val in = getClass.getClassLoader.getResourceAsStream("at/hazm/benchmark/compression/" + name)
    val text = new String(Iterator.continually(in.read()).takeWhile(_ >= 0).map(_.toByte).toArray, StandardCharsets.UTF_8)
    in.close()
    text
  }

  /**
    * Create new random number generator.
    *
    * @return random number generator
    */
  def newRandom():Random = {
    val random = new Random(12345)
    // To avoid quality degradation in Java standard RNG, discard first double value.
    random.nextDouble()
    random
  }

}
