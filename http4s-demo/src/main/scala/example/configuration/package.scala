package example

import zio._

package object configuration {

  type Config = Has[KafkaConfig] with Has[HttpConfig]

  case class KafkaConfig(brokers: Set[String])
  case class HttpConfig(port: Int)
  case class AppConfig(http: HttpConfig, kafka: KafkaConfig)

  val kafkaConfig: URIO[Has[KafkaConfig], KafkaConfig] = ZIO.access(_.get)
  val httpConfig: URIO[Has[HttpConfig], HttpConfig] = ZIO.access(_.get)

  object Configuration {
    val live: Layer[Throwable, Config] = ???

    val fixedTest: Layer[Throwable, Config] = ZLayer.fromEffectMany(
      Task {
        Has(HttpConfig(8081)) ++
          Has(KafkaConfig(Set("localhost:9093")))
      }
    )
  }
}
