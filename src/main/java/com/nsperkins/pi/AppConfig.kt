package com.nsperkins.pi

import com.nsperkins.pi.red5.Red5Adapter
import org.red5.server.Context
import org.red5.server.Server
import org.red5.server.api.IContext
import org.red5.server.scope.GlobalScope
import org.red5.server.scope.WebScope
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.StandardEnvironment
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver

@EnableWebMvc
@Configuration
open class AppConfig {

    @Bean
    open fun freemarkerViewResolver(): FreeMarkerViewResolver {
        val resolver = FreeMarkerViewResolver()
        resolver.isCache = true
        resolver.setPrefix("")
        resolver.setSuffix(".ftl")
        return resolver
    }

    @Bean("red5Adapter")
    open fun red5Adapter(): Red5Adapter {
        return Red5Adapter()
    }

    @Bean
    open fun env(): Environment {
        return StandardEnvironment()
    }


    @Bean(initMethod = "register")
    open fun webScope(ihandler:  Red5Adapter): WebScope {
        return object : WebScope() {
            init {
                server = Server()
                parent = GlobalScope()
                context = Context()
                handler = ihandler
                contextPath = "/red5"
                virtualHosts = "*"
            }
        }
    }
}
