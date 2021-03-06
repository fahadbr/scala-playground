import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable
import scala.reflect._

class ArrayMetrics[T: ClassTag](input: T*) extends mutable.IndexedSeq[T] {

  private[this] val innerArray = input.toArray
  private[this] var _accessCount = new AtomicLong(0)

  def accessCount: Long = _accessCount.get()

  override def length: Int =
    innerArray.length

  override def apply(idx: Int): T = {
    _accessCount.incrementAndGet()
    innerArray(idx)
  }

  override def update(idx: Int, elem: T): Unit =
    innerArray.update(idx, elem)

}

val am = new ArrayMetrics(0,1,1,2,3,5,8)

am.accessCount
am(0)
am.accessCount
am(1)


val map = mutable.HashMap[String, String]("hi" -> "hello")
map += "wassap" -> "man"

val m = Map("hi" -> "hello")

def hi(m: Map[String, String]) = m("hi")

hi(m)
