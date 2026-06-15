
<h1 align="center">
  <br>
  <a href="https://index-it.app"><img src="https://index-it.app/logo.png" alt="Index" width="200"></a>
  <br>
api
  <br>
</h1>

<h4 align="center">REST api for <a href="https://index-it.app" target="_blank">Index</a>.</h4>

<p align="center">
  <a href="https://github.com/index-it/api/actions/workflows/ghcr-api.yml"><img alt="CI/CD" src="https://github.com/index-it/api/actions/workflows/ghcr-api.yml/badge.svg"></a>
  &nbsp;
  <a href="https://index-it.app/discord"><img alt="Discord" src="https://img.shields.io/discord/1043536767934021713?color=4&logo=Discord"></a>
</p>

## Development
This api requires the following services:
- PostgresSQL
- Redis
- RabbitMq

A Docker compose file for development purposes is available in the root directory, named `docker-compose-dev.yml`  
It creates all the required services and their web dashboards:  

| Service    |   username   |     password     | endpoint       | web dashboard                           |    
|------------|:------------:|:----------------:|:---------------|:----------------------------------------|    
| PostgreSQL | IndexDevUser | IndexDevPassword | localhost:5432 | [localhost:8081](http://localhost:8081) |    
| Redis      |              |                  | localhost:6379 | [localhost:8082](http://localhost:8082) |    
| RabbitMq   |    guest     |      guest       | localhost:5672 | [localhost:8083](http://localhost:8083) |

There is a pre-made `.env` file for development located in `config/env/.env.local`, just copy it to the root directory and adjust it as needed

> To connect to the Postgres database via the web dashboard
> put as the `Server` the name that you gave to the postgres container,
> if using the `docker-compose-dev.yml` file it will be `index-postgres`.
> 
> The database by default is `indexdevdb`

#### Developer database user
A developer account for Index can be created by running the `CreateDevUser.kt` in `/scripts/`:  

### OpenAPI & Swagger
OpenAPI documentation is available at:
- public: [localhost:8080/docs/scalar](http://localhost:8080/docs/scalar)  
- internal: [localhost:8080/docs/internal/scalar](http://localhost:8080/docs/internal/scalar)

### Git
Use `[ci skip]` in a commit message to skip circleci process.  
Also please create a new branch for new features or substantial bug fixes
