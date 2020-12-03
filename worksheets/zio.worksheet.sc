import $ivy.`dev.zio::zio:1.0.3`
import zio._

val b = 2
val c = 3
val d = 3
val a = UIO(c * d * c * d)
val x = a.map(i => i.toHexString)

Runtime.default.unsafeRun(x)

def curry(a: Int)(b: Int)(c: Int) = {
  (c+b) * a
}

