import scala.collection.mutable.TreeMap
import scala.math.Ordering.IntOrdering

object MaxWindow {

  def maxWindow(nums: Array[Int], k: Int): Array[Int] = {
    implicit val maxOrder = new IntOrdering {
      override def compare(x: Int, y: Int): Int =
        super.compare(nums(y), nums(x)) match {
          case 0 => super.compare(y, x)
          case x => x
        }
    }

    val maxmap = new TreeMap[Int, Int]

    val output = Array.fill(nums.size - k+1)(0)
    for ((num, idx) <- nums.zipWithIndex) {
      maxmap.update(idx, num)
      if (maxmap.size > k) {
        maxmap.remove(idx - k)
      }
      if (maxmap.size == k) {
        output.update(idx - k+1, maxmap.head._2)
      }
    }

    output

  }

}
MaxWindow.maxWindow(Array(1, 3, 1, 2, 0, 5), 3)
MaxWindow.maxWindow(Array(1, 3, 1, 2, 0, 5), 4)

val x = Array(1,2,3,4)
