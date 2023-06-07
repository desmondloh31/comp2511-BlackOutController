package unsw.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class BlackoutController {
    List<DeviceConstructor> devices = new ArrayList<DeviceConstructor>();
    List<SatelliteConstructor> satellites = new ArrayList<SatelliteConstructor>();

    // helper functions for getting the deviceList and getting the satelliteList:
    public List<DeviceConstructor> getDeviceList() {
        return devices;
    }

    public List<SatelliteConstructor> getSatelliteList() {
        return satellites;
    }

    public void createDevice(String deviceId, String type, Angle position) {
        if (type.equals("HandheldDevice")) {
            devices.add(new HandheldDevice(deviceId, type, position));
        }
    }

    public void removeDevice(String deviceId) {
        // TODO: Task 1b)
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        // TODO: Task 1c)
    }

    public void removeSatellite(String satelliteId) {
        // TODO: Task 1d)
    }

    public List<String> listDeviceIds() {
        // TODO: Task 1e)
        return new ArrayList<>();
    }

    public List<String> listSatelliteIds() {
        // TODO: Task 1f)
        return new ArrayList<>();
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        // TODO: Task 1g)
    }

    public EntityInfoResponse getInfo(String id) {
        // TODO: Task 1h)
        return null;
    }

    public void simulate() {
        // TODO: Task 2a)
    }

    /**
     * Simulate for the specified number of minutes. You shouldn't need to modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        // TODO: Task 2 b)
        return new ArrayList<>();
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }
}
