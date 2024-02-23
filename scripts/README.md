# Nowhere Authorization Server

## Docker Compose Configuration

This `docker-compose.yaml` file defines the services that make up your application, and their configurations, to facilitate shared environments and simplify the setting up process.

### Services

There are several services defined in this file:

#### Postgres

- This service uses the `postgres:latest` image.
- The environment variables are obtained from the system's environment variables.
- It is assigned the port `5433` on the host and `5432` on the Docker container.
- It is configured to restart always.
- It has a healthcheck set up, which checks using the `pg_isready` command every 10s, up to 5 times before it fails.
- It uses two volumes, one for initialization scripts and the other for data persistence.

#### Auth-server

- This service uses a locally built image `authorization-server:latest`.
- Environment variables are a mix of local and services related variables (like `postgres` in `DATASOURCE_URL`).
- It is assigned the port `9000` on the host and also inside the Docker container.
- It is configured to restart always.
- It depends on the `postgres` service, meaning it will not start until the `postgres` service is up.

#### Redis Cache

- This service uses the `redis:latest` image.
- It is assigned the port `6379` on the host and inside the Docker container.
- It is configured to restart always.

#### Nginx

- This service uses the `nginx:latest` image.
- It is assigned the port `80` on the host and inside the Docker container.
- It uses the `nginx.conf` file from your project as its configuration.
- It depends on the `auth-server` service.

#### Pgadmin (commented out)

- This service uses the `dpage/pgadmin4:latest` image.
- It would be assigned the port `5050` on the host and the Docker container.
- It would be configured to restart always.
- It would use a volume for data persistence.

### Volumes

- `postgres-data` is used by the `postgres` service to store its data. Thus allowing the data to persist across container restarts.
- `pgadmin-data` is used by the `pgadmin` service for the same reason.

Remember, when the file changes, you have to stop (`docker-compose down`) and then start your services (`docker-compose up`) to make the changes take effect.

## Nginx Configuration

Here is the basic explanation using the `nginx.conf` file in this project.

### Worker Processes

At the top of the file, you see `worker_processes 5;`. This configures the number of worker system processes.

### Events

The `events` block contains the line `worker_connections 1024;`. This line indicates that each worker process can handle 1024 clients concurrently.

### HTTP

The `http` block contains configuration settings common for all servers.

### Server

The `server` block includes the following configurations:

- `listen 80;` - This sets the port to 80, on which to listen for incoming connections.
- `server_name localhost;` -  This configures the server block to apply if the host header of the request matches 'localhost'.

### Locations

These `location` blocks dictate how to route requests based on their URL:

- `location /auth-server/` block sets the proxy settings for all incoming requests with '/auth-server/' in the URI.
- `location /post-service/` and `location /client-server/` blocks configure their own proxies for specific paths.

### Proxy Pass

This directive indicates that the server should proxy the client's request to `host.docker.internal:<port_number>`, where `<port_number>` is the port of the specific service.

### Proxy Set Header

`proxy_set_header` directives forward certain client request headers to the proxied server, preserving information from the original request.

Remember, after modifying this file, you will need to reload (or restart) the Nginx service for the changes to take effect. Use `sudo systemctl reload nginx` for instance. Always test the configuration before applying it with `nginx -t`.

## Environment Variables

Following are the environment variables used in this configuration:

- `POSTGRES_USER` - This is the PostgreSQL user name, Configured here as `postgres`.
- `POSTGRES_PW` - This is the password for the `POSTGRES_USER`. Here it is configured as `nowhere`.
- `POSTGRES_DB` - This is the database that the PostgreSQL service should begin to use upon startup, it is set here as `oauth_nowhere`.
- `PGADMIN_EMAIL` - This is the default email for PgAdmin, a feature-rich Open Source administration and management platform for PostgreSQL. Here it is set as `postgres@gmail.com`.
- `PGADMIN_PW` - This is the password for the `PGADMIN_EMAIL`, set here as `nowhere`.
- `PRIVATE_KEY` - This is the private key used for secure transactions, and should be kept secret. It is used for signing the tokens.
- `PUBLIC_KEY` - This is the corresponding public key to the above `PRIVATE_KEY`. It is available publicly and used for verifying the tokens.
- `GITHUB_CLIENT_ID` - This is the client ID provided by GitHub for OAuth Applications.
- `GITHUB_CLIENT_SECRET` - This is the client secret provided by GitHub for OAuth Applications. It is used to confirm identity when receiving the access token.
- `GOOGLE_CLIENT_ID` - This is the client ID provided by Google for OAuth 2.0 Applications.
- `GOOGLE_CLIENT_SECRET` - This is the client secret provided by Google for OAuth 2.0 Applications. This is used when exchanging the auth code for access tokens.
- `REDIS_HOST` - This is the host name of the Redis Cache service.
- `REDIS_PORT` - This is the port number of the Redis Cache service.
- `REDIS_PW` - This is the password for the Redis Cache service.
These values are stored in environment variables to ensure sensitive information is not available in your application code and to allow for easy updating of these values.