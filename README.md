# Angular Jooby Starter

This starter repo serves as an Angular starter for anyone looking to get up and running with Angular and TypeScript and a Java server fast. 
I'm using [jooby](https://github.com/jooby-project/jooby) on the server side as it provides amenities like hot reload and [swagger](https://swagger.io/) integration. Especially the hot reload feature is essential for coding an angular application.

The client part of the project was initially generated by [Angular CLI](https://github.com/angular/angular-cli). The files generated by this tool were changed as little as possible. Thus it is possible to continue to work with Angular CLI and to replace them easily by newer versions.

## Target audience

You are an experienced java developer with at least some experience in the development of web applications with java. Then you heard of that shiny little thing called Angular. You sacrificed your free time to get to explore this new universe and get a basic understanding of it. But you still like your java backends. You feel no need to replace it with a Node JS based solution. Your are looking for something cool and smooth. Thats when you discovered jooby. Now it is the perfect moment to make profit out of this starter. It should help you personally to create small web applications fast with angular and jooby for fun and experience. And to convince your boss of this new approach. It is not intended as a starting point for big commercial web applications.


## Design decisions

- I'm using [PrimeNG](https://www.primefaces.org/primeng/#/) as UI lib for a reason. I'm exploring ways for the corporate java web developer who is used to work with JSF. While lower-level libs like Bootstrap are fine if you are a frontend developer, most corporate java developers prefer higher level abstractions as provided by PrimeNG. PrimeFaces is nowadays the dominant implementation of the JSF standard so the use of PrimeNG eases the comparability of both approaches.
- The angular project is a maven module as well. Only a simple maven POM file has been added to the usual angular files. The angular client will then be generated in production mode as a set of files located in the *dist/client* folder. These files are then packaged into a jar file that is the artifact of that module. The server reads these files via *class.getResourceAsStream* from the classpath. This is, what the Jooby module *AngularClient* does. There are other ways to get the client and the server together. 
  - The client could be part of the server module, providing the client files as maven resources. I had a feeling that this setup confuses IDEs. I also have the feeling that the client should not be a subordinate of the server, they should be partner :)
  - Jooby is already able to serve static assets located in the file system. To use this feature, i also had to place the client within the server module. Secondly prefer to have the client files in a cozy, versioned jar file as opposed to the cold and windy filesystem, threatened by unforeseen modifications.
  - It is quite common to use a proxy server like Apache HTTPD to serve the static files and route the other requests to a backend server. There is nothing wrong with this approach. I'm just aiming at an target audience that wants to evaluate Angular and therefore prefers the setup to be as simple as possible.
- I'm using https://github.com/eirslett/frontend-maven-plugin now to better integrate the nodejs world with maven.
- BYOPT a.k.a Bring Your Own Persistence Technology. The starter works without database in order to focus on the core integration aspects. Further experiments (Kotlin, Database, ...) will happen in cloned repos.

## Quick start

During development two servers are running in parallel. One is the jooby server on port 8080, the second one is the Angular development server on port 4200. Requests from the Angular client to the backend server are first send to the Angular server which forwards them to the jooby server, as defined in the *proxy.conf.json* file.

**Prerequisites** A JDK 8 and maven installed.

**Preparation** Checkout the starter project with `git clone https://github.com/protubero/angular-jooby-starter.git`

**Start Jooby Server** Open a terminal and change directory to the *server* sub folder. Then execute `mvn jooby:run`. The jooby server starts on port 8080. When opening http://localhost:8080/api/persons, a json structure with some person data should show up. All available URLs are listed on the console when the server starts up. 

**Start Angular Development Server** Open a second terminal and change directory to the *client* sub folder. To start the angular development server, execute `mvn compile -P dev`. To stop the server, it might be necessary to close the terminal window.

Now you have the development setup up and running. Open http://localhost:4200 in your browser to load the application.


The frontent-maven-plugin does all the necessary interaction with node/npm. It downloads nodejs locally into the */client/node* folder and handles the dependency resolution automatically. If you want to drive by yourself: 

1. Make sure you have a Node and NPM, the node package manager, installed on your system. Minimum version of Node is 4, minimum version of NPM is 3. It is sufficient to install Node on your system since NPM is installed together with Node.
1. Install [Angular CLI](https://github.com/angular/angular-cli)
1. Before we can start the Angular development server we have to resolve the dependencies by executing `npm install` at the client folder. NPM is looking for the modules defined in the *package.json* file and resolves all dependencies. This could take some time. A folder *node_modules* will be created which contains the JS libaries used, this is the .m2 in the JS world.
1. To start the client server, execute `npm start` in the second terminal. 

Recommended IDE: https://www.genuitec.com/products/angular-ide/


## Build distribution  

When you are happy with your work you will probably install it on a server or forward it to a friend. What we want is the application packaged e.g. in a ZIP file. After unpacking it into the file system, it should be sufficient to click on a start shell script or .bat file. The only precondition should be an installed JRE.

To create such an distribution package, simply execute `mvn clean package` in the root folder of the project. The maven build process first executes `mvn clean package` on the client module. The artifact of the client module is a jar containing all html/js/css files which make up the angular application.

The distribution files can be found in the */distribution/target* folder.

## Environment-independent Build

Ideally maven builds should be independent from their build environment, apart from deliberate dependencies. Building the distribution should work out-of-the-box, e.g. on a jenkins build server. The inclusion of frontend technology makes this difficult to achieve. The frontend-maven-plugin helps by handling the node/npm issues in the background. To check, if the build is really independent, i've added a simple dockerfile, deriving from the maven-3.5 docker image. Build the image by executing `docker build . --tag ajs` in the project root folder. Then execute it with `docker run -ti --rm -p 8080:8080 -p 4200:4200 ajs`. A shell opens where you can test the build in an isolated environment. You're working on a copy of the project files. Updated files will not be visible within the container!



	




	
