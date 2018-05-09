package utechandroid.com.radio.chatModels;

/**
 * Created by Utsav Shah on 12/16/2017.
 */

public class chatPdf {

    public String getLocalPath() {
        return localPath;
    }

    public chatPdf setLocalPath(String localPath) {
        this.localPath = localPath;
        return this;
    }

    public chatPdf() {
    }

    public String getStoragePath() {
        return StoragePath;
    }

    public chatPdf setStoragePath(String storagePath) {
        StoragePath = storagePath;
        return this;
    }

    public long getStorageSize() {
        return StorageSize;
    }

    public chatPdf setStorageSize(long storageSize) {
        StorageSize = storageSize;
        return this;
    }

    public chatPdf(String text, String localPath, String storagePath, long storageSize, String filename) {
        this.text = text;
        this.localPath = localPath;
        StoragePath = storagePath;
        StorageSize = storageSize;
        this.filename = filename;
    }

    public String getText() {
        return text;
    }

    public chatPdf setText(String text) {
        this.text = text;
        return this;
    }

    private String text;
    private String localPath;
    private String StoragePath;




    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private String filename;
    private long StorageSize;

}
