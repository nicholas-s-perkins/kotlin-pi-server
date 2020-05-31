package com.nsperkins.pi

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import java.io.Closeable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest(
        @Autowired private val restTemplate: TestRestTemplate,
        @Autowired private val config: AppConfig,
        @LocalServerPort private val port: Int
) {
    private val appUrl = "http://localhost:$port/"

    @Test
    fun testLandingPage() {
        assertThat(restTemplate.getForObject(appUrl, String::class.java)).contains("Hello")
    }

    @Test
    fun testUdp() {
        UdpClient(config.udpPort).use {
            val str = it.sendMessage("henlo")
            assertEquals("HENLO", str)
        }
    }

    private class UdpClient(private val port: Int) : Closeable {
        private val socket: DatagramSocket = DatagramSocket()
        private val address: InetAddress = InetAddress.getByName("localhost")

        fun sendMessage(msg: String): String {
            val buf = msg.toByteArray()
            var packet = DatagramPacket(buf, buf.size, address, port)
            socket.send(packet)
            packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)
            return String(packet.data, 0, packet.length)
        }

        override fun close() {
            socket.close()
        }
    }
}
