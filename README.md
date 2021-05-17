# webBudget back-end

Welcome to the webBudget project repository! Here you will find the back-end application for the webBudget 
project.

## Project setup

Unlike the older versions of webBudget, now we use Kotlin and let Java just for the runtime in the JVM. If you are not 
familiar with the language, don't worry! 

If you are a good Java developer, or an expert in script languages like TypeScript it will be quite easy to understand 
and work with Kotlin. 

> Before we start, make sure you have [Docker](https://docs.docker.com/get-docker/) in you system, the project makes use 
> of the runtime to local development and to run tests using test containers

After cloning the project, go to the folder named _docker_ and run this command: 

1. `docker-compose -p web-budget up`

This command should create an instance of the PostgreSQL database and Redis (used here to for cache) inside the docker 
runtime. Everything will be exposed in the default ports: _5432_ (postgres) and _6379_ (redis).

Now, if everything goes according to the plan, you can run the tests and see if the environment works:

2. 