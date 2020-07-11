# distributed-file-share-app

## How to get the project
```shell script
    git clone https://github.com/Hashan1100/distributed-file-share-app.git
```

## Environment Specification
* Java version : openjdk 11
* maven : 3.6 (> 3.2)
* preferred IDE : intellij idea

## How to build
* Go to the project root and run
    ```shell script
      mvn clean install -DskipTest
    ```
* Build will appear in the target folder `<module-name>/target`
    * eg:-

        ```shell script
            bootstrap-server/target/bootstrap-server/bin # Will contain the build
        ```
* Navigate to the module `cd <module-name>/target/<module-name>/bin` and run
`./module-name console`
    * eg:-

        ```shell script
            cd bootstrap-server/target/bootstrap-server/bin && ./bootstrap-server console
        ``` 