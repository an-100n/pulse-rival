package com.pulserival

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class PulseRivalApplication

fun main(args: Array<String>) {
	runApplication<PulseRivalApplication>(*args)
}
