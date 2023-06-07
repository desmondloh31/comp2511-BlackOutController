package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class HandheldDevice extends DeviceConstructor {
    private final float maxDistance = 50000;

    public HandheldDevice(String deviceId, String deviceType, Angle devicePosition) {
        super(deviceId, "HandheldDevice", devicePosition);
    }

    public EntityInfoResponse getInfo() {
        String deviceId = getDeviceId();
        Angle devicePosition = getDevicePosition();
        String deviceType = getDeviceType();
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
        for (SatelliteConstructor satellite : blackout.getSatelliteList()) {
            if (withinVisibleRange(satellite)) {
                list.add(satellite.getSatelliteId());
            }
        }
        return list;
    }

    private boolean withinVisibleRange(SatelliteConstructor satellite) {
        double distance = MathsHelper.getDistance(satellite.getSatelliteHeight(), satellite.getSatellitePosition(),
                getDevicePosition());
        boolean visible = MathsHelper.isVisible(satellite.getSatelliteHeight(), satellite.getSatellitePosition(),
                getDevicePosition());
        return distance <= maxDistance && visible;

    }

}
