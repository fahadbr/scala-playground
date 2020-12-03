package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.Behavior
import com.example.DeviceManager.RequestTrackDevice
import com.example.DeviceManager.RequestDeviceList
import com.example.DeviceManager.DeviceGroupTerminated

object DeviceManager {
  sealed trait Command

  final case class RequestTrackDevice(
      groupId: String,
      deviceId: String,
      replyTo: ActorRef[DeviceRegistered]
  ) extends DeviceManager.Command
      with DeviceGroup.Command

  final case class DeviceRegistered(device: ActorRef[Device.Command])

  final case class RequestDeviceList(
      requestId: Long,
      groupId: String,
      replyTo: ActorRef[ReplyDeviceList]
  ) extends DeviceManager.Command
      with DeviceGroup.Command

  final case class ReplyDeviceList(requestId: Long, ids: Set[String])

  private final case class DeviceGroupTerminated(groupId: String)
      extends DeviceManager.Command

}

class DeviceManager(ctx: ActorContext[DeviceManager.Command]) extends AbstractBehavior[DeviceManager.Command](ctx) {

  import DeviceManager._

  var groupIdToActor = Map.empty[String, ActorRef[DeviceGroup.Command]]

  override def onMessage(msg: DeviceManager.Command): Behavior[DeviceManager.Command] =
    msg match {
      case trackMsg @ RequestTrackDevice(groupId, _, replyTo) =>
        groupIdToActor.get(groupId) match {
          case Some()
        }
      case RequestDeviceList(requestId, groupId, replyTo) =>
      case DeviceGroupTerminated(groupId) =>
    }


  
}
