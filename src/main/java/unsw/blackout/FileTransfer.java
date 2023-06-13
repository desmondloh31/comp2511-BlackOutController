package unsw.blackout;

public class FileTransfer {
    public enum Direction {
        UPLOAD, DOWNLOAD
    }

    private FileConstructor file;
    private DeviceConstructor sourceDevice;
    private Direction direction;
    private int bytesTransferred;

    public FileTransfer(FileConstructor file, DeviceConstructor sourceDevice, Direction direction) {
        this.file = file;
        this.sourceDevice = sourceDevice;
        this.direction = direction;
        this.bytesTransferred = 0;
    }

    // Getter for file
    public FileConstructor getFile() {
        return this.file;
    }

    // Setter for file
    public void setFile(FileConstructor file) {
        this.file = file;
    }

    // Getter for sourceDevice
    public DeviceConstructor getSourceDevice() {
        return this.sourceDevice;
    }

    // Setter for sourceDevice
    public void setSourceDevice(DeviceConstructor sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    // Getter for direction
    public Direction getDirection() {
        return this.direction;
    }

    // Setter for direction
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // Getter for bytesTransferred
    public int getBytesTransferred() {
        return this.bytesTransferred;
    }

    // Setter for bytesTransferred
    public void setBytesTransferred(int bytesTransferred) {
        this.bytesTransferred = bytesTransferred;
    }

    // Check if the transfer is in progress
    public boolean isInProgress() {
        // Assuming that if bytesTransferred is less than file size, transfer is in
        // progress.
        // Modify this as per your specific logic for file transfers
        return this.bytesTransferred < this.file.getFileSize();
    }

}
