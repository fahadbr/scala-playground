package example.kafka

import java.time.LocalDateTime

import example.kafka.Kafka.Request
import example.kafka.Kafka.Producer
import example.kafka.Kafka.Result
import fs2.kafka.KafkaProducer
import fs2.kafka.ProducerRecord
import fs2.kafka.ProducerRecords
import zio._

final class KafkaService(producer: Producer, topic: String)
    extends Kafka.Service[Request] {

  override def publish(msg: Request): Task[Result] = {
    val key = LocalDateTime.now.toString()
    val record = ProducerRecord(topic, key, msg)
    producer.produce(ProducerRecords.one(record)).flatten
  }

}
