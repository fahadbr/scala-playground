package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.Device.ReadTemp
import com.example.Device.RespondTemp
import akka.actor.typed.Signal
import akka.actor.typed.PostStop

object Device {
  sealed trait Command
  final case class ReadTemp(requestId: Long, replyTo: ActorRef[RespondTemp])
      extends Command
  final case class RespondTemp(requestId: Long, value: Option[Double])
      extends Command
  final case class RecordTemp(
      requestId: Long,
      value: Double,
      replyTo: ActorRef[TempRecorded]
  ) extends Command
  final case class TempRecorded(requestId: Long) extends Command

  case object Passivate extends Command

  def apply(groupId: String, deviceId: String): Behavior[Command] =
    Behaviors.setup(context => new Device(context, groupId, deviceId))
}

class Device(
    context: ActorContext[Device.Command],
    groupId: String,
    deviceId: String
) extends AbstractBehavior[Device.Command](context) {

  import Device._

  var lastTempReading: Option[Double] = None

  context.log.info("Device actor {}-{} started", groupId, deviceId)

  override def onMessage(msg: Device.Command): Behavior[Device.Command] = {
    msg match {
      case ReadTemp(id, replyTo) =>
        replyTo ! RespondTemp(id, lastTempReading)
        this

      case RecordTemp(id, value, replyTo) =>
        context.log.info("Recorded temperature reading {} with {}", value, id)
        lastTempReading = Some(value)
        replyTo ! TempRecorded(id)
        this

      case Passivate => Behaviors.stopped

    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Device.Command]] = {
    case PostStop => {
      context.log.info("Device actor {}-{} stopped", groupId, deviceId)
      this
    }
  }

}
