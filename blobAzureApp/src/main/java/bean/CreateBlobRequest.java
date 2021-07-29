package bean;

public class CreateBlobRequest extends BlobRequest
{
    private boolean isBlob;

    public CreateBlobRequest( String blobName, String parentPath,
                              String containerName, boolean isBlob )
    {
        super(blobName, parentPath, containerName, isBlob);
        this.isBlob = isBlob;
    }

    public CreateBlobRequest() {}

    public static class Builder {

        private String blobName;
        private String parentPath;
        private String containerName;
        private boolean isBlob;

        public String getBlobName() {
            return blobName;
        }

        public void setBlobName(String blobName) {
            this.blobName = blobName;
        }

        public String getParentPath() {
            return parentPath;
        }

        public void setParentPath(String parentPath) {
            this.parentPath = parentPath;
        }

        public String getContainerName() {
            return containerName;
        }

        public void setContainerName(String containerName) {
            this.containerName = containerName;
        }

        public boolean isBlob() {
            return isBlob;
        }

        public void setBlob(boolean blob) {
            isBlob = blob;
        }

        public Builder withFolderName( String blobName )
        {
            this.blobName = blobName;
            return this;
        }

        public Builder withIsFolder( boolean blob )
        {
            this.isBlob = blob;
            return this;
        }

        public Builder withParentPath( String parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder withContainerName( String containerName )
        {
            this.containerName = containerName;
            return this;
        }

        public CreateBlobRequest build()
        {
            return new CreateBlobRequest(this.blobName, this.parentPath, this.containerName,
                    this.isBlob);
        }

    }
}