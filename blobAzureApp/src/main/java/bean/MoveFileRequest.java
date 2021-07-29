package bean;

import java.util.*;

public class MoveFileRequest extends BlobRequest
{
    private String destFileName;
    private String destParentPath;
    private boolean forceful;
    private boolean isFolder = false;

    public void setDestFileName(String destFileName) {
        this.destFileName = destFileName;
    }

    public void setDestParentPath(String destParentPath) {
        this.destParentPath = destParentPath;
    }

    public void setForceful(boolean forceful) {
        this.forceful = forceful;
    }

    public MoveFileRequest(String containerName, String srcFileName, String srcParentPath,
                           String destFileName, String destParentPath, boolean forceful,
                           boolean isFolder )
    {
        super(srcFileName, srcParentPath, containerName, isFolder);
        this.destFileName = destFileName;
        this.destParentPath = destParentPath;
        this.forceful = forceful;
        this.isFolder = isFolder;
    }

    public MoveFileRequest() {}

    public String getSrcFileName()
    {
        return getName();
    }

    public String getSrcParentPath()
    {
        return getParentPath();
    }

    public String getDestFileName()
    {
        return destFileName;
    }

    public String getDestParentPath()
    {
        return destParentPath;
    }

    public boolean isForceful()
    {
        return forceful;
    }

    public boolean isFolder()
    {
        return isFolder;
    }

    public void setFolder( boolean folder )
    {
        isFolder = folder;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;
        MoveFileRequest that = (MoveFileRequest) o;
        return forceful == that.forceful && isFolder == that.isFolder && Objects.equals(destFileName, that.destFileName)
                && Objects.equals(destParentPath, that.destParentPath);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), destFileName, destParentPath, forceful, isFolder);
    }

    public static class Builder {

        private String fileName;
        private String parentPath;
        private String containerName;
        private String destFileName;
        private String destParentPath;
        private boolean isFolder;
        private boolean forceful;

        public Builder withSrcFileName( String srcFileName )
        {
            this.fileName = srcFileName;
            return this;
        }

        public Builder withSrcParentPath( String srcParentPath )
        {
            this.parentPath = srcParentPath;
            return this;
        }

        public Builder withContainerName( String containerName )
        {
            this.containerName = containerName;
            return this;
        }

        public Builder withDestFileName( String destFileName )
        {
            this.destFileName = destFileName;
            return this;
        }

        public Builder withDestParentPath( String destParentPath )
        {
            this.destParentPath = destParentPath;
            return this;
        }

        public Builder withForceful()
        {
            this.forceful = true;
            return this;
        }

        public Builder withOutForceful()
        {
            this.forceful = false;
            return this;
        }

        public Builder withFolder( boolean isFolder )
        {
            this.isFolder = isFolder;
            return this;
        }

        public MoveFileRequest build()
        {
            return new MoveFileRequest(this.containerName, this.fileName, this.parentPath, this.destFileName,
                    this.destParentPath, this.forceful, this.isFolder);
        }
    }
}