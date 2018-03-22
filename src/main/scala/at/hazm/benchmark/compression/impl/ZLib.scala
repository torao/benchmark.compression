package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}
import java.util.zip.{DeflaterOutputStream, InflaterInputStream}

import at.hazm.benchmark.compression.Compressor

object ZLib extends Compressor.Stream {
  val id:String = "java:zlib"

  override def wrapInput(in:InputStream):InputStream = new InflaterInputStream(in)

  override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = withOnClosing[DeflaterOutputStream](new DeflaterOutputStream(out), _.finish())
}
