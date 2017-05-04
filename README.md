# Java Lambda

A simple framework for developing java based AWS Lambda functions. Built on [Guice] for easy 
dependency injection and module composition, this framework makes it wildly easy to build 
_usable_ Lambda functions.

## Developing a Function

Functions are composed on two main components:

 * Wrapper (extending LambdaBaseWrapper)
 * Handler

The wrapper is a light layer around the the handler function. It's necessary so we can provide a 
concrete input and output event classes, as well specifying the [Guice] modules and loader. The 
parent LambdaBaseWrapper handles instantiating the handler and keeping a handle to the instance as
long as the JVM is active (cutting down subsequent invocation time).
  
The handler provides the 'meat'of the function implementation. It takes an input message and 
returns an output message.

### Loading Properties

Properties can be loaded with the [PropertiesModule], easily mapping configuration values to 
[@Named] injectable values. By leveraging a standard Java Properties file you can easily 
configure your functions at build time without forcing static values into your code. 
 
#### Other Helpful Modules

| Module | Description |
| -------|-------------|
| DefaultCredentialsModule | Load AWS credentials from the default credentials chain |
| SingleInstanceModule | Bind a class to a single instance |
| InstanceToNamed | map a [@Named] tag to a class instance. Helps automatically injecting types |
| NullableNamedInstanceModule| like InstanceToNamed, but also supporting a `null` instance| 
| UtcClockModule | inject a Java 8 Clock, useful for testing and enforcing timestamps |


## Error Handling

Starting out developing Lambda functions, people often just return the error to the user. 
However, as function implementations become more complex and when leveraging the Lambda function 
with AWS API Gateway, you will want to start returning standard errors.

The [ExternalFacingRequestHandler] supports a variety of error handling utilities, including the 
ability to throw coherent, REST-style `400` errors and automatically translating unexpected 
exceptions into `500` errors.

## Logging

By default, AWS Lambda functions use log4j and eventually end up generating simple system.out log
statements. However, many existing code bases use [SLF4J]. We include an [Slf4jLogFactory] to 
seamlessly support the existing code and minimize the number of additional dependencies you need 
to include.

## Lambda-Dynamo

This module makes it easier to develop lambda functions that interact with DynamoDB. 
Specifically, it provides [Guice] modules to configure and load DynamoDB components. It also 
provides Junit rules for spinning up a local DynamoDB instance.

When using the DynamoDB test rule, you will need to run
```
$ mvn install
```
first to copy the necessary DynamoDB-local jars to the runtime dependencies folder.


## Lambda-archetype

Helpful module for generating a maven archetype to use when generating a new maven-based Lambda 
function. Install the archetype with:

```
$ mvn install
```

Then you can use it to generate a maven project suitable to creating a deployable lambda function:

```
$ mvn archetype:generate -DarchetypeGroupId=io.fineo.lambda -DarchetypeArtifactId=lambda-archetype -DarchetypeVersion=1.0-SNAPSHOT -DgroupId=io.fineo.YOUR_PROJECT -DartifactId=YOUR_ARTIFACT -Dversion=1.0-SNAPSHOT
```

[Guice]: https://github.com/google/guice
[SLF4J]: https://www.slf4j.org
[Slf4jLogFactory]: /blob/master/lambda-core/src/main/java/io/fineo/lambda/handle/Slf4jLogFactory.java
[ExternalFacingRequestHandler]: /blob/master/lambda-core/src/main/java/io/fineo/lambda/handle/external/ExternalFacingRequestHandler.java
[PropertiesModule]: /blob/master/lambda-core/src/main/java/io/fineo/lambda/configure/PropertiesModule.java
[@Named]: https://github.com/google/guice/wiki/BindingAnnotations
