package bean;

import java.util.Date;

public class RztAzureObject
{
    private String fullObjectPath;

    public String getFullObjectPath() {
        return fullObjectPath;
    }

    public void setFullObjectPath(String fullObjectPath) {
        this.fullObjectPath = fullObjectPath;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    private Date lastModified;

    private long size;

    private boolean folder;

    public RztAzureObject( String fullObjectPath, Date lastModified, long size,
                           boolean folder )
    {
        this.fullObjectPath = fullObjectPath;
        this.lastModified = lastModified;
        this.size = size;
        this.folder = folder;
    }

    public RztAzureObject() {
    }
}