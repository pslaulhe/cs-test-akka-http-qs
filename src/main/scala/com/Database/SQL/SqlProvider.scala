package Database.SQL

import java.sql._
import scala.util.{Failure, Success, Try}

case class SqlProvider(){
  private val connectionName = "jdbc:postgresql://127.0.0.1:5432/postgres"
  private val dbName = "postgres"
  private val dbPassword = "postgres"

  /**
   * Executes a command on the database
   * @param command the command to execute
   * TODO: Rollback transaction on failure?
   */
  def executeCommand(command: Connection => Unit): Unit = {
    val conn: Connection = this.getNewConnection

    Try(command(conn)) match {
      case Failure(ex) =>
        closeConnection(conn)
        throw ex
      case Success(_) =>
    }
  }

  private def getNewConnection: Connection = {
    DriverManager.getConnection(connectionName, dbName, dbPassword)
  }

  private def closeConnection(connection: Connection): Unit = {
    connection.close()
  }
}