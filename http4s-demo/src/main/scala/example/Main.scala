package example

import java.time.LocalDateTime

import scala.concurrent.ExecutionContext.Implicits.global

import cats.effect.{ExitCode => CatsExitCode}
import example.configuration._
import example.http.Routes
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

import kafka._

object Main extends App {

  type AppEnv = ZEnv with Config with Kafka.Publisher
  type AppTask[A] = ZIO[AppEnv, Throwable, A]

  val appEnv = Configuration.live >+> Kafka.live

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    val program = for {
      httpConfig <- configuration.httpConfig
      server <- ZIO.runtime[AppEnv].flatMap { implicit runtime =>
        BlazeServerBuilder[AppTask](global)
          .bindHttp(httpConfig.port)
          .withHttpApp(Routes(httpConfig.baseURI).router)
          .serve
          .compile[AppTask, AppTask, CatsExitCode]
          .drain
      }

    } yield server

    program
      .provideSomeLayer[ZEnv](appEnv)
      .tapError(err => putStrLn(s"Execution failed with error: $err"))
      .exitCode

  }
}
