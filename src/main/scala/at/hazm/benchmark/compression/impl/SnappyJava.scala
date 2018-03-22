package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor
import org.xerial.snappy.{Snappy, SnappyInputStream, SnappyOutputStream}

object SnappyJava extends Compressor.Block with Compressor.Stream {
  val id:String = "org.xerial.snappy:snappy-java"
  override val version:String = "1.1.7.1"

  override def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int = {
    Snappy.compress(uncompressed, 0, uncompressed.length, compressed, 0)
  }

  override def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int = {
    Snappy.uncompress(compressed, 0, length, uncompressed, 0)
  }

  override def wrapInput(in:InputStream):InputStream = new SnappyInputStream(in)

  override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = new SnappyOutputStream(out)
}
