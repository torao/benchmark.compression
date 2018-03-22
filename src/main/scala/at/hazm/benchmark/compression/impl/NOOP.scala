package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor

object NOOP extends Compressor.Block with Compressor.Stream[OutputStream] {
  val id:String = "java:*uncompress*"

  override def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int = {
    System.arraycopy(uncompressed, 0, compressed, 0, uncompressed.length)
    uncompressed.length
  }

  override def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int = {
    System.arraycopy(compressed, 0, uncompressed, 0, length)
    compressed.length
  }

  override def init(out:OutputStream, uncompressedSize:Int) = out

  override def finish(out:OutputStream):Unit = ()

  override def wrap(in:InputStream):InputStream = in
}
