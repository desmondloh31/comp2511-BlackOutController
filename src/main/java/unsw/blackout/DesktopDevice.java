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

public class DesktopDevice extends Device {
    private final double maxDistance = 200000;

    public DesktopDevice(String deviceId, String deviceType, Angle devicePosition) {
        super("DesktopDevice", deviceId, devicePosition);
    }

    public EntityInfoResponse getInfo() {
        String deviceType = super.getType();
        String deviceId = super.getId();
        Angle devicePosition = super.getPosition();
        double deviceHeight = RADIUS_OF_JUPITER;

        Map<String, FileInfoResponse> map = new HashMap<>();
        for (FileConstructor file : super.getFileList()) {
            String fileName = file.getFileName();
            String fileDetails = file.getFileDetails();
            int fileSize = fileDetails.length();
            FileInfoResponse fileInfo = new FileInfoResponse(fileName, fileDetails, fileSize, true);
            map.put(fileName, fileInfo);
        }
        return new EntityInfoResponse(deviceId, devicePosition, deviceHeight, deviceType, map);
    }

    public List<String> updateList(BlackoutController blackout) {
        List<String> list = new ArrayList<>();
        List<Satellite> satellites = blackout.getSatelliteList();
        for (Satellite satellite : satellites) {
            if (("TeleportingSatellite".equals(satellite.getType()) || "RelaySatellite".equals(satellite.getType()))
                    && withinVisibleRange(satellite)) {
                list.add(satellite.getId());
            }
        }
        return list;
    }

    private boolean withinVisibleRange(Satellite satellite) {
        double distance = MathsHelper.getDistance(satellite.getHeight(), satellite.getPosition(), super.getPosition());
        boolean visible = MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(), super.getPosition());
        return distance <= maxDistance && visible;
    }
}
