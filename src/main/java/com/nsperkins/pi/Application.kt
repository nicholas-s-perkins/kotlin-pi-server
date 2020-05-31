package com.nsperkins.pi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
open class Application {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("red5.deployment.type", "server");
            runApplication<Application>(*args)
        }
    }
}
