package tech.mlsql.common.utils.names

import tech.mlsql.common.utils.base

/**
 * 17/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
object NameConvert {
  def lowerCamelToLowerUnderScore(name: String) = {
    base.CaseFormat.LOWER_CAMEL.to(base.CaseFormat.LOWER_UNDERSCORE, name)
  }

  def upperCamelToLowerUnderScore(name: String) = {
    base.CaseFormat.UPPER_CAMEL.to(base.CaseFormat.LOWER_UNDERSCORE, name)
  }

  def lowerCamelToUpperUnderScore(name: String) = {
    base.CaseFormat.LOWER_CAMEL.to(base.CaseFormat.UPPER_UNDERSCORE, name)
  }

  def upperCamelToUpperUnderScore(name: String) = {
    base.CaseFormat.UPPER_CAMEL.to(base.CaseFormat.UPPER_UNDERSCORE, name)
  }

  def lowerUnderScoreToLowerCamel(name: String) = {
    base.CaseFormat.LOWER_UNDERSCORE.to(base.CaseFormat.LOWER_CAMEL, name)
  }

  def lowerUnderScoreToUpperCamel(name: String) = {
    base.CaseFormat.LOWER_UNDERSCORE.to(base.CaseFormat.UPPER_CAMEL, name)
  }

  def upperUnderScoreToUpperCamel(name: String) = {
    base.CaseFormat.UPPER_UNDERSCORE.to(base.CaseFormat.UPPER_CAMEL, name)
  }

  def upperUnderScoreToLowerCamel(name: String) = {
    base.CaseFormat.UPPER_UNDERSCORE.to(base.CaseFormat.LOWER_CAMEL, name)
  }


}
