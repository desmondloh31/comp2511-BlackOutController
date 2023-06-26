package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileEntity {
    private String id;
    private String type;
    private Angle position;

    protected int maxBandWidth;
    protected int currentBandwidth;
    protected int maxStorageSpace;
    protected int usedStorageSpace;
    protected int maxFileCap;

    protected List<FileConstructor> fileList = new ArrayList<>();
    protected Map<String, FileTransfer> fileTransfers;
    protected Map<String, FileConstructor> files;

    public FileEntity(String id, String type, Angle position) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.fileTransfers = new HashMap<>();
        this.files = new HashMap<>();
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

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public Angle getPosition() {
        return this.position;
    }

    public List<FileConstructor> getFileList() {
        return fileList;
    }

    public void addFile(FileConstructor file) {
        fileList.add(file);
        currentBandwidth++;
    }

    public void removeFile(FileConstructor file) {
        if (!fileList.contains(file)) {
            return; // or throw an exception
        }
        fileList.remove(file);
        currentBandwidth = Math.max(0, currentBandwidth - 1);
    }

    public Map<String, FileTransfer> getFileTransfers() {
        return this.fileTransfers;
    }

    public Map<String, FileConstructor> getFiles() {
        return this.files;
    }

    public abstract EntityInfoResponse getInfo();

    public abstract List<String> updateList(BlackoutController controller);
}
