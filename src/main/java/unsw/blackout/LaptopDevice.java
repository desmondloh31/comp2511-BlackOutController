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

public class LaptopDevice extends Device {
    private static final float maxDistance = 100000;

    public LaptopDevice(String deviceId, String deviceType, Angle devicePosition) {
        super("LaptopDevice", deviceId, devicePosition);
    }

    public EntityInfoResponse getInfo() {

        List<FileConstructor> deviceFiles = super.getFileList();
        Map<String, FileInfoResponse> map = new HashMap<>();
        for (FileConstructor file : deviceFiles) {
            String fileName = file.getFileName();
            String fileDetails = file.getFileDetails();
            int fileSize = fileDetails.length();
            FileInfoResponse fileInfo = new FileInfoResponse((fileName), fileDetails, fileSize, true);
            map.put(fileName, fileInfo);
        }
        return new EntityInfoResponse(super.getId(), super.getPosition(), RADIUS_OF_JUPITER, super.getType(), map);
    }

    public List<String> updateList(BlackoutController blackout) {
        List<String> list = new ArrayList<>();
        List<Satellite> satellites = blackout.getSatelliteList();
        for (Satellite satellite : satellites) {
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
