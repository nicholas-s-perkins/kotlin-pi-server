package com.nsperkins.pi.video

import java.io.Closeable
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.ceil

class MockRaspivid(private val udpPort: Int, private val videoPath: String) : IRaspivid {

    private lateinit var client: UdpClient

    private val shouldRun = AtomicBoolean(true)

    @Synchronized
    override fun start(): IRaspivid {
        shouldRun.set(true)
        client = UdpClient(udpPort)

        val bytes = File(videoPath).readBytes()

        val exe = Executors.newSingleThreadExecutor()
        exe.execute {
            while(shouldRun.get()){
                client.sendMessage(bytes)
            }
        }

        return this
    }

    @Synchronized
    override fun stop() {
        shouldRun.set(false)
        client.close()
    }


    private class UdpClient(private val port: Int) : Closeable {
        private val socket: DatagramSocket = DatagramSocket()
        private val address: InetAddress = InetAddress.getByName("localhost")

        fun sendMessage(bytes: ByteArray) {
            // send in 512byte chunks (recommended via internet), udp has max of 65507 byte packages
            val chunks = divideArray(bytes, 512)

            for (chunk in chunks) {
                val packet = DatagramPacket(chunk, chunk.size, address, port)
                socket.send(packet)
            }
        }

        override fun close() {
            socket.close()
        }

        private fun divideArray(source: ByteArray, chunksize: Int): Array<ByteArray> {
            val ret = Array(ceil(source.size / chunksize.toDouble()).toInt()) { ByteArray(chunksize) }
            var start = 0
            for (i in ret.indices) {
                ret[i] = Arrays.copyOfRange(source, start, start + chunksize)
                start += chunksize
            }
            return ret
        }
    }
}
