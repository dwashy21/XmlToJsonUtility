package com.dwashy.utility.cli

import reactor.core.publisher.Mono
import java.io.*
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator
import kotlin.system.exitProcess

/**
 * A utility class used to retrieve XML from a URI and perform various transformations
 * on it.
 *
 * @author dwashy21
 */
class CLIHelper {
    val defaultXmlSource = "http://www.bindows.net/documentation/download/ab.xml"
    private val xmlSchemaPath = "src/main/resources/schema/AddressBook.xsd"
    private var xmlSource = defaultXmlSource

    /**
     * Prompts the user to provide a new XML source, or accept the default
     * @return [String] response from the user, potentially null
     */
    fun promptUser(): String? {
        println("\nDefault XML source: $xmlSource")
        print("Provide new XML source if desired, press ENTER to accept default, or type QUIT to exit: ")
        return readLine()
    }

    /**
     * Checks the provided XML source to use as input for data transformation
     * @param newXmlSource [String?] the provided XML source, potentially null
     * @return the [String] representing the URI target where XML source will be retrieved
     */
    fun determineXmlSource(newXmlSource: String?): String {
        if (newXmlSource != null && newXmlSource.isNotBlank()) {
            if(newXmlSource.uppercase() == "QUIT"){
                exitProcess(0)
            }
            xmlSource = newXmlSource
            println("Using new XML source: $newXmlSource")
        } else {
            println("Using default XML source.\n")
            xmlSource = defaultXmlSource
        }
        return xmlSource
    }

    /**
     * Determines if the supplied XML adheres to the selected XSD schema at [xmlSchemaPath]
     * @param xml [Mono] of [String] The unvalidated [String] of XML wrapped in a [Mono]
     * @return a [Mono] wrapping the [Boolean] indicating validity
     */
    fun isValidXml(xml: Mono<String>): Mono<Boolean> {
        val validator = buildSchemaValidator(xmlSchemaPath)
        return xml.map{xmlBlob -> ByteArrayInputStream(xmlBlob.toByteArray())}
                  .map{encodedXmlBlob -> validator.validate(StreamSource(encodedXmlBlob))}
                  .map{_ -> true}
                  .doOnError{err -> println("XML failed validation against schema: " + err.localizedMessage)}
                  .onErrorResume{Mono.just(false)}
    }

    /**
     * Builds the schema [Validator] using the provided [schemaPath] to the XSD schema
     * @param schemaPath [String] the path to the schema XSD used to build the [Validator]
     * @return [Validator] that has been built with the XSD schema
     */
    private fun buildSchemaValidator(schemaPath: String): Validator {
        val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schema = schemaFactory.newSchema(File(schemaPath))
        return schema.newValidator()
    }

    /**
     * Simple enum class to represent XML or JSON data types
     */
    enum class DataType {
        XML,
        JSON
    }

    /**
     * Allows user to choose between printing JSON or XML result to console, or write to file
     */
    fun printToConsoleOrWriteFile(jsonOrXml: DataType, result: String) {
        println("Shall $jsonOrXml be printed to console, or written to file?")
        print("Press ENTER to write to file, or enter anything else to write to console:")
        val consoleOrFile = readLine()

        var newResult: String? = null
        if(jsonOrXml == DataType.XML) {
            val xslt = StreamSource(File("src/main/resources/xslt/format.xslt"))
            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ByteArrayInputStream(result.toByteArray()))
            val transformer = TransformerFactory.newInstance().newTransformer(xslt)
            val outputStream = ByteArrayOutputStream()
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no")
            transformer.transform(DOMSource(doc), StreamResult(outputStream))
            newResult = String(outputStream.toByteArray(), Charsets.UTF_8)
        }

        if(consoleOrFile != null && consoleOrFile.isNotBlank()) {
            println(if(jsonOrXml == DataType.XML) newResult else result)
        } else {
            val writer = BufferedWriter(FileWriter("output.${jsonOrXml.toString().lowercase()}"))
            writer.append(if(jsonOrXml == DataType.XML) newResult else result)
                    .close()
            println("Successfully written to file output.${jsonOrXml.toString().lowercase()}")
        }
        println()
    }
}