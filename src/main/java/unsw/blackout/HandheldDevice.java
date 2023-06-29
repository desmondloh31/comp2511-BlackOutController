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

public class HandheldDevice extends Device {
    private final float maxDistance = 50000;

    public HandheldDevice(String deviceId, String deviceType, Angle devicePosition) {
        super("HandheldDevice", deviceId, devicePosition);
    }

    public EntityInfoResponse getInfo() {
        String deviceId = super.getId();
        Angle devicePosition = super.getPosition();
        String deviceType = super.getType();
        double deviceHeight = RADIUS_OF_JUPITER;

        Map<String, FileInfoResponse> map = new HashMap<>();
        for (FileConstructor file : getFileList()) {
            String fileData = file.getFileDetails();
            String fileName = file.getFileName();
            int fileSize = fileData.length();
            boolean transferSuccess = true;
            FileInfoResponse fileInfo = new FileInfoResponse(fileName, fileData, fileSize, transferSuccess);
            map.put(fileName, fileInfo);

        }

        return new EntityInfoResponse((deviceId), devicePosition, deviceHeight, deviceType, map);
    }

    public List<String> updateList(BlackoutController blackout) {
        List<String> list = new ArrayList<>();
        for (Satellite satellite : blackout.getSatelliteList()) {
            if (withinVisibleRange(satellite)) {
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
