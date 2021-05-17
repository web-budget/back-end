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

After cloning the project and if your docker instance is working well, run this command:

1. `mvnw clean verify` 

This will clean (older builds), run linter (ktlint), compile the project and run some automated tests, if everything 
goes well you will see a message of _build success_ at the end of the process.

To just lint and check for formatting problems, run: `mvnw antrun:run@ktlint` and to auto-fix things, run 
`mvnw antrun:run@ktlint-format`

If you plan to develop in the project, after cloning the project, go to the folder named _docker_ and run this command:

2. `docker-compose -p web-budget up`

This command should create an instance of the PostgreSQL database and Redis (used here to for cache) inside the docker
runtime. Everything will be exposed in the default ports: _5432_ (postgres) and _6379_ (redis).

After that you just need to import the project in your favorite IDE and start coding.

> Since this is a Kotlin project is highly recommended to use IntelliJ IDEA for development, if you don't have
> it, please it [download here](https://www.jetbrains.com/?from=webBudget).

## Tech stack

- Kotlin 1.4
- Spring Boot 2.4
- PostgreSQL 13
- Redis 6

## FAQ

Questions? We try to answer some of them here:

1. Why separate back-end from front-end?
> The answer is simple: flexibility and to let people who don't know about the front-end stuff work where they are good 
> and productive without having to deal with things they didn't know/want

2. Why changing from Java to Kotlin
> Future! I believe in Kotlin being the future of Java or something very close to this. A lot of problems and pains that 
> we used to have with Java are solved in Kotlin, and the most important: we don't need lombok anymore