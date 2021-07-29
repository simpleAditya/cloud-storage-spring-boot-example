package java.com.razorthink.ai.s3dataconnector.bean;

import java.util.Objects;

/**
 * Created by antolivish on 06/05/20.
 */
public class MoveFileRequest extends S3Request {
    public MoveFileRequest() {}

	public void setDestFileName(String destFileName) {
		this.destFileName = destFileName;
	}

	public void setDestParentPath(String destParentPath) {
		this.destParentPath = destParentPath;
	}

	public void setForceful(boolean forceful) {
		this.forceful = forceful;
	}

	private String destFileName;
	private String destParentPath;
	private boolean forceful;
	private boolean isFolder = false;

	public MoveFileRequest( String bucketName, String srcFileName, String srcParentPath, String destFileName,
			String destParentPath, boolean forceful, boolean isFolder )
	{
		super(srcFileName, srcParentPath, bucketName);
		this.destFileName = destFileName;
		this.destParentPath = destParentPath;
		this.forceful = forceful;
		this.isFolder = isFolder;
	}

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
		private String bucketName;
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

		public Builder withBucketName( String bucketName )
		{
			this.bucketName = bucketName;
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
			return new MoveFileRequest(this.bucketName, this.fileName, this.parentPath, this.destFileName,
					this.destParentPath, this.forceful, this.isFolder);
		}

	}
}
