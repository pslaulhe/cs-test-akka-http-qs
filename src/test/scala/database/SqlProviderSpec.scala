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

    "get query" in {
      val sqlProvider = SqlProvider()
      val result = sqlProvider.get(connection => {
        connection.prepareStatement("SELECT 1").executeQuery()
      })(rs => {
        rs.getInt(1)
      })
      result.hasNext should be(true)
      result.next() should be(1)
      result.hasNext should be(false)
    }
  }
}
