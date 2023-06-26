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

public class BlackoutController {
    List<Device> deviceList = new ArrayList<Device>();
    List<Satellite> satelliteList = new ArrayList<Satellite>();

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

    // Helper function that finds Entire Entity by Id:

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
        System.out.println("File size: " + file.getFileSize());
        System.out.println("Available bandwidth before adding file: " + findDevice.getAvailableBandwidth());
        System.out.println("Used bandwidth before adding file: " + findDevice.getUsedBandwidth());

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
        // TODO: Task 2 c) // test:
        Device fromDevice = findDeviceById(fromId);
        Satellite fromSatellite = findSatelliteById(fromId);

        Device toDevice = findDeviceById(toId);
        Satellite toSatellite = findSatelliteById(toId);
        String noEntities = "Entities not found";

        if (fromDevice != null && toDevice != null) {
            sendFileBetweenDevices(fromDevice, toDevice, fileName);
        } else if (fromDevice != null && toSatellite != null) {
            sendFileFromDeviceToSatellite(fromDevice, toSatellite, fileName);
        } else if (fromSatellite != null && toDevice != null) {
            sendFileFromSatelliteToDevice(fromSatellite, toDevice, fileName);
        } else if (fromSatellite != null && toSatellite != null) {
            sendFileBetweenSatellites(fromSatellite, toSatellite, fileName);
        } else {
            throw new IllegalArgumentException(noEntities);
        }

    }

    // Helper method to send file between devices:
    private void sendFileBetweenDevices(Device fromDevice, Device toDevice, String fileName)
            throws FileTransferException {
        FileConstructor fileTransfer = fromDevice.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";
        if (fileTransfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }
        if (fromDevice.getUsedBandwidth() + fileTransfer.getFileSize() > fromDevice.getTotalBandwidth()) {
            throw new FileTransferException.InsufficientBandwidthException(fromDevice.getDeviceId());
        }
        if (toDevice.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toDevice.getTotalFiles() >= toDevice.getMaxFileCap()) {
                throw new FileTransferException.InsufficientStorageException(maxFiles);
            } else {
                throw new FileTransferException.InsufficientStorageException(maxStorage);
            }
        }
        fromDevice.removeFile(fileTransfer);
        toDevice.addFile(fileTransfer);
    }

    // Helper method to send file between satellites:
    private void sendFileBetweenSatellites(Satellite fromSatellite, Satellite toSatellite, String fileName)
            throws FileTransferException {
        FileConstructor fileTransfer = fromSatellite.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";
        if (fileTransfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }
        if (fromSatellite.getUsedBandwidth() + fileTransfer.getFileSize() > fromSatellite.getTotalBandwidth()) {
            throw new FileTransferException.InsufficientBandwidthException(fromSatellite.getSatelliteId());
        }
        if (toSatellite.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toSatellite.getTotalFiles() >= toSatellite.getMaxFileCap()) {
                throw new FileTransferException.InsufficientStorageException(maxFiles);
            } else {
                throw new FileTransferException.InsufficientStorageException(maxStorage);
            }
        }
        fromSatellite.removeFile(fileTransfer);
        toSatellite.addFile(fileTransfer);
    }

    // Helper method to send file from Device to Satellite:
    private void sendFileFromDeviceToSatellite(Device fromDevice, Satellite toSatellite, String fileName)
            throws FileTransferException {
        FileConstructor fileTransfer = fromDevice.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";

        if (fileTransfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        if (fromDevice.getUsedBandwidth() + fileTransfer.getFileSize() > fromDevice.getTotalBandwidth()) {
            throw new FileTransferException.InsufficientBandwidthException(fromDevice.getDeviceId());
        }

        if (toSatellite.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toSatellite.getTotalFiles() >= toSatellite.getMaxFileCap()) {
                throw new FileTransferException.InsufficientStorageException(maxFiles);
            } else {
                throw new FileTransferException.InsufficientStorageException(maxStorage);
            }
        }

        fromDevice.removeFile(fileTransfer);
        toSatellite.addFile(fileTransfer);
        System.out.println("File size: " + fileTransfer.getFileSize());
        System.out.println("Used bandwidth before transfer: " + fromDevice.getUsedBandwidth());
        System.out.println("Total bandwidth: " + fromDevice.getTotalBandwidth());
    }

    // Helper method to send file From Satellite to Device:
    private void sendFileFromSatelliteToDevice(Satellite fromSatellite, Device toDevice, String fileName)
            throws FileTransferException {
        FileConstructor fileTransfer = fromSatellite.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";

        if (fileTransfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        if (fromSatellite.getUsedBandwidth() + fileTransfer.getFileSize() > fromSatellite.getTotalBandwidth()) {
            throw new FileTransferException.InsufficientBandwidthException(fromSatellite.getSatelliteId());
        }

        if (toDevice.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toDevice.getTotalFiles() >= toDevice.getMaxFileCap()) {
                throw new FileTransferException.InsufficientStorageException(maxFiles);
            } else {
                throw new FileTransferException.InsufficientStorageException(maxStorage);
            }
        }

        fromSatellite.removeFile(fileTransfer);
        toDevice.addFile(fileTransfer);
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
