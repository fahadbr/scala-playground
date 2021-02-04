object Res {
  def ways(total: Int, k: Int): Int = {
    println(s"total=$total k=$k")
    if (k == 1)
      return 1

    val topWays = total / k
    val remainder = total % k

    val numberOfWaysKCanBeSummed = ways(k, k - 1) * topWays

    val numberOfWaysRCanBeSummed = remainder match {
      case 1 => 1
      case 0 => 0
      case r => ways(r, r)
    }

    val res = numberOfWaysRCanBeSummed + numberOfWaysKCanBeSummed + 1
    res.toInt % 1000000007
  }

  def calcSplit2(value: Int): Int = {
    value match {
      case 1 => return 1
      case 0 => return 0
      case _ => {}
    }

    var sums: Int = 1
    var bottom: Int = 1
    var top: Int = value - 1

    while (top > bottom) {
      sums += calcSplit2(top) + calcSplit2(bottom)
      top -= 1
      bottom += 1
      //println(s"calcSplit2 value=$value, top=$top, bottom=$bottom, sums=$sums")
    }
    sums += calcSplit2(top)
    //println(s"calcSplit2 value=$value, top=$top, bottom=$bottom, sums=$sums")

    sums
  }
}

Res.calcSplit2(2)
Res.calcSplit2(4)

import Res.ways

8 / 3
8 % 3

ways(8, 2)
ways(8, 3)
ways(5, 2)
// expected: 3

ways(56, 23)
// expected: 483076

ways(23, 22)

ways(4, 4)
//[1, 1, 1, 1, 1, 1, 1, 1]
//[1, 1, 1, 1, 1, 1, 2]
//[1, 1, 1, 1, 2, 2]
//[1, 1, 2, 2, 2]
//[2, 2, 2, 2]
//[1, 1, 1, 2, 3]
//[1, 1, 3, 3]
//[2, 3, 3]

// [23]
// [22, 1]
// [21, 2]

// [4]
// [3, 1]
// [2, 2]
// [2, 1, 1]
// [1, 1, 1, 1]
