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

# TODO

Since this is an explicit service defined atop the template the templatey bits can be torn out

* APP_URL in the k8s along with updating it every time the Jenkinsfile runs
* Adjusting the target URL back to meta.terasology.io and a good preview/test pattern
* Overhauling the readme, naturally
* Server _removal_ doesn't work, some content type mismatch issue
* The unit tests extending BaseTests seem to be pulling real jar files from Artifactory, not the mocked test resources (?!?)
* Go dig out remainder that was tossed out of build.gradle (codemetrics / quality etc)
* Set the edit secret from a secret text credential in Jenkins as an env var in the pod? For live. Dev can be hard coded. Test? 

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
* `$env:MICRONAUT_ENVIRONMENTS="dev"` in PowerShell

### Container Registry

The goal is to allow several different registries to work, with minimal effort to go nicely along with the no-nonsense Jib setup.

### Google Container Registry

[GCR](https://cloud.google.com/container-registry/docs/) is admittedly deprecated in favor of Google's newer [Artifact Registry](https://cloud.google.com/artifact-registry/docs), but plenty of older guides and apps still use it. Converting from GCR to AR should be minimal when the basics work.

First you need to enable its API on your GCP project like other features. Take a look at the basic config and make sure it is set up to match your needs and comfort level.

To get a credential to work with GCR you need to make a [service account](https://console.cloud.google.com/iam-admin/serviceaccounts) (IAM - Service Accounts in [GCP](https://console.cloud.google.com/)) with the Storage Admin role.

After you have a suitable service account you need a JSON file representing the key and its details to use within Jenkins (or elsewhere). This can be created in the UI or via CLI, example used for this repo:

`gcloud iam service-accounts keys create mykey --iam-account=gcr-service-user@teralivekubernetes.iam.gserviceaccount.com`

The `mykey` parameter becomes the json file written to disk in the active directory. Submit it as a Secret File credential in Jenkins and it'll be usable as within the `Jenkinsfile` in this repo, referred to by its credential id

#### Google Artifact Registry

This is much like GCR, with different values for the registry URL and the service account. The authentication takes a little more handling, see `build.gradle` for details

#### Nexus Repository

This is a more generic repository manager that can be used for many things, including Docker images. The setup is a bit more involved than GCR, but it is a good option for self-hosted solutions. Again see `build.gradle` for the exact setup there.

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
