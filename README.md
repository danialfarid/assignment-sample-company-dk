#Assignment: Companies CRUD 

A sample app for CRUD operations on companies and their owners. Backend is implemented using Java and front-end is implemented with Angular.js.
The app is deployed on [Heroku](https://dashboard.heroku.com/) and can be accessed here: [https://calm-meadow-37274.herokuapp.com/](https://calm-meadow-37274.herokuapp.com/)

## Backend API

* GET /company (Get all companies)

```
curl -XGET https://calm-meadow-37274.herokuapp.com/company
```
* POST /company (Create a company)

```
 curl -XPOST -d '{"name": "Company 1", "address": "Address 1", "city": "Toronto", "country":"Canada", "owners":[{"name": "Danial"}]}' https://calm-meadow-37274.herokuapp.com/company
```
* PUT /company/:id (Update a company)

```
 curl -XPUT -d '{"id": 1, "name": "Company 1", "address": "Address 1", "city": "Toronto", "country":"Canada", "owners":[{"name": "Danial"}]}' https://calm-meadow-37274.herokuapp.com/company/1
```
* DELETE /company/:id (Delete a company)

```
 curl -XDELETE https://calm-meadow-37274.herokuapp.com/company/1
```

* POST /company/:id/owner (Add an owner to the company)

```
 curl -XPOST -d '{"name": "Danial Farid"}' https://calm-meadow-37274.herokuapp.com/company/1/owner
```
* DELETE /company/:id/owner/:ownerId (Remove a company's owner) 

```
 curl -XDELETE https://calm-meadow-37274.herokuapp.com/company/1/owner/1
```

## For developers
### Backend
Backend is implemented with JavaSE and maven as build tool. The app is deployed on [Heroku](https://dashboard.heroku.com/) and is connecting to the embeded Postgresql relational database on Heroku.
The app is using [Maven3[(https://maven.apache.org/download.cgi) as build tool and [Spark java REST framework](http://sparkjava.com/) to expose its REST api.

```!sh
$ mvn clean package
```
Once you have commited the changes to git, you can run `git push heroku master` to deploy the changes on heroku server.
Run `heroku open` to open the app in the browser.
You can also run `heroku local web` to run the app locally, but you need to make sure the postgresql access is granted for your local machine.

### Front-end
Front-end is scaffolded using Yeoman, Angular.js and Grunt. To build the front-end run
```sh
$ cd web
$ grunt build 
```
This will copy the dist folder into the backend's `resources/public` folder which then will be served by Spark as static resources.
You can run `grunt serve`` to debug/develop the app.
