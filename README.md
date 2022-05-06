[![build](https://github.com/web-budget/back-end/actions/workflows/gradle.yml/badge.svg)](https://github.com/web-budget/back-end/actions/workflows/gradle.yml)
# webBudget back-end

Welcome to the webBudget repository! Here you will find the back-end application responsible for all the API's used by 
the current web interface and a future mobile app.

## Project setup

Unlike the older versions of webBudget, now we use Kotlin and let Java just for the runtime with the JVM. If you are not 
familiar with the language, don't worry! If you are a good Java developer, or an expert in script languages like 
TypeScript, should be quite easy to understand and work with Kotlin. 

> Before we start, make sure you have [Docker](https://docs.docker.com/get-docker/) in you system, the project makes use 
> of it, our dev config is in a docker-compose file and our tests run inside a [TestContainers](https://www.testcontainers.org/) environment 

After having cloned the project repository and assuming that you are already running your docker environment, run:

1. `gradlew clean build` 

This will clean (older builds), run [Detekt](https://detekt.github.io/detekt/), run some automated tests, and compile 
the project. If everything goes well, you will see a message of _build success_ at the end of the process.

To just lint the project and check if the code is compliant with our Detekt rules, run: `gradlew detekt`. At the first
run it should fix some simple problems, at the second run only the ones that require your manual intervention should be
reported.

If you plan to develop in the project, after cloning it, go to the folder named _docker_ and run this command:

2. `docker compose -p web-budget up`

It should create an instance of the PostgreSQL database and Redis (used here for cache) inside the docker runtime. 
Everything will be exposed at those ports: _5433_ for postgres and _6379_ for redis.

After that you just need to import the project in your favorite IDE and start coding.

> Since this is a Kotlin project is highly recommended to use IntelliJ IDEA for development, if you don't have
> it, please click [here](https://www.jetbrains.com/?from=webBudget) to download.

## Tech stack

- Kotlin 1.6
- Spring Boot 2.6
- PostgreSQL 13
- Redis 6

## FAQ

Questions? We try to answer some of them here:

1. Why separate back-end from front-end?
> The answer is simple: flexibility and to let people who don't know about the front-end stuff work where they are good 
> and productive without having to deal with things they didn't know/want

2. Why changing from Java to Kotlin
> Future! I believe in Kotlin being the future of Java or something very close to this. A lot of problems and pains that 
> we used to have with Java are solved in Kotlin, and the most important: we don't need lombok anymore.
> Jokes a part, of course Kotlin brings a lot of new things and perspectives to the JVM ecosystem and people around it 
> are putting a lot of efforts to make Kotlin good and stable as Java is. 