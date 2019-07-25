package tech.mlsql.common.utils

/**
  * 2019-07-25 WilliamZhu(allwefantasy@gmail.com)
  */
object Md5 {
  def md5Hash(text: String): String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map {
    "%02x".format(_)
  }.foldLeft("") {
    _ + _
  }
}
