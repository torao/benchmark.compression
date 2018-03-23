package at.hazm.benchmark.compression.impl

import java.io.{FilterInputStream, FilterOutputStream, InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor

object NOOP extends Compressor.Block with Compressor.Stream {
  val id:String = "java:*uncompress*"

  override def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int = {
    System.arraycopy(uncompressed, 0, compressed, 0, uncompressed.length)
    uncompressed.length
  }

  override def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int = {
    System.arraycopy(compressed, 0, uncompressed, 0, length)
    compressed.length
  }

  override def wrapInput(in:InputStream):InputStream = new FilterInputStream(in){/* to access protected constructor. */}

  override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = new FilterOutputStream(out)
}
