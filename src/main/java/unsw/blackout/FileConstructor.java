package unsw.blackout;

public class FileConstructor {
    private String fileName;
    private String fileDetails;

    // file constrctors:
    public FileConstructor(String fileName, String fileDetails) {
        this.fileName = fileName;
        this.fileDetails = fileDetails;
    }

    // getters and setters for getting and setting fileName and fileDetails:
    public String getFileName() {
        return this.fileName;
    }

    public String getFileDetails() {
        return this.fileDetails;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileDetails(String fileDetails) {
        this.fileDetails = fileDetails;
    }
}
