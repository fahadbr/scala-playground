package example.configuration

import com.typesafe.config

object ConfigLoader {

  def load: AppConfig = {
    val cfg = config.ConfigFactory.defaultApplication()

    AppConfig(
      kafka = loadKafkaConfig(cfg),
      http = loadHttpConfig(cfg)
    )
  }

  private def loadKafkaConfig(cfg: config.Config): KafkaConfig = {
    val kafkaProperties = cfg.getConfig("com.example.api.kafka")

    KafkaConfig(
      brokers = kafkaProperties.getString("brokers"),
      topic = kafkaProperties.getString("topic")
    )
  }

  private def loadHttpConfig(cfg: config.Config): HttpConfig = {
    val httpProperties = cfg.getConfig("com.example.api.http")

    HttpConfig(
      port = httpProperties.getInt("port"),
      baseURI = httpProperties.getString("baseURI")
    )
  }
}
