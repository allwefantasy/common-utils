package tech.mlsql.common.utils

import com.google.common.base.CaseFormat

/**
 * 17/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
object NameConvert {
  def camelToUnderScore(name: String) = {
    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
  }

  def underScoreToCamel(name: String) = {
    CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name)
  }
}
