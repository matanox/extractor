Integration testing for this project. Runs the sbt plugin for all sbt projects included under [src/main/resources/test-projects](https://github.com/CANVE/extractor/tree/master/sbt-plugin-test-lib/src/main/resources/test-projects), reporting their execution.

###Running

`sbt run` 

Note: To pull all test projects in, use `git submodule update --init`, as they are not explicitly included.

###Maintaining the list of test projects:

The projects to be used for the testing are placed under `src/main/resources/test-projects`, and they are managed as [git submodules](https://git-scm.com/docs/git-submodule). Loosely speaking this means they are not physically included in this repo, but rather linked to. This is why you have to pull them in after cloning this repo (or possibly after pulling this repo after the list has changed), through the command mentioned above.

##Adding a publicly available project as a test project:

1. Make sure the project you are going to add is adequately licensed.

2. Use `git submodule add <git clone link> src/main/resources/test-projects/<project name>`. E.g: 
```
git submodule add git@github.com:allenai/pipeline.git src/main/resources/test-projects/pipeline
```
