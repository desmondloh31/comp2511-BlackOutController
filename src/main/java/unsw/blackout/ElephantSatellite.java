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

public class ElephantSatellite extends Satellite {
    private static final double maxDistance = 400000;
    private static final double linearVelocity = 2500;
    private static final int maxStorage = 90;
    private static final int bytesRecieved = 20;
    private static final int bytesSent = 20;
    private int currentDownloadBandwidth;
    private int currentUploadBandwidth;
    private Angle radianShift = Angle.fromRadians(linearVelocity / MathsHelper.RADIUS_OF_JUPITER);

    private Map<String, FileTransfer> transientFiles;
    private Map<String, FileTransfer> fileTransfers;
    List<Device> devices = new ArrayList<Device>();
    List<Satellite> satellites = new ArrayList<Satellite>();

    public ElephantSatellite(String satelliteId, String satelliteType, double satelliteHeight,
            Angle satellitePosition) {
        super(satelliteId, "ElephantSatellite", satelliteHeight, satellitePosition);
        this.transientFiles = new HashMap<>();
        this.fileTransfers = new HashMap<>();
        this.currentDownloadBandwidth = 0;
        this.currentUploadBandwidth = 0;
    }

    public void updatePosition() {
        Angle updatedPosition = this.getSatellitePosition().add(radianShift);
        this.setSatellitePosition(updatedPosition);
        for (Map.Entry<String, FileTransfer> entry : this.fileTransfers.entrySet()) {
            FileTransfer transfer = entry.getValue();
            if (transfer.getDirection() == FileTransfer.Direction.UPLOAD) {
                Device device = findDeviceById(transfer.getTargetId());
                Satellite satellite = findSatelliteById(transfer.getTargetId());
                if (device != null && !isDeviceVisible(device) || satellite != null && !isSatelliteVisible(satellite)) {
                    transfer.setBytesTransferred(0);
                    transfer.setBytesRemaining(transfer.getFile().getFileSize());
                }
            }
            handleFileTransfer(transfer);
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
        for (Satellite satellite : blackout.getSatelliteList()) {
            if (!satellite.getSatelliteId().equals(this.getSatelliteId()) && isSatelliteInRange(satellite)
                    && isSatelliteVisible(satellite)) {
                list.add(satellite.getSatelliteId());
            }
        }
        for (Device device : blackout.getDeviceList()) {
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
        for (String keptFile : filesKept) {
            Device device = findDeviceById(transientFiles.get(keptFile).getTargetId());
            Satellite satellite = findSatelliteById(transientFiles.get(keptFile).getTargetId());
            if ((device != null && isDeviceVisible(device)) || (satellite != null && isSatelliteVisible(satellite))) {
                fileTransfers.put(keptFile, transientFiles.get(keptFile));
                transientFiles.remove(keptFile);
            }
        }
    }

    private void processFileTransfer(FileTransfer transfer) {
        // conditions for download request:
        if (transfer.getDirection() == FileTransfer.Direction.DOWNLOAD) {
            int bytesToBeRecieved = Math.min(transfer.getBytesRemaining(), bytesRecieved);
            if (currentDownloadBandwidth + bytesToBeRecieved <= bytesRecieved) {
                currentDownloadBandwidth += bytesToBeRecieved;
                transfer.setBytesTransferred(transfer.getBytesTransferred() + bytesToBeRecieved);
                transfer.setBytesRemaining(transfer.getBytesRemaining() - bytesToBeRecieved);
                // condition if the download process is completed:
                if (transfer.getBytesRemaining() == 0) {
                    FileConstructor downloadComplete = transfer.getFile();
                    this.files.put(downloadComplete.getFileName(), downloadComplete);
                    currentDownloadBandwidth -= bytesToBeRecieved;
                }
            }
        }
        // conditions for upload request:
        else if (transfer.getDirection() == FileTransfer.Direction.UPLOAD) {
            int bytesToSend = Math.min(transfer.getBytesRemaining(), bytesSent);
            if (currentUploadBandwidth + bytesToSend <= bytesSent) {
                currentUploadBandwidth += bytesToSend;
                transfer.setBytesTransferred(transfer.getBytesTransferred() + bytesToSend);
                transfer.setBytesRemaining(transfer.getBytesRemaining() - bytesToSend);
                // conditions if file upload has been completed:
                if (transfer.getBytesRemaining() == 0) {
                    FileConstructor uploadComplete = transfer.getFile();
                    this.files.remove(uploadComplete.getFileName());
                    currentUploadBandwidth -= bytesToSend;
                }
            }
        }
    }

    public boolean isDeviceInRange(Device device) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
        return distance <= maxDistance;
    }

    public boolean isSatelliteInRange(Satellite satellite) {
        double distance = MathsHelper.getDistance(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());
        return distance <= maxDistance;
    }

    public boolean isDeviceVisible(Device device) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                device.getDevicePosition());
    }

    public boolean isSatelliteVisible(Satellite satellite) {
        return MathsHelper.isVisible(this.getSatelliteHeight(), this.getSatellitePosition(),
                satellite.getSatelliteHeight(), satellite.getSatellitePosition());

    }

    // Helper function that finds device By Id:
    private Device findDeviceById(String deviceId) {
        for (Device device : this.devices) {
            if (device.getDeviceId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    // Helper function that finds satellite by Id:
    private Satellite findSatelliteById(String satelliteId) {
        for (Satellite satellite : this.satellites) {
            if (satellite.getSatelliteId().equals(satelliteId)) {
                return satellite;
            }
        }
        return null;
    }
}
