package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import java.util.ArrayList;
import java.util.List;

public abstract class SatelliteConstructor {
    private String satelliteId;
    private float satelliteHeight;
    private String satelliteType;
    private Angle satellitePosition;

    private List<FileConstructor> fileList = new ArrayList<FileConstructor>();

    public SatelliteConstructor(String satelliteId, float satelliteHeight, String satelliteType,
            Angle satellitePosition) {
        this.satelliteId = satelliteId;
        this.satelliteHeight = satelliteHeight;
        this.satelliteType = satelliteType;
        this.satellitePosition = satellitePosition;
    }

    // getters and setters for satelliteConstructor:
    public String getSatelliteId() {
        return this.satelliteId;
    }

    public float getSatelliteHeight() {
        return this.satelliteHeight;
    }

    public String getSatelliteType() {
        return this.satelliteType;
    }

    public Angle getSatellitePosition() {
        return this.satellitePosition;
    }

    public void setSatelliteId() {
        this.satelliteId = satelliteId;
    }

    public void setSatelliteHeight() {
        this.satelliteHeight = satelliteHeight;
    }

    public void setSatelliteType() {
        this.satelliteType = satelliteType;
    }

    public void setSatellitePosition() {
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

    // public abstract EntityInfoResponse getSatelliteInfo();
    // public abstract void updatePosition();
    // public abstract List<String>
    // updateListOfCommunicableEntities(BlackoutController controller);
}
