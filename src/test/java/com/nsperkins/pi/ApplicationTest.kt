package com.nsperkins.pi

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest(
        @Autowired private val restTemplate: TestRestTemplate,
        @LocalServerPort private val port: Int
){
    private val appUrl = "http://localhost:$port/"

    @Test
    fun testLandingPage() {
        assertThat(restTemplate.getForObject(appUrl, String::class.java)).contains("Hello")
    }
}
