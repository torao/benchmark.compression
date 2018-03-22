package at.hazm.benchmark.compression

import java.io.{InputStream, OutputStream}
import java.util

import scala.annotation.tailrec

sealed trait Compressor {
  val id:String
  val version:String = "Java " + System.getProperty("java.version")
}

object Compressor {

  trait Buffer extends Compressor {
    def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int

    def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int
  }

  trait Stream[O <: OutputStream] extends Compressor {
    def compress(uncompressed:Array[Byte], out:OutputStream):Unit = {
      val os = init(out, uncompressed.length)
      os.write(uncompressed)
      finish(os)
      os.close()
    }

    def decompress(in:InputStream, uncompressedSize:Int):Array[Byte] = {
      val is = wrap(in)
      val buffer = new Array[Byte](uncompressedSize)

      @tailrec
      def _read(pos:Int, length:Int):Int = {
        val len = is.read(buffer, pos, buffer.length - pos)
        if(len > 0) _read(pos + len, length + len) else length
      }

      val filled = _read(0, 0)
      if(filled != buffer.length) util.Arrays.copyOf(buffer, filled) else buffer
    }

    protected def init(out:OutputStream, uncompressedSize:Int):O

    protected def finish(out:O):Unit = ()

    protected def wrap(in:InputStream):InputStream = in
  }

}