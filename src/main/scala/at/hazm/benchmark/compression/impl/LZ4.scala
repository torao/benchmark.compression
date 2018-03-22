package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor
import net.jpountz.lz4.{LZ4BlockInputStream, LZ4BlockOutputStream, LZ4Factory}

object LZ4 extends Compressor.Block with Compressor.Stream {
  val id:String = "org.lz4:lz4-java"
  override val version:String = "1.4.1"

  private[this] val factory = LZ4Factory.fastestInstance()

  override def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int = {
    val compressor = factory.fastCompressor()
    compressor.compress(uncompressed, 0, uncompressed.length, compressed, 0, compressed.length)
  }

  override def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int = {
    val decompressor = factory.fastDecompressor()
    decompressor.decompress(compressed, 0, uncompressed, 0, uncompressed.length)
  }

  override def wrapInput(in:InputStream):InputStream = new LZ4BlockInputStream(in)

  override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = withOnClosing[LZ4BlockOutputStream](new LZ4BlockOutputStream(out), _.finish())
}
