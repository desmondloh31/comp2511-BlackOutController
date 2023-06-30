package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
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
        Angle currentPosition = this.getPosition();
        Angle updatedPosition = currentPosition.subtract(radianShift);
        this.setPosition(updatedPosition);
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
        return new EntityInfoResponse(this.getId(), this.getPosition(), this.getHeight(), this.getType(), map);
    }

    public List<String> updateList(BlackoutController blackout) {
        List<String> newList = new ArrayList<>();
        updateSatelliteList(blackout, newList);
        updateDeviceList(blackout, newList);
        return newList;
    }

    private void updateSatelliteList(BlackoutController blackout, List<String> list) {
        for (Satellite satellite : blackout.getSatelliteList()) {
            if (!satellite.getId().equals(this.getId())
                    && checkInRangeAndVisibility(satellite.getHeight(), satellite.getPosition())) {
                list.add(satellite.getId());
            }
        }
    }

    private void updateDeviceList(BlackoutController blackout, List<String> list) {
        for (Device device : blackout.getDeviceList()) {
            if ((device.getType().equals("HandheldDevice") || device.getType().equals("LaptopDevice"))
                    && checkInRangeAndVisibility(device.getPosition())) {
                list.add(device.getId());
            }
        }
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
