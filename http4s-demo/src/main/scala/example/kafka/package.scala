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
    case class Request(data: Map[String, String])

    type Publisher = Has[Service[Request]]
    type Result = ProducerResult[String, Request, Unit]
    type Producer = KafkaProducer[Task, String, Request]

    trait Service[A] {
      // publish will take a message of type A and return a functional
      // effect "Task" which will return a Result (type Result = ProducerResult[String, Request, Unit])
      // Note that Task[A] is just an alias for ZIO[Any, Throwable, A]
      def publish(msg: A): Task[Result]
    }

    def publish(msg: Request): ZIO[Publisher, Throwable, Result] =
      RIO.accessM(_.get.publish(msg))

    private def mkProducerSettings(config: KafkaConfig) =
      ProducerSettings(
        keySerializer = Serializer[Task, String],
        valueSerializer = Serializer.instance[Task, Request] {
          (topic, headers, kafkaReq) =>
            Task {
              kafkaReq.data.asJson.toString().getBytes("UTF-8")
            }
        }
      ).withBootstrapServers(config.brokers)

    // this creates a ZLayer which takes in an environment with
    // a KafkaConfig and returns one with a Kafka.Publisher
    val live: ZLayer[Has[KafkaConfig], Throwable, Publisher] =
      ZLayer.fromManaged(for {
        config <- configuration.kafkaConfig.toManaged_
        producerSettings = mkProducerSettings(config)
        producer <- producerResource[Task].using(producerSettings).toManagedZIO
      } yield new KafkaService(producer, config.topic))
  }

}
