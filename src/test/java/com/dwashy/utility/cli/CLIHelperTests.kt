package com.dwashy.utility.cli

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [CLIHelper]
 *
 * @author dwashy21
 */
class CLIHelperTests {
    private var cliHelper: CLIHelper? = null

    @BeforeEach
    fun beforeEach() {
        cliHelper = CLIHelper()
    }

    @ParameterizedTest
    @ValueSource(strings = ["quit", "QUIT", "QuIt"])
    @ExpectSystemExitWithStatus(0)
    fun determineXmlSource_quitHappyPath(input: String) {
        cliHelper!!.determineXmlSource(input)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "     ", "\n"])
    fun determineXmlSource_acceptDefault(input: String) {
        val xmlSource = cliHelper!!.determineXmlSource(input)
        assertThat(xmlSource).isEqualTo(cliHelper!!.defaultXmlSource)
    }

    @Test
    fun determineXmlSource_newSource() {
        val newSource = "blah"
        val xmlSource = cliHelper!!.determineXmlSource(newSource)
        assertThat(xmlSource).isEqualTo(newSource)
    }

    @ParameterizedTest
    @MethodSource
    fun isValidXml(xml: Mono<String>) {
        StepVerifier.create(cliHelper!!.isValidXml(xml))
            .assertNext{boolean -> run {
                assertTrue(boolean)
            }}
            .expectComplete()
            .verify()

    }

    @ParameterizedTest
    @MethodSource
    fun isInvalidXml(xml: Mono<String>) {
        StepVerifier.create(cliHelper!!.isValidXml(xml))
                .assertNext{boolean -> run {
                    assertFalse(boolean)
                }}
                .expectComplete()
                .verify()

    }

    companion object {
        @JvmStatic
        fun isValidXml(): Stream<Arguments> {
            val charset = StandardCharsets.US_ASCII
            return Stream.of(
                Arguments.of(Mono.just(Files.readString(Path.of("src/test/resources/xml/AddressBook_MissingFax.xml"), charset))),
                Arguments.of(Mono.just(Files.readString(Path.of("src/test/resources/xml/AddressBook_MissingPostalCode.xml"), charset))),
                Arguments.of(Mono.just(Files.readString(Path.of("src/test/resources/xml/AddressBook_MissingRegion.xml"), charset)))
            )
        }

        @JvmStatic
        fun isInvalidXml(): Stream<Arguments> {
            val charset = StandardCharsets.US_ASCII
            return Stream.of(
                    Arguments.of(Mono.just(Files.readString(Path.of("src/test/resources/xml/AddressBook_MissingAddress.xml"), charset))),
                    Arguments.of(Mono.just(Files.readString(Path.of("src/test/resources/xml/AddressBook_MissingCustomerID.xml"), charset)))
            )
        }
    }
}