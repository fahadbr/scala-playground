import scala.annotation.tailrec
import scala.util.Try

sealed trait List[+A] {
  def +:[B >: A](a: B): List[B]
}

case object Nil extends List[Nothing] {
  def +:[A](a: A): List[A] = Cons(a, Nil)
}

case class Cons[+A](head: A, tail: List[A]) extends List[A] {
  def +:[B >: A](a: B): List[B] = Cons(a, this)
}

object List {
  def sum(ints: List[Int]): Int =
    ints match {
      case Nil         => 0
      case Cons(x, xs) => x + sum(xs)
    }

  def product(ds: List[Double]): Double =
    ds match {
      case Nil          => 1.0
      case Cons(0.0, _) => 0
      case Cons(x, xs)  => x + product(xs)
    }

  def apply[A](as: A*): List[A] = {
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
  }

  def tail[A](as: List[A]): List[A] =
    as match {
      case Nil         => Nil
      case Cons(_, xs) => xs
    }

  def drop[A](as: List[A], n: Int): List[A] = {
    as match {
      case Nil                  => Nil
      case Cons(_, xs) if n > 0 => drop(xs, n - 1)
      case x if n == 0          => x
    }
  }

  @tailrec
  def dropWhile[A](as: List[A])(f: A => Boolean): List[A] = {
    as match {
      case Nil => Nil
      case Cons(x, xs) if f(x) => dropWhile(xs)(f)
      case l => l
    }
  }

  def init[A](as: List[A]): List[A] = {
    as match {
      case Nil => Nil
      case Cons(x, Nil) => Nil
      case Cons(x, xs) => Cons(x, init(xs))
    }
  }

  def foldRight[A,B] (as: List[A], z: B)(f: (A, B) => B): B =
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }
}

object `Exercise3.9` {


}

val l1 = 1 +: 2 +: 3 +: 4 +: 5 +: Nil

List.drop(l1, 1)
List.dropWhile(l1)(_ < 4)
List.init(l1)
List.foldRight(l1, 0)(_-_)

Vector(1,2,3,4,5).foldLeft(0)(_-_)
Vector(1,2,3,4,5).scanLeft(0)(_-_)
(((((0 - 1) - 2) - 3) - 4) - 5)

Vector(1,2,3,4,5).foldRight(0)(_-_)
Vector(1,2,3,4,5).scanRight(0)(_-_)
(1 - (2 - (3 - (4 - (5 - 0)))))
