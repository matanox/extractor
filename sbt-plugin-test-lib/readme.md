Integration testing for this project. Runs the sbt plugin for all sbt projects located in [src/main/resources/test-projects](https://github.com/CANVE/extractor/tree/master/sbt-plugin-test-lib/src/main/resources/test-projects), reporting their execution.

##Running

`sbt run` 

Note: To pull in all test projects linked by this repo, first use the following command, as they are not explicitly included within this repo:
```
git submodule update --init
```

##Updating all test projects to their latest online version
```
git submodule foreach --recursive git pull
```

##Maintaining the test projects

The projects used for testing are those that are present, when you run this project, at [src/main/resources/test-projects](https://github.com/CANVE/extractor/tree/master/sbt-plugin-test-lib/src/main/resources/test-projects). They are managed as [git submodules](https://git-scm.com/docs/git-submodule), which means they are not physically included in this repo, but rather they are pointed at by it. 

###Adding a test project ad-hoc

Just copy whatever project into the directory. 
This won't add it to the repo though. 

###Adding a publicly available project as a test project to this repo:

1. Make sure the project you are going to add is adequately licensed.

2. Use `git submodule add <git clone link> src/main/resources/test-projects/<project name>`. For example:
    ```
    git submodule add git@github.com:allenai/pipeline.git src/main/resources/test-projects/pipeline
    ```

###Removing test projects from the repo

TBD

