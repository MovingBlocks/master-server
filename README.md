meta-server
=========

Micronaut web application that serves meta-information about Terasology.
It is available for in-game use in JSON format and in HTML format for web browsers.

The server is available at
**http://meta.terasology.org**

Features
-------------
* Save and provide information about Terasology modules
* Receive and provide information about Terasology servers (Looking for game)
  * To edit server entries you need a password - see `Jenkinsfile` and `k8s/secrets.yaml` to see one way it is set up, locally you can set it via env var `META_SERVER_EDIT_SECRET`  
* Using https://db-ip.com/ for providing additional info about servers (disabled until API issue figured out)
* Use PostgresDB via Jooq for persistence
* Use Micronaut for core Frameworks


Run locally
-------------
Gradle-way:
1. Clone
2. Go to Project Dir
3. Set environment variable `MICRONAUT_ENVIRONMENTS` to `dev` (local h2 database)
4. `./gradlew run` at Linux/Macos or `gradlew run` at Windows
5. found at logs entry like `[main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 624ms. Server Running: http://localhost:39195`
6. Go To url described at logs with your browser

Docker-way:
1. Clone
2. Go To Project Dir
3. Run `docker build . -t test-meta-server`
4. Run `docker run --env "MICRONAUT_ENVIRONMENTS=dev" test-meta-server`

Deployment
-------------
Docker:
1. Clone repository
2. Go To project directory
3. `docker build . -t <tag>`
4. `docker push` image whatever you want

You can setup what you want with [Micronaut's configs](https://docs.micronaut.io/latest/guide/index.html#config):
Common (ENVIRONMENT_VARIABLES):

     MICRONAUT_SERVER_PORT - port (example - 80)
     DATASOURCES_DEFAULT_URL - url to PG database (example - postgres://name:pw@host:port/database
     META_SERVER_DBIP_API_KEY=<get one from db-ip.com>
     META_SERVER_EDIT_SECRET=<a password only known for admins with write access>

## Testing

Running locally you'll only see a home page, then an empty "Modules" section and an empty "Servers" section.

While it is easy to just hand-add a new server for testing making the module section update takes a little more effort. Some IDEs have support for `*.http` files where you can enter and execute a simple request, like the following entered into a `test.http` file:

```
POST http://localhost:8080/modules/update HTTP/1.1
Content-Type: application/json  

{
  "name": "Sample" 
}
```

## Behind the scenes

Some more gory details about how this template was put together

### Jib

In short Jib lets you automagically containerize a JVM web app through something like their [gradle plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin) without having to write or maintain a Dockerfile.

You can easily define what source image to use (it will otherwise default to something sensible) and what image it should produce and publish somewhere.

You can see other examples of Jib in action at https://github.com/GoogleContainerTools/jib/tree/master/examples

### Micronaut

For complete documentation see the later section with links

By default the `application.yml` from `src/main/resources` is loaded, but you can toggle the dev environment settings by running the app with an environment variable `MICRONAUT_ENVIRONMENTS` set to `dev` to then load the `application-dev.yml` - you can make any environment you like and even activate multiple at once. See more at https://docs.micronaut.io/latest/guide/#environments

* `export MICRONAUT_ENVIRONMENTS=dev` on Linux/MacOS
* `set MICRONAUT_ENVIRONMENTS=dev` on Windows
* `$env:MICRONAUT_ENVIRONMENTS="dev"` in PowerShell (the quotes are important)

### Container Registry

The template for this project could support different registries, for publishing Docker images for this particular application we'll just use Google Artifact Registry

See https://github.com/MovingBlocks/Logistics for initial infra setup, this project will assume everything is ready already. See `build.gradle` and the `Jenkinsfile` for local technical details.

## Micronaut 3.10.1 Documentation

- [User Guide](https://docs.micronaut.io/3.10.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.10.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.10.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

Geo-Location
-------------

The lookup service by [DB-IP.com](https://db-ip.com/) is used to retrieve additional information on the servers (or would be, if it worked at the moment)


Flag Icons
-------------

The flag icons are from [famfamfam](http://www.famfamfam.com/lab/icons/flags/).

License
-------------

This software is licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).

# TODO

Since this is an explicit service defined atop the template the templatey bits can be torn out

* APP_URL in the k8s along with updating it every time the Jenkinsfile runs (unless we use a multi-env pattern for testing)
* Adjusting the target URL back to meta.terasology.io and a good preview/test pattern
* Server _removal_ doesn't work, some content type mismatch issue
* The unit tests extending BaseTests seem to be pulling real jar files from Artifactory, not the mocked test resources (?!?)
* Go dig out remainder that was tossed out of build.gradle (codemetrics / quality etc)
* Set the edit secret from a secret text credential in Jenkins as an env var in the pod? For live. Dev can be hard coded. Test? 
