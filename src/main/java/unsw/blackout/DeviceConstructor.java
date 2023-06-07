package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public void setDeviceType() {
        this.deviceType = deviceType;
    }

    public void setDeviceId() {
        this.deviceId = deviceId;
    }

    public void setDevicePosition() {
        this.devicePosition = devicePosition;
    }

    // list all the files listed in the device:
    public List<FileConstructor> getFileList() {
        return fileList;
    }

    // adds respective file to device:
    public void addFile(FileConstructor file) {
        fileList.add(file);
    }

    // public abstract EntityInfoResponse getDeviceInfo();
    // public abstract List<String>
    // updateListOfCommunicableEntities(BlackoutController controller);

}
