package com.nsperkins.pi.video

import java.net.InetAddress

/**
 * An experimental interface for calling the raspivid program on a raspberry pi
 * and piping the data to a udp port
 */
class Raspivid(
        private val udpPort: Int
) : IRaspivid {

    private var process: Process? = null

    @Synchronized
    override fun start(): IRaspivid {
        if (process != null) throw IllegalStateException("raspivid already started")

        val localHostIp = InetAddress.getLoopbackAddress().hostAddress
        val udpAddress = "udp://${localHostIp}:${udpPort}"

        process = ProcessBuilder(
                "raspivid",
                "-t", "0",
                "-0", udpAddress
        )
                .redirectErrorStream(true)
                .start()

        return this
    }

    @Synchronized
    override fun stop() {
        process!!.destroy()
        process = null
    }
}
