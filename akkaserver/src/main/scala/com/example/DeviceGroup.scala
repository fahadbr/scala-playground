package com.example

import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.Behavior
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import com.example.DeviceGroup.DeviceTerminated
import akka.actor.typed.Signal
import akka.actor.typed.PostStop

object DeviceGroup {
  trait Command
  private final case class DeviceTerminated(
      device: ActorRef[Device.Command],
      groupId: String,
      deviceId: String
  ) extends Command

  def apply(groupId: String): Behavior[Command] =
    Behaviors.setup(ctx => new DeviceGroup(ctx, groupId))
}

class DeviceGroup(
    context: ActorContext[DeviceGroup.Command],
    groupId: String
) extends AbstractBehavior[DeviceGroup.Command](context) {

  import DeviceGroup._
  import DeviceManager._

  private var deviceIdActor = Map.empty[String, ActorRef[Device.Command]]

  context.log.info("DeviceGroup {} started", groupId)

  override def onMessage(
      msg: DeviceGroup.Command
  ): Behavior[DeviceGroup.Command] = {
    msg match {
      case trackMsg @ RequestTrackDevice(`groupId`, deviceId, replyTo) =>
        deviceIdActor.get(deviceId) match {
          case Some(deviceActor) =>
            replyTo ! DeviceRegistered(deviceActor)
          case None =>
            context.log.info("Creating device actor for {}", trackMsg.deviceId)
            val deviceActor =
              context.spawn(Device(groupId, deviceId), s"device-$deviceId")
            context.watchWith(deviceActor, DeviceTerminated(deviceActor, groupId, deviceId))
            deviceIdActor += deviceId -> deviceActor
            replyTo ! DeviceRegistered(deviceActor)
        }
        this

      case RequestTrackDevice(gId, _, _) =>
        context.log.warn(
          "Ignoring TrackDevice request for {}. this actor is responsible for {}.",
          gId,
          groupId
        )
        this

      case DeviceTerminated(_, _, deviceId) =>
        context.log.info("Device actor for {} has been terminated", deviceId)
        deviceIdActor -= deviceId
        this

      case RequestDeviceList(id, gId, replyTo) =>
        if (gId == groupId) {
          replyTo ! ReplyDeviceList(id, deviceIdActor.keySet)
          this
        } else Behaviors.unhandled

    }
  }

  override def onSignal
      : PartialFunction[Signal, Behavior[DeviceGroup.Command]] = {
    case PostStop =>
      context.log.info("DeviceGroup {} stopped", groupId)
      this
  }

}
