import scala.collection.immutable.ArraySeq

object MainApp {

  def main(args: Array[String]): Unit = {
    println("Starting 'scala-js'...")
    println(ArraySeq("10", "10", "10", "10").map(_.toInt).toString())
    ArraySeq("10", "10", "10", "10").map(_.toInt).foreach(println)
  }
}
