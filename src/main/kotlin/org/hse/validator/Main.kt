package org.hse.validator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ValidatorApplication

fun main(args: Array<String>) {
    runApplication<ValidatorApplication>(*args)
}
