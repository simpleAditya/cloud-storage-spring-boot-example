package java.com.razorthink.ai.s3dataconnector.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.razorthink.ai.dataconnector.bean.CreateFolderResponse;
import com.razorthink.ai.dataconnector.bean.ListFileResponse;
import com.razorthink.ai.dataconnector.bean.MoveFileResponse;
import com.razorthink.ai.dataconnector.exception.DataConnectorException;
import com.razorthink.ai.dataconnector.util.FilePathUtil;
import com.razorthink.ai.dataconnector.util.FileReadUtil;
import com.razorthink.ai.pojo.dataexplorer.FilePreviewRequest;
import com.razorthink.ai.pojo.dataexplorer.FilePreviewResponse;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import java.com.razorthink.ai.s3dataconnector.bean.*;
import java.com.razorthink.ai.s3dataconnector.util.ExceptionMessageUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/* *************************************************************************************************************************
 * Summary: This application demonstrates how to use the S3 Bucket Storage service.
 * It does so by creating a bucket, creating a file, then uploading that file, listing all files in a bucket,
 * and downloading the file. Then it deletes all the resources it created
 *
 * Documentation References:
 * Associated Article - https://docs.amazon.com/en-us/amazon/storage/bucket/amazon-s3-connector
 * What is a Storage Account - http://amazon.com/en-us/documentation/articles/storage-whatis-account/
 * Getting Started with Buckets - http://amazon.com/en-us/documentation/articles/storage-dotnet-how-to-use-buckets/
 * Bucket Service Concepts - http://msdn.amazon.com/en-us/library/dd179376.aspx
 * Bucket Service REST API - http://msdn.amazon.com/en-us/library/dd135733.aspx
 * *************************************************************************************************************************
 */
public class S3DataConnectorOperations
{
	/* *************************************************************************************************************************
	 * Instructions: Start an S3 storage emulator, such as S3, before running the app.
	 *    Alternatively, remove the "UseDevelopmentStorage=true;"; string and uncomment the 3 commented lines.
	 *    Then, update the storageConnectionString variable with your AccountName and Key and run the sample.
	 * *************************************************************************************************************************
	 */
	public static void main(String [] args) throws IOException, DataConnectorException {
		String accessKey = "accessKey";

		String secretKey = "secretKey";

		String bucketName = "bucketName";

		File sourceFile;

		System.out.println("Amazon Bucket storage quick start sample...");

		// Parse the accessKey & secretKey and create a bucket client to interact with Bucket storage...

		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withRegion(Regions.DEFAULT_REGION).withForceGlobalBucketAccessEnabled(true).build();

		// Creating a sample file...

		sourceFile = File.createTempFile("sampleFile", ".txt");

		System.out.println("Creating a sample file at: " + sourceFile.toString());

		Writer output = new BufferedWriter(new FileWriter(sourceFile));

		output.write("Hello Azure!");

		output.close();

		// Creating a file/folder...

		CreateFolderRequest createFolderRequest = new CreateFolderRequest();

		createFolderRequest.setName("TestingFile");
		createFolderRequest.setBucketName("quickstartcontainer");
		createFolderRequest.setParentPath("/");
		createFolderRequest.setFolder(true);
		createFolderRequest.setFileDelimiter("/");
		createFolderRequest.setStartIndex("0");
		createFolderRequest.setMaxKeys(10);

		createFolder(amazonS3, createFolderRequest);

		// Uploading a file...

		UploadFileRequest uploadFileRequest = new UploadFileRequest();

		uploadFileRequest.setFile(sourceFile);
		uploadFileRequest.setInputStream(null);
		uploadFileRequest.setName("Test2");
		uploadFileRequest.setBucketName("quickstartcontainer");
		uploadFileRequest.setParentPath("TestFolder/");
		uploadFileRequest.setFileDelimiter("/");
		uploadFileRequest.setStartIndex("0");
		uploadFileRequest.setMaxKeys(10);

		uploadFile(amazonS3, uploadFileRequest);

		// Downloading a file...

		CreateFolderRequest downloadRequest = new CreateFolderRequest();

		downloadRequest.setName("sampleFile254743665331038531.txt");
		downloadRequest.setBucketName("quickstartcontainer");
		downloadRequest.setParentPath("/");
		downloadRequest.setFileDelimiter("/");
		downloadRequest.setStartIndex("0");
		downloadRequest.setMaxKeys(10);

		downloadFile(amazonS3, downloadRequest);

		// Deleting a file/folder...

		CreateFolderRequest deleteFileRequest = new CreateFolderRequest();

		deleteFileRequest.setName("TestFolder");
		deleteFileRequest.setBucketName("quickstartcontainer");
		deleteFileRequest.setParentPath("TestFolder/");
		deleteFileRequest.setFolder(true);
		deleteFileRequest.setFileDelimiter("/");
		deleteFileRequest.setStartIndex("0");
		deleteFileRequest.setMaxKeys(10);

		deleteFile(amazonS3, deleteFileRequest);

		// Copy/Move a file/folder...

		MoveFileRequest copyFileRequest = new MoveFileRequest();

		copyFileRequest.setName("SubUrban");
		copyFileRequest.setBucketName("quickstartcontainer");
		copyFileRequest.setParentPath("NewFolder/");
		copyFileRequest.setFolder(true);
		copyFileRequest.setFileDelimiter("/");
		copyFileRequest.setStartIndex("0");
		copyFileRequest.setMaxKeys(10);
		copyFileRequest.setDestFileName("SubUrban");
		copyFileRequest.setDestParentPath("Directory/");
		copyFileRequest.setFolder(true);
		copyFileRequest.setForceful(true);

		copyFile(amazonS3, copyFileRequest);

		moveFile(amazonS3, copyFileRequest);

		// Get file meta info...

		CreateFolderRequest metaFileRequest = new CreateFolderRequest();

		metaFileRequest.setName("sampleFile254743665331038531.txt");
		metaFileRequest.setBucketName("quickstartcontainer");
		metaFileRequest.setParentPath("/");
		metaFileRequest.setFileDelimiter("/");
		metaFileRequest.setStartIndex("0");
		metaFileRequest.setMaxKeys(10);

		getFileMetaData(amazonS3, metaFileRequest);

		// Preview a file/folder...

		CreateFolderRequest previewFileRequest = new CreateFolderRequest();

		previewFileRequest.setName("sampleFile254743665331038531.txt");
		previewFileRequest.setBucketName("quickstartcontainer");
		previewFileRequest.setParentPath("/");
		previewFileRequest.setFileDelimiter("/");
		previewFileRequest.setStartIndex("0");
		previewFileRequest.setMaxKeys(10);

		FilePreviewRequest filePreviewRequest = new FilePreviewRequest();

		filePreviewRequest.setDelimiter("/");
		filePreviewRequest.setEscapeCharacter(null);
		filePreviewRequest.setHeaderEnabled(false);
		filePreviewRequest.setHeaderPosition(0);
		filePreviewRequest.setQuoteCharacter(null);

		previewFile(amazonS3, previewFileRequest, filePreviewRequest);

		// List/Search a file/folder...

		S3Request listRequest = new S3Request();

		listRequest.setName("Directory");
		listRequest.setBucketName("quickstartcontainer");
		listRequest.setParentPath("/");
		listRequest.setFolder(true);
		listRequest.setFileDelimiter("/");
		listRequest.setStartIndex("0");
		listRequest.setMaxKeys(5);

		listFiles(amazonS3, listRequest);

		searchFiles(amazonS3, listRequest,"sample");
	}

	public static CreateFolderResponse createFolder(AmazonS3 amazonS3, CreateFolderRequest request)
			throws DataConnectorException
	{
		try
		{
			// Check for valid folder name...
			FilePathUtil.isValidFileName(request.getName());

			if( isExists(amazonS3,
					new CreateFolderRequest(request.getName(), request.getParentPath(), request.getBucketName(),
							request.isFolder())) )
			{
				throw new DataConnectorException("File/Folder already exist with name " + request.getName() + " in " + (StringUtils
						.isNullOrEmpty(request.getParentPath()) ? "ROOT_FOLDER" : request.getParentPath()) + " .");
			}

			// Create meta-data for your folder and set content-length to 0...
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(0);

			// Create empty content...
			InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

			// Create a PutObjectRequest passing the folder name suffixed by /...
			PutObjectRequest putObjectRequest;
			if( request.isFolder() )
			{
				putObjectRequest = new PutObjectRequest(request.getBucketName(),
						FilePathUtil.appendSlashIfNot(request.getParentPath()) + FilePathUtil
								.appendSlashIfNot(request.getName()), emptyContent, metadata);
				// Send request to S3 to create folder...
			}
			else
			{
				putObjectRequest = new PutObjectRequest(request.getBucketName(),
						FilePathUtil.appendSlashIfNot(request.getParentPath()) + FilePathUtil
								.removeSlashIfEndsWith(request.getName()), emptyContent, metadata);
				// Send request to S3 to create folder...
			}
			amazonS3.putObject(putObjectRequest);

			CreateFolderResponse response = new CreateFolderResponse();
			response.setFolderName(request.getName());
			response.setAbsolutePath(request.getParentPath() + File.separator + request.getName());
			response.setParentPath(request.getParentPath());
			return response;
		}
		catch( DataConnectorException e )
		{
			throw e;
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static boolean isExists(AmazonS3 amazonS3, CreateFolderRequest folderExistsRequest) throws DataConnectorException
	{
		try
		{
			String prefix = folderExistsRequest.getParentPath();
			if( folderExistsRequest.getParentPath().startsWith(File.separator) )
			{
				prefix = folderExistsRequest.getParentPath().substring(1);
			}

			if( !prefix.isEmpty() )
			{
				prefix = prefix + File.separator;
			}
			if( folderExistsRequest.isFolder() )
			{
				ObjectListing objectListing = amazonS3
						.listObjects(folderExistsRequest.getBucketName(), prefix + folderExistsRequest.getName());
				return !objectListing.getObjectSummaries().isEmpty();
			}
			else
			{
				return amazonS3.doesObjectExist(folderExistsRequest.getBucketName(),
						prefix + FilePathUtil.removeSlashIfEndsWith(folderExistsRequest.getName()));
			}
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public boolean isPathExists( AmazonS3 amazonS3, String bucketName, String path ) throws DataConnectorException
	{
		try
		{
			return amazonS3.doesObjectExist(bucketName, path);
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static MoveFileResponse copyFile(AmazonS3 amazonS3, MoveFileRequest copyFileRequest) throws DataConnectorException
	{
		try
		{
			FilePathUtil.isValidFileName(copyFileRequest.getSrcFileName());
			FilePathUtil.isValidFileName(copyFileRequest.getDestFileName());
			if( !isExists(amazonS3,
					new CreateFolderRequest(copyFileRequest.getSrcFileName(), copyFileRequest.getSrcParentPath(),
							copyFileRequest.getBucketName(), copyFileRequest.isFolder())) )
			{
				throw new DataConnectorException(
						"Source file " + copyFileRequest.getSrcFileName() + " doesn't exist in " + (StringUtils
								.isNullOrEmpty(copyFileRequest.getParentPath()) ?
								"ROOT_FOLDER" :
								copyFileRequest.getParentPath()));
			}
			if( StringUtils.isNullOrEmpty(copyFileRequest.getDestFileName()) )
			{
				throw new DataConnectorException("Destination file name cannot be empty");
			}
			if( isExists(amazonS3,
					new CreateFolderRequest(copyFileRequest.getDestFileName(), copyFileRequest.getDestParentPath(),
							copyFileRequest.getBucketName(), copyFileRequest.isFolder())) && !copyFileRequest
					.isForceful() )
			{
				throw new DataConnectorException(
						"Destination file " + copyFileRequest.getSrcFileName() + " already exist in " + (StringUtils
								.isNullOrEmpty(copyFileRequest.getParentPath()) ?
								"ROOT_FOLDER" :
								copyFileRequest.getParentPath()) + ". Try forceful copy to replace it.");
			}
			if( copyFileRequest.isFolder() )
			{
				recursiveCopyFiles(amazonS3, copyFileRequest);
			}
			else
			{
				CopyObjectRequest req = new CopyObjectRequest(copyFileRequest.getBucketName(),
						FilePathUtil.appendSlashIfNot(copyFileRequest.getSrcParentPath()) + FilePathUtil
								.removeSlashIfEndsWith(copyFileRequest.getSrcFileName()),
						copyFileRequest.getBucketName(),
						FilePathUtil.appendSlashIfNot(copyFileRequest.getDestParentPath()) + FilePathUtil
								.removeSlashIfEndsWith(copyFileRequest.getDestFileName()));

				amazonS3.copyObject(req);
			}

			MoveFileResponse response = new MoveFileResponse();
			response.setFolderName(copyFileRequest.getDestFileName());
			response.setAbsolutePath(
					copyFileRequest.getSrcParentPath() + File.separator + copyFileRequest.getDestFileName());
			response.setParentPath(copyFileRequest.getParentPath());
			return response;

		}
		catch( DataConnectorException e )
		{
			throw e;
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	private static void recursiveCopyFiles( AmazonS3 amazonS3, MoveFileRequest copyFileRequest )
	{
		String nextIndex = null;
		List<ObjectListing> objectListings = new ArrayList<>();

		String prefix = copyFileRequest.getSrcParentPath() + File.separator + copyFileRequest.getSrcFileName();
		if( prefix.startsWith(File.separator) )
		{
			prefix = prefix.substring(1);
		}

		boolean isTruncated = true;

		while( isTruncated )
		{
			ObjectListing objectListing = amazonS3.listObjects(
					new ListObjectsRequest().withBucketName(copyFileRequest.getBucketName()).withPrefix(prefix)
							.withMarker(nextIndex));
			objectListings.add(objectListing);
			isTruncated = objectListing.isTruncated();
			nextIndex = objectListing.getNextMarker();
		}

		String srcPath = copyFileRequest.getSrcParentPath().isEmpty() ?
				copyFileRequest.getSrcFileName() :
				copyFileRequest.getSrcParentPath() + File.separator + copyFileRequest.getSrcFileName();

		String destPath = copyFileRequest.getDestParentPath().isEmpty() ?
				copyFileRequest.getDestFileName() :
				copyFileRequest.getDestParentPath() + File.separator + copyFileRequest.getDestFileName();

		objectListings.forEach(objectListing -> objectListing.getObjectSummaries().forEach(s3ObjectSummary -> {

			String replace = s3ObjectSummary.getKey().replace(srcPath, destPath);

			amazonS3.copyObject(new CopyObjectRequest(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey(),
					s3ObjectSummary.getBucketName(), replace));
		}));
	}

	public static ListFileResponse listFiles(AmazonS3 amazonS3, S3Request listFileRequest) throws DataConnectorException
	{

		ListFileResponse response = new ListFileResponse();
		try
		{
			RztS3ObjectList objectList = listBucketContentsWithPagination(amazonS3, listFileRequest.getBucketName(),
					listFileRequest.getParentPath(), listFileRequest.getStartIndex(),
					listFileRequest.getFileDelimiter(), listFileRequest.getMaxKeys());

			List<ListFileResponse.File> files = new ArrayList<>();
			int totalFolders = 0;
			int totalRegularFiles = 0;
			for( RztS3Object obj : objectList.getObjects() )
			{
				if( !obj.getFullObjectPath().equals(listFileRequest.getParentPath() + File.separator) )
				{
					if( obj.isFolder() )
					{
						totalFolders += 1;
						files.add(new ListFileResponse.File(
								FilePathUtil.getFileName(FilePathUtil.removeSlashIfEndsWith(obj.getFullObjectPath())),
								0, true, null));
					}
					else
					{
						totalRegularFiles += 1;
						files.add(new ListFileResponse.File(FilePathUtil.getFileName(obj.getFullObjectPath()),
								obj.getSize(), false, obj.getLastModified()));
					}
				}
			}
			response.setTotalFolders(totalFolders);
			response.setTotalRegularFiles(totalRegularFiles);
			response.setHasMoreItems(objectList.isHasMoreItems());
			response.setNextItemIndex(objectList.getNextItemIndex());
			response.setFiles(files);
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
		return response;
	}

	public static boolean deleteFile(AmazonS3 amazonS3, CreateFolderRequest deleteFileRequest) throws DataConnectorException
	{
		try
		{
			if( deleteFileRequest.isFolder() )
			{
				deleteFileRecursively(amazonS3, deleteFileRequest);
			}
			else
			{
				amazonS3.deleteObject(deleteFileRequest.getBucketName(),
						FilePathUtil.appendSlashIfNot(deleteFileRequest.getParentPath()) + FilePathUtil
								.removeSlashIfEndsWith(deleteFileRequest.getName()));
			}
		}
		catch( DataConnectorException e )
		{
			throw e;
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
		return true;
	}

	private static void deleteFileRecursively( AmazonS3 amazonS3, CreateFolderRequest deleteFileRequest )
			throws DataConnectorException
	{
		try
		{
			if( deleteFileRequest.isFolder() )
			{

				String fileDelimiter = "/";
				String prefix = deleteFileRequest.getParentPath();

				if( deleteFileRequest.getParentPath().startsWith(File.separator) )
				{
					prefix = deleteFileRequest.getParentPath().substring(1);
				}

				if( !deleteFileRequest.getParentPath().isEmpty() && !deleteFileRequest.getParentPath()
						.endsWith(fileDelimiter) )
				{
					prefix += fileDelimiter;
				}

				prefix = prefix + deleteFileRequest.getName() + fileDelimiter;

				String nextIndex = null;
				List<ObjectListing> objectListings = new ArrayList<>();
				boolean isTruncated = true;

				while( isTruncated )
				{
					ObjectListing objectListing = amazonS3.listObjects(
							new ListObjectsRequest().withBucketName(deleteFileRequest.getBucketName())
									.withPrefix(prefix).withMarker(nextIndex));
					objectListings.add(objectListing);
					isTruncated = objectListing.isTruncated();
					nextIndex = objectListing.getNextMarker();
				}

				for( ObjectListing objectListing : objectListings )
				{
					for( S3ObjectSummary objectSummary : objectListing.getObjectSummaries() )
					{
						amazonS3.deleteObject(deleteFileRequest.getBucketName(), objectSummary.getKey());
					}
				}
			}
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static MoveFileResponse moveFile(AmazonS3 amazonS3, MoveFileRequest moveFileRequest) throws DataConnectorException
	{
		try
		{
			if( copyFile(amazonS3, moveFileRequest) != null )
			{
				deleteFile(amazonS3,
						new CreateFolderRequest(moveFileRequest.getSrcFileName(), moveFileRequest.getSrcParentPath(),
								moveFileRequest.getBucketName(), moveFileRequest.isFolder()));

			}
			MoveFileResponse response = new MoveFileResponse();
			response.setFolderName(moveFileRequest.getDestFileName());
			response.setAbsolutePath(
					moveFileRequest.getSrcParentPath() + File.separator + moveFileRequest.getDestFileName());
			response.setParentPath(moveFileRequest.getParentPath());

			return response;
		}
		catch( DataConnectorException e )
		{
			throw e;
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static CreateFolderResponse uploadFile(AmazonS3 amazonS3, UploadFileRequest uploadFileRequest)
			throws DataConnectorException
	{
		try
		{
			if( isExists(amazonS3, new CreateFolderRequest(uploadFileRequest.getName(), uploadFileRequest.getParentPath(), uploadFileRequest.getBucketName(),
							false)) )
			{
				throw new DataConnectorException("File " + uploadFileRequest.getName() + " already exist in " + (StringUtils
						.isNullOrEmpty(uploadFileRequest.getParentPath()) ? "ROOT_FOLDER" : uploadFileRequest.getParentPath()) + " .");
			}
			FilePathUtil.isValidFileName(uploadFileRequest.getFileName());
			PutObjectRequest objReq;
			if( uploadFileRequest.getInputStream() != null )
			{
				objReq = new PutObjectRequest(uploadFileRequest.getBucketName(),
						FilePathUtil.appendSlashIfNot(uploadFileRequest.getParentPath()) + FilePathUtil
								.removeSlashIfEndsWith(uploadFileRequest.getFileName()),
						uploadFileRequest.getInputStream(), new ObjectMetadata());
			}
			else if( uploadFileRequest.getFile() != null )
			{
				objReq = new PutObjectRequest(uploadFileRequest.getBucketName(),
						FilePathUtil.appendSlashIfNot(uploadFileRequest.getParentPath()) + FilePathUtil
								.removeSlashIfEndsWith(uploadFileRequest.getFileName()), uploadFileRequest.getFile());
			}
			else
			{
				throw new DataConnectorException("Please provide file or input stream to upload");
			}
			amazonS3.putObject(objReq);

			CreateFolderResponse response = new CreateFolderResponse();
			response.setFolderName(uploadFileRequest.getName());
			response.setAbsolutePath(
					uploadFileRequest.getBucketName() + File.separator + uploadFileRequest.getParentPath()
							+ File.separator + uploadFileRequest.getName());
			response.setParentPath(uploadFileRequest.getParentPath());
			return response;
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static byte[] downloadFile(AmazonS3 amazonS3, CreateFolderRequest downloadRequest) throws DataConnectorException
	{
		S3ObjectInputStream objectData = null;
		try
		{
			isExists(amazonS3, downloadRequest);

			S3Object object = amazonS3.getObject(new GetObjectRequest(downloadRequest.getBucketName(),
					FilePathUtil.appendSlashIfNot(downloadRequest.getParentPath()) + FilePathUtil
							.removeSlashIfEndsWith(downloadRequest.getName())));
			objectData = object.getObjectContent();

			// Process the objectData stream...
			return IOUtils.toByteArray(objectData);
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
		catch( DataConnectorException e )
		{
			throw e;
		}
		catch( Exception e )
		{
			throw new DataConnectorException("Sorry! Something went wrong while reading the file content");
		}
		finally
		{
			closeS3ObjectInputStream(objectData);
		}
	}

	public static ListFileResponse.File getFileMetaData( AmazonS3 amazonS3, CreateFolderRequest downloadRequest )
			throws DataConnectorException
	{
		try
		{
			ObjectMetadata objectMetadata = amazonS3
					.getObjectMetadata(downloadRequest.getBucketName(), downloadRequest.getParentPath());

			String fileName = downloadRequest.getParentPath()
					.replace(FilePathUtil.appendSlashIfNot(downloadRequest.getParentPath()), "");

			return new ListFileResponse.File(FilePathUtil.removeSlashIfEndsWith(fileName),
					objectMetadata.getContentLength(), !fileName.equals(FilePathUtil.removeSlashIfEndsWith(fileName)),
					objectMetadata.getLastModified());
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static RztS3ObjectList listBucketContentsWithPagination(AmazonS3 s3client, String bucketName, String prefix,
																   String nextIndex, String fileDelimiter, int maxKeys)
	{
		if( StringUtils.isNullOrEmpty(fileDelimiter) )
		{
			fileDelimiter = "/";
		}
		if( maxKeys == 0 )
		{
			maxKeys = 100;
		}

		List<RztS3Object> objectsList = new ArrayList<>();
		long start = System.currentTimeMillis();
		boolean isTopLevel = false;

		if( prefix.isEmpty() || prefix.equals(fileDelimiter) )
		{
			isTopLevel = true;
		}
		if( !prefix.endsWith(fileDelimiter) )
		{
			prefix += fileDelimiter;
		}

		ListObjectsRequest listObjectsRequest;
		if( isTopLevel )
		{
			listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withDelimiter(fileDelimiter)
					.withMaxKeys(maxKeys).withMarker(nextIndex);
		}
		else
		{
			if( prefix.startsWith(File.separator) )
			{
				prefix = prefix.substring(1);
			}
			listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix)
					.withDelimiter(fileDelimiter).withMaxKeys(maxKeys).withMarker(nextIndex);
		}
		ObjectListing objects = s3client.listObjects(listObjectsRequest);
		long end = System.currentTimeMillis();
		for( S3ObjectSummary os : objects.getObjectSummaries() )
		{
			objectsList.add(new RztS3Object(os.getKey(), os.getLastModified(), os.getSize(), false));
		}
		for( String os : objects.getCommonPrefixes() )
		{
			objectsList.add(new RztS3Object(os, null, 0, true));
		}
		RztS3ObjectList razorthinkS3ObjectList = new RztS3ObjectList(objectsList, objects.getNextMarker(),
				(end - start));
		razorthinkS3ObjectList.setNextItemIndex(objects.getNextMarker());
		razorthinkS3ObjectList.setHasMoreItems(objects.isTruncated());
		return razorthinkS3ObjectList;
	}

	public static FilePreviewResponse previewFile(AmazonS3 amazonS3, CreateFolderRequest downloadRequest,
												  FilePreviewRequest filePreviewRequestUI)
			throws DataConnectorException
	{
		S3ObjectInputStream objectData = null;
		try
		{
			isExists(amazonS3, downloadRequest);

			S3Object object = amazonS3.getObject(new GetObjectRequest(downloadRequest.getBucketName(),
					FilePathUtil.appendSlashIfNot(downloadRequest.getParentPath()) + FilePathUtil
							.removeSlashIfEndsWith(downloadRequest.getName())));
			objectData = object.getObjectContent();

			// Process the objectData stream.
			InputStream inputStream = objectData.getDelegateStream();

			return FileReadUtil.parseFileForPreview(inputStream, downloadRequest.getName(),
					object.getObjectMetadata().getContentLength(), filePreviewRequestUI);

		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
		catch( DataConnectorException e )
		{
			throw e;
		}
		catch( Exception e )
		{
			throw new DataConnectorException("Something went wrong while previewing file");
		}
		finally
		{
			closeS3ObjectInputStream(objectData);
		}
	}

	private static void closeS3ObjectInputStream( S3ObjectInputStream objectData )
	{
		if( objectData != null )
		{
			try
			{
				objectData.close();
			}
			catch( IOException e )
			{
				//do nothing
			}
		}
	}

	public static ListFileResponse searchFiles(AmazonS3 amazonS3, S3Request listFileRequest, String searchKey)
			throws DataConnectorException
	{
		ListFileResponse response = new ListFileResponse();
		try
		{
			int maxKeys = listFileRequest.getMaxKeys();
			MutableInt searchCount = new MutableInt();
			searchCount.setValue(0);
			if( maxKeys == 0 )
			{
				maxKeys = 100;
			}
			List<ListFileResponse.File> files = new ArrayList<>();

			response.setTotalFolders(0);
			response.setTotalRegularFiles(0);

			String nextIndex = listFileRequest.getStartIndex();
			boolean hasMoreItems = true;
			while( hasMoreItems && listFileRequest.getMaxKeys() > searchCount.getValue() )
			{
				RztS3ObjectList objectList = listBucketContentsWithPagination(amazonS3, listFileRequest.getBucketName(),
						listFileRequest.getParentPath(), nextIndex, listFileRequest.getFileDelimiter(), maxKeys);
				MutableBoolean searchMore = new MutableBoolean();
				searchMore.setTrue();

				searchFiles(objectList, listFileRequest, searchKey, searchMore, searchCount, files, response);

				nextIndex = objectList.getNextItemIndex();
				hasMoreItems = objectList.isHasMoreItems();
				if( !searchMore.booleanValue() )
				{
					break;
				}
			}
			// Set HasMoreItems for the search result...
			response.setHasMoreItems(searchCount.getValue() >= listFileRequest.getMaxKeys() && hasMoreItems);
			response.setNextItemIndex(nextIndex);
			response.setFiles(files);
		}
		catch( AmazonClientException e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
		return response;
	}

	private static void searchFiles(RztS3ObjectList objectList, S3Request listFileRequest, String searchKey,
									MutableBoolean searchMore, MutableInt searchCount, List<ListFileResponse.File> files,
									ListFileResponse response)
	{
		for( RztS3Object obj : objectList.getObjects() )
		{
			if( !obj.getFullObjectPath().equals(listFileRequest.getParentPath() + File.separator) )
			{
				String fileName = FilePathUtil.getFileName(FilePathUtil.removeSlashIfEndsWith(obj.getFullObjectPath()));
				if( fileName.contains(searchKey) )
				{
					if( searchCount.getValue() >= listFileRequest.getMaxKeys() )
					{
						searchMore.setFalse();
						objectList.setNextItemIndex(fileName);
						objectList.setHasMoreItems(true);
						break;
					}
					searchCount.setValue(searchCount.getValue() + 1);
					if( obj.isFolder() )
					{
						response.setTotalFolders(response.getTotalFolders() + 1);
						files.add(new ListFileResponse.File(fileName, 0, true, null));
					}
					else
					{
						response.setTotalRegularFiles(response.getTotalRegularFiles() + 1);
						files.add(new ListFileResponse.File(fileName, obj.getSize(), false, obj.getLastModified()));
					}
				}
			}
		}
	}
}