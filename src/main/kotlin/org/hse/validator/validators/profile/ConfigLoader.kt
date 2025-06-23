package org.hse.validator.validators.profile

import org.hse.validator.configs.courseWorkConfig
import org.hse.validator.configs.finalQualificationWorkConfig
import org.hse.validator.validators.rules.Rule
import org.hse.validator.validators.rules.builder.RulesBuilder


object ConfigLoader {
    fun rulesFor(type: String): List<Rule> = when (type.lowercase()) {
        "выпускная квалификационная работа (вкр)" -> RulesBuilder.build(finalQualificationWorkConfig)
        "курсовая работа" -> RulesBuilder.build(courseWorkConfig)
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
}

