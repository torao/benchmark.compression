package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor
import org.apache.commons.compress.compressors.bzip2.{BZip2CompressorInputStream, BZip2CompressorOutputStream}
import org.apache.commons.compress.compressors.snappy.{SnappyCompressorInputStream, SnappyCompressorOutputStream}
import org.apache.commons.compress.compressors.zstandard.{ZstdCompressorInputStream, ZstdCompressorOutputStream}

object ApacheCompress {
  val Version:String = "1.16.1"
  val Id = "org.apache.commons:commons-compress"

  object Snappy extends Compressor.Stream {
    val id:String = s"$Id (snappy)"
    override val version:String = Version

    override def wrapInput(in:InputStream):InputStream = new SnappyCompressorInputStream(in)

    override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = withOnClosing[SnappyCompressorOutputStream](new SnappyCompressorOutputStream(out, expandSize), _.finish())
  }

  // ZStandard implementation of Apache Commons rely on "luben/zstd-jni"
  object ZStandard extends Compressor.Stream {
    val id:String = s"$Id (zstandard)"
    override val version:String = Version

    override def wrapInput(in:InputStream):InputStream = new ZstdCompressorInputStream(in)

    override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = new ZstdCompressorOutputStream(out)
  }

  object BZIP2 extends Compressor.Stream {
    val id:String = s"$Id (bzip2)"
    override val version:String = Version

    override def wrapInput(in:InputStream):InputStream = new BZip2CompressorInputStream(in)

    override def wrapOutput(out:OutputStream, expandSize:Int):OutputStream = withOnClosing[BZip2CompressorOutputStream](new BZip2CompressorOutputStream(out), _.finish())
  }

}
