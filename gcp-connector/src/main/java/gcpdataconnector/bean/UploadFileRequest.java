package gcpdataconnector.bean;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class UploadFileRequest extends CreateFileRequest
{
    private File file;
    private InputStream inputStream;

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public UploadFileRequest(String bucketName, String fileName, String parentPath, File file,
                             InputStream inputStream )
    {
        super(fileName, parentPath, bucketName);
        this.file = file;
        this.inputStream = inputStream;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName()
    {
        return getName();
    }

    public File getFile()
    {
        return file;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public static class Builder {

        private String fileName;
        private String parentPath;
        private String bucketName;
        private File file;
        private InputStream inputStream;

        public Builder withFileName( String fileName )
        {
            this.fileName = fileName;
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

        public Builder withFile( File file )
        {
            this.file = file;
            return this;
        }

        public Builder withInputStream( InputStream inputStream )
        {
            this.inputStream = inputStream;
            return this;
        }

        public UploadFileRequest build()
        {
            return new UploadFileRequest(this.bucketName
                    , this.fileName, this.parentPath, this.file, this.inputStream);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadFileRequest that = (UploadFileRequest) o;
        return Objects.equals(file, that.file) &&
                Objects.equals(inputStream, that.inputStream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file, inputStream);
    }

    public UploadFileRequest() {}
}