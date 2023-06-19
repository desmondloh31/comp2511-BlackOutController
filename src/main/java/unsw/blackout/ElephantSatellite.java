package unsw.blackout;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.MathsHelper;
import unsw.utils.Angle;

import java.util.List;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Map.Entry;

public class ElephantSatellite extends SatelliteConstructor {
    private static final double maxDistance = 400000;
    private static final double linearVelocity = 2500;
    private static final int maxStorage = 90;
    private static final int bytesRecieved = 20;
    private static final int bytesSent = 20;
    private Angle radianShift = Angle.fromRadians(linearVelocity / MathsHelper.RADIUS_OF_JUPITER);

    private Map<String, FileTransfer> transientFiles;
    private Map<String, FileTransfer> fileTransfers;

    public ElephantSatellite(String satelliteId, String satelliteType, double satelliteHeight,
            Angle satellitePosition) {
        super(satelliteId, "ElephantSatellite", satelliteHeight, satellitePosition);
        this.transientFiles = new HashMap<>();
        this.fileTransfers = new HashMap<>();
    }

    public void updatePosition() {
        Angle updatedPosition = this.getSatellitePosition().add(radianShift);
        this.setSatellitePosition(updatedPosition);
        for (Map.Entry<String, FileTransfer> entry : this.fileTransfers.entrySet()) {
            handleFileTransfer(entry.getValue());
        }

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
        for (SatelliteConstructor satellite : blackout.getSatelliteList()) {
            if (!satellite.getSatelliteId().equals(this.getSatelliteId()) && isSatelliteInRange(satellite)
                    && isSatelliteVisible(satellite)) {
                list.add(satellite.getSatelliteId());
            }
        }
        for (DeviceConstructor device : blackout.getDeviceList()) {
            if ((device.getDeviceType().equals("LaptopDevice") || device.getDeviceType().equals("DesktopDevice"))
                    && isDeviceInRange(device) && isDeviceVisible(device)) {
                list.add(device.getDeviceId());
            }
        }

        return list;
    }

    // implementation of the knapsack method:
    public void handleFileTransfer(FileTransfer transfer) {
        int storageNeeded = transfer.getFile().getFileSize();
        if (getUsedStorage() + storageNeeded > maxStorage) {
            newTransfer();
            if (getUsedStorage() + storageNeeded > maxStorage) {
                return;
            }
        }
        processFileTransfer(transfer);
    }

    private void newTransfer() {
        if (transientFiles.isEmpty()) {
            return;
        }
        int size = transientFiles.size();
        int maxBytes = maxStorage;
        List<Entry<String, FileTransfer>> transientList = new ArrayList<>(transientFiles.entrySet());
        int[][] dp = new int[size + 1][maxBytes + 1];

        for (int count = 1; count <= size; count++) {
            FileTransfer transfer = transientList.get(count - 1).getValue();
            int fileSize = transfer.getFile().getFileSize();
            int fileValue = transfer.getBytesTransferred();
            for (int countInner = 0; countInner <= maxBytes; countInner++) {
                if (fileSize <= countInner) {
                    dp[count][countInner] = Math.max(dp[count - 1][countInner],
                            dp[count - 1][countInner - fileSize] + fileValue);
                } else {
                    dp[count][countInner] = dp[count - 1][countInner];
                }
            }
        }

        List<String> filesKept = new ArrayList<>();
        int countInner = maxBytes;
        for (int count = size; count > 0 && count >= 0; count--) {
            if (dp[count][countInner] != dp[count - 1][countInner]) {
                filesKept.add(transientList.get(count - 1).getKey());
                countInner = countInner - transientList.get(count - 1).getValue().getFile().getFileSize();
            }
        }
        transientFiles.keySet().removeIf(setKey -> !filesKept.contains(setKey));
    }

    private void processFileTransfer(FileTransfer transfer) {
        // Download Request:
        if (transfer.getDirection() == FileTransfer.Direction.DOWNLOAD) {
            int storageToBeRecieved = Math.min(transfer.getBytesRemaining(), bytesRecieved);
            transfer.setBytesTransferred(transfer.getBytesTransferred() + storageToBeRecieved);
            transfer.setBytesRemaining(transfer.getBytesRemaining() - storageToBeRecieved);
            // if download process is completed:
            if (transfer.getBytesRemaining() == 0) {
                FileConstructor downloadComplete = transfer.getFile();
                this.files.put(downloadComplete.getFileName(), downloadComplete);
            }
        }
        // conditions for upload request:
        else if (transfer.getDirection() == FileTransfer.Direction.UPLOAD) {
            int storageToSend = Math.min(transfer.getBytesRemaining(), bytesSent);
            transfer.setBytesTransferred(transfer.getBytesTransferred() + storageToSend);
            transfer.setBytesRemaining(transfer.getBytesRemaining() - storageToSend);
            // condition if file upload has been completed:
            if (transfer.getBytesRemaining() == 0) {
                FileConstructor uploadComplete = transfer.getFile();
                this.files.remove(uploadComplete.getFileName());
            }
        }
    }

    public boolean isDeviceInRange(DeviceConstructor device) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
        return distance <= maxDistance;
    }

    public boolean isSatelliteInRange(SatelliteConstructor satellite) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());
        return distance <= maxDistance;
    }

    public boolean isDeviceVisible(DeviceConstructor device) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
    }

    public boolean isSatelliteVisible(SatelliteConstructor satellite) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());

    }
}
