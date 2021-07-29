package java.com.razorthink.ai.s3dataconnector.bean;

import lombok.Data;

/**
 * Created by antolivish on 06/05/20.
 */
@Data
public class CreateFolderRequest extends S3Request {

	private boolean isFolder;

	public CreateFolderRequest( String folderName, String parentPath, String bucketName )
	{
		super(folderName, parentPath, bucketName);
	}

	public CreateFolderRequest( String folderName, String parentPath, String bucketName, boolean isFolder )
	{
		super(folderName, parentPath, bucketName);
		this.isFolder = isFolder;
	}

    public CreateFolderRequest() {}

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

		public Builder withIsFolder( boolean folder )
		{
			this.isFolder = folder;
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
			return new CreateFolderRequest(this.folderName, this.parentPath, this.bucketName, this.isFolder);
		}

	}
}
