package unsw.blackout;

public class FileTransferException extends Exception {
    public FileTransferException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public static class VirtualFileNotFoundException extends FileTransferException {
        public VirtualFileNotFoundException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }

    public static class VirtualFileNoBandwidthException extends FileTransferException {
        public VirtualFileNoBandwidthException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }

    public static class VirtualFileAlreadyExistsException extends FileTransferException {
        public VirtualFileAlreadyExistsException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }

    public static class VirtualFileNoStorageException extends FileTransferException {
        public VirtualFileNoStorageException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }
}
