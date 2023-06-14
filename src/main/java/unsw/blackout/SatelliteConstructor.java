package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SatelliteConstructor {
    private String satelliteId;
    private double satelliteHeight;
    private String satelliteType;
    private Angle satellitePosition;

    private final int maxStorageSpace = 100;
    private final int maxBandWidth = 100;
    private int currentBandwidth = 0;

    private List<FileConstructor> fileList = new ArrayList<FileConstructor>();

    public SatelliteConstructor(String satelliteId, String satelliteType, double satelliteHeight,
            Angle satellitePosition) {
        this.satelliteId = satelliteId;
        this.satelliteType = satelliteType;
        this.satelliteHeight = satelliteHeight;
        this.satellitePosition = satellitePosition;
    }

    // getters and setters for satelliteConstructor:
    public String getSatelliteId() {
        return this.satelliteId;
    }

    public double getSatelliteHeight() {
        return this.satelliteHeight;
    }

    public String getSatelliteType() {
        return this.satelliteType;
    }

    public Angle getSatellitePosition() {
        return this.satellitePosition;
    }

    public void setSatellitePosition(Angle satellitePosition) {
        this.satellitePosition = satellitePosition;
    }

    // list all the files listed in the device:
    public List<FileConstructor> getFileList() {
        return fileList;
    }

    // adds respective file to device:
    public void addFile(FileConstructor file) {
        fileList.add(file);
    }

    // bandwidth, storage space and FileTransfer parameters:
    public boolean hasEnoughBandwidth(int fileSize) {
        return this.currentBandwidth + 1 <= this.maxBandWidth;
    }

    public boolean hasEnoughStorageSpace(int fileSize) {
        int used = this.files.values().stream().mapToInt(FileConstructor::getFileSize).sum();
        return used + fileSize <= this.maxStorageSpace;
    }

    public void startFileTransfer(FileConstructor file, DeviceConstructor sourceDevice) {
        this.files.put(file.getFileName(), file);
        FileTransfer transfer = new FileTransfer(file, sourceDevice, FileTransfer.Direction.DOWNLOAD);
        this.fileTransfers.put(file.getFileName(), transfer);
        this.currentBandwidth++;
    }

    public FileConstructor getFileByName(String fileName) {
        for (FileConstructor file : this.fileList) {
            if (file.getFileName().equals(fileName)) {
                return file;
            }
        }

        return null;
    }

    public abstract EntityInfoResponse getInfo();

    public abstract void updatePosition();

    public abstract List<String> updateList(BlackoutController controller);

    protected Map<String, FileTransfer> fileTransfers;
    protected Map<String, FileConstructor> files;

    public SatelliteConstructor() {
        this.fileTransfers = new HashMap<>();
        this.files = new HashMap<>();
    }

    public Map<String, FileTransfer> getFileTransfers() {
        return this.fileTransfers;
    }

    public Map<String, FileConstructor> getFiles() {
        return this.files;
    }
}
