package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import java.util.*;

public abstract class FileEntity {
    protected String id;
    protected Angle position;

    protected int maxBandWidth;
    protected int currentBandwidth;
    protected int maxStorageSpace;
    protected int usedStorageSpace;
    protected int maxFileCap;

    protected List<FileConstructor> fileList = new ArrayList<>();
    protected Map<String, FileTransfer> fileTransfers;
    protected Map<String, FileConstructor> files;

    public FileEntity() {
        this.fileTransfers = new HashMap<>();
        this.files = new HashMap<>();
    }

    // Shared methods between Satellite and Device
    public FileConstructor getFileByID(String fileName) {
        for (FileConstructor file : this.fileList) {
            if (file.getFileName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

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

    public List<FileConstructor> getFileList() {
        return fileList;
    }

    public void addFile(FileConstructor file) {
        fileList.add(file);
    }

    public void removeFile(FileConstructor file) {
        fileList.remove(file);
    }

    public Map<String, FileTransfer> getFileTransfers() {
        return this.fileTransfers;
    }

    public Map<String, FileConstructor> getFiles() {
        return this.files;
    }

    // Abstract methods that need to be implemented in child classes
    public abstract EntityInfoResponse getInfo();

    public abstract void updatePosition();

    public abstract List<String> updateList(BlackoutController controller);
}
