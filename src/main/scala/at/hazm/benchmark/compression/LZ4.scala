package at.hazm.benchmark.compression

import java.io.{InputStream, OutputStream}

import net.jpountz.lz4.{LZ4BlockInputStream, LZ4BlockOutputStream, LZ4Factory}

object LZ4 extends Compressor.Buffer with Compressor.Stream[LZ4BlockOutputStream] {
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

  override def init(out:OutputStream, uncompressedSize:Int) = new LZ4BlockOutputStream(out)

  override def finish(out:LZ4BlockOutputStream):Unit = out.finish()

  override def wrap(in:InputStream):InputStream = new LZ4BlockInputStream(in)
}
