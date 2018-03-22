package at.hazm.benchmark.compression

import java.io._
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.text.{DateFormat, SimpleDateFormat}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{Date, Random, Timer, TimerTask}

object Main {

  val algorithms = Seq(
    NOOP, SnappyJava, LZ4,
    ZStandard(1), ZStandard(3), ZStandard(6), ZStandard(9), ZStandard(16),
    ZLib, GZip, ApacheCompress.BZIP2 /*, Brotli */)
  val loopTimeInMillis:Long = 10 * 1000
  val maxBinarySize:Int = 100 * 1024 * 1024

  def main(args:Array[String]):Unit = {

    // load classes and warming up JIT
    locally {
      val zeros = new Array[Byte](maxBinarySize)
      exec(System.out, f"Warning up with zero-filled bytes", zeros)
    }

    val fileName = "benchmark_" + System.getProperty("os.arch").toLowerCase + "_" +
      System.getProperty("os.name").replaceAll("\\s+", "").toLowerCase + "_" +
      new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".md"
    val out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))
    out.println("# JavaVM Compression Library Benchmark & Comparison")
    out.println()
    out.println(DateFormat.getDateTimeInstance().format(new Date()))
    out.println(System.getProperty("user.name") + " @ " + InetAddress.getLocalHost.getHostName)
    out.println(System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.version"))
    out.println()

    // zero-filled array
    locally {
      val zeros = new Array[Byte](maxBinarySize)
      exec(out, f"Zero-filled `Byte[${zeros.length}]` Array", zeros)
      out.flush()
    }

    // Uniform random bytes
    locally {
      val random = new Array[Byte](maxBinarySize)
      newRandom().nextBytes(random)
      exec(out, f"Uniform Random filled `Byte[${random.length}%d]` Array", random)
      out.flush()
    }

    // Gaussian random INT32
    locally {
      val random = randomBinary(maxBinarySize / 4, 4) { (buffer, random) =>
        buffer.putInt((random.nextGaussian() * math.sqrt(100)).toInt)
      }
      exec(out, f"Norm(μ=0,σ<sup>2</sup>=100) Random `Int[${random.length / 4}%d]` Array", random)
      out.flush()
    }

    // Gaussian random FLOAT64
    locally {
      val random = randomBinary(maxBinarySize / 8, 8) { (buffer, random) =>
        buffer.putDouble(random.nextGaussian())
      }
      exec(out, f"Norm(μ=0,σ<sup>2</sup>=1) Random `Double[${random.length / 8}%d]` Array", random)
      out.flush()
    }

    // CSV data
    locally {
      val random = newRandom()
      val buffer = new StringBuilder()
      while(buffer.length < maxBinarySize) {
        if(buffer.nonEmpty) buffer.append(',')
        buffer.append(random.nextDouble())
      }
      val sample = buffer.toString().getBytes(StandardCharsets.US_ASCII)
      exec(out, f"Random Floating-Point CSV ASCII Text", sample)
      out.flush()
    }

    // The unanimous declaration of the thirteen United States of America
    locally {
      val text = loadText("the-unanimous-declaration-of-the-thirteen-USA.txt")
      val sample = text.getBytes(StandardCharsets.US_ASCII)
      exec(out, f"United States Declaration of Independence, ASCII Text", sample)
      out.flush()
    }

    // Japanese text as UTF-8
    locally {
      val text = loadText("soseki-kokoro.txt")
      val sample = text.getBytes(StandardCharsets.UTF_8)
      exec(out, f"夏目漱石 こころ, Japanese UTF-8 Text", sample)
      out.flush()
    }

    out.close()
  }

  def exec(out:Appendable, title:String, buffer:Array[Byte], msec:Long = loopTimeInMillis):Unit = {
    System.out.println(title)

    def flush():Unit = out match {
      case p:PrintWriter => p.flush();
      case p:PrintStream => p.flush()
    }

    out.append(
      f"""## $title
         |
         |datasize=${buffer.length / 1024 / 1024}%,dMB, entropy=${entropy(buffer)}%s
         |
         || Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
         ||:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
         |""".stripMargin)
    algorithms.foreach { cmp =>
      cmp match {
        case buf:Compressor.Buffer =>
          benchmark(buffer, buf:Compressor.Buffer, msec).print(out)
        case _ =>
      }
      flush()
      cmp match {
        case str:Compressor.Stream[_] =>
          benchmark(buffer, str, msec).print(out)
        case _ =>
      }
      flush()
    }
    out.append(s"\n")
    flush()
  }

  private[this] def randomBinary(length:Int, width:Int)(append:(ByteBuffer, Random) => Unit):Array[Byte] = {
    val random = newRandom()
    val buffer = ByteBuffer.allocate(length * width)
    for(_ <- 0 until length) {
      append(buffer, random)
    }
    buffer.array()
  }

  private[this] def newRandom():Random = {
    val random = new Random(12345)
    random.nextDouble()
    random
  }

  private[this] val timer = new Timer(true)

  private[this] def benchmark(msec:Long)(f:(() => Boolean) => Unit):Long = {
    val stopped = new AtomicBoolean(false)
    timer.schedule(new TimerTask {
      override def run():Unit = stopped.set(true)
    }, msec)
    val t0 = System.currentTimeMillis()
    f(() => stopped.get())
    System.currentTimeMillis() - t0
  }

  private[this] def benchmark(uncompressed:Array[Byte], cmp:Compressor.Buffer, msec:Long):Report = {
    val compressed = new Array[Byte]((uncompressed.length * 1.2).toInt)
    val length = cmp.compress(uncompressed, compressed)
    val expected = java.util.Arrays.copyOf(uncompressed, uncompressed.length)
    val actual = new Array[Byte](uncompressed.length)

    // 圧縮の計測
    var compressCount = 0L
    val compressTime = benchmark(msec) { stopped =>
      do {
        cmp.compress(uncompressed, compressed)
        compressCount += 1
      } while(!stopped())
    }

    // 展開の計測
    var uncompressCount = 0L
    val uncompressTime = benchmark(msec) { stopped =>
      do {
        cmp.decompress(compressed, length, actual)
        uncompressCount += 1
      } while(!stopped())
    }

    Report(cmp, "buffer",
      (uncompressed.length - length).toDouble / uncompressed.length,
      uncompressed.length * compressCount * 1000L / compressTime.toDouble,
      uncompressed.length * uncompressCount * 1000L / uncompressTime.toDouble,
      verify(uncompressed, compressed, expected, actual)
    )
  }

  private[this] def benchmark(uncompressed:Array[Byte], cmp:Compressor.Stream[_], msec:Long):Report = {
    val expected = uncompressed.clone()

    // 圧縮の計測
    val out = new ByteArrayOutputStream(uncompressed.length)
    var compressCount = 0L
    val compressTime = benchmark(msec) { stopped =>
      do {
        out.reset()
        cmp.compress(uncompressed, out)
        compressCount += 1
      } while(!stopped())
    }

    // 展開の計測
    val compressed = out.toByteArray
    val in = new ByteArrayInputStream(compressed)
    in.mark(0)
    val actual = cmp.decompress(in, uncompressed.length)
    var uncompressCount = 0L
    val uncompressTime = benchmark(msec) { stopped =>
      do {
        in.reset()
        cmp.decompress(in, uncompressed.length)
        uncompressCount += 1
      } while(!stopped())
    }

    Report(cmp, "stream",
      (uncompressed.length - compressed.length).toDouble / uncompressed.length,
      uncompressed.length * compressCount * 1000L / compressTime.toDouble,
      uncompressed.length * uncompressCount * 1000L / uncompressTime.toDouble,
      verify(uncompressed, compressed, expected, actual)
    )
  }

  private[this] def verify(uncompressed:Array[Byte], compressed:Array[Byte], expected:Array[Byte], actual:Array[Byte]):String = {
    if(expected.length < 0 || uncompressed.length < 0 || compressed.length < 0 || actual.length < 0) {
      // for snappy-java, probably jni broken jvm heap
      "FATAL: JVM Heap Broken"
    } else diff(expected, uncompressed).map { i =>
      val (a, b) = (expected(i), uncompressed(i))
      f"buffer over-written: $a%02X != $b%02X @ $i"
    }.orElse {
      if(expected.length != actual.length) {
        Some(f"decompress fail: ${compressed.length}%,dB -> ${actual.length}%,dB != ${expected.length}%,dB")
      } else diff(expected, actual).map { i =>
        val (a, b) = (expected(i), actual(i))
        f"decompress fail: $a%02X != $b%02X @ $i"
      }
    }.getOrElse("")
  }

  private[this] def diff[T](a:Array[T], b:Array[T]):Option[Int] = if(a.length != b.length) {
    Some(math.min(a.length, b.length) + 1)
  } else {
    val i = a.indices.indexWhere { i => a(i) != b(i) }
    if(i < 0) None else Some(i)
  }

  private[this] def entropy(values:Array[Byte]):String = {
    val counts = new Array[Int](Byte.MaxValue - Byte.MinValue + 1)
    values.indices.foreach(i => counts(values(i) - Byte.MinValue) += 1)
    val e = -counts.map(c => (c + 1).toDouble / (values.length + 1)).map(p => p * math.log(p)).sum
    f"$e%,.3f"
  }

  private[this] def loadText(name:String):String = {
    val in = getClass.getClassLoader.getResourceAsStream("at/hazm/benchmark/compression/" + name)
    val text = new String(Iterator.continually(in.read()).takeWhile(_ >= 0).map(_.toByte).toArray, StandardCharsets.UTF_8)
    in.close()
    text
  }

  case class Report(cmp:Compressor, method:String, rate:Double, compress:Double, decompress:Double, error:String = "") {
    def print(out:Appendable):Unit = {
      val ratePercent = rate match {
        case _ if rate == 0.0 || rate == 1.0 => f"${rate * 100}%.1f%%"
        case _ if rate > 0.99999 => f"${rate * 100}%.4f%%"
        case _ if rate > 0.9999 => f"${rate * 100}%.3f%%"
        case _ if rate > 0.999 => f"${rate * 100}%.2f%%"
        case _ if math.abs(rate) < 0.01 => f"${rate * 100}%.3f%%"
        case _ if math.abs(rate) < 0.10 => f"${rate * 100}%.2f%%"
        case _ => f"${rate * 100}%.1f%%"
      }
      out.append(
        Seq(cmp.id, cmp.version, method,
          ratePercent,
          f"${compress / 1024 / 1024.0}%,.1f",
          f"${decompress / 1024 / 1024.0}%,.1f",
          error
        ).mkString("| ", " | ", " |\n")
      )
    }
  }

}