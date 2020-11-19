package example

import zio._

package object configuration {

  // ZIO module which contains all necessary configs to run the app
  type Config = Has[KafkaConfig] with Has[HttpConfig]

  case class KafkaConfig(brokers: String, topic: String)
  case class HttpConfig(port: Int, baseURI: String)
  case class AppConfig(http: HttpConfig, kafka: KafkaConfig)

  // convenience accessors for getting configs out of a ZIO effect's environment
  val kafkaConfig: URIO[Has[KafkaConfig], KafkaConfig] = ZIO.access(_.get)
  val httpConfig: URIO[Has[HttpConfig], HttpConfig] = ZIO.access(_.get)

  object Configuration {
    //val live: Layer[Throwable, Config] = ???

    val fixedTest: Layer[Throwable, Config] = ZLayer.succeedMany(
      Has(
        HttpConfig(
          port = 8081,
          baseURI = "/api"
        )
      )
        ++ Has(KafkaConfig(brokers = "localhost:9093", topic = "demo-topic"))
    )
  }
}
