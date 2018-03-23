package at.hazm.benchmark.compression

import java.io._
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.text.{DateFormat, SimpleDateFormat}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{Date, Properties, Timer, TimerTask}

import at.hazm.benchmark.compression.Benchmark._
import at.hazm.benchmark.compression.impl._

object Main {

  /** Compression algorithm implementations to benchmark. */
  val CompressionImpls = Seq(
    NOOP,  SnappyJava, LZ4,
    ZStandard(1), ZStandard(3), ZStandard(6), ZStandard(9), ZStandard(16),
    ZLib, GZip, ApacheCompress.BZIP2, Brotli)

  /** ByteArray generator for benchmark. */
  val Benchmarks = Seq(
    ZeroFilledByteArray, UniformRandomByteArray, GaussianRandomIntArray(sigma2 = 100), GaussianRandomDoubleArray(),
    RandomDoubleCSV, USDeclarationOfIndependenceText, JapaneseNovelKokoro
  )

  /** Time of repeat to compress or expand a binary. */
  val MeasurementTimeInMillis:Long = 10 * 1000

  val PreferredBinarySize:Int = 10 * 1024 * 1024

  def main(args:Array[String]):Unit = {

    // load classes and warming up JIT
    System.out.println("# WARMING UP!")
    exec(System.out, ZeroFilledByteArray)

    // open report file and write headings
    val fileName = "benchmark_" + System.getProperty("os.arch").toLowerCase + "_" +
      System.getProperty("os.name").replaceAll("\\s+", "").toLowerCase + "_" +
      new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".md"
    val out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))
    out.println("# JavaVM Compression Libraries Benchmark & Comparison")
    out.println()
    out.println(shortInfo(Seq(
      "Date" -> DateFormat.getDateTimeInstance().format(new Date()),
      "Author" -> s"${System.getProperty("user.name")} @ ${InetAddress.getLocalHost.getHostName}",
      "JavaVM" -> s"${System.getProperty("java.vm.name")} ${System.getProperty("java.vm.vendor")} ${System.getProperty("java.vm.version")}"
    ) ++ {
      val file = new File("machine-info.xml")
      if(file.isFile) {
        val prop = new Properties()
        prop.loadFromXML(new ByteArrayInputStream(Files.readAllBytes(file.toPath)))
        Seq("product" -> "Machine Model", "platform" -> "Operating System", "processor" -> "CPU", "memory" -> "Memory", "description" -> "Description").map { case (key, title) =>
          title -> prop.getProperty(key, "-")
        }
      } else Seq.empty
    }:_*))
    out.println()

    // execute benchmark and write report
    Benchmarks.foreach { benchmark =>
      exec(out, benchmark)
      out.flush()
    }

    out.close()
  }

  def exec(out:Appendable, bm:Benchmark, msec:Long = MeasurementTimeInMillis):Unit = {
    val title = bm.title(PreferredBinarySize)
    val sample = bm.newSample(PreferredBinarySize)

    def flush():Unit = out match {
      case p:PrintWriter => p.flush()
      case p:PrintStream => p.flush()
      case _ => ()
    }

    System.out.println(title)
    val info = shortInfo(
      "Block Size" -> s"${Report.unitText(sample.length)}B",
      "Block Entropy" -> Report.flexPrecision(entropy(sample), 3, 6),
      "Filled By" -> s"<code>${bm.sampleCode()}</code>",
      "Sample Bytes" -> s"<small>${sample.take(15).map(b => f"${b & 0xFF}%02X").mkString("", " ", "...")}</small>"
    )
    out.append(s"## $title\n\n$info\n\n")
    out.append(
      f"""|| Name | Version | Method | Ratio | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
         ||:-----|:--------|:-------|------:|---------------------------------:|-----------------------------------:|:----|
         |""".stripMargin)
    CompressionImpls.foreach { cmp =>
      // run block type compression benchmark
      cmp match {
        case buf:Compressor.Block => benchmark(sample, buf:Compressor.Block, msec).print(out)
        case _ =>
      }
      flush()
      // run stream type compression benchmark
      cmp match {
        case str:Compressor.Stream => benchmark(sample, str:Compressor.Stream, msec).print(out)
        case _ =>
      }
      flush()
    }
    out.append(s"\n")
    flush()
  }

  private[this] def shortInfo(info:(String, String)*):String = {
    info.filter(_._2.nonEmpty).map { case (name, value) => s"<b>$name</b>: $value" }.mkString("<div><small>", ", ", " </small></div>")
  }

  private[this] val timer = new Timer(true)

  private[this] def benchmark(msec:Long)(f:(() => Boolean) => Unit):Long = {
    val stopped = new AtomicBoolean(false)
    timer.schedule(new TimerTask {
      override def run():Unit = stopped.set(true)
    }, msec)
    val t0 = System.currentTimeMillis()
    f(() => stopped.get())
    System.currentTimeMillis() - t0
  }

  private[this] def benchmark(uncompressed:Array[Byte], cmp:Compressor.Block, msec:Long):Report = {
    val compressed = new Array[Byte]((uncompressed.length * 1.2).toInt)
    val length = cmp.compress(uncompressed, compressed)
    val expected = java.util.Arrays.copyOf(uncompressed, uncompressed.length)
    val actual = new Array[Byte](uncompressed.length)

    // 圧縮の計測
    var compressCount = 0L
    val compressTime = benchmark(msec) { stopped =>
      do {
        cmp.compress(uncompressed, compressed)
        compressCount += 1
      } while(!stopped())
    }

    // 展開の計測
    var uncompressCount = 0L
    val uncompressTime = benchmark(msec) { stopped =>
      do {
        cmp.decompress(compressed, length, actual)
        uncompressCount += 1
      } while(!stopped())
    }

    Report(cmp, "block",
      (uncompressed.length - length).toDouble / uncompressed.length,
      uncompressed.length * compressCount * 1000L / compressTime.toDouble,
      uncompressed.length * uncompressCount * 1000L / uncompressTime.toDouble,
      verify(uncompressed, compressed, expected, actual)
    )
  }

  private[this] def benchmark(uncompressed:Array[Byte], cmp:Compressor.Stream, msec:Long):Report = {
    val expected = uncompressed.clone()

    // 圧縮の計測
    val out = new ByteArrayOutputStream(uncompressed.length)
    var compressCount = 0L
    val compressTime = benchmark(msec) { stopped =>
      do {
        out.reset()
        cmp.compress(uncompressed, out)
        compressCount += 1
      } while(!stopped())
    }

    // 展開の計測
    val compressed = out.toByteArray
    val in = new ByteArrayInputStream(compressed)
    in.mark(0)
    val actual = cmp.decompress(in, uncompressed.length)
    var uncompressCount = 0L
    val uncompressTime = benchmark(msec) { stopped =>
      do {
        in.reset()
        cmp.decompress(in, uncompressed.length)
        uncompressCount += 1
      } while(!stopped())
    }

    Report(cmp, "stream",
      (uncompressed.length - compressed.length).toDouble / uncompressed.length,
      uncompressed.length * compressCount * 1000L / compressTime.toDouble,
      uncompressed.length * uncompressCount * 1000L / uncompressTime.toDouble,
      verify(uncompressed, compressed, expected, actual)
    )
  }

  private[this] def verify(uncompressed:Array[Byte], compressed:Array[Byte], expected:Array[Byte], actual:Array[Byte]):String = {
    if(expected.length < 0 || uncompressed.length < 0 || compressed.length < 0 || actual.length < 0) {
      // for snappy-java, probably jni broken jvm heap
      "FATAL: JVM Heap Broken"
    } else diff(expected, uncompressed).map { i =>
      val (a, b) = (expected(i), uncompressed(i))
      f"buffer over-written: $a%02X != $b%02X @ $i"
    }.orElse {
      if(expected.length != actual.length) {
        Some(f"decompress fail: ${compressed.length}%,dB -> ${actual.length}%,dB != ${expected.length}%,dB")
      } else diff(expected, actual).map { i =>
        val (a, b) = (expected(i), actual(i))
        f"decompress fail: $a%02X != $b%02X @ $i"
      }
    }.getOrElse("")
  }

  private[this] def diff[T](a:Array[T], b:Array[T]):Option[Int] = if(a.length != b.length) {
    Some(math.min(a.length, b.length) + 1)
  } else {
    val i = a.indices.indexWhere { i => a(i) != b(i) }
    if(i < 0) None else Some(i)
  }

  /**
    * Calculate information entropy for specified bytes.
    *
    * @param values bytes to calculate its entropy
    * @return entropy string
    */
  private[this] def entropy(values:Array[Byte]):Double = {
    // calculate entropy
    -values.indices.foldLeft(new Array[Int](256)) { case (array, i) =>
      array(values(i) - Byte.MinValue) += 1
      array
    }
      // to avoid divergence, the number of occurrence 1 is assigned to all bytes
      .map(_ + 1)
      // therefore the total increases +256
      .map { count => count.toDouble / (values.length + 256) }
      .map(p => p * math.log(p)).sum
  }

}
