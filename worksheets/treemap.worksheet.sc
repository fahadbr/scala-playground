import scala.collection.mutable.TreeMap
import scala.math.Ordering.IntOrdering

val nums = Array(3, 2, 1, 5, 6, 2, 8, 0, -1)


implicit val maxOrder = new IntOrdering {
  override def compare(x: Int, y: Int): Int =
    super.compare(nums(y), nums(x))
}

val maxmap = new TreeMap[Int, Int]
maxmap ++= nums.take(3)
  .zipWithIndex
  .map { case (a, b) => (b, a) }


var output = Array.fill(nums.size - 3)(0)

for (idx <- 3 to nums.size - 1) {
  maxmap.update(idx, nums(idx))
  if (maxmap.size > 3) {
    maxmap.remove(idx - 3)
  }
  output.update(idx-3, maxmap.head._2)
}

output
