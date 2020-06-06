package com.nsperkins.pi.video

import org.springframework.integration.handler.GenericHandler
import org.springframework.messaging.MessageHeaders
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class VideoStreamHandler : GenericHandler<ByteArray> {

    private val consumers: MutableMap<UUID, Subscriber> = ConcurrentHashMap<UUID, Subscriber>()

    override fun handle(payload: ByteArray, headers: MessageHeaders): Any? {
        for (consumer in consumers) {
            consumer.value.receiveBytes(payload)
        }
        return null
    }


    // called via another thread
    fun subscribe(key: UUID): OutputStream {
        val output = BufferedOutputStream(ByteArrayOutputStream(512), 512)
        consumers[key] = object : Subscriber {
            override fun receiveBytes(chunk: ByteArray) {
                output.write(chunk)
                output.flush()
            }

        }
        return output;
    }

    fun remove(key: UUID) {
        consumers.remove(key)
    }

    @FunctionalInterface
    interface Subscriber {
        fun receiveBytes(chunk: ByteArray)
    }
}
