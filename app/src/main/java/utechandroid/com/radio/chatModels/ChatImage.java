package utechandroid.com.radio.chatModels;

/**
 * Created by Utsav Shah on 14-Dec-17.
 */

public class ChatImage {

    public String getLocalPath() {
        return localPath;
    }

    public ChatImage setLocalPath(String localPath) {
        this.localPath = localPath;
        return this;
    }

    public ChatImage() {
    }

    public String getStoragePath() {
        return StoragePath;
    }

    public ChatImage setStoragePath(String storagePath) {
        StoragePath = storagePath;
        return this;
    }

    public long getStorageSize() {
        return StorageSize;
    }

    public ChatImage setStorageSize(long storageSize) {
        StorageSize = storageSize;
        return this;
    }

    public ChatImage(String text, String localPath, String storagePath, long storageSize) {
        this.text = text;
        this.localPath = localPath;
        StoragePath = storagePath;
        StorageSize = storageSize;
    }

    public String getText() {
        return text;
    }

    public ChatImage setText(String text) {
        this.text = text;
        return this;
    }

    private String text;
    private String localPath;
    private String StoragePath;
    private long StorageSize;
}
