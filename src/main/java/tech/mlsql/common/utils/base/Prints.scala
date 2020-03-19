package tech.mlsql.common.utils.base

/**
 * 18/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
class Prints {
  /**
   * Format a sequence with semantics similar to calling .mkString(). Any elements beyond
   * maxNumToStringFields will be dropped and replaced by a "... N more fields" placeholder.
   *
   * @return the trimmed and formatted string.
   */
  def truncatedString[T](
                          seq: Seq[T],
                          start: String,
                          sep: String,
                          end: String,
                          maxNumFields: Int = 20): String = {
    if (seq.length > maxNumFields) {

      val numFields = math.max(0, maxNumFields - 1)
      seq.take(numFields).mkString(
        start, sep, sep + "... " + (seq.length - numFields) + " more fields" + end)
    } else {
      seq.mkString(start, sep, end)
    }
  }

  /** Shorthand for calling truncatedString() without start or end strings. */
  def truncatedString[T](seq: Seq[T], sep: String): String = truncatedString(seq, "", sep, "")
}
