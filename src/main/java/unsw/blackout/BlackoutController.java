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
    List<Device> devices = new ArrayList<Device>();
    List<SatelliteConstructor> satellites = new ArrayList<SatelliteConstructor>();

    // Helper functions for getting the deviceList and getting the satelliteList:
    public List<Device> getDeviceList() {
        return devices;
    }

    public List<SatelliteConstructor> getSatelliteList() {
        return satellites;
    }

    // Helper function that finds device By Id:
    public Device findDeviceById(String deviceId) {
        for (Device device : this.devices) {
            if (device.getDeviceId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    // Helper function that finds satellite by Id:
    public SatelliteConstructor findSatelliteById(String satelliteId) {
        for (SatelliteConstructor satellite : this.satellites) {
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
            devices.add(new HandheldDevice(deviceId, type, position));
        } else if (type.equals("DesktopDevice")) {
            devices.add(new DesktopDevice(deviceId, type, position));
        } else if (type.equals("LaptopDevice")) {
            devices.add(new LaptopDevice(deviceId, type, position));
        }
    }

    public void removeDevice(String deviceId) {
        // TODO: Task 1b)
        Iterator<Device> deviceIterator = devices.iterator();
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
            satellites.add(new StandardSatellite(satelliteId, type, height, position));
        } else if (type.equals("RelaySatellite")) {
            satellites.add(new RelaySatellite(satelliteId, type, height, position));
        } else if (type.equals("TeleportingSatellite")) {
            satellites.add(new TeleportingSatellite(satelliteId, type, height, position));
        } else if (type.equals("ElephantSatellite")) {
            satellites.add(new ElephantSatellite(satelliteId, type, height, position));
        }
    }

    public void removeSatellite(String satelliteId) {

        Iterator<SatelliteConstructor> satelliteIterator = satellites.iterator();
        while (satelliteIterator.hasNext()) {
            SatelliteConstructor satellite = satelliteIterator.next();
            if (satellite.getSatelliteId().equals(satelliteId)) {
                satelliteIterator.remove();
                break;
            }
        }
    }

    public List<String> listDeviceIds() {

        List<String> allDeviceIds = new ArrayList<>();
        for (Device deviceId : devices) {
            allDeviceIds.add(deviceId.getDeviceId());
        }
        return allDeviceIds;
    }

    public List<String> listSatelliteIds() {

        List<String> allSatelliteIds = new ArrayList<>();
        for (SatelliteConstructor satelliteId : satellites) {
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
        SatelliteConstructor satellite = findSatelliteById(id);
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
        for (SatelliteConstructor satellite : satellites) {
            satellite.updatePosition();
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
        for (SatelliteConstructor satellite : satellites) {
            if (satellite.getSatelliteId().equalsIgnoreCase(id)) {
                EntitiesInRange = satellite.updateList(this);
                break;
            }
        }

        if (EntitiesInRange.isEmpty()) {
            for (Device device : devices) {
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
        SatelliteConstructor fromSatellite = findSatelliteById(fromId);

        Device toDevice = findDeviceById(toId);
        SatelliteConstructor toSatellite = findSatelliteById(toId);
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
            throw new FileTransferException.VirtualFileNoBandwidthException(fromDevice.getDeviceId());
        }
        if (toDevice.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toDevice.getTotalFiles() >= toDevice.getMaxFileCap()) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxFiles);
            } else {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxStorage);
            }
        }
        fromDevice.removeFile(fileTransfer);
        toDevice.addFile(fileTransfer);
    }

    // Helper method to send file between satellites:
    private void sendFileBetweenSatellites(SatelliteConstructor fromSatellite, SatelliteConstructor toSatellite,
            String fileName) throws FileTransferException {
        FileConstructor fileTransfer = fromSatellite.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";
        if (fileTransfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }
        if (fromSatellite.getUsedBandwidth() + fileTransfer.getFileSize() > fromSatellite.getTotalBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(fromSatellite.getSatelliteId());
        }
        if (toSatellite.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toSatellite.getTotalFiles() >= toSatellite.getMaxFileCap()) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxFiles);
            } else {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxStorage);
            }
        }
        fromSatellite.removeFile(fileTransfer);
        toSatellite.addFile(fileTransfer);
    }

    // Helper method to send file from Device to Satellite:
    private void sendFileFromDeviceToSatellite(Device fromDevice, SatelliteConstructor toSatellite, String fileName)
            throws FileTransferException {
        FileConstructor fileTransfer = fromDevice.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";

        if (fileTransfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        if (fromDevice.getUsedBandwidth() + fileTransfer.getFileSize() > fromDevice.getTotalBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(fromDevice.getDeviceId());
        }

        if (toSatellite.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toSatellite.getTotalFiles() >= toSatellite.getMaxFileCap()) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxFiles);
            } else {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxStorage);
            }
        }

        fromDevice.removeFile(fileTransfer);
        toSatellite.addFile(fileTransfer);
        System.out.println("File size: " + fileTransfer.getFileSize());
        System.out.println("Used bandwidth before transfer: " + fromDevice.getUsedBandwidth());
        System.out.println("Total bandwidth: " + fromDevice.getTotalBandwidth());
    }

    // Helper method to send file From Satellite to Device:
    private void sendFileFromSatelliteToDevice(SatelliteConstructor fromSatellite, Device toDevice, String fileName)
            throws FileTransferException {
        FileConstructor fileTransfer = fromSatellite.getFileByID(fileName);
        String maxFiles = "Max Files Reached";
        String maxStorage = "Max Storage Reached";

        if (fileTransfer == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        if (fromSatellite.getUsedBandwidth() + fileTransfer.getFileSize() > fromSatellite.getTotalBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(fromSatellite.getSatelliteId());
        }

        if (toDevice.getAvailableBandwidth() < fileTransfer.getFileSize()) {
            if (toDevice.getTotalFiles() >= toDevice.getMaxFileCap()) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxFiles);
            } else {
                throw new FileTransferException.VirtualFileNoStorageSpaceException(maxStorage);
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
