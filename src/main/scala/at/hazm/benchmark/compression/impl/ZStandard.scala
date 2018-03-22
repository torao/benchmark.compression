package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor
import com.github.luben.zstd.{Zstd, ZstdInputStream, ZstdOutputStream}

case class ZStandard(level:Int) extends Compressor.Block with Compressor.Stream[ZstdOutputStream] {
  val id:String = s"com.github.luben:zstd-jni (level=$level)"
  override val version:String = "1.3.3-4"

  override def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int = {
    val len = Zstd.compressByteArray(compressed, 0, compressed.length, uncompressed, 0, uncompressed.length, level)
    if(Zstd.isError(len)) {
      throw new IllegalStateException(s"zstd: ${Zstd.getErrorName(len)}")
    }
    len.toInt
  }

  override def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int = {
    val len = Zstd.decompressByteArray(uncompressed, 0, uncompressed.length, compressed, 0, length)
    if(Zstd.isError(len)) {
      throw new IllegalStateException(s"zstd: ${Zstd.getErrorName(len)}")
    }
    len.toInt
  }

  override def init(out:OutputStream, uncompressedSize:Int) = new ZstdOutputStream(out, level)

  // ZStandard output stream MUST close
  override def finish(out:ZstdOutputStream):Unit = out.close()

  override def wrap(in:InputStream):InputStream = new ZstdInputStream(in)
}
