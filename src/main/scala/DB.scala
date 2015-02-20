import java.sql.DriverManager

// CREATE DATABASE IF NOT EXISTS `gelp` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
object DB {
  case class Table(value: String)
  case class Column(value: String)

  Class.forName("com.mysql.jdbc.Driver")

  def getNewConnection = DriverManager.getConnection("jdbc:mysql://localhost/gelp", "root", "root")

  def insertInto(table: Table, fields: List[(Column, Any)]) = {
    val conn = getNewConnection
    try {
      val columnsTuple = fields.map(_._1.value).mkString("(", ", ", ")")
      val valuesTuple = fields.map(_ => "?").mkString("(", ", ", ")")
      val unity = List.fill(2) { fields.head._1.value }.mkString(" = ")

      val statement = s"INSERT INTO ${table.value} $columnsTuple VALUES $valuesTuple ON DUPLICATE KEY UPDATE $unity"

      val prep = conn.prepareStatement(statement)
      fields.map(_._2).zipWithIndex.foreach({ case (v, i) => v match {
        case s: String => prep.setString(i + 1, s)
        case d: Double => prep.setDouble(i + 1, d)
        case b: BigInt => prep.setLong(i + 1, b.toLong)
      }})

      println(s"Executing: $statement; ${fields.map(_._2).mkString("(", ",", ")")}")
      prep.executeUpdate
    }
    finally {
      conn.close()
    }
  }
}
