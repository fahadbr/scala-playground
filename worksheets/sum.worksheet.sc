// vim:foldmethod=marker
import scala.collection.mutable.Queue
import java.util.ArrayDeque

object Res {
  // first method {{{ //

  def ways(total: Int, k: Int): Int = {
    println(s"total=$total k=$k")
    if (k == 1) return 1

    val topWays = total / k
    val remainder = total % k

    //val numberOfWaysKCanBeSummed = calcSplit2(k) * topWays
    val numberOfWaysKCanBeSummed = ways(k, k - 1) * topWays

    val numberOfWaysRCanBeSummed = remainder match {
      case 1 => 1
      case 0 => 0
      //case r => calcSplit2(r)
      case r => ways(r, r - 1)
    }

    val res = numberOfWaysRCanBeSummed + numberOfWaysKCanBeSummed + 1
    res.toInt % 1000000007
  }

  def calcSplit2(value: Int): Int = {
    //val x = (1 to value).toSet.subsets().filter(_.sum == value).toStream
    //x.foreach(println)
    //x.length
    value match {
      case 2 => return 1
      case 1 => return 1
      case 0 => return 0
      case _ => {}
    }

    var sums: Int = 0
    var bottom: Int = 1
    var top: Int = value - 1

    while (top > bottom) {
      sums += calcSplit2(top) + calcSplit2(bottom)
      println(s"calcSplit2 value=$value, top=$top, bottom=$bottom, sums=$sums")
      top -= 1
      bottom += 1
    }

    if (top > 1)
      sums += calcSplit2(top)
    println(s"calcSplit2 value=$value, top=$top, sums=$sums")

    sums
  }

  // }}} first method //

  def ways2(total: Int, k: Int): Int = {
    var numways = 1

    var j = k
    while (j > 1) {
      val queue = Queue.fill(total)(1)
      numways += waysWithQueue(queue, j)
      j -= 1
    }

    numways
  }

  def waysWithQueue(queue: Queue[Int], k: Int): Int = {
    var ways = 0
    var sum = 0
    while (sum < k) {
      sum += queue.dequeue()
    }

    ways = 1
    println(queue, sum)

    while (!queue.isEmpty && queue.front != k) {
      queue.enqueue(sum)
      sum = 0
      while (sum < k && !queue.isEmpty && queue.front != k) {
        val head = queue.dequeue()
        sum += head
        if (sum > 1) {
          ways += 1
          println(queue, sum)
        }
      }
    }
    ways
  }
}

import Res._

//ways2(8, 2)
//ways2(8, 3)
//ways2(5, 2)

//ways2(5,4)

//ways(4, 4)
//
//
// 8, 3
//[1, 1, 1, 1, 1, 1, 1, 1]
//[1, 1, 1, 1, 1, 1, 2]
//[1, 1, 1, 1, 2, 2]
//[1, 1, 2, 2, 2]
//[2, 2, 2, 2]
//[1, 1, 1, 1, 1, 3]
//[1, 1, 1, 2, 3]
//[1, 1, 3, 3]
//[2, 3, 3]

// [5]
// [4, 1]
// [3, 1, 1]
// [2, 1, 1, 1]
// [1, 1, 1, 1, 1]
// [3, 2]
// [2, 2, 1]

//ways2(3,3)

def assertEq[A: Numeric](a: A, b: A) = {
  if (a == b) "Success"
  else s"expected $b got $a"
}
def solution(total: Int, k: Int): Int = ways2(total, k)

//problem 000
assertEq(solution(5, 3), 5)
////problem 001
assertEq(solution(4, 2), 3)
//problem 002
assertEq(solution(5, 2), 3)
//problem 003
assertEq(solution(3, 1), 1)
//problem 004
assertEq(solution(2, 1), 1)
//problem 005
assertEq(solution(56, 23), 483076)
//problem 006
assertEq(solution(91, 30), 57521307)
//problem 007
assertEq(solution(82, 38), 20129938)
//problem 008
assertEq(solution(104, 16), 78213911)
//problem 009
assertEq(solution(842, 91), 143119619)
//problem 010
assertEq(solution(230, 73), 558307613)
//problem 011
assertEq(solution(566, 21), 512342767)
//problem 012
assertEq(solution(619, 99), 362103031)
