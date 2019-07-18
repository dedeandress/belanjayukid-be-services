package modules

import com.google.inject.{AbstractModule, Provides}
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcBackend
import slick.jdbc.PostgresProfile

/**
  * A trait that declares an interface for a database access object that should be injected by the DI.
  */
private[modules] trait Database {

  def config: DatabaseConfig[PostgresProfile]

  def db: JdbcBackend#DatabaseDef

  def profile: PostgresProfile
}

trait AppDatabase extends Database

class DBModule extends AbstractModule {

  override def configure(): Unit = ()

  /**
    * Provides a database access object.
    *
    * @param dbConfigProvider provides db configs for the default db
    */
  @Provides
  def provideDatabase (dbConfigProvider: DatabaseConfigProvider): AppDatabase = new AppDatabase {

    override val config = dbConfigProvider.get[PostgresProfile]

    override def db = config.db

    override def profile = config.profile
  }
}
