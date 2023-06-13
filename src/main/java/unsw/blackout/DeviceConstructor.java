package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DeviceConstructor {
    private String deviceType;
    private String deviceId;
    private Angle devicePosition;

    private List<FileConstructor> fileList = new ArrayList<FileConstructor>();

    // constructor for device constructor (i.e any device)
    public DeviceConstructor(String deviceType, String deviceId, Angle devicePosition) {
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.devicePosition = devicePosition;
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
    }

    public abstract EntityInfoResponse getInfo();

    public abstract List<String> updateList(BlackoutController controller);

    protected Map<String, FileTransfer> fileTransfers;
    protected Map<String, FileConstructor> files;

    public DeviceConstructor() {
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
