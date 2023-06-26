package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Map.Entry;

public class TeleportingSatellite extends Satellite {
    private static final double maxDistance = 200000;
    private static final double linearVelocity = 1000;
    private Angle radianShift = Angle.fromRadians(linearVelocity / MathsHelper.RADIUS_OF_JUPITER);
    private boolean isMovingAntiClokwise = true;

    public TeleportingSatellite(String satelliteId, String satelliteType, double satelliteHeight,
            Angle satellitePosition) {
        super(satelliteId, "TeleportingSatellite", satelliteHeight, satellitePosition);
    }

    public void updateSatellitePosition() {
        Angle currentPosition = this.getSatellitePosition();
        if (currentPosition.toDegrees() >= 205) {
            teleportSatellite();
        } else {
            moveSatellite();
        }
    }

    private void moveSatellite() {
        Angle updatedPosition = this.getSatellitePosition().add(radianShift);
        this.setSatellitePosition(updatedPosition);
    }

    private void teleportSatellite() {
        this.setSatellitePosition(Angle.fromDegrees(0));
        isMovingAntiClokwise = !isMovingAntiClokwise;
        if (this.fileTransfers == null) {
            return;
        }
        for (Iterator<Entry<String, FileTransfer>> iterator = this.fileTransfers.entrySet().iterator(); iterator
                .hasNext();) {
            Entry<String, FileTransfer> entry = iterator.next();
            FileTransfer transfer = entry.getValue();
            if (transfer == null) {
                continue;
            }
            FileConstructor file = transfer.getFile();
            if (file == null) {
                continue;
            }
            String newFile = file.getFileDetails();
            if (newFile == null) {
                continue;
            }
            newFile = newFile.replace("t", "");
            if (transfer.getDirection() == FileTransfer.Direction.UPLOAD) {
                fileUpload(iterator, file, newFile, transfer);
            } else if (transfer.getDirection() == FileTransfer.Direction.DOWNLOAD) {
                fileDownload(file, newFile, transfer);
            }

        }
    }

    // Helper functions for fileUpload and fileDownload:
    private void fileUpload(Iterator<Entry<String, FileTransfer>> iterator, FileConstructor file, String newFile,
            FileTransfer transfer) {
        iterator.remove();
        this.files.remove(file.getFileName());
        Device source = transfer.getSourceDevice();
        if (source != null && source.getFiles() != null && source.getFiles().get(file.getFileName()) != null) {
            source.getFiles().get(file.getFileName()).setFileDetails(newFile);
        }
    }

    private void fileDownload(FileConstructor file, String newFile, FileTransfer transfer) {
        file.setFileDetails(newFile);
        transfer.setBytesTransferred(file.getFileSize());
    }

    public EntityInfoResponse getInfo() {
        String satelliteId = this.getSatelliteId();
        String satelliteType = this.getSatelliteType();
        Angle satellitePosition = this.getSatellitePosition();
        double satelliteHeight = this.getSatelliteHeight();
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
        for (Satellite satellite : blackout.getSatelliteList()) {
            if (!satellite.getSatelliteId().equals(this.getSatelliteId()) && isSatelliteInRange(satellite)
                    && isSatelliteVisible(satellite)) {
                list.add(satellite.getSatelliteId());
            }
        }
        for (Device device : blackout.getDeviceList()) {
            if (isDeviceInRange(device) && isDeviceVisible(device)) {
                list.add(device.getDeviceId());
            }
        }
        return list;
    }

    // checks if device is in range:
    public boolean isDeviceInRange(Device device) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
        return distance <= maxDistance;
    }

    // checks if satellite is in range:
    public boolean isSatelliteInRange(Satellite satellite) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());
        return distance <= maxDistance;
    }

    // checks if device is visible:
    public boolean isDeviceVisible(Device device) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
    }

    // checks if satellite is visible:
    public boolean isSatelliteVisible(Satellite satellite) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());
    }
}
