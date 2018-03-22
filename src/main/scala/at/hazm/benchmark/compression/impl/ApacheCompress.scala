package at.hazm.benchmark.compression.impl

import java.io.{InputStream, OutputStream}

import at.hazm.benchmark.compression.Compressor
import org.apache.commons.compress.compressors.bzip2.{BZip2CompressorInputStream, BZip2CompressorOutputStream}
import org.apache.commons.compress.compressors.snappy.{SnappyCompressorInputStream, SnappyCompressorOutputStream}
import org.apache.commons.compress.compressors.zstandard.{ZstdCompressorInputStream, ZstdCompressorOutputStream}

object ApacheCompress {
  val Version:String = "1.16.1"
  val Id = "org.apache.commons:commons-compress"

  object Snappy extends Compressor.Stream[SnappyCompressorOutputStream] {
    val id:String = s"$Id (snappy)"
    override val version:String = Version

    override def init(out:OutputStream, uncompressedSize:Int) = new SnappyCompressorOutputStream(out, uncompressedSize)

    override def finish(out:SnappyCompressorOutputStream):Unit = out.finish()

    override def wrap(in:InputStream):InputStream = new SnappyCompressorInputStream(in)
  }

  // ZStandard implementation of Apache Commons rely on "luben/zstd-jni"
  object ZStandard extends Compressor.Stream[ZstdCompressorOutputStream] {
    val id:String = s"$Id (zstandard)"
    override val version:String = Version

    override def init(out:OutputStream, uncompressedSize:Int) = new ZstdCompressorOutputStream(out)

    override def wrap(in:InputStream):InputStream = new ZstdCompressorInputStream(in)
  }

  object BZIP2 extends Compressor.Stream[BZip2CompressorOutputStream] {
    val id:String = s"$Id (bzip2)"
    override val version:String = Version

    override def init(out:OutputStream, uncompressedSize:Int) = new BZip2CompressorOutputStream(out)

    override def wrap(in:InputStream):InputStream = new BZip2CompressorInputStream(in)
  }

}
