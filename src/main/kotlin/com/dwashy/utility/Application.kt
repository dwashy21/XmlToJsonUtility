package com.dwashy.utility

import com.dwashy.utility.cli.CLIHelper
import org.json.JSONObject
import org.json.XML
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI


/**
 * The main entry point for execution during runtime
 *
 * @author dwashy21
 */
class Application {

    companion object {
        /**
         * The main entry point of this application. Will be executed at runtime
         * @param args [Array] of [String] arguments provided during execution
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val webClient = WebClient.builder().build()
            val cliHelper = CLIHelper()
            lateinit var xml: Mono<String>
            var validXml = false

            while(!validXml) {
                val userInput = cliHelper.promptUserForXmlSource()
                val xmlSource = cliHelper.determineXmlSource(userInput)
                xml = webClient.get()
                        .uri(URI(xmlSource))
                        .retrieve()
                        .bodyToMono(String::class.java)

                validXml = cliHelper.isValidXml(xml).block()!!
                if(!validXml) {
                    println("URI source has provided bad XML that does not adhere to the schema.")
                }
            }

            val json = xml.map{xmlBlob -> XML.toJSONObject(xmlBlob)}
                    .map{jsonDump -> jsonDump.toString(4)}
                    .block()!!

            println("\nXML converted to JSON.")
            var userChoice = cliHelper.promptUserWriteToConsoleOrFile(CLIHelper.DataType.JSON)
            cliHelper.writeToConsoleFile(userChoice, CLIHelper.DataType.JSON, json)

            val newXml = XML.toString(JSONObject(json))
            println("JSON converted back to XML.")
            userChoice = cliHelper.promptUserWriteToConsoleOrFile(CLIHelper.DataType.XML)
            cliHelper.writeToConsoleFile(userChoice, CLIHelper.DataType.XML, newXml)
        }
    }
}