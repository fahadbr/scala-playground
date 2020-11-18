package example

import java.time.LocalDateTime

import scala.concurrent.ExecutionContext.Implicits.global

import cats.effect.{ExitCode => CatsExitCode}
import fs2.kafka._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import zio._
import zio.console._
import zio.interop.catz._

object Main extends CatsApp {

  type KafkaTask[A] = RIO[ZEnv, A]
  val dsl: Http4sDsl[KafkaTask] = Http4sDsl[KafkaTask]
  import dsl._

  case class KafkaRequest(data: Map[String, String])
  type KProducer = KafkaProducer[KafkaTask, String, KafkaRequest]

  val kafkaReqSerializer = Serializer.instance[KafkaTask, KafkaRequest] {
    (topic, headers, kafkaReq) =>
      RIO {
        kafkaReq.data.asJson.toString().getBytes("UTF-8")
      }
  }

  val producerSettings =
    ProducerSettings(
      keySerializer = Serializer[KafkaTask, String],
      valueSerializer = kafkaReqSerializer
    ).withBootstrapServers("localhost:9093")

  implicit val decoder = jsonOf[KafkaTask, KafkaRequest]

  def sendToKafka(req: KafkaRequest, producer: KProducer) = {
    val key = LocalDateTime.now().toString()
    val record = ProducerRecord("demo-topic", key, req)
    producer.produce(ProducerRecords.one(record)).flatten
  }

  def mkHttpApp(producer: KProducer) = {

    val helloWorldService = HttpRoutes.of[KafkaTask] {
      case GET -> Root / "helloworld" =>
        Ok("Hello world!")
      case req @ POST -> Root / "submit" =>
        for {
          kafkaReq <- req.as[KafkaRequest]
          produceResult <- sendToKafka(kafkaReq, producer).either
          resp <- produceResult match {
            case Left(ex)     => InternalServerError(ex.getMessage)
            case Right(value) => Ok(s"published: ${value.records.size}")
          }
        } yield resp
    }

    Router[KafkaTask]("/api" -> helloWorldService).orNotFound
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {

    val program = producerResource[KafkaTask]
      .using(producerSettings)
      .use { producer =>
        BlazeServerBuilder[KafkaTask](global)
          .bindHttp(8081)
          .withHttpApp(mkHttpApp(producer))
          .serve
          .compile[KafkaTask, KafkaTask, CatsExitCode]
          .drain
      }

    program
      .provideLayer(ZEnv.live)
      .tapError(err => putStrLn(s"Execution failed with error: $err"))
      .exitCode

  }
}
