//#full-example
package com.example

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import com.example.Device._
import com.example.DeviceManager.DeviceRegistered
import com.example.DeviceManager.RequestTrackDevice
import scala.concurrent.duration._
import com.example.DeviceManager.RequestDeviceList
import com.example.DeviceManager.ReplyDeviceList

class DeviceSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "Device actor" must {
    "reply with empty reading if no temperature is known" in {
      val probe = createTestProbe[RespondTemp]()
      val deviceActor = spawn(Device("group", "device"))

      deviceActor ! Device.ReadTemp(requestId = 42, probe.ref)
      val response = probe.receiveMessage()
      response.requestId should ===(42)
      response.value should ===(None)
    }

    "reply with latest temperature reading" in {
      val recordProbe = createTestProbe[TempRecorded]()
      val readProbe = createTestProbe[RespondTemp]()
      val deviceActor = spawn(Device("group", "device"))

      deviceActor ! Device.RecordTemp(
        requestId = 1,
        value = 24.0,
        recordProbe.ref
      )
      recordProbe.expectMessage(Device.TempRecorded(1))

      deviceActor ! Device.ReadTemp(2, readProbe.ref)
      val response1 = readProbe.receiveMessage()
      response1.requestId should ===(2)
      response1.value should ===(Some(24.0))

      deviceActor ! Device.RecordTemp(
        requestId = 3,
        value = 55.0,
        recordProbe.ref
      )
      recordProbe.expectMessage(Device.TempRecorded(3))

      deviceActor ! Device.ReadTemp(4, readProbe.ref)
      val response2 = readProbe.receiveMessage()
      response2.requestId should ===(4)
      response2.value should ===(Some(55.0))

    }

    "be able to register a device actor" in {
      val probe = createTestProbe[DeviceRegistered]()
      val groupActor = spawn(DeviceGroup("group"))

      groupActor ! RequestTrackDevice("group", "device1", probe.ref)
      val registered1 = probe.receiveMessage()
      val deviceActor1 = registered1.device

      groupActor ! RequestTrackDevice("group", "device2", probe.ref)
      val registered2 = probe.receiveMessage()
      val deviceActor2 = registered2.device
      deviceActor1 should !==(deviceActor2)

      val recordProbe = createTestProbe[TempRecorded]()
      deviceActor1 ! RecordTemp(0, 1.0, recordProbe.ref)
      recordProbe.expectMessage(TempRecorded(0))
      deviceActor2 ! RecordTemp(1, 2.0, recordProbe.ref)
      recordProbe.expectMessage(TempRecorded(1))
    }

    "ignore requests for wrong groupId" in {
      val probe = createTestProbe[DeviceRegistered]()
      val groupActor = spawn(DeviceGroup("group"))

      groupActor ! RequestTrackDevice("poopgroup", "device1", probe.ref)
      probe.expectNoMessage(500.milliseconds)
    }

    "return same actor for same deviceId" in {
      var probe = createTestProbe[DeviceRegistered]()
      val groupActor = spawn(DeviceGroup("g1"))

      groupActor ! RequestTrackDevice("g1", "d1", probe.ref)
      val resp1 = probe.receiveMessage()

      groupActor ! RequestTrackDevice("g1", "d1", probe.ref)
      val resp2 = probe.receiveMessage()

      resp1.device should ===(resp2.device)

    }

    "be able to list active devices" in {
      val registeredProbe = createTestProbe[DeviceRegistered]()
      val groupActor = spawn(DeviceGroup("group"))

      Iterator("dev1", "dev2").foreach { device =>
        groupActor ! RequestTrackDevice("group", device, registeredProbe.ref)
        registeredProbe.receiveMessage()
      }

      val deviceListProbe = createTestProbe[ReplyDeviceList]()
      groupActor ! RequestDeviceList(0, "group", deviceListProbe.ref)
      deviceListProbe.expectMessage(ReplyDeviceList(0, Set("dev1", "dev2")))
    }

    "be able to list active devices after one shuts down" in {
      val registeredProbe = createTestProbe[DeviceRegistered]()
      val groupActor = spawn(DeviceGroup("group"))

      val toShutdown = Iterator("dev1", "dev2").map { device =>
        groupActor ! RequestTrackDevice("group", device, registeredProbe.ref)
        val msg = registeredProbe.receiveMessage()
        msg.device
      }.toArray.head


      val deviceListProbe = createTestProbe[ReplyDeviceList]()
      groupActor ! RequestDeviceList(1, "group", deviceListProbe.ref)
      deviceListProbe.expectMessage(ReplyDeviceList(1, Set("dev1", "dev2")))

      toShutdown ! Passivate
      registeredProbe.expectTerminated(toShutdown, registeredProbe.remainingOrDefault)

      registeredProbe.awaitAssert {
        groupActor ! RequestDeviceList(2, "group", deviceListProbe.ref)
        deviceListProbe.expectMessage(ReplyDeviceList(2, Set("dev2")))
      }
    }
  }

}
