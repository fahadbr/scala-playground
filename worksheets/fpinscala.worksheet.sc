import scala.annotation.tailrec
// vim:foldmethod=marker

// chapter 2 {{{

// {{{ factorial
def factorial(n: Int): Int = {
  @tailrec
  def go(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else go(n - 1, n * acc)
  go(n, 1)
}

factorial(4)
factorial(3)
// }}}

// {{{ isSorted

def isSorted[A](as: Array[A])(ordered: (A, A) => Boolean): Boolean = {
  def go(i: Int): Boolean =
    if (i <= 0) true
    else if (!ordered(as(i - 1), as(i))) false
    else go(i - 1)

  go(as.length - 1)
}

isSorted(Array[Int]()) { (a, b) => a <= b }
isSorted(Array(1)) { (a, b) => a <= b }
isSorted(Array(1, 2, 3)) { (a, b) => a <= b }
isSorted(Array(1, 2, 2)) { (a, b) => a <= b }
isSorted(Array(1, 3, 2)) { (a, b) => a <= b }
isSorted(Array(4, 3, 3)) { (a, b) => a <= b }

// }}}

// partial1 {{{ //

def partial1[A, B, C](a: A, f: (A, B) => C): B => C =
  (b: B) => f(a, b)

// }}} partial1 //

// curry {{{ //

def curry[A, B, C](f: (A, B) => C): A => (B => C) =
  a => b => f(a, b)

// }}} curry  //

// uncurry {{{ //

def uncurry[A, B, C](f: A => B => C): (A, B) => C =
  (a, b) => f(a)(b)

// }}} uncurry //

// compose {{{ //

def compose[A, B, C](f: B => C, g: A => B): A => C =
  a => f(g(a))

// }}} compose //

// whileloop {{{ //

@tailrec
private def whileloop(predicate: => Boolean)(f: => Unit): Unit = {
  if (!predicate) {
    return
  }

  f
  whileloop(predicate)(f)
}

var a = 0
whileloop(a < 5) {
  println(a)
  a += 1
}

// }}} whileloop //

// }}} chapter 2

// chapter 3 {{{ //

// never {{{ //

def neverReturns(): Nothing = {
  throw new Exception()
}

def returnUnit(): Unit = {
  throw new Exception()
}

def returnAny(): Any = {
  throw new Exception()
}

def returnNull(): Null = null

// }}} never //

sealed trait List[+A]

case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A] {
  override def toString(): String = {
    s"List(${List
      .foldRight(this, Vector.empty[String]) { (x, v) => x.toString +: v }
      .mkString(",")})"
  }
}

object List {
  def apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  def sum(ints: List[Int]): Int =
    ints match {
      case Nil              => 0
      case Cons(head, tail) => head + sum(tail)
    }

  def product(ints: List[Int]): Double =
    ints match {
      case Nil              => 0.0
      case Cons(0.0, _)     => 0.0
      case Cons(head, tail) => head * product(tail)
    }

  def tail[A](l: List[A]): List[A] =
    l match {
      case Nil        => Nil
      case Cons(_, t) => t
    }

  def setHead[A](l: List[A], newHead: A): List[A] =
    l match {
      case Nil           => Cons(newHead, Nil)
      case Cons(_, tail) => Cons(newHead, tail)
    }

  def drop[A](l: List[A], n: Int): List[A] =
    if (n == 0) l
    else
      l match {
        case Nil        => Nil
        case Cons(_, t) => drop(t, n - 1)
      }

  def dropWhile[A](l: List[A], f: A => Boolean): List[A] =
    l match {
      case Nil        => Nil
      case Cons(h, t) => if (f(h)) dropWhile(t, f) else l
    }

  def init[A](l: List[A]): List[A] =
    l match {
      case Nil          => Nil
      case Cons(h, Nil) => Nil
      case Cons(h, t)   => Cons(h, init(t))
    }

  def foldRight[A, B](as: List[A], acc: B)(f: (A, B) => B): B =
    as match {
      case Nil         => acc
      case Cons(x, xs) => f(x, foldRight(xs, acc)(f))
    }
}

val l = List(1, 2, 3)
List.tail(l)
List.tail(Nil)

List.setHead(l, 4)
List.setHead(Nil, 4)

List.drop(l, 1)
List.drop(l, 2)
List.drop(Nil, 5)

List.dropWhile(l, (i: Int) => i < 2)
List.dropWhile(l, (i: Int) => i > 2)
List.dropWhile(Nil, (i: Int) => i > 2)

List.init(l)
List.init(List.init(l))

// }}} chapter 3  //
