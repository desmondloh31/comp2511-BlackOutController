package unsw.blackout;

import java.util.HashMap;
import java.util.Map;

public abstract class FileEntity {
    protected String Id;
    protected Map<String, FileConstructor> files;

    public FileEntity(String Id) {
        this.Id = Id;
        this.files = new HashMap<>();
    }

    public String getId() {
        return Id;
    }

    public boolean doesFileExist(String fileName) {
        return files.containsKey(fileName);
    }

    public abstract void addFile(FileConstructor file) throws FileTransferException;
}
