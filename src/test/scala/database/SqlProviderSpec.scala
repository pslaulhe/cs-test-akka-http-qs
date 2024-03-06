package database

import Database.SQL.SqlProvider
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SqlProviderSpec extends AnyWordSpec with Matchers {
  "SqlProvider" should {
    "execute a command" in {
      val sqlProvider = SqlProvider()
      sqlProvider.executeCommand(connection => {
        connection.prepareStatement("SELECT 1").executeQuery()
      }) should be(())
    }
  }
}
