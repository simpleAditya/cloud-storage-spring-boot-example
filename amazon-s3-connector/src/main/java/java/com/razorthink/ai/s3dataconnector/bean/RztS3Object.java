package java.com.razorthink.ai.s3dataconnector.bean;

import lombok.Data;

import java.util.Date;

/**
 * Created by antolivish on 06/05/20.
 */
@Data
public class RztS3Object {

    private String fullObjectPath;

    private Date lastModified;

    private long size;

    private boolean folder;

    public RztS3Object( String fullObjectPath, Date lastModified, long size, boolean folder )
    {
        this.fullObjectPath = fullObjectPath;
        this.lastModified = lastModified;
        this.size = size;
        this.folder = folder;
    }

    public RztS3Object() {
    }
}
