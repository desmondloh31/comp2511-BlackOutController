package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Device {
    private String deviceType;
    private String deviceId;
    private Angle devicePosition;

    protected int maxBandWidth;
    protected int currentBandwidth;
    protected int maxStorageSpace;
    protected int usedStorageSpace;
    private int maxFileCap;

    private List<FileConstructor> fileList = new ArrayList<FileConstructor>();

    // constructor for device constructor (i.e any device)
    public Device(String deviceType, String deviceId, Angle devicePosition) {
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.devicePosition = devicePosition;
    }

    public FileConstructor getFileByID(String fileName) {
        for (FileConstructor file : this.fileList) {
            if (file.getFileName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    // bandwidth, storage space and FileTransfer parameters:
    public int getAvailableBandwidth() {
        return maxBandWidth - currentBandwidth;
    }

    public int getAvailableStorageSpace() {
        return maxStorageSpace - usedStorageSpace;
    }

    public int getUsedBandwidth() {
        return this.currentBandwidth;
    }

    public int getTotalBandwidth() {
        return this.maxBandWidth;
    }

    public int getUsedStorage() {
        int usedStorage = 0;
        for (FileConstructor file : this.fileList) {
            usedStorage += file.getFileSize();
        }
        return usedStorage;
    }

    public boolean hasEnoughBandwidth(int fileSize) {
        return this.currentBandwidth + 1 <= this.maxBandWidth;
    }

    public boolean hasEnoughStorageSpace(int fileSize) {
        int used = this.files.values().stream().mapToInt(FileConstructor::getFileSize).sum();
        return used + fileSize <= this.maxStorageSpace;
    }

    public int getTotalFiles() {
        return fileList.size();
    }

    public int getMaxFileCap() {
        return maxFileCap;
    }

    // getters and setters for deviceType, deviceId, and devicePosition:
    public String getDeviceType() {
        return this.deviceType;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Angle getDevicePosition() {
        return this.devicePosition;
    }

    // list all the files listed in the device:
    public List<FileConstructor> getFileList() {
        return fileList;
    }

    // adds respective file to device:
    public void addFile(FileConstructor file) {
        fileList.add(file);
        currentBandwidth++;
    }

    // removes file from device:
    public void removeFile(FileConstructor file) {
        if (!fileList.contains(file)) {
            return; // or throw an exception
        }
        fileList.remove(file);
        currentBandwidth = Math.max(0, currentBandwidth - 1);
    }

    public abstract EntityInfoResponse getInfo();

    public abstract List<String> updateList(BlackoutController controller);

    protected Map<String, FileTransfer> fileTransfers;
    protected Map<String, FileConstructor> files;

    public Device() {
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
