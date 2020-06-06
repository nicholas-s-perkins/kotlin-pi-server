package com.nsperkins.pi

import com.nsperkins.pi.video.IRaspivid
import com.nsperkins.pi.video.Raspivid
import com.nsperkins.pi.video.VideoStreamHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.ip.dsl.Udp
import org.springframework.jmx.support.RegistrationPolicy
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver


@EnableWebMvc
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
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
    fun streamHandler(): VideoStreamHandler {
        return VideoStreamHandler()
    }

    @Bean
    fun udpInConfig(handler: VideoStreamHandler): IntegrationFlow {
        return IntegrationFlows
                .from(Udp.inboundAdapter(udpPort).id("udpIn"))
                .handle(handler)
                .get()
    }

    @Bean
    fun raspivid(): IRaspivid {
        return Raspivid(udpPort);
    }
}
