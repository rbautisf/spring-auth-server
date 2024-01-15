# Getting Started

This is a sample project to demonstrate how to use Spring Boot with Spring Oauth2 Authorization Server.

## Prerequisites

* Java 17
* Docker
* Docker Compose
* Gradle
* OpenSSL

## Build

```shell
./gradlew build
```

## Run

Execute the docker-compose file to start the database.

```shell
scripts/docker-compose up
```

Start the application with the following command:
```shell
./gradlew bootRun
```


## Environment Variables
Required env variables for the application to run.

```shell
DATASOURCE_URL=jdbc:postgresql://localhost:5433/oauth_nowhere
DB_USERNAME=postgres
DB_PASSWORD=nowhere
```

## Private Key Files

Create your own private key files and place them in the root directory of the project. This is for testing purposes, in production you should use a secure key store like ansible vault.

Create private key
```shell
openssl genrsa -des3 -out private.pem 2048
```
Create public key
```shell
openssl rsa -in private.pem -outform PEM -pubout -out public.pem
```
Create pkcs8 format
```shell
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.pem -out private-pkcs8.pem
```

Place the private--pkcs8.pem and the public.pem in the resources folder.

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.1/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.1/gradle-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.1/reference/htmlsingle/index.html#web)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.2.1/reference/htmlsingle/index.html#web.security)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.2.1/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

