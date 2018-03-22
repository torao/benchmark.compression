package at.hazm.benchmark.compression

import java.io.{InputStream, OutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

object GZip extends Compressor.Stream[GZIPOutputStream] {
  val id:String = "java:gzip"

  override def init(out:OutputStream, uncompressedSize:Int) = new GZIPOutputStream(out)

  override def finish(out:GZIPOutputStream):Unit = out.finish()

  override def wrap(in:InputStream):InputStream = new GZIPInputStream(in)
}
