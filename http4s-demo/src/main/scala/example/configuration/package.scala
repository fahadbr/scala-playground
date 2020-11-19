package example

import zio._

package object configuration {

  // ZIO module which contains all necessary configs to run the app
  type Config = Has[KafkaConfig] with Has[HttpConfig]

  case class KafkaConfig(brokers: String, topic: String)
  case class HttpConfig(port: Int, baseURI: String)
  case class AppConfig(http: HttpConfig, kafka: KafkaConfig)

  // convenience accessors for getting configs out of a ZIO effect's environment
  // a ZIO[Has[Config], Nothing, Config] pretty much describes a functional effect (you can think of it as a closure)
  // that will pull a "Config" out of an environment that "Has" a "Config"
  // and cannot fail (hence the "Nothing" type parameter)
  val kafkaConfig: ZIO[Has[KafkaConfig], Nothing, KafkaConfig] =
    ZIO.access(_.get)

  val httpConfig: ZIO[Has[HttpConfig], Nothing, HttpConfig] =
    ZIO.access(_.get)


  object Configuration {
    val live: ZLayer[Any, Throwable, Config] = ZLayer.fromFunctionMany { _ =>
      val appCfg = ConfigLoader.load

      Has(appCfg.kafka) ++
        Has(appCfg.http)
    }
  }
}
