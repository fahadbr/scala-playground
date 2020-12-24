package example

import java.sql.DriverManager
import java.util.Properties
import java.{util => ju}

import scala.jdk.CollectionConverters._

import org.apache.calcite.adapter.java.ReflectiveSchema
import org.apache.calcite.jdbc.CalciteConnection
import org.apache.calcite.rel.`type`.RelDataType
import org.apache.calcite.rel.`type`.RelDataTypeFactory
import org.apache.calcite.rel.`type`.RelProtoDataType
import org.apache.calcite.schema.Schema
import org.apache.calcite.schema.SchemaFactory
import org.apache.calcite.schema.SchemaPlus
import org.apache.calcite.schema.Table
import org.apache.calcite.schema.impl.AbstractSchema
import org.apache.calcite.schema.impl.AbstractTable


object Hello extends Greeting with App {
  println(greeting)
}

trait Greeting {
  lazy val greeting: String = "hello"
}

object Example {
  //case class Employee(empid: Int, deptno: Int)
  //case class Department(deptno: Int)
  //case class HrSchema() {
  ////private[this] var emps: Array[Employee] = Array.empty
  ////private[this] var depts: Array[Department] = Array.empty
  //var emps: Array[Employee] = Array.empty
  //var depts: Array[Department] = Array.empty
  //}

  def run() = {
    Class.forName("org.apache.calcite.jdbc.Driver")
    val info = new Properties()
    info.setProperty("lex", "JAVA")

    val conn = DriverManager.getConnection("jdbc:calcite:", info)
    val calciteConn = conn.unwrap(classOf[CalciteConnection])
    val rootSchema = calciteConn.getRootSchema()
    val schema = new ReflectiveSchema(new Types.HrSchema())
    rootSchema.add("hr", schema)

    val statement = calciteConn.createStatement()
    //val resultSet = statement.executeQuery("""
    //select d.deptno, min(e.empid)
    //from hr.emps as e
    //join hr.depts as d
    //on e.deptno = d.deptno
    //group by d.deptno
    //having count(*) > 0
    //""")

    val resultSet = statement.executeQuery("""
        select * from hr.emps""")
    println(resultSet.toString())
    resultSet.close()
    statement.close()
    conn.close()

  }
}
