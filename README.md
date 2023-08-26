
<h1 align="center">
  <br>
  <a href="https://index-it.app"><img src="https://index-it.app/logo.png" alt="Index" width="200"></a>
  <br>
api
  <br>
</h1>

<h4 align="center">REST api for <a href="https://index-it.app" target="_blank">Index</a>.</h4>

<p align="center">
  <a href="https://dl.circleci.com/status-badge/redirect/gh/index-it/api/tree/main"><img src="https://dl.circleci.com/status-badge/img/gh/index-it/api/tree/main.svg?style=svg&circle-token=b98428ac97372542adab9fe5a0a337cb94aa00e5"></a>
  &nbsp;
  <a href="https://index-it.app/discord"><img alt="Discord" src="https://img.shields.io/discord/1043536767934021713?color=4&logo=Discord"></a>
</p>

## Development
This api requires the following services:
- MongoDB
- Redis
- RabbitMq  

A Docker compose file for development purposes is available in the root directory, named `docker-compose-dev.yml`  
It creates all the required services and their web dashboards:  

| Service | username | password | endpoint | web dashboard                           |    
|---------|:----------:|:----------:|:----------|:----------------------------------------|    
| MongoDB |   |   | localhost:27017 | [localhost:8081](http://localhost:8081) |    
| Redis |   |   | localhost:6379 | [localhost:8082](http://localhost:8082) |    
| RabbitMq | guest | guest | localhost:5672 | [localhost:8083](http://localhost:8083) |

There is a pre-made `.env` file for development located in `/env/.env.development`, just copy it to the root directory and adjust it as needed

> Important: editing documents with the MongoDB dashboard **is not recommended** as it tends to mess up data types!

##### Swagger UI
Swagger is available at [localhost:$PORT/swagger-ui](http://localhost:8080/swagger-ui)