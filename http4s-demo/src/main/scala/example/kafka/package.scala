package example

import example.Main.AppTask
import example.configuration.KafkaConfig
import fs2.kafka._
import io.circe.generic.auto._
import io.circe.syntax._
import zio._
import zio.interop.catz._

package object kafka {

  object Kafka {
    trait Service[A] {
      def publish(msg: A): Task[Result]
    }

    case class Request(data: Map[String, String])

    type Publisher = Has[Service[Request]]
    type Result = ProducerResult[String, Request, Unit]
    type Producer = KafkaProducer[Task, String, Request]

    val kafkaReqSerializer = Serializer.instance[Task, Request] {
      (topic, headers, kafkaReq) =>
        RIO {
          kafkaReq.data.asJson.toString().getBytes("UTF-8")
        }
    }

    def publish(msg: Request): RIO[Publisher, Result] =
      RIO.accessM(_.get.publish(msg))

    // this creates a ZLayer which
    val live: ZLayer[Has[KafkaConfig], Throwable, Publisher] =
      ZLayer.fromManaged(for {
        config <- configuration.kafkaConfig.toManaged_
        producerSettings = ProducerSettings(
          keySerializer = Serializer[Task, String],
          valueSerializer = kafkaReqSerializer
        ).withBootstrapServers(config.brokers)

        producer <- producerResource[Task].using(producerSettings).toManagedZIO
      } yield new KafkaPublisherService(producer, config.topic))

  }

}
