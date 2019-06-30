# Belanjayuk.id Backend api services

System Requirements:
* Scala 2.12.8
* Play Framework 2.7.2
* Sangria GraphQL 1.4.2
* Slick 3.3.0

Project Structure :
* app:
    * controllers
        * `AppController` (for graphql endpoint and parse json)
    * errors
        * `AlreadyExist` (for handling error when something already exist like username or something unique)
        * `AmbigousResult` (for handling error when query or something have a ambigous)
        * `NotFound` (for handling error when search something like name and result is nothing)
    * graphql
        * resolvers (resolver berisi method-method yang bisa dipanggil saat request, kegunaan resolver untuk meresolve query atau mutation contohnya untuk memanggil repository tetapi seharusnya memanggil services karna untuk contoh jadi memanggil repository)
            * `RoleResolver`
            * `UserResolver`
        * schemas (schemas berisi struktur dari method-method yang bisa dipanggil disini yang akan memparse query or mutation dan memanggil resolver yang dibutuhkan. schemas juga berguna untuk men-generate struktur seperti apa yang bisa dipanggil saat request)
            * `RoleSchema`
            * `UserSchema`
        * `GraphQL` (adalah root yang berisi schema-schema yang dapat digunakan saat request. `GraphQL` dipanggil juga di `AppController`)
    * models
        * `Role` (berisi representasi dari Table database dan di class `Role` juga berisi object dari `RoleTable` yang digunakan untuk Slick)
        * `User` (berisi representasi dari Table database dan di class `User` juga berisi object dari `UserTable` yang digunakan untuk Slick)
    * modules
        * `DbModules` (berisi configurasi database seperti profile, driver, dan pengaturan lainnya)
        * `RoleModules` (berisi configurasi untuk menselaraskan RoleRepository dan RoleRepositoryImpl agar dapat digunakan)
        * `UserModules` (berisi configurasi untuk menselaraskan UserRepository dan UserRepositoryImpl agar dapat digunakan)
    * repositories
        * `RoleRepository` (is a interface or in Scala known as **Trait** for CRUD for Role)
        * `RoleRepositoryImpl` (is a implementation for RoleRepository)
        * `UserRepository` (is a interface or in Scala known as **Trait** for CRUD for User)
        * `UserRepositoryImpl` (is a implementation for UserRepository)
    * services
    * utilities
    * views
        * `graphiql.scala.html` (is ui for graphql but is not a mandatory because you can request a api from Postman or anything that can support GraphQL)
    
* conf
    * evolution
        * default
            * 1.sql (for generate database. notes: actually it can auto generate but I don't know how it can work hehe :') )
    * application.conf (notes for implement the modules you must write configuration like play.modules.enabled += "modules.RoleModule" and play.modules.enabled += "modules.DBModule". if you not write that you can have a error message like "CreationException: Unable to create injector, see the following errors:" and a message no implementation for repositories blablabla)
    * logback.xml (for setup logger)
    * routes
* build.sbt (all dependencies are written here) 


#### To Do List for write GraphQL Code
1. write a model and define a table for slick
2. write a repository like crud for your model (trait of repository and repository implementation)
3. write modules for repository and include DbModules for DB Configuraion
4. write configuration for modules in application.conf
5. write resolver for resolve a query or mutation from request (1 resolver 1 models)
6. write schema (1 schema 1 model)
7. write `GraphQL` for combine all schemas query and mutation
8. write controllers (only single endpoint for graphql but if you use graphiql you must add one endpoint for graphiql)
9. add a route for graphql (POST endpoint and if you use graphiql you must add one root)
10. and 
```scala
while(noSuccess) {
    tryAgain()
    if(dead) break
}
```

> pardon my english. - DA99 -
