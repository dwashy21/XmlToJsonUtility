package com.dwashy.utility

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.assertNotNull

@SpringBootTest
@ExtendWith(SpringExtension::class)
open class TestApplication {
    @Autowired lateinit var webClient: WebClient

    @Test
    fun webClientBeanExists() {
        assertNotNull(webClient)
    }
}