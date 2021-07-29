package gcpdataconnector.bean;

import lombok.Data;

@Data
public class CreateFolderRequest extends CreateFileRequest
{
    private boolean isFolder;

    public CreateFolderRequest( String folderName, String parentPath, String bucketName )
    {
        super(folderName, parentPath, bucketName);
    }

    public CreateFolderRequest( String folderName, String parentPath,
                                String bucketName, boolean isFolder)
    {
        super(folderName, parentPath, bucketName);
        this.isFolder = isFolder;
    }

    public static class Builder {

        private String folderName;
        private String parentPath;
        private String bucketName;
        private boolean isFolder;

        public Builder withFolderName( String folderName )
        {
            this.folderName = folderName;
            return this;
        }

        public Builder withIsFolder( boolean isFolder )
        {
            this.isFolder = isFolder;
            return this;
        }

        public Builder withParentPath( String parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder withBucketName( String bucketName )
        {
            this.bucketName = bucketName;
            return this;
        }

        public CreateFolderRequest build()
        {
            return new CreateFolderRequest(this.folderName, this.parentPath, this.bucketName,
                    this.isFolder);
        }
    }
    public CreateFolderRequest() {}
}