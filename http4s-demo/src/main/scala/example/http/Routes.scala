package example.http

import example.kafka.Kafka
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import zio.interop.catz._
import zio._

case class Routes[R <: Kafka.Publisher](baseURI: String) {

  type HttpTask[A] = RIO[R, A]

  val dsl: Http4sDsl[HttpTask] = Http4sDsl[HttpTask]

  implicit val decoder = jsonOf[HttpTask, Kafka.Request]

  lazy val routes: HttpRoutes[HttpTask] = {
    import dsl._

    HttpRoutes.of[HttpTask] {
      case GET -> Root / "helloworld" =>
        Ok("Hello world!")
      case req @ POST -> Root / "submit" =>
        for {
          kafkaReq <- req.as[Kafka.Request]
          produceResult <- Kafka.publish(kafkaReq).either
          resp <- produceResult match {
            case Left(ex)     => InternalServerError(ex.getMessage)
            case Right(value) => Ok(s"published: ${value.records.size}")
          }
        } yield resp
    }

  }

  def router = Router[HttpTask](baseURI -> routes).orNotFound
}
