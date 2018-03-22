package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import at.hazm.benchmark.compression.Compressor

object GZip extends Compressor.Stream {
  val id:String = "java:gzip"

  override def wrapInput(in:InputStream):InputStream = new GZIPInputStream(in)

  override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = withOnClosing[GZIPOutputStream](new GZIPOutputStream(out), _.finish())
}
