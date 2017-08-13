# Angular Jooby Starter

This starter repo serves as an Angular starter for anyone looking to get up and running with Angular and TypeScript and a Java server fast. 
I'm using [jooby](https://github.com/jooby-project/jooby) on the server side as it provides amenities like hot reload and [swagger](https://swagger.io/) integration. Especially the hot reload feature is essential for coding an angular application.

The client part of the project is based on the foundations of the [Angular CLI](https://github.com/angular/angular-cli) project. The files generated by this tool are changed as little as possible. Thus it is possible to replace them easily by newer versions.

## Design decisions

- I'm using [PrimeNG](https://www.primefaces.org/primeng/#/) as UI lib for a reason. I'm exploring ways for the corporate java web developer who is used to work with JSF. While lower-level libs like Bootstrap are fine if you are a frontend developer, most corporate java developers prefer higher level abstractions as provided by PrimeNG. PrimeFaces is nowadays the dominant implementation of the JSF standard so the use of PrimeNG eases the comparability of both approaches.
- The angular project is a maven module as well. Only a simple maven POM file has been added to the usual angular files. The angular client will then be generated in production mode as a set of files located in the *dist/client* folder. These files are then packaged into a jar file that is the artifact of that module. The server reads these files via *class.getResourceAsStream* from the classpath. This is, what the Jooby module *AngularClient* does. There are other ways to get the client and the server together. 
  - The client could be part of the server module, providing the client files as maven resources. I had a feeling that this setup confuses IDEs. I also have the feeling that the client should not be a subordinate of the server, they should be partner :)
  - Jooby is already able to serve static assets located in the file system. To use this feature, i also had to place the client within the server module. Secondly prefer to have the client files in a cozy, versioned jar file as opposed to the cold and windy filesystem, threatened by unforeseen modifications.
  - It is quite common to use a proxy server like Apache HTTPD to serve the static files and route the other requests to a backend server. There is nothing wrong with this approach. I'm just aiming at an target audience that wants to evaluate Angular an therefore prefers the setup to be as simple as possible.
- I'm fully aware that i broke the build system rules - for now. This build depends on the environment, i.e. on the installed node/npm/cli. I could have used https://github.com/eirslett/frontend-maven-plugin, but i opted against it. I don't think that this could create problems for the intended use but needs re-evaluation if one creates a production-grade application.

## Quick start

During development two servers are running in parallel. One is the jooby server on port 8080, the second one is the Angular development server on port 4200. Requests from the Angular client to the backend server are first send to the Angular server which forwards them to the jooby server, as defined in the *proxy.conf.json* file.

To start both server, execute all of the following steps:

1. Checkout the starter project with `git clone https://github.com/protubero/angular-jooby-starter.git`
1. Make sure you have a Node and NPM, the node package manager, installed on your system. Minimum version of Node is 4, minimum version of NPM is 3. It is sufficient to install Node on your system since NPM is installed together with Node.   
1. Install [Angular CLI](https://github.com/angular/angular-cli)
1. You should have a running maven installation on your system
1. To start the java server, execute `mvn jooby:run` at the *server* sub folder. The jooby server starts on port 8080. When opening (http://localhost:8080/api/persons), a json structure with some person data should show up. All available URLs are listed on the console when the server starts up. 
1. To retrieve the dependenies of the client, execute `npm update` (or `npm install`) at the *client* folder. NPM is looking for the modules defined in the *package.json* file and resolves all dependencies. This could take some time. A folder *node_modules* will be created which contains the JS libaries used, this is the .m2 in the JS world.
1. To start the client, execute `npm start` at the *client* folder. 

Now you have the development setup up and running. Open (http://localhost:4200) in your browser to load the application.

Recommended IDE: https://www.genuitec.com/products/angular-ide/
 

## Build distribution  
 
The project is structured as a multi-module maven project. Beside the server and client module there is a third module named distribution. 
It contains a maven assembly descriptor that packs together the server and client into one zip file and a tarball. To build the distribution, execute `mvn package` in the root folder. The server is accessible via (http://localhost:8080).

	




	
