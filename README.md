# Getting Started

This is a sample project demonstrating the use of Spring Boot with Spring Oauth2 Authorization Server.

## Prerequisites
Make sure to have these tools installed
* Java 21
* Docker
* Docker Compose
* Gradle
* OpenSSL

## Build & Run
Before running the application, make sure to build it. Execute the command below:
```shell
./gradlew build
```

## Testing

This project uses TestContainers for integration testing, providing real PostgreSQL and Redis instances during tests.

### Running Tests
```shell
# Run unit tests only
./gradlew test

# Run integration tests only
./gradlew intTest

# Run all tests
./gradlew test intTest
```

**Note**: Integration tests require Docker to be running as they use TestContainers to spin up PostgreSQL and Redis containers.

For more details about the TestContainers implementation, see [TESTCONTAINERS.md](TESTCONTAINERS.md).

Execute the docker-compose file to start the database.
```shell
scripts/docker-compose up
```
Please note that the init.sql file is used to create the database schema and the initial data, if you are using your own database, please make sure to have the schema and data created.  

Optional: Build the docker image and use the image from the docker-compose file to start the application.
```shell
docker build -t auth-nowhere .
```

Finally, start the application with the command:
```shell
./gradlew bootRun
```
Once the application starts, Swagger documents can be accessed via [Swagger UI](http://localhost:9000/swagger-ui/index.html), and PGAdmin via [PGAdmin Login Portal](http://localhost:5050/login).


## Environment Setup
The run time environment can be setup using the following environment variables:
```shell
DATASOURCE_URL=
DB_USERNAME=
DB_PASSWORD=
PRIVATE_KEY=
PUBLIC_KEY=
GITHUB_CLIENT_ID=
GITHUB_CLIENT_SECRET=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
REDIS_HOST=
REDIS_PORT=
```
## Key Generation
#### Creating Private Key
The following command generates a DES3 encrypted RSA private key:
```shell
openssl genrsa -des3 -out private.pem 2048
```
#### Creating Public Key
The next step is to create a public key from the private key we just generated. Use the command below:
```shell
openssl rsa -in private.pem -outform PEM -pubout -out public.pem
```
#### Creating PKCS8 Format
Finally, you need to convert the private key to pkcs8 format. This is the format the application can read. Use the following command:
```shell
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.pem -out private-pkcs8.pem
```

After executing the commands, two files should be generated: `private-pkcs8.pem` and `public.pem`. Please place them in the resources folder.

## Additional Notes
#### Nginx Configuration
The Nginx configuration file is located in the `scripts` folder. The configuration file is used to handle reverse proxy calls to the auth server.

#### Postman collection
The postman collection included in the root folder.

The collection contains a folder to execute the Authorization code flow ony through requests. 

Consider to update the host and port if using docker-compose.

#### User Login For Testing
- **Username:** `user@user.com`
- **Password:** `user`

#### Registered Client for Testing
- **client_id:** `nowhere-client`
- **client_secret:** `nowhere-client`

#### Callback URLs
- https://oidcdebugger.com/debug
- https://oauthdebugger.com/debug

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

