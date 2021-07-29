// MIT License
// Copyright (c) Microsoft Corporation. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE

package blobQuickstart.blobAzureApp;


import bean.*;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/* *************************************************************************************************************************
 * Summary: This application demonstrates how to use the Blob Storage service.
 * It does so by creating a container, creating a file, then uploading that file, listing all files in a container,
 * and downloading the file. Then it deletes all the resources it created
 *
 * Documentation References:
 * Associated Article - https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-java
 * What is a Storage Account - http://azure.microsoft.com/en-us/documentation/articles/storage-whatis-account/
 * Getting Started with Blobs - http://azure.microsoft.com/en-us/documentation/articles/storage-dotnet-how-to-use-blobs/
 * Blob Service Concepts - http://msdn.microsoft.com/en-us/library/dd179376.aspx
 * Blob Service REST API - http://msdn.microsoft.com/en-us/library/dd135733.aspx
 * *************************************************************************************************************************
 */
public class AzureApp
{
	/* *************************************************************************************************************************
	 * Instructions: Start an Azure storage emulator, such as Azurite, before running the app.
	 *    Alternatively, remove the "UseDevelopmentStorage=true;"; string and uncomment the 3 commented lines.
	 *    Then, update the storageConnectionString variable with your AccountName and Key and run the sample.
	 * *************************************************************************************************************************
	 */
	public static final String accountName = "accountName";

	public static final String accountKey = "accountKey";

	public static final String storageConnectionString = "storageConnectionString";

	public static void main( String[] args )
	{
		File sourceFile;

		System.out.println("Azure Blob storage quick start sample...");

		CloudStorageAccount storageAccount;
		CloudBlobClient blobClient;
		CloudBlobContainer container;

		try
		{
			// Parse the connection string and create a blob client to interact with Blob storage...

			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference("quickstartcontainer");

			// Create the container if it does not exist with public access...

			System.out.println("Creating container: " + container.getName());
			container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());

			// Creating a sample file...

			sourceFile = File.createTempFile("sampleFile", ".txt");

			System.out.println("Creating a sample file at: " + sourceFile.toString());

			Writer output = new BufferedWriter(new FileWriter(sourceFile));

			output.write("Hello Azure!");

			output.close();

			// Creating a file/folder...

			CreateBlobRequest createBlobRequest = new CreateBlobRequest();

			createBlobRequest.setName("TestingFile");
			createBlobRequest.setContainerName("quickstartcontainer");
			createBlobRequest.setParentPath("/");
			createBlobRequest.setBlob(true);
			createBlobRequest.setFileDelimiter("/");
			createBlobRequest.setStartIndex("0");
			createBlobRequest.setMaxKeys(10);

			createFolder(createBlobRequest, container);

			// Uploading a file...

			UploadFileRequest uploadFileRequest = new UploadFileRequest();

			uploadFileRequest.setFile(sourceFile);
			uploadFileRequest.setInputStream(null);
			uploadFileRequest.setName("Test2");
			uploadFileRequest.setContainerName("quickstartcontainer");
			uploadFileRequest.setParentPath("TestFolder/");
			uploadFileRequest.setBlob(true);
			uploadFileRequest.setFileDelimiter("/");
			uploadFileRequest.setStartIndex("0");
			uploadFileRequest.setMaxKeys(10);

			uploadFile(container, uploadFileRequest);

			// Downloading a file...

			CreateBlobRequest downloadRequest = new CreateBlobRequest();

			downloadRequest.setName("sampleFile254743665331038531.txt");
			downloadRequest.setContainerName("quickstartcontainer");
			downloadRequest.setParentPath("/");
			downloadRequest.setBlob(false);
			downloadRequest.setFileDelimiter("/");
			downloadRequest.setStartIndex("0");
			downloadRequest.setMaxKeys(10);

			downloadFile(container, downloadRequest);

			// Deleting a file/folder...

			CreateBlobRequest deleteFileRequest = new CreateBlobRequest();

			deleteFileRequest.setName("TestFolder");
			deleteFileRequest.setContainerName("quickstartcontainer");
			deleteFileRequest.setParentPath("TestFolder/");
			deleteFileRequest.setBlob(true);
			deleteFileRequest.setFileDelimiter("/");
			deleteFileRequest.setStartIndex("0");
			deleteFileRequest.setMaxKeys(10);

			deleteFile(container, deleteFileRequest);

			// Copy/Move a file/folder...

		    MoveFileRequest copyFileRequest = new MoveFileRequest();

			copyFileRequest.setName("SubUrban");
			copyFileRequest.setContainerName("quickstartcontainer");
			copyFileRequest.setParentPath("NewFolder/");
			copyFileRequest.setBlob(true);
			copyFileRequest.setFileDelimiter("/");
			copyFileRequest.setStartIndex("0");
			copyFileRequest.setMaxKeys(10);
			copyFileRequest.setDestFileName("SubUrban");
			copyFileRequest.setDestParentPath("Directory/");
			copyFileRequest.setFolder(true);
			copyFileRequest.setForceful(true);

			copyFile(copyFileRequest, container);

			moveFile(copyFileRequest, container);

			// Preview a file/folder...

			CreateBlobRequest previewFileRequest = new CreateBlobRequest();

			previewFileRequest.setName("sampleFile254743665331038531.txt");
			previewFileRequest.setContainerName("quickstartcontainer");
			previewFileRequest.setParentPath("/");
			previewFileRequest.setBlob(false);
			previewFileRequest.setFileDelimiter("/");
			previewFileRequest.setStartIndex("0");
			previewFileRequest.setMaxKeys(10);

			FilePreviewRequest filePreviewRequest = new FilePreviewRequest();

			filePreviewRequest.setDelimiter("/");
			filePreviewRequest.setEscapeCharacter(null);
			filePreviewRequest.setHeaderEnabled(false);
			filePreviewRequest.setHeaderPosition(0);
			filePreviewRequest.setQuoteCharacter(null);

			previewFile(container, previewFileRequest, filePreviewRequest);

			// List/Search a file/folder...

			BlobRequest listRequest = new BlobRequest();

			listRequest.setName("Directory");
			listRequest.setContainerName("quickstartcontainer");
			listRequest.setParentPath("/");
			listRequest.setBlob(true);
			listRequest.setFileDelimiter("/");
			listRequest.setStartIndex("0");
			listRequest.setMaxKeys(5);

			listFiles(listRequest, container);

			searchFiles(container,listRequest,"sample");
		}

		catch (StorageException ex)
		{
			System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	public static void createFolder(CreateBlobRequest createBlobRequest, CloudBlobContainer cloudBlobContainer)
			throws IOException, DataConnectorException, StorageException, URISyntaxException
	{
		if( !cloudBlobContainer.exists() )
			throw new DataConnectorException("Container not present !");

		if( !FilePathUtil.isValidFileName(createBlobRequest.getName()) )
			throw new DataConnectorException("Not a valid file/folder name");

		if( isExists(cloudBlobContainer,
				new CreateBlobRequest(createBlobRequest.getName(), createBlobRequest.getParentPath(), createBlobRequest.getContainerName(),
						createBlobRequest.isBlob())))
		{
				throw new DataConnectorException("File/Folder already exist with name " + createBlobRequest.getName() + " in " + (StringUtils
						.isEmpty(createBlobRequest.getParentPath()) ? "ROOT_FOLDER" : createBlobRequest.getParentPath()) + " .");

		}

		// Create empty content...

		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		String parentPath, folderName;

		if(createBlobRequest.isBlob())
		{
			parentPath = FilePathUtil.appendSlashIfNot(createBlobRequest.getParentPath());
			folderName = FilePathUtil.appendSlashIfNot(createBlobRequest.getName());
		}
		else
		{
			parentPath = FilePathUtil.appendSlashIfNot(createBlobRequest.getParentPath());
			folderName = FilePathUtil.removeSlashIfEndsWith(createBlobRequest.getName());
		}

		CloudBlockBlob blob = cloudBlobContainer.getBlockBlobReference(parentPath+ folderName +
				File.separator +emptyContent);

		blob.upload(emptyContent,0);

		// Removing the empty content...

		blob.deleteIfExists();

		CreateFolderResponse response = new CreateFolderResponse();

		response.setFolderName(createBlobRequest.getName());
		response.setAbsolutePath(createBlobRequest.getParentPath() + File.separator + createBlobRequest.getName());
		response.setParentPath(createBlobRequest.getParentPath());
	}

	public static boolean isExists(CloudBlobContainer cloudBlobContainer,CreateBlobRequest folderExistsRequest)
			throws DataConnectorException {
		try
		{
			CloudBlockBlob blob;

			String prefix = folderExistsRequest.getParentPath();

			if( folderExistsRequest.getParentPath().startsWith(File.separator) )
			{
				prefix = folderExistsRequest.getParentPath().substring(1);
			}
			if( !prefix.isEmpty() )
			{
				prefix = prefix + File.separator;
			}
			if( folderExistsRequest.isBlob() )
			{
				blob = cloudBlobContainer.getBlockBlobReference(prefix + folderExistsRequest.getName());
			}
			else
			{
				blob = cloudBlobContainer.getBlockBlobReference(
						prefix + FilePathUtil.removeSlashIfEndsWith(folderExistsRequest.getName()));
			}
			return (blob.exists());
		}
		catch( Exception e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static void uploadFile(CloudBlobContainer cloudBlobContainer, UploadFileRequest uploadFileRequest)
			throws DataConnectorException
	{
		try
		{
			if( isExists(cloudBlobContainer, new CreateBlobRequest(uploadFileRequest.getName(), uploadFileRequest.getParentPath(), uploadFileRequest.getContainerName(),
					false)) )
			{
				throw new DataConnectorException("File " + uploadFileRequest.getName() + " already exist in " +
						(StringUtils.isEmpty(uploadFileRequest.getParentPath()) ? "ROOT_FOLDER" : uploadFileRequest.getParentPath()) + " .");
			}
			FilePathUtil.isValidFileName(uploadFileRequest.getFileName());

			CloudBlob cloudBlob = cloudBlobContainer.getBlockBlobReference(uploadFileRequest.getParentPath()+File.separator+uploadFileRequest.getFileName());

			if( uploadFileRequest.getInputStream() != null )
			{
				cloudBlob.upload(uploadFileRequest.getInputStream(), IOUtils.toByteArray(uploadFileRequest.getInputStream()).length);

			}
			else if( uploadFileRequest.getFile() != null )
			{
				cloudBlob.uploadFromFile(uploadFileRequest.getFile().getAbsolutePath());
			}
			else
			{
				throw new DataConnectorException("Please provide file or input stream to upload");
			}
			CreateFolderResponse response = new CreateFolderResponse();

			response.setFolderName(uploadFileRequest.getName());
			response.setParentPath(uploadFileRequest.getParentPath());
			response.setAbsolutePath(
					uploadFileRequest.getContainerName() + File.separator + uploadFileRequest.getParentPath()
							+ File.separator + uploadFileRequest.getName());
		}
		catch( Exception e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static void downloadFile(CloudBlobContainer cloudBlobContainer, CreateBlobRequest downloadRequest)
			throws DataConnectorException
	{
		try
		{
			isExists(cloudBlobContainer, new CreateBlobRequest(downloadRequest.getName(), downloadRequest.getParentPath(),
					downloadRequest.getContainerName(), downloadRequest.isBlob()));

			CloudBlockBlob cloudBlob = cloudBlobContainer.getBlockBlobReference(
					FilePathUtil.appendSlashIfNot(downloadRequest.getParentPath()) +
							FilePathUtil.removeSlashIfEndsWith(downloadRequest.getName()));

			cloudBlob.downloadAttributes();

			int length = (int) cloudBlob.getProperties().getLength();

			byte[] file = new byte[length];

			cloudBlob.downloadToByteArray(file, 0);
		}
		catch( Exception e )
		{
			throw new DataConnectorException("Sorry! Something went wrong while reading the file content");
		}
	}

	public static void deleteFile(CloudBlobContainer cloudBlobContainer, CreateBlobRequest deleteFileRequest)
			throws StorageException {
		try
		{
			if( deleteFileRequest.isBlob() )
			{
				deleteFileRecursively(cloudBlobContainer, deleteFileRequest);

				cloudBlobContainer.getBlockBlobReference(FilePathUtil.removeSlashIfEndsWith(deleteFileRequest.getName()))
						.deleteIfExists();
			}
			else
			{
				cloudBlobContainer.getBlockBlobReference(deleteFileRequest.getName()).deleteIfExists();
			}
		}
		catch(DataConnectorException | URISyntaxException e )
		{
			System.out.println(e.getMessage());
		}
	}

	private static void deleteFileRecursively( CloudBlobContainer cloudBlobContainer, CreateBlobRequest deleteFileRequest )
			throws DataConnectorException
	{
		try
		{
			if( deleteFileRequest.isBlob() )
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

				for (ListBlobItem blob : cloudBlobContainer.listBlobs(prefix))
				{
					if(blob instanceof CloudBlobDirectory)
					{
						CloudBlockBlob srcBlob = cloudBlobContainer.getBlockBlobReference(blob.getUri().getPath());

						System.out.println(srcBlob.getName());
						String folderPath = srcBlob.getName().replace(cloudBlobContainer.getName(),"");
						folderPath = folderPath.startsWith(File.separator) ? folderPath.replaceFirst("/","") : folderPath;

						deleteFileRequest.setParentPath(folderPath);
						deleteFileRecursively(cloudBlobContainer, deleteFileRequest);

						CloudBlockBlob directory = cloudBlobContainer.getBlockBlobReference(FilePathUtil.removeSlashIfEndsWith(folderPath));
						directory.deleteIfExists();
						continue;
					}
					CloudBlockBlob srcBlob = cloudBlobContainer.getBlockBlobReference(((CloudBlockBlob) blob).getName());

					System.out.println(srcBlob.deleteIfExists());
				}
			}
		}
		catch( Exception e )
		{
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static void copyFile(MoveFileRequest copyFileRequest, CloudBlobContainer cloudBlobContainer)
	{
		try
		{
			FilePathUtil.isValidFileName(copyFileRequest.getSrcFileName());
			FilePathUtil.isValidFileName(copyFileRequest.getDestFileName());

			if( !isExists(cloudBlobContainer, new CreateBlobRequest(copyFileRequest.getSrcFileName(), copyFileRequest.getSrcParentPath(),
					copyFileRequest.getContainerName(), copyFileRequest.isFolder())) )
			{
				throw new DataConnectorException(
						"Source file " + copyFileRequest.getSrcFileName() + " doesn't exist in " + (StringUtils
								.isEmpty(copyFileRequest.getParentPath()) ?
								"ROOT_FOLDER" :
								copyFileRequest.getParentPath()));
			}
			if( StringUtils.isEmpty(copyFileRequest.getDestFileName()) )
			{
				throw new DataConnectorException("Destination file name cannot be empty");
			}
			if( isExists(cloudBlobContainer, new CreateBlobRequest(copyFileRequest.getDestFileName(), copyFileRequest.getDestParentPath(),
					copyFileRequest.getContainerName(), copyFileRequest.isFolder())) && !copyFileRequest
					.isForceful() )
			{
				throw new DataConnectorException(
						"Destination file " + copyFileRequest.getSrcFileName() + " already exist in " + (StringUtils
								.isEmpty(copyFileRequest.getParentPath()) ?
								"ROOT_FOLDER" :
								copyFileRequest.getParentPath()) + ". Try forceful copy to replace it.");
			}
			if(copyFileRequest.isFolder()) {

				recursiveCopy(cloudBlobContainer,copyFileRequest);
			}
			else
			{
				CloudBlockBlob srcBlob = cloudBlobContainer.getBlockBlobReference(copyFileRequest.getSrcParentPath() + File.separator + copyFileRequest.getSrcFileName());
				CloudBlockBlob destBlob = cloudBlobContainer.getBlockBlobReference(copyFileRequest.getDestParentPath() + File.separator + copyFileRequest.getDestFileName());

				destBlob.startCopy(srcBlob);

				waitForCopyToComplete(destBlob);

			}
			MoveFileResponse response = new MoveFileResponse();

			response.setFolderName(copyFileRequest.getDestFileName());
			response.setAbsolutePath(
					copyFileRequest.getSrcParentPath() + File.separator + copyFileRequest.getDestFileName());
			response.setParentPath(copyFileRequest.getParentPath());
		}
		catch(DataConnectorException | URISyntaxException | StorageException | InterruptedException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private static void recursiveCopy(CloudBlobContainer cloudBlobContainer , MoveFileRequest copyFileRequest) throws DataConnectorException {
		try
		{
			String parentPath = copyFileRequest.getParentPath();
			for (ListBlobItem blob : cloudBlobContainer.listBlobs(copyFileRequest.getSrcParentPath()))
			{
				if(blob instanceof CloudBlobDirectory){
					CloudBlockBlob srcBlob = cloudBlobContainer.getBlockBlobReference(blob.getUri().getPath());
					String folderPath = srcBlob.getName().replace(cloudBlobContainer.getName(),"");
					folderPath = FilePathUtil.removeSlashIfStartsWith(folderPath);
					copyFileRequest.setParentPath(folderPath);

					String[] list = folderPath.split(File.separator);

					CreateBlobRequest createBlobRequest = new CreateBlobRequest(list[list.length -1], copyFileRequest.getDestParentPath()+copyFileRequest.getDestFileName() ,cloudBlobContainer.getName() ,
							true);

					try {
						createFolder(createBlobRequest, cloudBlobContainer);
					}
					catch (DataConnectorException e){
						//log.info("Folder already Created");
					}
					recursiveCopy(cloudBlobContainer,copyFileRequest);
					continue;
				}
				parentPath = FilePathUtil.removeSlashIfStartsWith(parentPath);
				CloudBlockBlob srcBlob = cloudBlobContainer.getBlockBlobReference(((CloudBlockBlob) blob).getName());

				CloudBlockBlob destBlob = cloudBlobContainer.getBlockBlobReference(copyFileRequest.getDestParentPath()
						+ File.separator + ((CloudBlockBlob) blob).getName().replace(parentPath,""));

				destBlob.startCopy(srcBlob);

				waitForCopyToComplete(destBlob);
			}
		}
		catch( Exception e )
		{
			throw new DataConnectorException("Sorry! Something went wrong while copying the file content");
		}
	}

	private static void waitForCopyToComplete(CloudBlob blob) throws InterruptedException, StorageException {
		CopyStatus copyStatus = CopyStatus.PENDING;

		while (copyStatus == CopyStatus.PENDING) {

			Thread.sleep(1000);

			blob.downloadAttributes();

			copyStatus = blob.getCopyState().getStatus();
		}
	}

	public static void moveFile(MoveFileRequest moveFileRequest, CloudBlobContainer cloudBlobContainer)
	{
		try
		{
			copyFile(moveFileRequest, cloudBlobContainer);

			deleteFile(cloudBlobContainer, new CreateBlobRequest(moveFileRequest.getSrcFileName(), moveFileRequest.getSrcParentPath(),
					moveFileRequest.getContainerName(), moveFileRequest.isFolder()));

			MoveFileResponse response = new MoveFileResponse();

			response.setFolderName(moveFileRequest.getDestFileName());
			response.setAbsolutePath(moveFileRequest.getSrcParentPath() + File.separator + moveFileRequest.getDestFileName());
			response.setParentPath(moveFileRequest.getParentPath());
		}
		catch( StorageException e )
		{
			System.out.println(e.getMessage());
		}
	}

	public static void previewFile(CloudBlobContainer cloudBlobContainer, CreateBlobRequest downloadRequest, FilePreviewRequest filePreviewRequestUI)
			throws DataConnectorException
	{
		InputStream inputStream = null;

		try
		{
			isExists(cloudBlobContainer, downloadRequest);

			CloudBlob cloudBlob = cloudBlobContainer.getBlockBlobReference(
					FilePathUtil.appendSlashIfNot(downloadRequest.getParentPath()) +
							FilePathUtil.removeSlashIfEndsWith(downloadRequest.getName()));

			// Process the InputStream...

			inputStream = cloudBlob.openInputStream();

			if( (cloudBlob.getProperties().getLength() / (1024 * 1024)) > 5 )
			{
				throw new DataConnectorException("Preview not supported for files with size more than 5MB");
			}

			FileReadUtil.parseFileForPreview(inputStream, downloadRequest.getName(),
					cloudBlob.getProperties().getLength() / 1024, filePreviewRequestUI);
		}
		catch( DataConnectorException e )
		{
			System.out.println(e.getMessage());
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
		catch( Exception e )
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			closeAzureObjectInputStream(inputStream);
		}
	}

	private static void closeAzureObjectInputStream( InputStream objectData )
	{
		if( objectData != null )
		{
			try
			{
				objectData.close();
			}
			catch( IOException e ) {}
		}
	}

	public static void listFiles(BlobRequest listFileRequest, CloudBlobContainer cloudBlobContainer)
			throws DataConnectorException
	{
		ListFileResponse response = new ListFileResponse();
		try
		{
			RztAzureObjectList objectList = listBlobContentsWithPagination(listFileRequest, cloudBlobContainer);

			List<ListFileResponse.File> files = new ArrayList<>();

			int totalFolders = 0;
			int totalRegularFiles = 0;

			for( RztAzureObject obj : objectList.getObjects() )
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
			System.out.println(files.size());
			System.out.println(files);

			response.setTotalFolders(totalFolders);
			response.setTotalRegularFiles(totalRegularFiles);
			response.setHasMoreItems(objectList.isHasMoreItems());
			response.setNextItemIndex(objectList.getNextItemIndex());
			response.setFiles(files);
		}
		catch( Exception e )
		{
			System.out.println(e.getMessage());
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
	}

	public static RztAzureObjectList listBlobContentsWithPagination(BlobRequest listFileRequest, CloudBlobContainer cloudBlobContainer)
			throws URISyntaxException, StorageException
	{
		if( StringUtils.isEmpty(listFileRequest.getFileDelimiter()) )
		{
			listFileRequest.setFileDelimiter("/");
		}
		if( listFileRequest.getMaxKeys() == 0 )
		{
			listFileRequest.setMaxKeys(100);
		}

		List<RztAzureObject> objectsList = new ArrayList<>();

		long start = System.currentTimeMillis();
		boolean isTopLevel = false;

		if( listFileRequest.getParentPath().isEmpty() || listFileRequest.getParentPath().equals(listFileRequest.getFileDelimiter()) )
		{
			isTopLevel = true;
		}
		if( !listFileRequest.getParentPath().endsWith(listFileRequest.getFileDelimiter()) )
		{
			listFileRequest.setParentPath(listFileRequest.getParentPath() + listFileRequest.getFileDelimiter());
		}

		boolean isFolder = false, hasMoreItems = false;
		int index = 0;

		ResultSegment<ListBlobItem> blobItems;

		if (isTopLevel)
			blobItems = cloudBlobContainer.listBlobsSegmented(null, false, EnumSet.noneOf(BlobListingDetails.class), 10, null, null, null);
		else {
			if (listFileRequest.getParentPath().startsWith(File.separator)) {
				listFileRequest.setParentPath(listFileRequest.getParentPath().substring(1));
			}
			blobItems = cloudBlobContainer.listBlobsSegmented(listFileRequest.getParentPath(), false, EnumSet.noneOf(BlobListingDetails.class), 10, null, null, null);
		}

		for (ListBlobItem blobItem : blobItems.getResults()) {

			if (blobItem instanceof CloudBlobDirectory) {
				CloudBlockBlob srcBlob = cloudBlobContainer.getBlockBlobReference(blobItem.getUri().getPath());

				String[] folderName = srcBlob.getName().split("/");

				objectsList.add(new RztAzureObject(folderName[folderName.length - 1] + "/", null, 0, true));
			} else {
				CloudBlockBlob srcBlob = cloudBlobContainer.getBlockBlobReference(((CloudBlockBlob) blobItem).getName());

				srcBlob.downloadAttributes();

				objectsList.add(new RztAzureObject(srcBlob.getName(), srcBlob.getProperties().getLastModified(),
						srcBlob.getProperties().getLength(), false));
			}
		}

		long end = System.currentTimeMillis();

		RztAzureObjectList razorThinkAzureObjectList = new RztAzureObjectList(objectsList, null,
				(end - start));
		razorThinkAzureObjectList.setNextItemIndex(String.valueOf(blobItems.getContinuationToken().getNextMarker()));
		razorThinkAzureObjectList.setHasMoreItems(blobItems.getHasMoreResults());

		return razorThinkAzureObjectList;
	}

	public static ListFileResponse searchFiles(CloudBlobContainer cloudBlobContainer, BlobRequest listFileRequest, String searchKey)
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
				RztAzureObjectList objectList = listBlobContentsWithPagination(listFileRequest, cloudBlobContainer);
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
		catch( Exception e )
		{
			System.out.println(e.getMessage());
			throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
		}
		return response;
	}

	private static void searchFiles(RztAzureObjectList objectList, BlobRequest listFileRequest, String searchKey,
									MutableBoolean searchMore, MutableInt searchCount, List<ListFileResponse.File> files,
									ListFileResponse response )
	{
		for( RztAzureObject obj : objectList.getObjects() )
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