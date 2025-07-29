# TestContainers Implementation Documentation

## Overview

This project uses [TestContainers](https://www.testcontainers.org/) to provide real database and Redis instances for integration testing, replacing the previous embedded database (H2) and embedded Redis setup.

## What was Changed

### Before
- **Database**: H2 in-memory database
- **Redis**: Embedded Redis server (via `it.ozimov:embedded-redis`)
- **Configuration**: Manual setup of embedded components

### After
- **Database**: PostgreSQL 15 container (same as production)
- **Redis**: Redis 7 container (same as production)
- **Configuration**: TestContainers with automatic lifecycle management

## TestContainers Setup

### Dependencies Added

```gradle
// TestContainers BOM for consistent versioning
intTestImplementation platform('org.testcontainers:testcontainers-bom:1.19.5')

// TestContainers dependencies
intTestImplementation 'org.testcontainers:junit-jupiter'
intTestImplementation 'org.testcontainers:postgresql'
intTestImplementation 'org.testcontainers:testcontainers'
intTestImplementation 'org.springframework.boot:spring-boot-testcontainers'
intTestImplementation 'org.postgresql:postgresql'
```

### Configuration Classes

#### TestContainersConfig
- **Location**: `src/intTest/java/com/nowhere/springauthserver/config/TestContainersConfig.java`
- **Purpose**: Defines PostgreSQL and Redis containers as Spring beans
- **Features**:
  - Container reuse for faster test execution
  - Shared containers across test classes
  - Automatic container startup in static initializer

#### BaseIntegrationTest
- **Location**: `src/intTest/java/com/nowhere/springauthserver/config/BaseIntegrationTest.java`
- **Purpose**: Base class for all integration tests
- **Features**:
  - Configures TestContainers annotations
  - Sets up dynamic properties for database and Redis connections
  - Manages container lifecycle

### Application Configuration

The integration test `application.yaml` was updated to:
- Enable Flyway migrations (previously disabled)
- Use PostgreSQL dialect instead of H2
- Remove hardcoded database configuration (now provided by TestContainers)
- Configure Redis session store

## Container Details

### PostgreSQL Container
- **Image**: `postgres:15-alpine`
- **Database**: `oauth_nowhere`
- **Username**: `postgres`
- **Password**: `nowhere`
- **Port**: Dynamically assigned
- **Features**: Container reuse enabled for performance

### Redis Container
- **Image**: `redis:7-alpine`
- **Port**: Dynamically assigned (6379 inside container)
- **Features**: Container reuse enabled for performance

## Benefits

1. **Production Parity**: Tests now run against the same database and Redis versions as production
2. **Real Database Features**: Full PostgreSQL features including constraints, triggers, and advanced SQL
3. **Isolation**: Each test run gets fresh containers, ensuring clean state
4. **Performance**: Container reuse reduces startup time
5. **Reliability**: More realistic testing environment reduces production surprises

## Test Execution

### Prerequisites
- Docker must be available and running
- Sufficient Docker permissions for TestContainers

### Running Tests
```bash
# Run integration tests
./gradlew intTest

# Run with info logging to see container startup
./gradlew intTest --info
```

### Test Lifecycle
1. Static initializer starts containers when first test class loads
2. Containers are shared across all test classes in the test run
3. Database is cleaned between test methods (in BaseOauthFlowTest setUp method)
4. Containers are automatically stopped after all tests complete

## Container Management

### Reuse
Containers are configured with `.withReuse(true)` which means:
- If Docker containers from previous runs are still available, they will be reused
- This significantly improves test startup time
- Containers are only recreated if the configuration changes

### Dynamic Properties
Connection details are configured at runtime using `@DynamicPropertySource`:
```java
@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", TestContainersConfig.postgres::getJdbcUrl);
    registry.add("spring.datasource.username", TestContainersConfig.postgres::getUsername);
    registry.add("spring.datasource.password", TestContainersConfig.postgres::getPassword);
    registry.add("spring.data.redis.host", TestContainersConfig.redis::getHost);
    registry.add("spring.data.redis.port", TestContainersConfig.redis::getFirstMappedPort);
}
```

## Troubleshooting

### Common Issues

1. **Docker not available**: Ensure Docker is running and accessible
2. **Container startup timeouts**: Increase timeout or check Docker resources
3. **Port conflicts**: TestContainers handles port allocation automatically
4. **Permission issues**: Ensure proper Docker permissions

### Debug Information

To see container startup details:
```bash
./gradlew intTest --info --debug
```

To see TestContainers logs:
```bash
export TESTCONTAINERS_DEBUG=true
./gradlew intTest
```

## Migration Notes

### Removed Dependencies
- `com.h2database:h2` - No longer needed
- `it.ozimov:embedded-redis:0.7.3` - Replaced by TestContainers Redis

### Removed Classes
- `RedisConfig.java` - Embedded Redis configuration no longer needed

### Modified Classes
- `BaseIntegrationTest.java` - Updated to use TestContainers
- `BaseOauthFlowTest.java` - Removed embedded Redis setup, updated database access
- `application.yaml` (intTest) - Updated for PostgreSQL and TestContainers

## Future Improvements

1. **Parallel Test Execution**: Can be enabled with proper container isolation
2. **Custom Images**: Could use custom Docker images with pre-loaded test data
3. **Additional Services**: Easy to add other services like Elasticsearch, Kafka, etc.
4. **CI/CD Optimization**: Container images can be pre-pulled in CI environments