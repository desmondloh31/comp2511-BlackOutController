package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StandardSatellite extends Satellite {
    private static final double linearVelocity = 2500;
    private static final double maxDistance = 150000;
    private final Angle radianShift = Angle.fromRadians(linearVelocity / MathsHelper.RADIUS_OF_JUPITER);

    public StandardSatellite(String satelliteId, String satelliteType, double satelliteHeight,
            Angle satellitePosition) {
        super(satelliteId, "StandardSatellite", satelliteHeight, satellitePosition);
    }

    // updates the movement of the StandardSatellite Satellite type
    public void updateSatellitePosition() {
        Angle currentPosition = this.getSatellitePosition();
        Angle updatedPosition = currentPosition.subtract(radianShift);
        this.setSatellitePosition(updatedPosition);
    }

    // helper to generate a FileInfoResponse to be used to get Satellite Info in
    // getInfo()
    private FileInfoResponse createResponse(FileConstructor fileResponse) {
        String fileName = fileResponse.getFileName();
        String fileContent = fileResponse.getFileDetails();
        int fileSize = fileContent.length();
        return new FileInfoResponse(fileName, fileContent, fileSize, true);
    }

    public EntityInfoResponse getInfo() {
        List<FileConstructor> fileList = this.getFileList();
        Map<String, FileInfoResponse> map = new HashMap<>();

        for (FileConstructor file : fileList) {
            FileInfoResponse info = createResponse(file);
            map.put(file.getFileName(), info);
        }
        return new EntityInfoResponse(this.getSatelliteId(), this.getSatellitePosition(), this.getSatelliteHeight(),
                this.getSatelliteType(), map);
    }

    public List<String> updateList(BlackoutController blackout) {
        List<String> newList = new ArrayList<>();
        updateSatelliteList(blackout, newList);
        updateDeviceList(blackout, newList);
        return newList;
    }

    private void updateSatelliteList(BlackoutController blackout, List<String> list) {
        for (Satellite satellite : blackout.getSatelliteList()) {
            if (!satellite.getSatelliteId().equals(this.getSatelliteId())
                    && checkInRangeAndVisibility(satellite.getSatelliteHeight(), satellite.getSatellitePosition())) {
                list.add(satellite.getSatelliteId());
            }
        }
    }

    private void updateDeviceList(BlackoutController blackout, List<String> list) {
        for (Device device : blackout.getDeviceList()) {
            if ((device.getDeviceType().equals("HandheldDevice") || device.getDeviceType().equals("LaptopDevice"))
                    && checkInRangeAndVisibility(device.getDevicePosition())) {
                list.add(device.getDeviceId());
            }
        }
    }

    private boolean checkInRangeAndVisibility(Angle position) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(), position);
        return distance <= maxDistance
                && MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(), position);
    }

    private boolean checkInRangeAndVisibility(double height, Angle position) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(), height,
                position);
        return distance <= maxDistance
                && MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(), height, position);
    }
}
