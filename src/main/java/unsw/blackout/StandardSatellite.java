package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StandardSatellite extends SatelliteConstructor {
    private static final double linearVelocity = 2500;
    private static final double maxDistance = 150000;
    private final Angle radianShift = Angle.fromRadians(linearVelocity / MathsHelper.RADIUS_OF_JUPITER);

    public StandardSatellite(String satelliteId, String satelliteType, double satelliteHeight,
            Angle satellitePosition) {
        super(satelliteId, "StandardSatellite", satelliteHeight, satellitePosition);
    }

    public void updatePosition() {
        Angle currentPosition = this.getSatellitePosition();
        Angle updatedPosition = radianShift.add(currentPosition);
        this.setSatellitePosition(updatedPosition);
    }

    public EntityInfoResponse getInfo() {
        String satelliteId = this.getSatelliteId();
        String satelliteType = this.getSatelliteType();
        Angle satellitePosition = this.getSatellitePosition();
        double satelliteHeight = this.getSatelliteHeight();

        List<FileConstructor> fileList = this.getFileList();
        Map<String, FileInfoResponse> map = new HashMap<>();

        for (FileConstructor file : fileList) {
            String fileName = file.getFileName();
            String fileDetails = file.getFileDetails();
            int fileSize = fileDetails.length();
            FileInfoResponse info = new FileInfoResponse(fileName, fileDetails, fileSize, true);
            map.put(fileName, info);
        }
        return new EntityInfoResponse(satelliteId, satellitePosition, satelliteHeight, satelliteType, map);
    }

    public List<String> updateList(BlackoutController blackout) {
        List<String> list = new ArrayList<>();
        for (SatelliteConstructor satellite : blackout.getSatelliteList()) {
            if (!satellite.getSatelliteId().equals(this.getSatelliteId()) && isSatelliteInRange(satellite)
                    && isSatelliteVisible(satellite)) {
                list.add(satellite.getSatelliteId());
            }
        }
        for (DeviceConstructor device : blackout.getDeviceList()) {
            if ((device.getDeviceType().equals("HandheldDevice") || device.getDeviceType().equals("LaptopDevice"))
                    && isDeviceInRange(device) && isDeviceVisible(device)) {
                list.add(device.getDeviceId());
            }
        }

        return list;
    }

    public boolean isDeviceInRange(DeviceConstructor device) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
        return distance <= maxDistance;
    }

    public boolean isSatelliteInRange(SatelliteConstructor satellite) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());
        return distance <= maxDistance;
    }

    public boolean isDeviceVisible(DeviceConstructor device) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
    }

    public boolean isSatelliteVisible(SatelliteConstructor satellite) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());

    }
}
