
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
- PostgreSQL
- Redis
- RabbitMq
- Prometheus
- Grafana

A Docker compose file for development purposes is available in the root directory, named `docker-compose-dev.yml`  
It creates all the required services and their web dashboards:  

| Service    |   username   |     password     | endpoint       | web dashboard                           |    
|------------|:------------:|:----------------:|:---------------|:----------------------------------------|    
| PostgreSQL | IndexDevUser | IndexDevPassword | localhost:5432 | [localhost:8081](http://localhost:8081) |    
| Redis      |              |                  | localhost:6379 | [localhost:8082](http://localhost:8082) |    
| RabbitMq   |    guest     |      guest       | localhost:5672 | [localhost:8083](http://localhost:8083) |
| Prometheus |              |                  | localhost:9090 |                                         |
| Grafana    |    admin     |      admin       |                | [localhost:3000](http://localhost:3000) |

There is a pre-made `.env` file for development located in `/env/.env.development`, just copy it to the root directory and adjust it as needed

> To connect to the Postgres database via the web dashboard
> put as the `Server` the name that you gave to the postgres container,
> if using the `docker-compose-dev.yml` file it will be `index-postgres`.
> 
> The database by default is `indexdevdb`

#### Developer database user
A developer account for Index can be created by running the `CreateDevUser.kt` in `/scripts/`:  
email: `giuliopime@gmail.com`  
password: `Password1!`

### Stripe
To receive Stripe webhooks on the api running on your device use:
```shell
stripe listen --forward-to localhost:8080/webhook/stripe
```

### Grafana
##### Security
When connecting to the [Grafana dashboard](http://localhost:3000) you will be prompted to change the password!  
For development purposes it's fine to keep `admin` as the password, please set a strong password in public facing environments instead  

##### Adding Prometheus datasource
From the Grafana homepage, click `Add your first datasource` and select Prometheus or navigate to `Connections > Add new connection > Prometheus` and click `Add new datasource`.  

Choose a `name` (can be whatever you like), provide the url which with the dev docker compose setup would be `http://localhost:9090`, scroll to the bottom and hit `Save & Test`  

##### Creating a dashboard for api monitoring
There are various pre-made dashboards you can use.  
My go to is [this one](https://grafana.com/grafana/dashboards/4701-jvm-micrometer/)

##### Swagger UI
Swagger is available at [localhost:$PORT/swagger](http://localhost:8080/swagger)

### Git
Use `[ci skip]` in a commit message to skip circleci process.  
Also please create a new branch for new features or substantial bug fixes

## App review
email: `index-review@gmail.com`  
password: `tr5zFHAEspHSy53A`