package example

import example.Main.KafkaRequest
import zio._

package object kafka {

  object Kafka {
    trait Service[A] {
      def publish(msg: A): Task[Unit]
    }

    type KafkaProducer = Has[Service[KafkaRequest]]

    def publish(msg: KafkaRequest): RIO[KafkaProducer, Unit] =
      RIO.accessM(_.get.publish(msg))

  }
}
