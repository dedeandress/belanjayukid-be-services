package services

import com.google.inject.Inject
import graphql.resolvers.UserResolver
import java.util.UUID

import models.User

class UserService @Inject()(userResolver: UserResolver){

  def registerUser(username: String, password: String ,email: String, userProfileId: UUID) ={
    userResolver.addUser(new User(username = username, password = password, email = email, userProfileId = userProfileId))
  }

}
