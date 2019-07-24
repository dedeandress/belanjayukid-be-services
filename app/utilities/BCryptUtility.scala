package util
import org.mindrot.jbcrypt.BCrypt

object BCryptUtility {
  def check(password: String, dbPassword : String): Boolean = {
    BCrypt.checkpw(password, dbPassword)
  }

  def hashPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }
}
