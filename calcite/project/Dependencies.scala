import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.2"
  lazy val apacheCalcite = "org.apache.calcite" % "calcite-core" % "1.26.0"
  lazy val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % "3.5.+"
  lazy val postgresql = "org.postgresql" % "postgresql" % "9.4-1206-jdbc42"
}
