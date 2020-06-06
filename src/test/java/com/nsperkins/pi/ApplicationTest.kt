package com.nsperkins.pi

import com.nsperkins.pi.video.IRaspivid
import com.nsperkins.pi.video.MockRaspivid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.util.StreamUtils
import org.springframework.web.client.ResponseExtractor
import java.io.File
import java.io.FileOutputStream


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
    fun testRaspivid() {
        restTemplate.postForLocation("$appUrl/video/stream/start", null)

        val file = File.createTempFile("download", "tmp")
        file.deleteOnExit()

        assertEquals(0, file.readBytes().size)

        //see bytes delivered
        restTemplate.execute("$appUrl/video/stream", HttpMethod.GET, null, ResponseExtractor { httpRes ->
            StreamUtils.copy(httpRes.getBody(), FileOutputStream(file))
        })

        Thread.sleep(500)

        assertTrue(file.readBytes().isNotEmpty())

        //cleanup
        restTemplate.postForLocation("$appUrl/video/stream/stop", null)

    }

    //    @Test
//    fun testUdp() {
//        UdpClient(config.udpPort).use {
//            val str = it.sendMessage("henlo")
//            assertEquals("HENLO", str)
//        }
//    }

//    @Test
//    fun testFile() {
//        val vidBytes = File("test_video.mp4").readBytes()
//        assertEquals(1136991, vidBytes.size)
//    }

//    private class UdpClient(private val port: Int) : Closeable {
//        private val socket: DatagramSocket = DatagramSocket()
//        private val address: InetAddress = InetAddress.getByName("localhost")
//
//        fun sendMessage(msg: String): String {
//            val buf = msg.toByteArray()
//            var packet = DatagramPacket(buf, buf.size, address, port)
//            socket.send(packet)
//            packet = DatagramPacket(buf, buf.size)
//            socket.receive(packet)
//            return String(packet.data, 0, packet.length)
//        }
//
//        override fun close() {
//            socket.close()
//        }
//    }

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun raspivid(): IRaspivid {
            return MockRaspivid(11111, "test_video.mp4")
        }
    }
}
