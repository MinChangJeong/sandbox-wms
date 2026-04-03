package com.wms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class WmsApplication

fun main(args: Array<String>) {
    runApplication<WmsApplication>(*args)
}
