<!--# <span id="anchor"></span>Spring Cloud Native Apps Course Labs -->

# <span id="anchor-1"></span>Spring Cloud Connectors Lab

In this lab, you will learn how to use Spring Cloud Connectors libraries
to simplify the lookup of service information and creation of service
connector in a cloud portable way. You will achieve
development-production parity (12Factor) by developing and testing your
application first locally, and then push and test on Cloud Foundry. As
optional lab part, you will also deploy your application in a Lattice
(pseudo-)cluster. 

<!--In another optional part of the lab, you will extent
Spring Cloud Connectors to support service bindings and connectors to
micro-services with URI http:// schema. -->

**In this lab you will learn:**

-   How to configure an applications with Spring Cloud Local Connector
-   How to integrate Spring Cloud Local Connector in a Spring Boot app
    that use a variety of Spring modules
<!--    -   Spring Cloud + Spring MVC + Spring Data + Spring Boot + Thymeleaf (VDL) -->
-   How use Spring Cloud Connectors with application bound services in Cloud Foundry
-   \[Bonus\] How use Spring Cloud Connectors with Lattice
-   \[Bonus\] How to extend Spring Cloud Connectors

**Specific techniques you will work with:**

-   How to import Spring Cloud Connectors Maven Dependencies in the `pom.xml`
-   How to configure Spring Cloud Local Connector
-   How to use Spring Profiles in a Cloud and Local Environment
-   How to bind to services
-   How to write a manifest.yml for an application
-   How to push an application to Cloud-Foundry with the `cf` CLI
-   \[Bonus\] How to use Lattice `ltc` CLI

Expected time duration to complete the lab: 20min(local) + 15min(PWS) + 15min(Lattice) = 50min

### <span id="anchor-2"></span>**Configure Application for the Local “Cloud” Environment**

The application you are going to configure is built with a variety of
Spring technologies, including Spring Framework, Spring MVC, Spring
Data, and Spring Boot. In the provided lab workspace, you can find it as
project `cna-0x-connectors`. Most dependencies have already been
setup in the Maven project descriptor file (`pom.xml`). (This was done
with the help of *Spring Starter*.)

The project should also used Spring Cloud Connectors project, which have
not yet been setup as maven dependencies. You first step is to import
this dependencies for the local connector. Later you will add the
dependencies for other connectors -- Cloud Foundry, and Lattice.

### <span id="anchor-3"></span>TODO 1 - Import Maven Dependency for Spring Cloud Local Connector

Start by importing the Maven Dependency for Spring Cloud Connectors –
including the Spring Cloud core module, and the Cloud Foundry connector,
the Local Cloud connector for testing, and the Spring Connectors
modules. (The Spring Cloud Core dependency is imported automatically by
transitivity.)

1.  Add the dependencies below to the `pom.xml` file of project `cna-0x-connectors`.

```XML
<!-- Spring Cloud: Spring Connectors -->	
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-spring-service-connector</artifactId>
</dependency>
	
<!-- Spring Cloud: localconfig for development -->		
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-localconfig-connector</artifactId>
</dependency>
```

**TIP:** Note that is not required to specify the `<version>`
element for the dependencies, since they are setup automatically in the
pom file for `spring-bom` (indirectly through `spring-boot-starter-*`).

**TIP:** Maven should automatically update your project dependencies. If
not, use option **Maven &gt; Update Project (ALT + F5)** from the
context menu.

### <span id="anchor-4"></span>TODO 2 – Create a Configuration Class for Cloud Environment

Now you can start using Spring Cloud in `cna-0x-connectors` project.
Implement a Spring Java Configuration class to define key beans for the
cloud environment.

Open file `CloudConfig.java`, in package `cna.connectors` (under
source directory tree `src/main/java`).

1.  Define this class as Spring Java config class with annotation
    `@Configuration`.
2.  Specify that this configuration class is only enabled in the Spring
    `cloud` profile, with annotation `@Profile`.
3.  Add a bean factory method annotated with `@Bean`, named `cloud()` and with return type:
    `[org.springframework.cloud.]Cloud`. Create the bean instance by
    first creating an instance of `CloudFactory`, and calling
    `getCloud()`.
4.  Add another bean factory method annotated with `@Bean`, named
    `applicationInstanceInfo` and with return type:
    `[org.springframework.cloud.]ApplicationInstanceInfo`.
    Create the bean instance using the `cloud()` bean.

**TIP:** Use CTRL+SPACE or Quick-Fix IDE command to import the classes
used in your Java code.

### <span id="anchor-5"></span>TODO 3a – Run Test for Application (Fails)

You can now give your first shot at deploying locally the
cna-0x-connectors application.

Open test class `ApplicationTests.java`, in package `io.cna.cna-connectors` 
(under source directory tree `src/test/java`).

1.  Enable profile `cloud` with annotation `@ActiveProfiles`
2.  Define a field of type `Cloud` and inject it with annotation
    `@Autowired`
3.  Run the test class (**Run As &gt; Unit Test**). An exception should
    be thrown.

```nohighlight
java.lang.IllegalStateException: Failed to load ApplicationContext
...
Caused by: org.springframework.cloud.CloudException: No suitable cloud connector found
```

The reason why the test fails is because the local Spring Cloud
connector is not being enabled. (And, obviously, you not yet deploying
it to a real cloud environment like Cloud Foundry). For this to work,
the local connector needs to pick up a property named
`spring.cloud.appId` in a configuration file. This is your next step.

### <span id="anchor-6"></span>TODO 3b – Configure Local Cloud Connector

Open the file named `spring-cloud.properties`, located in directory
`src/test/resources`.

1.  Define a property named: `spring.cloud.appId = cna-connectors`

```properties
spring.cloud.appId:cna-connectors
```


Next, open the file named `spring-cloud-bootstrap.properties`, located
in directory same directory.

1.  Define a property named `spring.cloud.propertiesFile` and set the
    value to the location of `spring-cloud.properties` (in the
    same directory) `spring.cloud.propertiesFile = src/test/resources/spring-cloud.properties`

Time to run the test again.

### <span id="anchor-7"></span>TODO 3c – Run Again Test for Application (Success)

Navigate back to test class `ApplicationTests.java`.

1.  Run the test class (**Run As &gt; Unit Test**). Confirm that the
    test passes.
2.  Improve the test method `contextLoads()` to get the
    `ApplicationInstanceInfo` from the `Cloud` field and print to
    `System.out` the value of properties: `appId`, `instanceId`,
    `properties`, and `services`
3.  Confirm the displayed value of property `appId` is `cna-connectors`. 
   (`InstanceId` is randomly generated, and other properties are empty for now.)

### <span id="anchor-8"></span>**Configure DataSource in Local “Cloud” Environment**

You application needs to access a database. In this lab, you start your
work with relational databases. Later steps, you will experiment with
alternative NoSQL databases and other kinds of services.

JPA Repositories - for the domain class `Album`- are being provided
automatically by Spring Data JPA, but you still need to enabled it.

### <span id="anchor-9"></span>TODO 4a – Enable JPA Repositories and Run Test

Take a few minutes to open and review the content of Java files under
packages: `io.cna.domain`, `io.cna.repository`, `io.cna.repository.jpa`

Notice, in particular, that interface `JpaAlbumRepository` is used by
Spring Data JPA to produce a proxy implementation of `AlbumRepository` using JPA.

1.  Navigate back to test class `ApplicationTests.java.` Enable the
    profile `jpa` in the `@ActiveProfiles` annotation.
2.  Defined a field of type `AlbumRepository` and inject it with `@Autowired`.
3.  In the method `contextLoads()` call and print the result of `AlbumRepository.count()`.
4.  Run the test class. Confirm that it passes, and the `Album` count is not zero.

**TIP:** If you are curious about which `Album`s are being counted,
open file `src/main/resources/album.json`. And review the code for
`AlbumRepositoryPopulator`.

### <span id="anchor-10"></span>TODO 4b – Configure Embedded H2 DataSource

The above test was successful because Spring Boot is taking the
responsibility of both configuring JPA and creating an embedded
`DataSource` of type H2 named `testdb` once it finds
`org.h2.Driver` in the classpath. You may also prefer to configure a
`DataSource` explicitly.

Open file `DataSourceConfig.java`, in package `io.cna`(under source
directory tree `src/main/java`).

1.  Define this class as Spring Java config class with annotation
    `@Configuration`.
2.  Find method `dataSource()` and define it as bean factory method
    with `@Bean`. Specify that this bean is only enabled in profile
    `h2` with annotation `@Profile`.
3.  Enable the profile `h2` in `@ActiveProfiles` annotation of `ApplicationTests`.
4.  Open the console view. Run the test class, and check that it still runs with success.
5.  Confirm by looking at the log message that an H2 `DataSource` is
    still being created, but this time is named (rather than `testdb`).

### <span id="anchor-11"></span>TODO 5a – Create MySql Databases

Since your application is going to run with a MySql DB later on when you
deploy it to Cloud Foundry, you might prefer to perform your local
integration tests with a MySql DB rather than an embedded H2 database –
to have the local environment match more closely the Cloud Environment.

*Next steps in the lab assume that you have installed a MySQL server in
your machine during the Lab Setup. If not, install MySQL server now
using the provided instructions. You should also remember the root
password that you used during setup.*

1.  Login to MySql console

```bash
&gt; mysql -uroot -pmypassword
```

2.  Create a database named **cna**

```sql
mysql&gt create database cna
```

### <span id="anchor-12"></span>TODO 5b – Configure MySql Connector

Next add dependencies for the MySQL database driver.

1.  Add the MySQL driver and DBCP2 dependencies to the `pom.xml`.

```XML
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-dbcp2</artifactId>
</dependency>
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
</dependency>
```

**TIP:** If you prefer to use MariaDB rather than MySql, import the maven
dependency `org.mariadb.jdbc:mariadb-java-client`.

### <span id="anchor-13"></span>TODO 5c – Configure Hibernate and Database Connection

You need to configure Hibernate (the default JPA provider) to work with
MySql. You should also externalize the configuration of the database
connection URL and credentials.

1.  Find file `application.properties`, under directory
    `src/main/resources`. Add property entries to configure for MySql
    Dialect and auto schema creation/update.

```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect	
spring.jpa.hibernate.ddl-auto=update
````

2.  Add also property entries to configure the local MySql Database connection.

```properties
db.url=jdbc:mysql://localhost/cna
db.driver=com.mysql.jdbc.Driver
db.username=root
db.password=local-mysql-root-password
````

Next come back to your Java app.

### <span id="anchor-14"></span>TODO 5d – Defined MySQL DataSource

1.  In Java class `DataSourceConfig`, define and autowire a field of
    type `Environment`.
2.  Uncomment method `createBasicDataSource()`.
3.  Define a factory method named `dataSource2()` - annotated with
    `@Bean` – of type `DataSource`. Call provided method
    `createBasicDataSource()`with parameters extracted from the
    `Environment`. Make the bean enabled only in profile
    `mysql-local` with annotation `@Profile`.

```properties
createBasicDataSource(environment.getProperty("db.url"),
  environment.getProperty("db.driver"),
  environment.getProperty("db.username"),
  environment.getProperty("db.password"));
````

  
1.  In class `ApplicationTests`, modify attributes of annotation
    `@ActiveProfiles` to enable `mysql-local` profile (rather than `h2`).
2.  Run the test class (**Run As &gt; Unit Test**). Confirm that the test passes.
3.  Login again to MySql console (if needed), and confirm that the
    schema for database has been automatically created and populated.
    Confirm that the data makes sense by comparing with content of file
    `album.json`.

```properties
mysql &gt; use  cna-connectors
mysql &gt; show tables;
mysql &gt; select * from album;
```

### <span id="anchor-15"></span>**Bind to MySql Service in Local “Cloud” Environment**

Rather than explicitly create MySql DataSource explicitly you can also
create a connector to a MySql Service with Spring Cloud Connectors. This
is the scenario you will use when deploying to Cloud Foundry, so it is
just natural that you use the same approach in the local environment.
(This is actually called the development-production parity principle in
the 12Factors.)

### <span id="anchor-16"></span>TODO 6 – Bind to MySql Service

1.  Open file `cloud.properties`, in directory `src/test/resources`,
    and add an entry to define a service named `mysql` to connect to a
    MySql database.

**TIP:** Make sure you set the MySQL password correctly in the service
URI.

```properties
spring.cloud.mysql = mysql://root:mypassword@localhost/cna-connectors
```

1.  In class `DataSourceConfig`, define and inject a field of type `Cloud`.
2.  Define a bean factory method named `dataSource3()` that calls
    `Cloud.getServiceConnector()` to lookup the `mysql` service.
    Make the bean enabled only in profile `cloud`.
3.  In class `ApplicationTests`, enable only profiles `cloud` and
    `jpa`
4.  Run the test class. Confirm that the test passes.

### <span id="anchor-17"></span>TODO 7 – Scan for Services

You can also have the MySql DataSource service being automatically
picked up.

1.  In Java class `CloudConfig`, add annotation `@ServiceScan` to
    automatically scan for services.
2.  Comment annotation `@Configuration` in class `DataSourceConfig`,
    or alternatively, comment annotation `@Bean` in `datasource3()`
3.  Run the test class `ApplicationTests`, confirm that the test
    still passes.

**TIP:** Notice that with the `@ServiceScan` you don't need the
`DataSourceConfig` class anymore.

### <span id="anchor-18"></span>**Running Application in Local Environment**

It now time to setup your application as a webapp and run it in the
local environment.

### <span id="anchor-19"></span>TODO 8 – Setup Web Controllers

1.  Open class `AlbumController`, in package
    `io.cna-connectors.web.controller` in under source folder
    `src/main/java`, and make it a Spring managed component by
    annotating it with `@Controller`.
2.  Add `@Autowired` to the constructor.
3.  Open class `InfoController`, in same folder, and uncomment the
    body of the class and the import statements.
4.  Make it a web component with `@Controller`.

Now configure it and run it.

### <span id="anchor-20"></span>TODO 9a – Configure and Run Application

1.  Open file `application.properties`, and add an entry to enable
    profile `cloud` and `jpa`.

```properties
spring.profiles.include=jpa,cloud			
#spring.profiles.enabled=jpa,cloud 			# alternative
```

2.  Next, open class `MyApplication` and run it (**Run As &gt; Java
    Application** or **Run As &gt; Spring Boot Application**). Confirm
    that an exception is thrown complaining that no cloud connector
    is found.
3.  The error happens because the file
    `spring-cloud-bootstrap.properties` is under
    `src/test/resources` and this folder is not bundled into the
    **WAR** file deployed into the *Servlet Container* (Tomcat)
    working directory. You can fix this by explicitly setting the value
    of JVM property `spring.cloud.propertiesFile`.

4.  In the first line of method `MyApplication.main()` set the value
    of system/JVM property `spring.cloud.propertiesFile` to point to
    the right place.

```properties
System.setProperty("spring.cloud.propertiesFile", "src/test/resources/spring-cloud.properties");
```

5.  Run the app again and confirm that now it starts without error.
6.  Start/Open a window for your favorite browser (e.g. firebox, chrone,
    IE, or Eclipse/STS internal browser) and open page with URL
    `localhost:8080`. Confirm that the application shows up. Navigate
    around to get familiarized with the page structure. In particular,
    check the content of the `info` pages, the list of `Album`s and
    the `Album` details.

Rather than changing the `main()` method of the app you can also set
the JMV property by creating as a *Lunch Configuration*.

### <span id="anchor-21"></span>TODO 9b – Run Application with a Launch Configuration

1.  Comment the `System.setProperty()` on the `Application.main()` method
2.  Create a Lunch Configurations. (**Run As &gt; Run Configurations...**).
3.  Set the appropriate JVM property on the **(x)Arguments** tab.
4.  Run again. Confirm that the app still starts without errors and it
    is accessible from the browser.

```XML
-Dspring.cloud.propertiesFile=src/test/resources/spring-cloud.properties
```


## <span id="anchor-22"></span>**Deploying & Binding to Services in Cloud Foundry (PWS)**

So far you have developed and tested your application in the local
“cloud” environment. It is now time to deploy to a real cloud
environment – Cloud Foundry on PWS.

### <span id="anchor-23"></span>TODO 11 - Import Maven Dependency for Spring Cloud Foundry Connector

Start by importing the Maven dependency for the **Cloud Foundry
Connector** from the *Spring Cloud Connectors* project. (You should
already have imported the Local Config Cloud connector, and the Spring
Services Connectors modules, earlier in the beginning of the lab).

1.  Add the dependency below to the `pom.xml` file of project `cna-0x-connectors`.

```XML
<!-- Spring Cloud: to deploy to Cloud Foundry -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-cloudfoundry-connector</artifactId>
</dependency>
```

### <span id="anchor-24"></span>TODO 12a – Build WAR for Application

1.  Open a terminal/console/shell, and change to the root directory of
    the project `cna-0x-connectors`. Build your project with
    **maven**.

```bash
mvn package -Dmaven.test.skip=true	
```

2.  Confirm BUILD was successful and that the WAR file was created.

```bash
./target/cna-0x-connectors-0.0.1-SNAPSHOT.war.original
```

### <span id="anchor-25"></span>TODO 12b – Write Manifest file for Application

Now write a YAML manifest file to configure the details how the app
should be deployed to Cloud Foundry - PWS.

Open file `manifest.yml` in the root directory and configure the
application deployment with the following settings.

1.  Name: `cna-connectors`
2.  Host/Route to `cna-connectors-${username}`, where
    `${username}` should be replaced by your username on
    PWS (or a random string)
3.  Count of instances to: `1`
4.  Memory: `512M`
5.  Path to WAR file
6.  Bind to service `mysql`

```bash
applications:
  - name: cna-connectors
  - host: cna-connectors-${myusername}		
  - instances: 1			
  - memory: 512M
  - path: ./target/cna-0x-connectors-0.0.1-SNAPSHOT.war.original
  - services:
    - mysql
```



You need now to provision (create) a `mysql` service instance to bind to the app.

*The next steps assumed that you have created a PWS Cloud Foundry
account, and that you have installed Cloud Foundry CLI and put the
`cf` command directory in the PATH (environment variable of your
terminal/shell).*

### <span id="anchor-26"></span>TODO 12c – Create MySql Service in PWS Cloud Foundry

1.  Start by login to PWS with the `cf` CLI tool (if you have not done
    it before)

```bash
cf l -u myuser -p mypassword -a api.run.pivotal.io
```

2.  Check the available services and mysql plans in PWS market place
    with command `marketplace` or `m`


```bash
cf m
```

3.  Create/Provision a **cleardb **MySql service with free plan
    `spark` and name the service instance **mysql. **Use command
    `create-service` or `cs`.

```bash
cf cs cleardb spark mysql
```

4.  Confirm that the service was created with command `services` or `s`.

```bash
cf s
```

5.  Notice that no app is bound yet to the service.

### <span id="anchor-27"></span>TODO 12d – Deploy Application to PWS Cloud Found

Now you are ready to deploy to Cloud Foundry - PWS.

1.  Push the application to PWS using the created `manifest.yml` file.
    Use `cf` CLI command `push` or `p`.

```bash
cf p
```

**TIP:** The CLI `cf` assumes that the app manifest file is called
`manifest.yml` by default.

1.  Double-Check the status of the app to confirm that it was staged and
    deployed successfully. Use command `apps` or `a`.

```bash
cf a
cf app  cna-connectors
```

1.  Open a new tab/window in the browser and open page in URL:`
    cna-connectors-${myuser}.cfapps.io`
2.  Navigate around and confirm that the app behaves in the same way as
    in the local environment.

## <span id="anchor-28"></span>**BONUS: Deploying & Binding to Services in Lattice**

In addition to deploy you Spring Cloud Connectors based application to
Cloud Foundry, you can also deploy it to other cloud and containerized
environments. In particular, you can deploy and use the support to
services in Lattice.

**TIP:** Lattice uses a subset of the components in Cloud Foundry Diego
elastic run-time, to provide a developer-friendly light-weighted
platform to deploy applications to a local or distributed container
environment. Services and applications can be started inside/as Docker
containers. Application can also be built and deployed from JAR files as
CF droplets.

**TIP:** This part of the lab requires that you have a running Lattice
run-time and the `ltc`command installed. If you have not installed
yet, see the appendix for instructions how to do it. Depending on the
training delivery settings, you trainer might additionally/alternatively
give you access to a VM already running Lattice.

### <span id="anchor-29"></span>TODO 3.01 - Import Maven Dependency for Lattice Connector

Start by importing the Maven dependency for the *Lattice Connector*.
(You should already have imported the Spring Services Connector modules
earlier in the beginning of the lab).

1.  Add the dependency below to the `pom.xml` file of project `cna-0x-connectors`.

```XML	
<!-- Spring Cloud: to deploy to Lattice -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-lattice-connector</artifactId>
</dependency>
```


### <span id="anchor-30"></span>TODO 3.02 - Start MySQL Service as a Docker Container in Lattice

Services in Lattice can be started the same way as applications - as a
Docker container. For most of the commonly used services there are
already Docker containers available in Docker Hub -- where Lattice
fetches container-images, by default. This makes it very easy to get
started and deploy those services.

1.  Start by deploying a `mysql` service as an application using the
    `ltc create` command below.

```bash
ltc create mysql mysql --memory-mb 1024 --env MYSQL_ROOT_PASSWORD=mysql --run-as-root --tcp-routes 3306:3306
```

2.  Confirm that the MySQL service started successfully with `ltc`
    commands `list` and `status`.

```bash
ltc list
ltc status mysql
```

3.  You can also use the mysql client inside the app to query the
    MySQL server.

```bash
ltc ssh mysql
```

### <span id="anchor-31"></span>TODO 3.03 - Build Image for Application

You should now build a droplet out of your application WAR.
Specify droplet name `cna-connectors`. 
(If running in a shared environment, use name `cna-connectors-${username}`, where `${username}` is your username.)


```bash
ltc build-droplet cna-connectors java -p cna-connectors-solution-0.0.1-SNAPSHOT.war.original
```

**TIP:** `java` in the command above specifies that the Java buildpack should be used. You could also have specified the full URI
 (https://github.com/cloudfoundry/java-buildpack).

**TIP:** If you are running **ltc** from a different machine where you
are building your application, you need to **scp **the JAR file to the
remote machine with **ltc**.

```bash
scp  cna-connectors-solution-0.0.1-SNAPSHOT.war.original myuser@xx.xx.xx.xx:.
```

Confirm that the droplet was create sucessfully by listing the droplets with command `list-droplets` or `lsd`.

```bash
ltc lsd
```

### <span id="anchor-32"></span>TODO 3.04 - Deploy Application to Lattice

You can now deploy (launch) your application droplet to Lattice.
Specify application name `cna-connectors`. (If running in a shared environment, use name `cna-connectors-${username}`, where `${username}` is your username.)

```bash
 ltc launch-droplet cna-connectors cna-connectors
```

Confirm that the application was started with success:
```bash
ltc ls
```

### <span id="anchor-33"></span>TODO 3.05 - Test Application in Lattice

Test that the opplication is running by openning the browser in the application URI.

```bash
http://cna-connectors.xx.xx.xx.xx.xip.io/
```

<!--
### <span id="anchor-34"></span>**BONUS: Extending Spring Cloud Connectors**

In this part of the lab, you will extend the Spring Cloud Connectors to
allow service binding to micro-services to be mapped to ServiceInfo and
to create a RestTemplate as a connector.

### <span id="anchor-35"></span>TODO 4.01 - Representing MicroServices

xx

```bash
```
-->

## <span id="anchor-36"></span>**Appendix - Installing Lattice**

You can install Lattice in one of two way -- using a Vagrant VM image,
or manually in a Linux machine.

*The Lattice Vagrant VM image requires a machine with 64bits
virtualization enabled. If you are running on Windows, you need to make
sure that -- your CPU supports hardware virtualization, your BIOS as an
option to enable hardware virtualization, and that this optional is also
enabled in windows. If you don’t have this features, you need to use
manual install in a Linux box with 64bits virtualization. Notice, that
changing the Lattice Vagrant config file to 32bits VM images is not
enough, since Lattice Go-written binaries are compiled to 64bits.*

### <span id="anchor-37"></span>Installing Lattice using Vagrant

Start by downloading and installing Vagrant in your Machine:

[html](https://www.vagrantup.com/downloads.html)

You need also need to download and install VirtualBox as virtualization
provider for Vagrant.

[Downloads](https://www.virtualbox.org/wiki/Downloads)

Download the Lattice bundle. Unzip it, and start vagrant.

```bash
[Linux VM]
https://github.com/cloudfoundry-incubator/lattice/releases/download/v0.5.0/lattice-bundle-v0.5.0-linux.zip
[Mac]
https://github.com/cloudfoundry-incubator/lattice/releases/download/v0.5.0/lattice-bundle-v0.5.0-osx.zip
```

```bash
unzip lattice-bundle-0.5.0.zip
cd lattice-bundle-0.5.0/vagrant
vagrant up
```

Login in the Vagrant VM.

```bash
vagrant sh
```

Test your deployment by targeting the runtime with command **ltc
target**.

```bash
ltc target 192.168.11.11.xip.io
ltc list
ltc create lattice-app cloudfoundry/lattice-app
ltc list
ltc status lattice-app
ltc rm lattice-app
```

### <span id="anchor-38"></span>Installing Lattice Manually in Linux

Login/ssh to a Linux machine. (Instruction below assume Ubuntu 12.x or 14.x).

Start by installing the dependencies for Lattice and CF Diego Elastic Runtime.

```bash
sudo apt-get update
sudo apt-get -y install curl gcc make quota
sudo apt-get -y install linux-image-extra-$(uname -r)
sudo apt-get -y install jq lighttpd lighttpd-mod-webdav
sudo apt-get -y install btrfs-tools
```

Create configuration file for Lattice. In the command below replace
`x.x.x.x` with the IP of Linux machine.

**TIP:** *Consul*** which is one of the services installed and started
by Lattice requires the machine to have a private IP address. Make sure
the Linux machine where you are installing as one. When using a Cloud
hosted machine check that the *Private Networking* option (or an
equivalent one) is enabled.

```bash
sudo mkdir -p /var/lattice/setup
sudo tee /var/lattice/setup/lattice-environment >/dev/null &lt;&lt;EOF
LATTICE_USERNAME=lattice
LATTICE_PASSWORD=lattice
CONSUL_SERVER_IP=x.x.x.x
SYSTEM_DOMAIN=x.x.x.x.xip.io
LATTICE_CELL_ID=cell-01
GARDEN_EXTERNAL_IP=x.x.x.x
EOF
```

Download the latest version of lattice. Extract the script file inside
the tarbar **lattice-build/scripts/install-from-tar**, as shown below.

```bash
curl  http://lattice.s3.amazonaws.com/releases/backend/lattice-latest.tgz -o lattice.tgz
tar xzf lattice.tgz --strip-components 2 lattice-build/scripts/install-from-tar
```

Download CF Elastic-Runtime. Extract it to the location specified below.

```bash
wget https://github.com/cloudfoundry/stacks/releases/download/1.6.0/cflinuxfs2-1.6.0.tar.gz --quiet -O cflinuxfs2-1.6.0.tar.gz
sudo mkdir -p /var/lattice-image/rootfs/cflinuxfs2
sudo tar -xzf cflinuxfs2-1.6.0.tar.gz -C /var/lattice-image/rootfs/cflinuxfs2
```

Download CF Elastic-Runtime. Extract it to the location specified below.

```bash
wget https://github.com/cloudfoundry/stacks/releases/download/1.6.0/cflinuxfs2-1.6.0.tar.gz --quiet -O cflinuxfs2-1.6.0.tar.gz
sudo mkdir -p /var/lattice-image/rootfs/cflinuxfs2
sudo tar -xzf cflinuxfs2-1.6.0.tar.gz -C /var/lattice-image/rootfs/cflinuxfs2
```

Download the Lattice bundle and extract the ltc command. Copied it to
**/usr/local/bin**.

```bash
[Linux VM]
https://github.com/cloudfoundry-incubator/lattice/releases/download/v0.5.0/lattice-bundle-v0.5.0-linux.zip
[Mac]
https://github.com/cloudfoundry-incubator/lattice/releases/download/v0.5.0/lattice-bundle-v0.5.0-osx.zip
```

```bash
unzip lattice-bundle-0.5.0.zip ltc
sudo cp ltc /usr/local/bin
```

Test your deployment by targeting the runtime with command **ltc
target**.

```bash
ltc target 45.55.45.155.xip.io
username: lattice
password: lattice
ltc list
ltc create lattice-app cloudfoundry/lattice-app
ltc list
ltc status lattice-app
ltc rm lattice-app
```


