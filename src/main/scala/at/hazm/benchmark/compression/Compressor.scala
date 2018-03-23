package at.hazm.benchmark.compression

import java.io.{FilterOutputStream, InputStream, OutputStream}
import java.util

import scala.annotation.tailrec

/**
  * A trait that represents the compression algorithm implementations for the benchmark.
  */
sealed trait Compressor {
  val id:String
  val version:String = "Java " + System.getProperty("java.version")
}

object Compressor {

  /**
    * Block compressor can compress/decompress on buffered memory data.
    */
  trait Block extends Compressor {

    /**
      * Compress specified buffered memory data to other.
      *
      * @param uncompressed input buffer
      * @param compressed   output buffer
      * @return length actually written in `compressed`
      */
    def compress(uncompressed:Array[Byte], compressed:Array[Byte]):Int

    def decompress(compressed:Array[Byte], length:Int, uncompressed:Array[Byte]):Int
  }

  /**
    * Stream compressor supply streaming compression/decompression by InputStream and OutputStream.
    */
  trait Stream extends Compressor {
    def compress(uncompressed:Array[Byte], out:OutputStream):Unit = {
      val os = wrapOutput(out, uncompressed.length)
      os.write(uncompressed)
      os.close()
    }

    def decompress(in:InputStream, expectedUncompressSize:Int):Array[Byte] = {
      val is = wrapInput(in)
      val buffer = new Array[Byte](expectedUncompressSize)

      @tailrec
      def _read(pos:Int, length:Int):Int = {
        val len = is.read(buffer, pos, buffer.length - pos)
        if(len > 0) _read(pos + len, length + len) else length
      }

      val filled = _read(0, 0)
      is.close()
      if(filled != buffer.length) util.Arrays.copyOf(buffer, filled) else buffer
    }

    protected def wrapInput(in:InputStream):InputStream

    protected def wrapOutput(out:OutputStream, expandSize:Int):OutputStream

    protected def withOnClosing[T <: OutputStream](os:T, onClosing:(T) => Unit):OutputStream = new FilterOutputStream(os) {
      override def close():Unit = {
        onClosing(os)
        super.close()
      }
    }
  }

}