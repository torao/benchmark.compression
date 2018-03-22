package at.hazm.benchmark.compression

import java.io.{InputStream, OutputStream}
import java.util.zip.{DeflaterOutputStream, InflaterInputStream}

object ZLib extends Compressor.Stream[DeflaterOutputStream] {
  val id:String = "java:zlib"

  override def init(out:OutputStream, uncompressedSize:Int) = new DeflaterOutputStream(out)

  override def finish(out:DeflaterOutputStream):Unit = out.finish()

  override def wrap(in:InputStream):InputStream = new InflaterInputStream(in)
}
