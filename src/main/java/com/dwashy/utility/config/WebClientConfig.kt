package com.dwashy.utility.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuration that provides a [WebClient] bean to the Spring Application Context
 *
 * @author dwashy21
 */
@Configuration
open class WebClientConfig {
    @Bean
    open fun webClient(): WebClient {
        return WebClient.builder().build()
    }
}