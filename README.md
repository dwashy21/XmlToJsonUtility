# XmlToJsonUtility
Simple CLI app written in Kotlin (1.5.31) on Java 11. Queries a URI ([default](http://www.bindows.net/documentation/download/ab.xml)) 
as an XML source. Attempts to  validate that XML against the local schema, and converts that schema to JSON and back.
Optionally will accept a different URI as an XML source.

### XML and JSON Conversion
XML to JSON and JSON to XML conversion support is provided by the *[org.json JSON-java](https://github.com/stleary/JSON-java)* library. 
During execution, user will have the option to write output to a file(s), or directly to the console.

### Dependency Management
Maven is used for dependency management in this project.

### Testing
Testing is performed using JUnit5 and the *[junit5-system-exit](https://github.com/tginsberg/junit5-system-exit)* library to validate calls to System.exit()

### Execution
To execute this application an IDE may be used, or the command-line. One method to launch the app if Maven is installed 
is with the following commands:

Before execution of the below command, ensure you are at the root of the project where *pom.xml* is accessible.
```
mvn clean install
mvn compile exec:java
```
