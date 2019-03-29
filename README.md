#Spring Required Property Loading Sample

##Purpose
The purpose of this repo is to demonstrate how to force SpringBoot to short-circuit loading when a specified property 
is unavailable.  This technique is useful when you want to keep an application from starting if it does not have a 
certain set of configuration.

##Relevant Configuration Details
Refer to comments in `SampleConfiguration.java` to understand which property is required for the application to load:

```java
@Configuration
@ConfigurationProperties(prefix = "message.default")
@Validated // required to tell Spring to validate this Configuration on load
public class SampleConfiguration {
    // If SpringBoot cannot find message.default.sample SOMEWHERE (Environment Variables, Properties Files,
    // or Config Server), this application will refuse to start.
    @NotNull
    private String sample;

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
```

Notice that the property `message.default.sample` exists only in the files `application-local.yml` or 
`application-test.yml`. This means that in the way this application is setup currently it will only load if the 
`local` or `test` Spring profile is active.

##How To Use This Repo To Demo
* Check out the source code:
```bash
$ git clone https://github.com/bthelen/spring-property-load.git 
$ cd spring-property-load
```
- Try to run without activating one of those profiles
```bash
$ mvn spring-boot:run -DskipTests
```
- Notice the application failed to start
- Run with activating the profile
```bash
$ mvn spring-boot:run -DskipTests -Dspring.profiles.active=local
```
- Notice the application did start
- Check the `actuator` endpoint to inspect the environment -- notice `message.default.sample` is in there
```bash
$ curl localhost:8080/actuator/env
```

##How to use This Repo To Show Spring Cloud Config Server Integration on Pivotal Cloud Foundry
* Make sure you are logged on to a foundation where you have access to `cf push` and you have the ability to 
`cf create-service` an instance of Spring Cloud Config Server.
- Create a config server instance pointing to a [sample git repo](https://github.com/bthelen/spring-config-repo) that includes `message.default.sample` in 
`application-cloud.yml`
```bash
$ cf create-service p-config-server standard config-server -c '{"git": {"uri": "https://github.com/bthelen/spring-config-repo.git" }}'
```
- Review the file [application-cloud.yml](https://github.com/bthelen/spring-config-repo/blob/master/application-cloud.yml) to see that `message.default.sample` is there.
- Compile the sample
```bash
$ mvn clean install -Dspring.profiles.active=test
```
- Push the sample
```bash
$ cf push
```
- Notice it does load, even though `local` not `test` Spring profile is active
- Recall that Pivotal Cloud Foundry automatically activates a Spring profile called `cloud`
  * Review the log of the application start
  ```bash
  $ cf logs spring-property-load --recent
  ```
  - You should see a line similar to the following that shows the configuration contained in `application-cloud.yml` 
  was loaded into the Spring Boot context.
  ```bash
  2019-03-29T10:39:10.97-0500 [APP/PROC/WEB/0] OUT 2019-03-29 15:39:10.973  INFO 23 --- [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='configService', propertySources=[MapPropertySource {name='configClient'}, MapPropertySource {name='https://github.com/bthelen/spring-config-repo.git/application-cloud.yml'}, MapPropertySource {name='https://github.com/bthelen/spring-config-repo.git/application.yml'}]}
   ```
- Check the `actuator` endpoint to inspect the environment -- notice `message.default.sample` is in there
```bash
$ curl <route-to-your-app-on-PCF>/actuator/env
```
- Now unbind the application from the Spring Cloud Config Server
```bash
$ cf unbind-service spring-property-load config-server
```
- Attempt to restart the app
```bash
$ cf restart spring-property-load
```
- Notice that application failed to start because `message.default.sample` was not available.
