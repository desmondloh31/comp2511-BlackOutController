package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class RelaySatellite extends SatelliteConstructor {
    private static final double maxDistance = 300000;
    private static final double linearVelocity = 1500;
    private boolean directionShift = false;
    private final Angle radianShift = Angle.fromRadians(linearVelocity / RADIUS_OF_JUPITER);

    public RelaySatellite(String satelliteId, String satelliteType, double satelliteHeight, Angle satellitePosition) {
        super(satelliteId, "RelaySatellite", satelliteHeight, satellitePosition);
    }

    public void updatePosition() {
        Angle currentPosition = this.getSatellitePosition();
        if (currentPosition.toDegrees() <= 140 || currentPosition.toDegrees() >= 345) {
            increment(currentPosition);
            directionShift = false;
        } else if (currentPosition.toDegrees() >= 190 && currentPosition.toDegrees() < 345) {
            decrement(currentPosition);
            directionShift = true;
        } else {
            if (directionShift) {
                decrement(currentPosition);
            } else {
                increment(currentPosition);
            }
        }
    }

    // increasing position of RelaySatellite:
    private void increment(Angle satellitePosition) {
        Angle increment = satellitePosition.add(radianShift);
        this.setSatellitePosition(increment);
    }

    // decreasing position of RelaySatellite:
    private void decrement(Angle satellitePosition) {
        Angle decrement = satellitePosition.subtract(radianShift);
        this.setSatellitePosition(decrement);
    }

    public EntityInfoResponse getInfo() {
        String satelliteId = this.getSatelliteId();
        String satelliteType = this.getSatelliteType();
        double satelliteHeight = this.getSatelliteHeight();
        Angle satellitePosition = this.getSatellitePosition();

        Map<String, FileInfoResponse> map = new HashMap<>();
        for (FileConstructor file : this.getFileList()) {
            String fileName = file.getFileName();
            String fileDetails = file.getFileDetails();
            int fileSize = fileDetails.length();
            FileInfoResponse fileInfo = new FileInfoResponse(fileName, fileDetails, fileSize, true);
            map.put(fileName, fileInfo);
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
            if (isDeviceInRange(device) && isDeviceVisible(device)) {
                list.add(device.getDeviceId());
            }
        }
        return list;
    }

    // checks if device is in range:
    public boolean isDeviceInRange(DeviceConstructor device) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
        return distance <= maxDistance;
    }

    // checks if satellite is in range:
    public boolean isSatelliteInRange(SatelliteConstructor satellite) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());
        return distance <= maxDistance;
    }

    // checks if device is visible:
    public boolean isDeviceVisible(DeviceConstructor device) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
    }

    // checks if satellite is visible:
    public boolean isSatelliteVisible(SatelliteConstructor satellite) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());
    }
}
