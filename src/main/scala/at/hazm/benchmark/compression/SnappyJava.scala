package at.hazm.benchmark.compression

import java.io.{InputStream, OutputStream}

import org.xerial.snappy.{Snappy, SnappyInputStream, SnappyOutputStream}

object SnappyJava extends Compressor.Buffer with Compressor.Stream[SnappyOutputStream] {
  val id:String = "org.xerial.snappy:snappy-java"
  override val version:String = "1.1.7.1"

  override def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int = {
    Snappy.compress(uncompressed, 0, uncompressed.length, compressed, 0)
  }

  override def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int = {
    Snappy.uncompress(compressed, 0, length, uncompressed, 0)
  }

  override def init(out:OutputStream, uncompressedSize:Int) = new SnappyOutputStream(out)

  override def finish(out:SnappyOutputStream):Unit = ()

  override def wrap(in:InputStream):InputStream = new SnappyInputStream(in)
}
