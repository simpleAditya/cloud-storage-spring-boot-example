package gcpdataconnector.bean;

import lombok.Data;

import java.util.Date;

@Data
public class RztGoogleObject
{
    private String fullObjectPath;

    private Date lastModified;

    private long size;

    private boolean folder;

    public RztGoogleObject( String fullObjectPath, Date lastModified, long size,
                           boolean folder )
    {
        this.fullObjectPath = fullObjectPath;
        this.lastModified = lastModified;
        this.size = size;
        this.folder = folder;
    }

    public RztGoogleObject() {
    }
}