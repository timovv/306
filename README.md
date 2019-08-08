SOFTENG 306 Project 1
=====================

[![CircleCI](https://circleci.com/gh/timovv/306.svg?style=svg&circle-token=61b40179d47c1370d4ecf661800770f71a60787f)](https://circleci.com/gh/timovv/306)

Project Setup
-------------

1. Clone the repo: 
``` 
https://github.com/timovv/306.git
```

2. Import as Gradle project in Intellij (preferably enable auto-import)
3. Install Lombok Intellij plugin 
4. Run the following Gradle task: 
```
./gradlew generateGrammarSource
```
Or, just run `./gradlew assemble`

Building
--------

To build an executable jar, run

```
./gradlew clean shadowJar
```

The jar will be placed in `build/libs/`.
