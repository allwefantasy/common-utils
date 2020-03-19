package tech.mlsql.common.utils.base

import java.math.{MathContext, RoundingMode}
import java.util.Locale
import java.util.concurrent.TimeUnit

import tech.mlsql.common.utils.distribute.socket.server.{ByteUnit, JavaUtils}

/**
 * 18/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
object Measurement {
  def timeStringAs(str: String, unit: TimeUnit) = {
    JavaUtils.timeStringAs(str, unit)
  }

  def timeStringAsMs(str: String): Long = timeStringAs(str, TimeUnit.MILLISECONDS)

  def timeStringAsSec(str: String): Long = timeStringAs(str, TimeUnit.SECONDS)

  def byteStringAs(str: String, unit: ByteUnit) = {
    JavaUtils.byteStringAs(str, unit)
  }

  def byteStringAsBytes(str: String): Long = byteStringAs(str, ByteUnit.BYTE)

  def byteStringAsKb(str: String): Long = byteStringAs(str, ByteUnit.KiB)

  def byteStringAsMb(str: String): Long = byteStringAs(str, ByteUnit.MiB)

  def byteStringAsGb(str: String): Long = byteStringAs(str, ByteUnit.GiB)

  /**
   * Return the string to tell how long has passed in milliseconds.
   */
  def getUsedTimeMs(startTimeMs: Long): String = {
    " " + (System.currentTimeMillis - startTimeMs) + " ms"
  }

  def memoryStringToMb(str: String): Int = {
    // Convert to bytes, rather than directly to MB, because when no units are specified the unit
    // is assumed to be bytes
    (JavaUtils.byteStringAsBytes(str) / 1024 / 1024).toInt
  }

  /**
   * Returns a human-readable string representing a duration such as "35ms"
   */
  def msDurationToString(ms: Long): String = {
    val second = 1000
    val minute = 60 * second
    val hour = 60 * minute
    val locale = Locale.US

    ms match {
      case t if t < second =>
        "%d ms".formatLocal(locale, t)
      case t if t < minute =>
        "%.1f s".formatLocal(locale, t.toFloat / second)
      case t if t < hour =>
        "%.1f m".formatLocal(locale, t.toFloat / minute)
      case t =>
        "%.2f h".formatLocal(locale, t.toFloat / hour)
    }
  }

  def bytesToString(size: BigInt): String = {
    val EB = 1L << 60
    val PB = 1L << 50
    val TB = 1L << 40
    val GB = 1L << 30
    val MB = 1L << 20
    val KB = 1L << 10

    if (size >= BigInt(1L << 11) * EB) {
      // The number is too large, show it in scientific notation.
      BigDecimal(size, new MathContext(3, RoundingMode.HALF_UP)).toString() + " B"
    } else {
      val (value, unit) = {
        if (size >= 2 * EB) {
          (BigDecimal(size) / EB, "EB")
        } else if (size >= 2 * PB) {
          (BigDecimal(size) / PB, "PB")
        } else if (size >= 2 * TB) {
          (BigDecimal(size) / TB, "TB")
        } else if (size >= 2 * GB) {
          (BigDecimal(size) / GB, "GB")
        } else if (size >= 2 * MB) {
          (BigDecimal(size) / MB, "MB")
        } else if (size >= 2 * KB) {
          (BigDecimal(size) / KB, "KB")
        } else {
          (BigDecimal(size), "B")
        }
      }
      "%.1f %s".formatLocal(Locale.US, value, unit)
    }
  }

  def bytesToString(size: Long): String = bytesToString(BigInt(size))

  /**
   * Convert a quantity in megabytes to a human-readable string such as "4.0 MB".
   */
  def megabytesToString(megabytes: Long): String = {
    bytesToString(megabytes * 1024L * 1024L)
  }
}
