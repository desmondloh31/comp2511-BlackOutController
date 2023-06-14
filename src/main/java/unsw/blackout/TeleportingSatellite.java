package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeleportingSatellite extends SatelliteConstructor {
    private static final double maxDistance = 200000;
    private static final double linearVelocity = 1000;
    private Angle radianShift = Angle.fromRadians(linearVelocity / MathsHelper.RADIUS_OF_JUPITER);
    private boolean clockWiseShift = false;

    public TeleportingSatellite(String satelliteId, String satelliteType, double satelliteHeight,
            Angle satellitePosition) {
        super(satelliteId, "TeleportingSatellite", satelliteHeight, satellitePosition);
    }

    public void updatePosition() {
        Angle oldPosition = super.getSatellitePosition();
        if (clockWiseShift) {
            oldPosition = oldPosition.subtract(radianShift);
        } else {
            oldPosition = oldPosition.add(radianShift);
        }
        super.setSatellitePosition(oldPosition);

        if (oldPosition.toDegrees() >= 180) {
            super.setSatellitePosition(Angle.fromDegrees(0));
            clockWiseShift = !clockWiseShift;
            teleportSatellite();
        }
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
        List<DeviceConstructor> devices = new ArrayList<DeviceConstructor>();
        List<SatelliteConstructor> satellites = new ArrayList<SatelliteConstructor>();
        devices = blackout.getDeviceList();
        satellites = blackout.getSatelliteList();
        for (SatelliteConstructor satellite : satellites) {
            if (satellite.getSatelliteId().equals(super.getSatelliteId())) {
                continue;
            } else {
                if (isSatelliteInRange(satellite) && isSatelliteVisible(satellite)) {
                    list.add(satellite.getSatelliteId());
                }
            }
        }

        for (DeviceConstructor device : devices) {
            if (isDeviceVisible(device)) {
                list.add(device.getDeviceId());
            }
        }
        return list;
    }

    // function to handle the teleporting method for the satellite:
    public void teleportSatellite() {
        if (Math.abs(this.getSatellitePosition().toDegrees() - 180) < 1e-6) {
            this.setSatellitePosition(Angle.fromDegrees(0));
        }
        Map<String, FileTransfer> fileTransfers = this.getFileTransfers();
        if (fileTransfers != null) {
            for (Map.Entry<String, FileTransfer> entry : fileTransfers.entrySet()) {
                FileTransfer transfer = entry.getValue();
                if (transfer != null && transfer.isInProgress()) {
                    if (transfer.getDirection() == FileTransfer.Direction.UPLOAD) {
                        FileConstructor file = transfer.getFile();
                        if (file != null) {
                            String remainingData = file.getFileDetails();
                            if (remainingData != null) {
                                remainingData = remainingData.substring(transfer.getBytesTransferred());
                                String filteredData = remainingData.replace("t", "").replace("T", "");
                                file.setFileDetails(filteredData);
                                transfer.setBytesTransferred(file.getFileSize());
                            }
                        }
                    }
                    // Handle device to satellite dataTransfer:
                    else if (transfer.getDirection() == FileTransfer.Direction.DOWNLOAD) {
                        String fileName = transfer.getFile().getFileName();
                        this.getFiles().remove(fileName);
                        DeviceConstructor sourceDevice = transfer.getSourceDevice();
                        if (sourceDevice != null) {
                            Map<String, FileConstructor> deviceFiles = sourceDevice.getFiles();
                            if (deviceFiles != null) {
                                FileConstructor deviceFile = deviceFiles.get(fileName);
                                if (deviceFile != null) {
                                    String deviceData = deviceFile.getFileDetails();
                                    if (deviceData != null) {
                                        String filteredData = deviceData.replace("t", "").replace("T", "");
                                        deviceFile.setFileDetails(filteredData);
                                        fileTransfers.remove(entry.getKey());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
