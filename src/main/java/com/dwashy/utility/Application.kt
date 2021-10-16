package com.dwashy.utility

import com.dwashy.utility.cli.CLIHelper
import org.json.JSONObject
import org.json.XML
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI


/**
 * The main application of this project. Serves as the entry point for execution during runtime
 *
 * @author dwashy21
 */
@SpringBootApplication
open class Application : CommandLineRunner {
    @Autowired private lateinit var webClient: WebClient

    /**
     * Companion object containing the main method of the application. Analogous to the traditional
     * 'public static void main(String[] args)' method in Java applications
     */
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }

    /**
     * The main entry point of this application. Will be executed at runtime
     * @param args [Array] of [String] arguments provided during execution
     */
    override fun run(args: Array<String>) {
        val cliHelper = CLIHelper()
        lateinit var xml: Mono<String>
        var validXml = false
        var quit = false

        while(!validXml || quit) {
            val userInput = cliHelper.promptUser()
            val xmlSource = cliHelper.determineXmlSource(userInput)
            xml = webClient.get()
                           .uri(URI(xmlSource))
                           .retrieve()
                           .bodyToMono(String::class.java)

            validXml = cliHelper.isValidXml(xml!!).block()
            if(!validXml) {
                println("URI source has provided bad XML that does not adhere to the schema.")
            }
        }

        val json = xml.map{xmlBlob -> XML.toJSONObject(xmlBlob)}
                      .map{jsonDump -> jsonDump.toString(4)}
                      .block()

        println("\nXML converted to JSON.")
        cliHelper.printToConsoleOrWriteFile(CLIHelper.DataType.JSON, json)

        val newXml = XML.toString(JSONObject(json))
        println("JSON converted back to XML.")
        cliHelper.printToConsoleOrWriteFile(CLIHelper.DataType.XML, newXml)
    }
}