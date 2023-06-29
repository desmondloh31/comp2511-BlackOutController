package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class RelaySatellite extends Satellite {
    private static final double maxDistance = 300000;
    private static final double linearVelocity = 1500;
    private boolean directionShift = false;
    private final Angle radianShift = Angle.fromRadians(linearVelocity / RADIUS_OF_JUPITER);

    public RelaySatellite(String satelliteId, String satelliteType, double satelliteHeight, Angle satellitePosition) {
        super(satelliteId, "RelaySatellite", satelliteHeight, satellitePosition);
        if (satellitePosition.toDegrees() <= 180) {
            directionShift = true;
        }
    }

    public void updateSatellitePosition() {
        Angle currentPosition = super.getPosition();
        if (currentPosition.toDegrees() <= 140) {
            currentPosition = RelayMovement(currentPosition, radianShift, false);
            directionShift = false;
        } else if (currentPosition.toDegrees() > 345) {
            currentPosition = RelayMovement(currentPosition, radianShift, true);
            directionShift = true;
        } else {
            currentPosition = directionShift ? RelayMovement(currentPosition, radianShift, true)
                    : RelayMovement(currentPosition, radianShift, false);

        }
        this.setPosition(currentPosition);
    }

    private Angle RelayMovement(Angle satellitePosition, Angle angularShift, boolean decrement) {
        Angle updatedPosition = decrement ? satellitePosition.subtract(angularShift)
                : satellitePosition.add(angularShift);
        double accurateDecimalValue = new BigDecimal(updatedPosition.toRadians()).setScale(15, RoundingMode.HALF_UP)
                .doubleValue();
        return Angle.fromRadians(accurateDecimalValue);
    }

    // method to get necessary satellite info:
    public EntityInfoResponse getInfo() {
        Map<String, FileInfoResponse> map = new HashMap<>();
        for (FileConstructor file : this.getFileList()) {
            FileInfoResponse fileInfo = createResponse(file);
            map.put(file.getFileName(), fileInfo);
        }
        return new EntityInfoResponse(this.getId(), this.getPosition(), this.getHeight(), this.getType(), map);
    }

    // helper to generate a FileInfoResponse to be used to get Satellite Info in
    // getInfo()
    private FileInfoResponse createResponse(FileConstructor fileResponse) {
        String fileName = fileResponse.getFileName();
        String fileDetails = fileResponse.getFileDetails();
        int fileSize = fileDetails.length();
        return new FileInfoResponse(fileName, fileDetails, fileSize, true);
    }

    public List<String> updateList(BlackoutController blackout) {
        List<String> list = new ArrayList<>();
        for (Satellite satellite : blackout.getSatelliteList()) {
            if (!satellite.getId().equals(this.getId())
                    && checkInRangeAndVisibility(satellite.getHeight(), satellite.getPosition())) {
                list.add(satellite.getId());
            }
        }
        for (Device device : blackout.getDeviceList()) {
            if (checkInRangeAndVisibility(device.getPosition())) {
                list.add(device.getId());
            }
        }
        return list;
    }

    private boolean checkInRangeAndVisibility(Angle position) {
        double distance = MathsHelper.getDistance(this.getHeight(), this.getPosition(), position);
        return distance <= maxDistance && MathsHelper.isVisible(this.getHeight(), this.getPosition(), position);
    }

    private boolean checkInRangeAndVisibility(double height, Angle position) {
        double distance = MathsHelper.getDistance(this.getHeight(), this.getPosition(), height, position);
        return distance <= maxDistance && MathsHelper.isVisible(this.getHeight(), this.getPosition(), height, position);
    }
}
