[![build](https://github.com/web-budget/back-end/actions/workflows/build.yml/badge.svg)](https://github.com/web-budget/back-end/actions/workflows/build.yml)

# webBudget back-end

Welcome to the back-end application for webBudget project!

The project is based on:

- Kotlin 1.9
- Spring Boot 3
- Postgres 15
- Testcontainers

## Project setup

If you are familiar with Java projects using maven, there is nothing too different here. The project is using Gradle as
build tool and Kotlin will compile through it.

> Quick note before start: your [docker](https://docs.docker.com/get-docker/) environment is running? Since we use 
> testcontainers to run the tests and also to develop things in the project, is required to have a docker instance running.

Clone the project, and:

`gradlew clean build` 

This will clean (older builds), run [Detekt](https://detekt.github.io/detekt/), some automated tests, and compile the project. If everything goes 
well, you will see a message of _build success_ at the end of the process.

To just lint the project and check if the code is compliant with our Detekt rules, run: `gradlew detekt`. At the first
run it should fix some simple problems, at the second run only the ones that require your manual intervention should be
reported.

If you plan to develop in the project, after cloning it, go in the root of the project, run this command:

`docker compose -p web-budget up`

It should start some required services to run the project locally and after that you just need to import the project in 
your favorite IDE, happy coding!

> Since this is a Kotlin project is highly recommended to use IntelliJ IDEA for development, if you don't have it, 
> please click [here](https://www.jetbrains.com/?from=webBudget) to download.

## FAQ

General questions about the project:

- **Why separate front-end and back-end?** Basically because this will make people more confortable to develop inside the
  project, not everyone are able to work in big monolith full of files and with trick configurations to deal with. Doing
  like this will help beginners searching for a cool project to start with some contributions.
- **How can I start contributing?** You can start by looking to the [project board here](https://github.com/orgs/web-budget/projects/6)!
- **Why not stay with Java?** For me Kotlin is more complete in terms of functional programming if compared to Java. This 
makes it better? Of course not, but is not possible to ignore the fact that Kotlin has a much more interesting toolset if 
compared to his "old brother".
