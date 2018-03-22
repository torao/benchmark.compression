package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import at.hazm.benchmark.compression.Compressor

object GZip extends Compressor.Stream[GZIPOutputStream] {
  val id:String = "java:gzip"

  override def init(out:OutputStream, uncompressedSize:Int) = new GZIPOutputStream(out)

  override def finish(out:GZIPOutputStream):Unit = out.finish()

  override def wrap(in:InputStream):InputStream = new GZIPInputStream(in)
}
