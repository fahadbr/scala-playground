//
//
// Resources to inject
trait DB {
  def getUsers: Int
}

case class RealDB()() extends DB {
  override def getUsers: Int = {
    println("this is real tho")
    30
  }
}

case class MockDB() extends DB {
  override def getUsers: Int = 20
}

trait InputStream {
  def getStreamData: Stream[Int]
}

case class RealInputStream() extends InputStream {
  def getStreamData: Stream[Int] = Stream.from(0, 2)
}

case class MockInputStream() extends InputStream {
  def getStreamData: Stream[Int] = Stream.from(0, 1)
}

//
//
// Config(s) to provide resource
trait DBConfig {
  val db: DB
}

trait StreamConfig {
  val inputStream: InputStream
}

trait AllConfigs extends DBConfig with StreamConfig

object AppConfig extends AllConfigs {
  val db: DB = RealDB()
  val inputStream: InputStream = RealInputStream()
}

object TestConfig extends AllConfigs {
  val db: DB = MockDB()
  val inputStream: InputStream = MockInputStream()
}

//
//
// Object(s) which use resource
class UserRepo(implicit dbCfg: DBConfig) {
  val db = dbCfg.db
  def printUserCount = println(s"got ${db.getUsers}")
}

class Streamer(implicit streamCfg: StreamConfig) {
  val inputStream = streamCfg.inputStream
  def getFirst10 = inputStream.getStreamData.take(10)
}

class App(implicit cfg: AllConfigs) {
  val userRepo = new UserRepo()
  val streamer = new Streamer()

  def doStuff = {
    userRepo.printUserCount
    for (i <- streamer.getFirst10) {
      println(s"streamed $i")
    }
  }
}

//
//
// Make test config implicitly available
def test = {
  implicit val cfg = TestConfig
  val app = new App()
  app.doStuff
}

// Make real config implicitly available
def real = {
  implicit val cfg = AppConfig
  val app = new App()
  app.doStuff
}

test
real
