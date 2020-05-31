package com.nsperkins.pi

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.ip.dsl.Udp
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver


@EnableWebMvc
@Configuration
class AppConfig {

    val udpPort = 11111

    @Bean
    fun freemarkerViewResolver(): FreeMarkerViewResolver {
        val resolver = FreeMarkerViewResolver()
        resolver.isCache = true
        resolver.setPrefix("")
        resolver.setSuffix(".ftl")
        return resolver
    }

    @Bean
    fun udpInConfig(): IntegrationFlow {
        return IntegrationFlows
                .from(Udp.inboundAdapter(udpPort).id("udpIn"))
                .transform { p: ByteArray? -> String(p!!).toUpperCase() }
                .handle(Udp
                        .outboundAdapter("headers['ip_packetAddress']")
                        .socketExpression("@udpIn.socket")
                )
                .get()
    }
}
