# install4j-11-configuration-cache-error
Uses the Install4J Gradle plugin (version 11.0.0.1) to create an installer, demonstrating the plugin does not appear to support Gradle's configuration cache.

## To test support for configuration caching:
1. Edit [./basic-map-data/build.gradle.kts](./basic-map-data/build.gradle.kts?plain=L87) to specify a valid Install4J license parameter:  
   ```
   // TODO: Specify Install4J license information   
   install4j {   
      license = "FLOAT:install4j.my.company.com"   
   }
   ```
2. Execute the Gradle task to build the installer:  
   `.\gradlew.bat installer` (Windows)  
   `./gradlew installer` (Unix/macOS)
3. Note the Gradle outputs relating to configuration cache as follows:
```
Configuration on demand is an incubating feature.
Calculating task graph as no cached configuration is available for tasks: installer
```
[Install4J output...]
``` 
Configuration cache entry discarded with 1 problem.

FAILURE: Build failed with an exception.

* What went wrong:
Configuration cache problems found in this build.

1 problem was found storing the configuration cache.
- Task `:basic-map-data:installer` of type `com.install4j.gradle.Install4jTask`: invocation of 'Task.project' at execution time is unsupported.
  See https://docs.gradle.org/8.10.2/userguide/configuration_cache.html#config_cache:requirements:use_project_during_execution

See the complete report at file:///C:/Dev/Projects/SCM/Demos/install4j-11-configuration-cache-error/build/reports/configuration-cache/3iclcln7vtt4ox1j5spz0k7p8/ewa1idb7vkgp53sgr2v8ttp24/configuration-cache-report.html
> Invocation of 'Task.project' by task ':basic-map-data:installer' at execution time is unsupported.

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 1s
```
4. Note the configuration cache report indicates
   ```
   invocation of `Task.project` at execution time is unsupported.
   task :basic-map-data:installer of type com.install4j.gradle.Install4jTask
   Exception at com.install4j.gradle.Install4jTask._init_$lambda$1(Unknown Source)
   ```
5. Note the `writeConfiguration` task in the [./basic-map-data/build.gradle.kts](./basic-map-data/build.gradle.kts?plain=L87) build script uses a nearly identical configuration as the `installer` task and this task is compatible with configuration cache.
   1. Different task type (`DefaultTask` vs `Install4jTask`) 
   2. Does not set the `Install4jTask` extension properties `projectFile` or `variables`.
