import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.TreeMap
import scala.math.Ordering.IntOrdering

object Solution {

  def maxWindow(nums: Array[Int], k: Int): Array[Int] = {
    implicit val maxOrder = new IntOrdering {
      override def compare(x: Int, y: Int): Int =
        super.compare(nums(y), nums(x)) match {
          case 0 => super.compare(y, x)
          case x => x
        }
    }

    val maxmap = new TreeMap[Int, Int]

    val output = Array.fill(nums.size - k + 1)(0)
    for ((num, idx) <- nums.zipWithIndex) {
      maxmap.update(idx, num)
      if (maxmap.size > k) {
        maxmap.remove(idx - k)
      }
      if (maxmap.size == k) {
        output.update(idx - k + 1, maxmap.head._2)
      }
    }

    output

  }

  def lengthOfLIS(nums: Array[Int]): Int = {
    val dp = Array.fill(nums.size)(1)

    for ((num, idx) <- nums.zipWithIndex.reverse) {
      for (j <- idx to nums.size - 1) {
        if (nums(j) > num) {
          dp.update(idx, Math.max(dp(idx), dp(j) + 1))
        }
      }
    }

    dp.max
  }

  def getLargestString(s: String, k: Int): String = {
    val charCount = Array.fill(26)(0)
    for (c <- s) {
      val idx = c - 'a'
      charCount.update(idx, charCount(idx) + 1)
    }

    val output = ArrayBuffer.empty[Char]
    for {
      cidx <- (0 to 25).reverse
      if charCount(cidx) != 0
    } {

      val c = (cidx + 'a').toChar
      var count = 0

      while (charCount(cidx) > 0) {
        val nextChar = if (count < k) {
          charCount(cidx) -= 1
          count += 1
          c
        } else {
          (0 to cidx - 1).reverse.filter(charCount(_) != 0).headOption match {
            case Some(idx) =>
              charCount(idx) -= 1
              count = 0
              (idx + 'a').toChar
            case None => return output.mkString
          }
        }

        output += nextChar
      }
    }

    output.mkString
  }

}
Solution.maxWindow(Array(1, 3, 1, 2, 0, 5), 3)
Solution.maxWindow(Array(1, 3, 1, 2, 0, 5), 4)

Solution.lengthOfLIS(Array(10, 9, 2, 5, 3, 7, 101, 18))

Solution.getLargestString("abczzzzz", 2)
Solution.getLargestString("zzzazz", 2)
Solution.getLargestString("axxzzx", 2)
