import java.sql.{Connection, ResultSet, DriverManager}

import com.clinkle.sql.{Executor, Node}

object DBRunner {
  Class.forName("com.mysql.jdbc.Driver")

  def getNewConnection = DriverManager.getConnection("jdbc:mysql://localhost/gelp", "root", "root")

  def runInNewTransaction[T](f: (Executor) => T): T = {
    import ConnStateExecutor._
    val conn = getNewConnection

    try {
      f(makeConnStateExecutor(conn))
    }
    finally {
      conn.close()
    }
  }

  private object ConnStateExecutor {
    private def logQuery(q: Node): Unit = println(s"${ q.sql }, ${ q.getParams }.")

    def makeConnStateExecutor(implicit conn: Connection): Executor = new Executor {
      private val connectionExecutor = Executor.ConnectionExecutor(conn)

      override def executeQuery(query: Node): ResultSet = {
        logQuery(query)
        connectionExecutor.executeQuery(query)
      }

      override def executeUpdate(query: Node): Int = {
        logQuery(query)
        connectionExecutor.executeUpdate(query)
      }

      override def executeKeys(query: Node): ResultSet = {
        logQuery(query)
        connectionExecutor.executeKeys(query)
      }
    }
  }
}

