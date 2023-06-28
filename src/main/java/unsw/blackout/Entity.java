package unsw.blackout;

public interface Entity {
    String getId();

    FileConstructor getFileByID(String id);

    double getUsedBandwidth();

    double getTotalBandwidth();

    double getAvailableBandwidth();

    int getTotalFiles();

    int getMaxFileCap();

    void removeFile(FileConstructor file);

    void addFile(FileConstructor file);
}
