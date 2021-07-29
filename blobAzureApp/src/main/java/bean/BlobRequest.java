package bean;

public class BlobRequest
{
        private String name;

        private String parentPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        this.isBlob = blob;
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    private String containerName;

        private String fileDelimiter;

        private int maxKeys;

        private String startIndex;

        private boolean isBlob;

        public BlobRequest( String name, String parentPath, String containerName,
                            String fileDelimiter, String startIndex, Integer maxKeys )
        {
            this.name = name;
            this.parentPath = parentPath;
            this.containerName = containerName;
            this.fileDelimiter = fileDelimiter;
            this.startIndex = startIndex;
            this.maxKeys = maxKeys;
        }

        public BlobRequest( String name, String parentPath, String containerName, boolean isBlob )
        {
            this.name = name;
            this.parentPath = parentPath;
            this.containerName = containerName;
            this.isBlob = isBlob;
        }

    public BlobRequest( String name, String parentPath, String containerName )
    {
        this.name = name;
        this.parentPath = parentPath;
        this.containerName = containerName;
    }

        public BlobRequest() {}
}