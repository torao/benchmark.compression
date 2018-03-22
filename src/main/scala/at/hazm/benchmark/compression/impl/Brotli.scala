package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor
import org.meteogroup.jbrotli.io.{BrotliInputStream, BrotliOutputStream}
import org.meteogroup.jbrotli.{BrotliCompressor, BrotliDeCompressor, Brotli => B}

object Brotli extends Compressor.Block with Compressor.Stream[BrotliOutputStream] {
  val id:String = "org.meteogroup.jbrotli:jbrotli"
  override val version:String = "0.5.0"

  override def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int = {
    val compressor = new BrotliCompressor()
    compressor.compress(B.DEFAULT_PARAMETER, uncompressed, 0, uncompressed.length, compressed, 0, compressed.length)
  }

  override def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int = {
    val decompressor = new BrotliDeCompressor()
    decompressor.deCompress(compressed, 0, length, uncompressed, 0, uncompressed.length)
  }

  override def init(out:OutputStream, uncompressedSize:Int) = new BrotliOutputStream(out)

  override def wrap(in:InputStream):InputStream = new BrotliInputStream(in)

}
