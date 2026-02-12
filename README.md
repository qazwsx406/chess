# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

## Link to Sequence Diagram

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfJCIK-P8gK0eh8KVEB-rgeWKkwes+h-DsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3r5d5LsKMBihKboynKZbvEqmAqsGGpujAaAQMwABmvgStAMDqTsMDiiA0AouAIW8mFNl2T2fYwPlMVbhl6IwFASAJMwEBZR2tkujANRIFl8qFQCMDxPo8Tbt5Nn+sy0yXtASAAF4oBwUYxnGhSgZhyCpjA6YAIwETmqh5vM0FFiW9Q+AtepLatux0U2w4OkmvrOl2G7us1mWGO1nUNT1Xn1INw0FaZGnjZN02Cq99IwIecgoM+8Tnpe17wwK4X1I+AYY1uXntnppYueKGSqABmAkyB1T6YRhkXcZYwUVR9aQlcO3vfAe3YTAuH4aMDMJUZZEs5ebPIRzjYMZ43h+P4XgoOgMRxIkSsqy5vhYKJgqgfUDTSBG-ERu0EbdD0cmqApwzo-BiEq+K43BhwlCeXTOkPLC-p25RDtoKSNM1f1Dn2NrzlCdrblqB5sM+VV-L+WAqO+5Lc6hW9lTLpF4pPgT8iyvKrP+0lqoagAkmgVAmkg67F0hlULtjwdfXV-ZE9z+uSnd8QPWtG0oLGCnabtJRgGmTjHULp3nQWYxXdAN0933T0y43+6052uP58AcdUreidGCg3DHpeqf++nCeLlnEXSMfTKGKjr6fcTVn+lrp4U1TQezWBnMe6PfaAsszPVlkxBWKJ1z+GwOKDU-E0QwAAOJKg0LrX+BtEGmwtvYJUtt66OzQM7EMbsR4fjfqWc+SFA7kL1p9eoyAcjIJzM5NETC1DRxJHvGQB8EaMmTmffBAd15hRvvUKKecXzaELsaCWJdkrqhgJXauyA66yIbljTefVW69nbi-TuHtXTL3ao9AeQ94xc2TLzceB1J4nX5LPS6xZF4KiMStNaDZ6LCLei3bekj5BcKxvQ1hKCMSXybveCK4jKwICQUqD0Gj9FbwEmiAAPGwnk5Q45kO9qTYJOYv4IEAjQxJNQXhjBwTmOeDQXA1M6Esf+El4SWLHjhPCBEKlqCqTUlwdSYCgMYvLAIHAADsbgnAoCcDECMwQ4BcQAGzwAnIYNhRQrG0MaQbVoHRsG4LQJQghRDXZQGJO0pUAA5SCiwZiWDYQ2LmulyH1H2QHNY4wOkXKMtc25lkcnrK0fUJG6I2EYjgEsthHDY4d33hnXhTIU6CLCRvHGOcJRPykXFQRpcUqKKrjXVR9t1E8Obh9JJbcskktKd3CiK9TFbVIc0-aR07G5nzI466LjqXGPcf0hJPj8Z+N3lC7hMLRyIzBUqDE7ylSkhav9DqiQga9VqigTY8B4j8mBYikRQpcZrliXMeJRLNF2XSVIjEOo9SKOkO7Tsr8cn1FBUeFA4L-yFOpsU9BMA3lKnLtIAsh1wjBECA0xMDK+bAKFh031-rA3BrXnLZi-hLDHwcqq2I8qwDJr7BAVVAApCATs2ExGSKANUqyx5-Mkk0ZkMkegdLwWog56riHHIItgBAwBk1QDgBAByUA9gAHUWDlzNt8KNfragBqDSGjC2SvxPIRa8n4Hau09r7YO4do6QTjoWFOuNQcKV2QAFYFrQMC-N4oXWEhjjarRcMiVJ3hY2oRCTREookVedFRdn1YoUUovFMiCXoC8YuPlZKO6evmpytx61oyDzpRYyookJ5T2zPY1lhYnGllutBx6HiXpGpKXZNF-ihWBJgHw4F46tWZx1e+-VKBYrynHb+jURaWowByrdBivLD39XA3oz1AAhEMELaXD0QzzFpNjBZoZZRdTD7K9DrhRNenI+GeOEb5aa-x8iNRrvyiaM0NZwyFFlW1DNiqQZqpQCADUQ1Ea9vytkY01dTi6G4OS3q-pAzGbDEhcT5iAHc2QzYzM090MKfnlh+oRmXYmeov08zAMFXdSVf1PG7bO0YUgBRxKZGH0KmwFoIFEqOmY0I2+5kxXkYMefra7mJN6gXrPUqApRTfklP0jOppSGrGtNk-0hNCsvCdtVhm0b8pEDBlgMAbA7bCB5HjGgilkkjYmzNhbYwpCHn2q9T8r8fy7IgG4HgUJATCsnZm+d19dG74n0MCaGJJXd68fS19abeB2teaazzGb32f6rb-vSvr0mI0jH6UAA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
