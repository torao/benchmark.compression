package at.hazm.benchmark.compression

case class Report(cmp:Compressor, method:String, rate:Double, compress:Double, decompress:Double, error:String = "") {
  def print(out:Appendable):Unit = out.append(
    Seq(cmp.id, cmp.version, method,
      Report.flexPercentPrecision(rate, 1, 5),
      Report.flexPrecision(compress / 1024 / 1024.0, 1, 4),
      Report.flexPrecision(decompress / 1024 / 1024.0, 1, 4),
      if(error.isEmpty) "✔" else "✘" + error
    ).mkString("| ", " | ", " |\n")
  )
}

object Report {

  def unitText(value:Int):String = if(value < 1024) {
    f"$value%,d"
  } else {
    def _search(value:Double, unit:Seq[String]):String = if(math.abs(value) < 1024.0 || unit.length == 1) {
      flexPrecision(value, 1, 4) + unit.head
    } else _search(value / 1024.0, unit.drop(1))

    _search(value / 1024.0, Seq("k", "M", "G", "T", "P"))
  }

  def flexPercentPrecision(_value:Double, defaultPrecision:Int, maxPrecision:Int):String = {
    val value = _value * 100.0
    if(math.abs(value) >= 99.9 && math.abs(value) < 100.0) {
      val precision = math.ceil(math.abs(math.log10(100.0 - math.abs(value)))).toInt
      precisionString(value, precision, maxPrecision) + "%"
    } else flexPrecision(value, defaultPrecision, maxPrecision) + "%"
  }

  def flexPrecision(value:Double, defaultPrecision:Int, maxPrecision:Int):String = {
    val precision = if(math.abs(value) <= 0.10 && math.abs(value) > 0.0) {
      math.ceil(math.abs(math.log10(math.abs(value)))).toInt
    } else defaultPrecision
    precisionString(value, precision, maxPrecision)
  }

  private[this] def precisionString(value:Double, precision:Int, maxPrecision:Int):String = {
    String.format("%1$,." + math.min(precision, maxPrecision) + "f", Array[Object](new java.lang.Double(value)):_*)
  }
}