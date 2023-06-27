package blackout;

import org.eclipse.jetty.websocket.common.frames.BinaryFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;

import unsw.blackout.BlackoutController;
import unsw.blackout.DesktopDevice;
import unsw.blackout.Device;
import unsw.blackout.FileConstructor;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task1ExampleTests {
    @Test
    public void testExample() {
        // Task 1
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 3 devices
        // 2 devices are in view of the satellite
        // 1 device is out of view of the satellite
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));

        assertEquals(new EntityInfoResponse("DeviceA", Angle.fromDegrees(30), RADIUS_OF_JUPITER, "HandheldDevice"),
                controller.getInfo("DeviceA"));
        assertEquals(new EntityInfoResponse("DeviceB", Angle.fromDegrees(180), RADIUS_OF_JUPITER, "LaptopDevice"),
                controller.getInfo("DeviceB"));
        assertEquals(new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice"),
                controller.getInfo("DeviceC"));
    }

    @Test
    public void testDelete() {
        // Task 1
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 3 devices and deletes them
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        controller.removeDevice("DeviceA");
        controller.removeDevice("DeviceB");
        controller.removeDevice("DeviceC");
        controller.removeSatellite("Satellite1");
    }

    @Test
    public void basicFileSupport() {
        // Task 1
        BlackoutController controller = new BlackoutController();

        // Creates 1 device and add some files to it
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC"), controller.listDeviceIds());
        assertEquals(new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice"),
                controller.getInfo("DeviceC"));

        controller.addFileToDevice("DeviceC", "Hello World", "My first file!");
        Map<String, FileInfoResponse> expected = new HashMap<>();
        expected.put("Hello World",
                new FileInfoResponse("Hello World", "My first file!", "My first file!".length(), true));
        assertEquals(
                new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice", expected),
                controller.getInfo("DeviceC"));
    }

    // personal test: (test DesktopDevice for debugging)
    @Test
    public void testDesktopDeviceCreationInController() {
        // Construct a new BlackoutController
        BlackoutController controller = new BlackoutController();

        // Create a device using the controller
        String deviceId = "DeviceID1";
        String deviceType = "DesktopDevice";
        Angle devicePosition = Angle.fromDegrees(60);
        controller.createDevice(deviceId, deviceType, devicePosition);

        // Print the size of the device list after creating the device
        System.out.println("Size of device list: " + controller.getDeviceList().size());

        // Verify the device is in the controller's devices list
        Device createdDevice = null;
        for (Device device : controller.getDeviceList()) {
            System.out.println("Found device with ID: " + device.getDeviceId()); // Print all device IDs
            if (device.getDeviceId().equals(deviceId)) {
                createdDevice = device;
                break;
            }
        }

        // Check that the createdDevice is not null
        assertNotNull(createdDevice, "The device was not created");

        // Check the createdDevice has the expected properties
        assertEquals(deviceId, createdDevice.getDeviceId(), "Device ID does not match");
        assertEquals(deviceType, createdDevice.getDeviceType(), "Device type does not match");
        assertEquals(devicePosition, createdDevice.getDevicePosition(), "Device position does not match");
    }

    @Test
    public void testSatelliteCreation() {

        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 500.0, Angle.fromDegrees(60));
        controller.createSatellite("Satellite2", "RelaySatellite", 300.0, Angle.fromDegrees(300));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 360.0, Angle.fromDegrees(180));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite3"),
                controller.listSatelliteIds());

        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(60), 500.0, "StandardSatellite"),
                controller.getInfo("Satellite1"));
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(300), 300.0, "RelaySatellite"),
                controller.getInfo("Satellite2"));
        assertEquals(new EntityInfoResponse("Satellite3", Angle.fromDegrees(180), 360.0, "TeleportingSatellite"),
                controller.getInfo("Satellite3"));

    }

    @Test
    public void testDeviceCreation() {

        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device1", "DesktopDevice", Angle.fromDegrees(60));
        controller.createDevice("Device2", "LaptopDevice", Angle.fromDegrees(300));
        controller.createDevice("Device3", "HandheldDevice", Angle.fromDegrees(180));

        assertListAreEqualIgnoringOrder(Arrays.asList("Device1", "Device2", "Device3"), controller.listDeviceIds());

        assertEquals(new EntityInfoResponse("Device1", Angle.fromDegrees(60), RADIUS_OF_JUPITER, "DesktopDevice"),
                controller.getInfo("Device1"));
        assertEquals(new EntityInfoResponse("Device2", Angle.fromDegrees(300), RADIUS_OF_JUPITER, "LaptopDevice"),
                controller.getInfo("Device2"));
        assertEquals(new EntityInfoResponse("Device3", Angle.fromDegrees(180), RADIUS_OF_JUPITER, "HandheldDevice"),
                controller.getInfo("Device3"));

    }

    @Test
    public void testDeviceRemoval() {

        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device1", "DesktopDevice", Angle.fromDegrees(60));
        controller.createDevice("Device2", "LaptopDevice", Angle.fromDegrees(300));
        controller.createDevice("Device3", "HandheldDevice", Angle.fromDegrees(180));

        controller.removeDevice("Device1");
        controller.removeDevice("Device2");
        controller.removeDevice("Device3");

        assertTrue(controller.getDeviceList().isEmpty());
    }

    @Test
    public void testSatelliteRemoval() {

        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 500.0, Angle.fromDegrees(60));
        controller.createSatellite("Satellite2", "RelaySatellite", 300.0, Angle.fromDegrees(300));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 360.0, Angle.fromDegrees(180));

        controller.removeSatellite("Satellite1");
        controller.removeSatellite("Satellite2");
        controller.removeSatellite("Satellite3");

        assertTrue(controller.getSatelliteList().isEmpty());
    }

    @Test
    public void testListDeviceIds() {

        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device1", "DesktopDevice", Angle.fromDegrees(60));
        controller.createDevice("Device2", "LaptopDevice", Angle.fromDegrees(300));
        controller.createDevice("Device3", "HandheldDevice", Angle.fromDegrees(180));

        List<String> devices = controller.listDeviceIds();

        assertTrue(devices.contains("Device1"));
        assertTrue(devices.contains("Device2"));
        assertTrue(devices.contains("Device3"));
    }

    @Test
    public void testListSatelliteIds() {

        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 500.0, Angle.fromDegrees(60));
        controller.createSatellite("Satellite2", "RelaySatellite", 300.0, Angle.fromDegrees(300));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 360.0, Angle.fromDegrees(180));

        List<String> satellites = controller.listSatelliteIds();

        assertTrue(satellites.contains("Satellite1"));
        assertTrue(satellites.contains("Satellite2"));
        assertTrue(satellites.contains("Satellite3"));
    }

    @Test
    public void testAddFiles() {

        BlackoutController controller = new BlackoutController();

        String device1 = "Device1";
        String deviceType1 = "HandheldDevice";
        Angle devicePosition1 = Angle.fromDegrees(60);
        controller.createDevice(device1, deviceType1, devicePosition1);

        assertTrue(controller.listDeviceIds().contains(device1));
        assertEquals(new EntityInfoResponse(device1, devicePosition1, RADIUS_OF_JUPITER, deviceType1),
                controller.getInfo(device1));

        String fileName1 = "RandomFile";
        String fileDetails1 = "GeniusHacker";
        controller.addFileToDevice(device1, fileName1, fileDetails1);

        Map<String, FileInfoResponse> expectedFileAddition = new HashMap<>();
        expectedFileAddition.put(fileName1, new FileInfoResponse(fileName1, fileDetails1, fileDetails1.length(), true));
        assertEquals(
                new EntityInfoResponse(device1, devicePosition1, RADIUS_OF_JUPITER, deviceType1, expectedFileAddition),
                controller.getInfo(device1));

        // creates a second device to add more files:
        String device2 = "Device2";
        String deviceType2 = "DesktopDevice";
        Angle devicePosition2 = Angle.fromDegrees(45);
        controller.createDevice(device2, deviceType2, devicePosition2);

        assertTrue(controller.listDeviceIds().contains(device2));
        assertEquals(new EntityInfoResponse(device2, devicePosition2, RADIUS_OF_JUPITER, deviceType2),
                controller.getInfo(device2));

        // addition of multiple files to second created device:
        String[] fileName2 = {
                "HelloWorld", "COMP2511", "Randomizer"
        };
        String[] fileDetails2 = {
                "Slacker", "I hate comp2511", "deliciousCoder"
        };
        for (int count = 0; count < fileName2.length; count++) {
            controller.addFileToDevice(device2, fileName2[count], fileDetails2[count]);
        }

        Map<String, FileInfoResponse> expectedSecondaryAddition = new HashMap<>();
        for (int count = 0; count < fileName2.length; count++) {
            expectedSecondaryAddition.put(fileName2[count],
                    new FileInfoResponse(fileName2[count], fileDetails2[count], fileDetails2[count].length(), true));
        }

        assertEquals(new EntityInfoResponse(device2, devicePosition2, RADIUS_OF_JUPITER, deviceType2,
                expectedSecondaryAddition), controller.getInfo(device2));

    }

    @Test
    public void testAddFileToDeviceWithValidDeviceId() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("device1", "HandheldDevice", Angle.fromDegrees(45));
        controller.createDevice("device2", "DesktopDevice", Angle.fromDegrees(60));
        controller.createDevice("device3", "LaptopDevice", Angle.fromDegrees(105));

        controller.addFileToDevice("device1", "file1", "stuff1");
        controller.addFileToDevice("device2", "file2", "stuff2");
        controller.addFileToDevice("device3", "file3", "stuff3");

        Device device1 = controller.findDeviceById("device1");
        Device device2 = controller.findDeviceById("device2");
        Device device3 = controller.findDeviceById("device3");

        FileConstructor file1 = device1.getFileByID("file1");
        FileConstructor file2 = device2.getFileByID("file2");
        FileConstructor file3 = device3.getFileByID("file3");
        assertNotNull(file1);
        assertNotNull(file2);
        assertNotNull(file3);

        assertEquals("file1", file1.getFileName());
        assertEquals("stuff1", file1.getFileDetails());

        assertEquals("file2", file2.getFileName());
        assertEquals("stuff2", file2.getFileDetails());

        assertEquals("file3", file3.getFileName());
        assertEquals("stuff3", file3.getFileDetails());

    }

    @Test
    public void testAddFileToDeviceInvalidDeviceId() {
        BlackoutController controller = new BlackoutController();
        String deviceId = "device";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> controller.addFileToDevice(deviceId, "file", "random"));

        assertEquals("no device found with id: " + deviceId, thrown.getMessage());

    }

    @Test
    public void testAddFileToDeviceInvalidSatelliteId() {
        BlackoutController controller = new BlackoutController();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> controller.addFileToDevice("satellite", "file", "random"));

        assertEquals("no device found with id: " + "satellite", thrown.getMessage());
    }

    @Test
    public void testGetInfoWithValidId() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("device1", "HandheldDevice", Angle.fromDegrees(45));
        controller.createDevice("device2", "DesktopDevice", Angle.fromDegrees(60));
        controller.createDevice("device3", "LaptopDevice", Angle.fromDegrees(105));

        controller.createSatellite("Satellite1", "StandardSatellite", 500.0, Angle.fromDegrees(60));
        controller.createSatellite("Satellite2", "RelaySatellite", 300.0, Angle.fromDegrees(300));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 360.0, Angle.fromDegrees(180));

        EntityInfoResponse responseD1 = controller.getInfo("device1");
        EntityInfoResponse responseD2 = controller.getInfo("device2");
        EntityInfoResponse responseD3 = controller.getInfo("device3");

        assertNotNull(responseD1);
        assertNotNull(responseD2);
        assertNotNull(responseD3);

        assertEquals("device1", responseD1.getDeviceId());
        assertEquals("device2", responseD2.getDeviceId());
        assertEquals("device3", responseD3.getDeviceId());

    }

    @Test
    public void testGetInfoInvalidId() {
        BlackoutController controller = new BlackoutController();
        EntityInfoResponse response = controller.getInfo("device");
        assertNull(response);

    }

    @Test
    public void testGetInfoWithValidSatelliteId() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("satellite1", "StandardSatellite", 1000, Angle.fromDegrees(45));
        controller.createSatellite("satellite2", "RelaySatellite", 1500, Angle.fromDegrees(60));
        controller.createSatellite("satellite3", "TeleportingSatellite", 2500, Angle.fromDegrees(120));

        EntityInfoResponse response1 = controller.getInfo("satellite1");
        EntityInfoResponse response2 = controller.getInfo("satellite2");
        EntityInfoResponse response3 = controller.getInfo("satellite3");

        assertNotNull(response1);
        assertNotNull(response2);
        assertNotNull(response3);

        assertEquals("satellite1", response1.getDeviceId());
        assertEquals("satellite2", response2.getDeviceId());
        assertEquals("satellite3", response3.getDeviceId());

    }

    @Test
    public void testGetInfoInvalidSatelliteId() {
        BlackoutController controller = new BlackoutController();
        EntityInfoResponse response = controller.getInfo("satelliteTest");
        assertNull(response);
    }

}
