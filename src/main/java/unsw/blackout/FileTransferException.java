package unsw.blackout;

public class FileTransferException extends Exception {
    public FileTransferException(String message) {
        super(message);
    }

    public static class VirtualFileNotFoundException extends FileTransferException {
        public VirtualFileNotFoundException(String message) {
            super(message);
        }
    }

    public static class VirtualFileAlreadyExistsException extends FileTransferException {
        public VirtualFileAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InsufficientBandwidthException extends FileTransferException {
        public InsufficientBandwidthException(String message) {
            super(message);
        }
    }

    public static class InsufficientStorageException extends FileTransferException {
        public InsufficientStorageException(String message) {
            super(message);
        }
    }
}
