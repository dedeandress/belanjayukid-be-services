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
        * input
            * `StaffInput`
            * `UserInput`
            * `UserProfileInput`
            * `ProductInput`
            * `ProductDetailInput`
        * resolvers (resolver berisi method-method yang bisa dipanggil saat request, kegunaan resolver untuk meresolve query atau mutation contohnya untuk memanggil repository tetapi seharusnya memanggil services karna untuk contoh jadi memanggil repository)
            * `RoleResolver`
            * `UserResolver`
            * `UserProfileResolver`
            * `CategoryResolver`
            * `ProductResolver`
            * `ProductDetailResolver`
            * `ProductStockResolver`
        * schemas (schemas berisi struktur dari method-method yang bisa dipanggil disini yang akan memparse query or mutation dan memanggil resolver yang dibutuhkan. schemas juga berguna untuk men-generate struktur seperti apa yang bisa dipanggil saat request)
            * `SchemaDefinition`
        * `GraphQL` (adalah root yang berisi schema-schema yang dapat digunakan saat request. `GraphQL` dipanggil juga di `AppController`)
        * `GraphQLType` (adalah kumpulan type yang bisa digunakan sebagai type atau input type dari graphql)
    * models
        * `Role` (berisi representasi dari Table database dan di class `Role` juga berisi object dari `RoleTable` yang digunakan untuk Slick)
        * `User` (berisi representasi dari Table database dan di class `User` juga berisi object dari `UserTable` yang digunakan untuk Slick)
        * `Category` (berisi representasi dari Table database dan di class `Category` juga berisi object dari `CategoryTable` yang digunakan untuk Slick)
        * `Customer` (berisi representasi dari Table database dan di class `Customer` juga berisi object dari `Customer` yang digunakan untuk Slick)
        * `ProductDetail` (berisi representasi dari Table database dan di class `ProductDetail` juga berisi object dari `ProductDetailTable` yang digunakan untuk Slick)
        * `Products` (berisi representasi dari Table database dan di class `Products` juga berisi object dari `ProductsTable` yang digunakan untuk Slick)
        * `ProductStock` (berisi representasi dari Table database dan di class `ProductStock` juga berisi object dari `ProductStockTable` yang digunakan untuk Slick)
        * `Sessions` (berisi representasi dari Table database dan di class `Session` juga berisi object dari `SessionTable` yang digunakan untuk Slick)
        * `Shipment` (berisi representasi dari Table database dan di class `Shipment` juga berisi object dari `UserTable` yang digunakan untuk Slick)
        * `Staff` (berisi representasi dari Table database dan di class `Staff` juga berisi object dari `StaffTable` yang digunakan untuk Slick)
        * `Store` (berisi representasi dari Table database dan di class `Store` juga berisi object dari `StoreTable` yang digunakan untuk Slick)
        * `Supplier` (berisi representasi dari Table database dan di class `Supplier` juga berisi object dari `SupplierTable` yang digunakan untuk Slick)
        * `Transaction` (berisi representasi dari Table database dan di class `Trasaction` juga berisi object dari `TrasactionTable` yang digunakan untuk Slick)
        * `TransactionDetail` (berisi representasi dari Table database dan di class `TrasactionDetail` juga berisi object dari `TrasactionDetailTable` yang digunakan untuk Slick)
        * `UserProfile` (berisi representasi dari Table database dan di class `UserProfile` juga berisi object dari `UserProfileTable` yang digunakan untuk Slick)
    * modules
        * `DbModules` (berisi configurasi database seperti profile, driver, dan pengaturan lainnya)
        * `RoleModules` (berisi configurasi untuk menselaraskan RoleRepository dan RoleRepositoryImpl agar dapat digunakan)
        * `CategoryModules` (berisi configurasi untuk menselaraskan CategoryRepository dan CategoryRepositoryImpl agar dapat digunakan)
        * `StaffModules` (berisi configurasi untuk menselaraskan StaffRepository dan StaffRepositoryImpl agar dapat digunakan)
        * `UserModules` (berisi configurasi untuk menselaraskan UserRepository dan UserRepositoryImpl agar dapat digunakan)
        * `UserProfileModules` (berisi configurasi untuk menselaraskan UserProfileRepository dan UserProfileRepositoryImpl agar dapat digunakan)
        * `ProductModules` (berisi configurasi untuk menselaraskan ProductRepository dan ProductRepositoryImpl agar dapat digunakan)
        * `ProductDetailModules` (berisi configurasi untuk menselaraskan ProductDetailRepository dan ProductDetailRepositoryImpl agar dapat digunakan)
        * `ProductStockModules` (berisi configurasi untuk menselaraskan ProductStockRepository dan ProductStockRepositoryImpl agar dapat digunakan)
    * repositories
        *  repositoryInterfaces
            * `CategoryRepository` (is a interface or in Scala known as **Trait** for CRUD for Category)
            * `ProductsRepository` (is a interface or in Scala known as **Trait** for CRUD for Products)
            * `ProductDetailRepository` (is a interface or in Scala known as **Trait** for CRUD for ProductDetail)
            * `ProductStockRepository` (is a interface or in Scala known as **Trait** for CRUD for ProductStock)
            * `RoleRepository` (is a interface or in Scala known as **Trait** for CRUD for Role)
            * `UserRepository` (is a interface or in Scala known as **Trait** for CRUD for User)
            * `StaffRepository` (is a interface or in Scala known as **Trait** for CRUD for Staff)
            * `UserProfileRepository` (is a interface or in Scala known as **Trait** for CRUD for UserProfile)
        * `RoleRepositoryImpl` (is a implementation for RoleRepository)
        * `UserRepositoryImpl` (is a implementation for UserRepository)
        * `CategoryRepositoryImpl` (is a implementation for UserRepository)
        * `ProductsRepositoryImpl` (is a implementation for ProductsRepository)
        * `ProductDetailRepositoryImpl` (is a implementation for ProductDetailRepository)
        * `ProductStockRepositoryImpl` (is a implementation for ProductStockRepository)
        * `StaffRepositoryImpl` (is a implementation for StaffRepository)
        * `UserProfileRepositoryImpl` (is a implementation for UserProfileRepository)
    * services
        * `CategoryService`(is a service for manage category like a business logic in here)
        * `ProductDetailService`
        * `ProductService`
        * `ProductStockService`
        * `StaffService`
        * `UserService`
    * utilities
        * `AuthContext` (is a utility for authentication)
        * `BCryptUtility` (is a utility for BCrypt)
        * `BelanjaYukConstant` (is a utility for constant variable)
        * `CustomScalar` (is a utility for scalar graphql)
        * `QueryUtility` (is a utility for TableQuery variable)
        * `SchemaGenerator` (is a utility for generate DDL)
    * views
        * `graphiql.scala.html` (is ui for graphql but is not a mandatory because you can request a api from Postman or anything that can support GraphQL)  
* conf
    * evolution
        * default
            * `1.sql` (for generate database. is auto generate where /schema endpoint was called)
    * `application.conf` (notes for implement the modules you must write configuration like play.modules.enabled += "modules.RoleModule" and play.modules.enabled += "modules.DBModule". if you not write that you can have a error message like "CreationException: Unable to create injector, see the following errors:" and a message no implementation for repositories blablabla)
    * `logback.xml` (for setup logger)
    * `routes`
* `build.sbt` (all dependencies are written here) 


#### To Do List for write GraphQL Code
1. write a model and define a table for slick
2. write a repository like crud for your model (trait of repository and repository implementation)
3. write modules for repository and include DbModules for DB Configuraion
4. write configuration for modules in application.conf
5. write service (1 service 1 model and call repository)
6. write graphql type or input type in `GraphQLType` and write Input type in package graphql.input
7. write resolver for resolve a query or mutation from request (1 resolver 1 models and called service)
8. write schema definition in `SchemaDefinition`
9. write `GraphQL` for combine all schemas query and mutation
10. write controllers (only single endpoint for graphql but if you use graphiql you must add one endpoint for graphiql)
11. add a route for graphql (POST endpoint and if you use graphiql you must add one root)
12. and 
```scala
while(noSuccess) {
    tryAgain()
    if(dead) break
}
```
## GraphQL Schema
```graphql

# for `dateOfBirth` argument, please use full ISO 8601 "yyyy-MM-dd'T'HH:mm:ssXXX" format but insert with epoch (LongType)

scalar BigDecimal

type Category{
    id: ID
    name: String
}

type Product{
    id: ID
    SKU: String
    name: String
    stock: Int
    category: Category
    productDetail: [ProductDetail]
}

type ProductDetail{
    id: ID
    productStock: ProductStock
    price: BigDecimal
    value: Int
    product: Product
}

type ProductStock{
    id: ID
    name: String
}

type UserProfile{
    id: ID
    fullName: String
    address: String
    phoneNumber: String
    noNik: String
    dateOfBirth: Long
}

type User{
    id: ID
    username: String
    password: String
    email: String
}

type Staff {
    id: ID
    user: User
    role: Role
}

input UserProfileInput {
    fullname: String
    phoneNumber: String
    address: String
    noNik: String
    dateOfBirth: Long
}

input UserInput {
    username: String
    password: String
    email: String
}

input ProductDetailInput{
    productStockId: ID
    sellingPrice: BigDecimal
    purchasePrice: BigDecimal
    value: Int
}

input ProductInput {
    SKU: String
    name: String
    categoryId: ID
    productDetailInput: [ProductDetailInput]
}

input StaffInput {
    userInput: UserInput !
    userProfileInput: UserProfileInput !
    roleName: String!
}

mutation addStaff($input: StaffInput!){
    addStaff(staff: $input){
        id
        user{
          username
          password
          email
          userProfile{
            address
            fullName
            dateOfBirth
            noNik
            phoneNumber
          }
        }
        role{
          name
        }
    }
}

mutation addProduct($input: ProductInput!){
    addProduct(product: $input){
        id
        name
        SKU
        stock
        category{
            id
            name
        }
        productDetail{
            purchasePrice
            sellingPrice
            productStock{
                id
                name
            }
            value
        }
    }
}

```
> pardon my english. - DA99 -