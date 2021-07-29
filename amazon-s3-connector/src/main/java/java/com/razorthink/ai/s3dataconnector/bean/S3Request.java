package java.com.razorthink.ai.s3dataconnector.bean;

import lombok.Data;

/**
 * Created by antolivish on 06/05/20.
 */
@Data
public class S3Request {

	private String name;

	private String parentPath;

	private String bucketName;

	private String fileDelimiter;

	private String startIndex;

	private int maxKeys;

	private boolean isFolder;

	public S3Request( String name, String parentPath, String bucketName )
	{
		this.name = name;
		this.parentPath = parentPath;
		this.bucketName = bucketName;
	}

    public S3Request() {}
}