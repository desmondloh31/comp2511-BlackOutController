package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.blackout.Entity;

public class BlackoutController {
    List<Device> deviceList = new ArrayList<Device>();
    List<Satellite> satelliteList = new ArrayList<Satellite>();
    List<FileEntity> entityList = new ArrayList<FileEntity>();

    // Helper functions for getting the deviceList and getting the satelliteList:
    public List<Device> getDeviceList() {
        return deviceList;
    }

    public List<Satellite> getSatelliteList() {
        return satelliteList;
    }

    // Helper function that finds device By Id:
    public Device findDeviceById(String deviceId) {
        for (Device device : this.deviceList) {
            if (device.getDeviceId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    // Helper function that finds satellite by Id:
    public Satellite findSatelliteById(String satelliteId) {
        for (Satellite satellite : this.satelliteList) {
            if (satellite.getSatelliteId().equals(satelliteId)) {
                return satellite;
            }
        }
        return null;
    }

    // Helper function that finds entity by Id (can be either Device or Satellite)
    public FileEntity findEntityById(String entityId) {
        for (FileEntity entity : entityList) {
            if (entity.getId().equals(entityId)) {
                return entity;
            }
        }
        return null;
    }

    public void createDevice(String deviceId, String type, Angle position) {
        // System.out.println("Creating device: " + deviceId + ", " + type);
        if (type.equals("HandheldDevice")) {
            deviceList.add(new HandheldDevice(deviceId, type, position));
        } else if (type.equals("DesktopDevice")) {
            deviceList.add(new DesktopDevice(deviceId, type, position));
        } else if (type.equals("LaptopDevice")) {
            deviceList.add(new LaptopDevice(deviceId, type, position));
        }
    }

    public void removeDevice(String deviceId) {
        // TODO: Task 1b)
        Iterator<Device> deviceIterator = deviceList.iterator();
        while (deviceIterator.hasNext()) {
            Device device = deviceIterator.next();
            if (device.getDeviceId().equals(deviceId)) {
                deviceIterator.remove();
                break;
            }
        }
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {

        if (type.equals("StandardSatellite")) {
            satelliteList.add(new StandardSatellite(satelliteId, type, height, position));
        } else if (type.equals("RelaySatellite")) {
            satelliteList.add(new RelaySatellite(satelliteId, type, height, position));
        } else if (type.equals("TeleportingSatellite")) {
            satelliteList.add(new TeleportingSatellite(satelliteId, type, height, position));
        } else if (type.equals("ElephantSatellite")) {
            satelliteList.add(new ElephantSatellite(satelliteId, type, height, position));
        }
    }

    public void removeSatellite(String satelliteId) {

        Iterator<Satellite> satelliteIterator = satelliteList.iterator();
        while (satelliteIterator.hasNext()) {
            Satellite satellite = satelliteIterator.next();
            if (satellite.getSatelliteId().equals(satelliteId)) {
                satelliteIterator.remove();
                break;
            }
        }
    }

    public List<String> listDeviceIds() {

        List<String> allDeviceIds = new ArrayList<>();
        for (Device deviceId : deviceList) {
            allDeviceIds.add(deviceId.getDeviceId());
        }
        return allDeviceIds;
    }

    public List<String> listSatelliteIds() {

        List<String> allSatelliteIds = new ArrayList<>();
        for (Satellite satelliteId : satelliteList) {
            allSatelliteIds.add(satelliteId.getSatelliteId());
        }
        return allSatelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        // TODO: Task 1g)
        Device findDevice = findDeviceById(deviceId);
        if (findDevice == null) {
            throw new IllegalArgumentException("no device found with id: " + deviceId);
        }
        FileConstructor file = new FileConstructor(filename, content);
        findDevice.addFile(file);
    }

    public EntityInfoResponse getInfo(String id) {
        // TODO: Task 1h)
        Device device = findDeviceById(id);
        Satellite satellite = findSatelliteById(id);
        if (device != null) {
            return device.getInfo();
        }
        if (satellite != null) {
            return satellite.getInfo();
        }
        return null;
    }

    public void simulate() {
        // TODO: Task 2a)
        for (Satellite satellite : satelliteList) {
            satellite.updateSatellitePosition();
        }

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
        List<String> EntitiesInRange = new ArrayList<>();
        for (Satellite satellite : satelliteList) {
            if (satellite.getSatelliteId().equalsIgnoreCase(id)) {
                EntitiesInRange = satellite.updateList(this);
                break;
            }
        }

        if (EntitiesInRange.isEmpty()) {
            for (Device device : deviceList) {
                if (device.getDeviceId().equalsIgnoreCase(id)) {
                    EntitiesInRange = device.updateList(this);
                    break;
                }
            }
        }
        return EntitiesInRange;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        FileEntity fromEntity = findEntityById(fromId);
        FileEntity toEntity = findEntityById(toId);
        String noEntitiesFound = "No Entities Found";

        if (fromEntity != null && toEntity != null) {
            sendFileBetweenEntities(fileName, fromEntity, toEntity);
        } else {
            throw new IllegalArgumentException(noEntitiesFound);
        }
    }

    // Helper method to send file between entities (entity can either by Device or
    // Satellite):
    private void sendFileBetweenEntities(String fileName, FileEntity fromEntity, FileEntity toEntity)
            throws FileTransferException {
        FileConstructor transfer = fromEntity.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";
        if (transfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }
        if (fromEntity.getUsedBandwidth() + transfer.getFileSize() > fromEntity.getTotalBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(fromEntity.getId());
        }
        if (toEntity.getFileByID(fileName) != null) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
        }
        if (toEntity.getAvailableBandwidth() < transfer.getFileSize()) {
            if (toEntity.getTotalFiles() >= toEntity.getMaxFileCap()) {
                throw new FileTransferException.VirtualFileNoStorageException(maxFiles);
            } else {
                throw new FileTransferException.VirtualFileNoStorageException(maxStorage);
            }
        }
        fromEntity.removeFile(transfer);
        toEntity.addFile(transfer);
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
