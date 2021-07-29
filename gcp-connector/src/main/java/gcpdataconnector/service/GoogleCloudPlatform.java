package gcpdataconnector.service;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import com.razorthink.ai.dataconnector.bean.CreateFolderResponse;
import com.razorthink.ai.dataconnector.bean.ListFileResponse;
import com.razorthink.ai.dataconnector.bean.MoveFileResponse;
import com.razorthink.ai.dataconnector.enums.DSColumnType;
import com.razorthink.ai.dataconnector.exception.DataConnectorException;
import com.razorthink.ai.dataconnector.util.FilePathUtil;
import com.razorthink.ai.dataconnector.util.FileReadUtil;
import com.razorthink.ai.pojo.dataexplorer.FilePreviewRequest;
import com.razorthink.ai.pojo.dataexplorer.FilePreviewResponse;
import com.razorthink.bigbrain.eventmanager.util.StringUtils;
import gcpdataconnector.bean.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.data.domain.Sort;

import java.io.*;
import java.util.*;

/* *************************************************************************************************************************
 * Summary: This application demonstrates how to use the Google Blob Storage service.
 * It does so by creating a bucket, creating a file, then uploading that file, listing all files in a bucket,
 * and downloading the file. Then it deletes all the resources it created.
 *
 * Documentation References:
 * Associated Article - https://cloud.google.com/storage/docs
 * What is a Storage Account - https://cloud.google.com/storage
 * Getting Started with Blobs - https://googleapis.dev/python/storage/latest/blobs.html
 * Blob Service Concepts - http://msdn.google.com/en-us/library/dd179376.aspx
 * Blob Service REST API - http://msdn.google.com/en-us/library/dd135733.aspx
 * *************************************************************************************************************************
 */
public class GoogleCloudPlatform {
    /* *************************************************************************************************************************
     * Instructions: Start a Google storage emulator, such as Google console, before running the app.
     *    Alternatively, remove the "UseDevelopmentStorage=true;"; string and uncomment the 3 commented lines.
     *    Then, update the credentials variable with your GCP-credentials and Key and run the sample.
     * *************************************************************************************************************************
     */
    public static void main(String[] args) {
        Storage storage;

        String projectName = "rzt-ide";
        String bucketName = "rztaios-dev";

        try {
            // Parse the credentials and create a blob client to interact with Blob storage...

            File credentialsPath = new File("/home/adityasrivastava/Downloads/rzt-ide-308fea8cdea5.json");

            FileInputStream serviceAccountStream = new FileInputStream(credentialsPath);

            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);

            storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectName).build().getService();

            // Creating a sample file...

            File sourceFile;

            sourceFile = File.createTempFile("sampleFile", ".txt");

            System.out.println("Creating a sample file at: " + sourceFile.toString());

            Writer output = new BufferedWriter(new FileWriter(sourceFile));

            output.write("Hello Azure!");

            output.close();

            System.out.println("GCP-storage quick start sample...");

            // Creating a file/folder...

            CreateFolderRequest createFolderRequest = new CreateFolderRequest();

            createFolderRequest.setName("");
            createFolderRequest.setBucketName("rztaios-dev");
            createFolderRequest.setParentPath("/");
            createFolderRequest.setFolder(true);
            createFolderRequest.setFileDelimiter("/");
            createFolderRequest.setStartIndex("0");
            createFolderRequest.setMaxKeys(0);

            createFolder(createFolderRequest, storage, bucketName);

            // Uploading a file...

            UploadFileRequest uploadFileRequest = new UploadFileRequest();

            uploadFileRequest.setFile(sourceFile);
            uploadFileRequest.setInputStream(null);
            uploadFileRequest.setName("");
            uploadFileRequest.setBucketName("rztaios-dev");
            uploadFileRequest.setParentPath("");
            uploadFileRequest.setFileDelimiter("/");
            uploadFileRequest.setStartIndex("0");
            uploadFileRequest.setMaxKeys(0);

            uploadFile(storage, uploadFileRequest, bucketName);

            // Downloading a file...

            CreateFolderRequest downloadRequest = new CreateFolderRequest();

            downloadRequest.setName(".txt");
            downloadRequest.setBucketName("rztaios-dev");
            downloadRequest.setParentPath("/");
            downloadRequest.setFileDelimiter("/");
            downloadRequest.setStartIndex("0");
            downloadRequest.setMaxKeys(0);

            downloadFile(storage, downloadRequest, bucketName);

            // Deleting a file/folder...

            CreateFolderRequest deleteFileRequest = new CreateFolderRequest();

            deleteFileRequest.setName("");
            deleteFileRequest.setBucketName("rztaios-dev");
            deleteFileRequest.setParentPath("/");
            deleteFileRequest.setFolder(true);
            deleteFileRequest.setFileDelimiter("/");
            deleteFileRequest.setStartIndex("0");
            deleteFileRequest.setMaxKeys(0);

            deleteFile(storage, deleteFileRequest, bucketName);

            // Copy/Move a file/folder...

            MoveFileRequest copyFileRequest = new MoveFileRequest();

            copyFileRequest.setName("");
            copyFileRequest.setBucketName("rztaios-dev");
            copyFileRequest.setParentPath("");
            copyFileRequest.setFolder(true);
            copyFileRequest.setFileDelimiter("/");
            copyFileRequest.setStartIndex("0");
            copyFileRequest.setMaxKeys(0);
            copyFileRequest.setDestFileName("");
            copyFileRequest.setDestParentPath("/");
            copyFileRequest.setFolder(true);
            copyFileRequest.setForceful(true);

            copyFile(copyFileRequest, storage, bucketName);

            moveFile(copyFileRequest, storage, bucketName);

            // Preview a file/folder...

            CreateFolderRequest previewFileRequest = new CreateFolderRequest();

            previewFileRequest.setName(".txt");
            previewFileRequest.setBucketName("rztaios-dev");
            previewFileRequest.setParentPath("/");
            previewFileRequest.setFolder(false);
            previewFileRequest.setFileDelimiter("/");
            previewFileRequest.setStartIndex("0");
            previewFileRequest.setMaxKeys(0);

            FilePreviewRequest filePreviewRequest = new FilePreviewRequest();

            filePreviewRequest.setDelimiter("/");
            filePreviewRequest.setEscapeCharacter(null);
            filePreviewRequest.setHeaderEnabled(false);
            filePreviewRequest.setHeaderPosition(0);
            filePreviewRequest.setQuoteCharacter(null);

            previewFile(storage, previewFileRequest, filePreviewRequest, bucketName);

            // List/Search a file/folder...

            CreateFileRequest listRequest = new CreateFileRequest();

            listRequest.setName("");
            listRequest.setBucketName("rztaios-dev");
            listRequest.setParentPath("/");
            listRequest.setFileDelimiter("/");
            listRequest.setStartIndex("0");
            listRequest.setMaxKeys(5);

            listFiles(listRequest, storage, bucketName);

            searchFiles(listRequest, storage, "", bucketName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static CreateFolderResponse createFolder(CreateFolderRequest createFolderRequest, Storage storage, String bucketName)
            throws DataConnectorException {

        if (!FilePathUtil.isValidFileName(createFolderRequest.getName()))
            throw new DataConnectorException("Not a valid folder name");

        if (!FilePathUtil.isValidFileName(createFolderRequest.getName()))
            throw new DataConnectorException("Not a valid file/folder name");

        if (isExists(storage,
                new CreateFolderRequest(createFolderRequest.getName(), createFolderRequest.getParentPath(), createFolderRequest.getBucketName(),
                        createFolderRequest.isFolder()), bucketName)) {
            throw new DataConnectorException("File/Folder already exist with name " + createFolderRequest.getName() + " in " + (StringUtils
                    .isNullorEmpty(createFolderRequest.getParentPath()) ? "ROOT_FOLDER" : createFolderRequest.getParentPath()) + " .");
        }

        String parentPath = FilePathUtil.removeSlashIfStartsWith(createFolderRequest.getParentPath());
        String folderName = FilePathUtil.appendSlashIfNot(createFolderRequest.getName());
        String folderPath = "";
        if (StringUtils.isNullorEmpty(parentPath)) {
            folderPath = folderName;
        } else {
            folderPath = parentPath + File.separator + folderName;
        }
        try {
            byte[] bytes = new byte[0];

            BlobId blobId = BlobId.of(bucketName, folderPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            storage.create(blobInfo, bytes);
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }

        CreateFolderResponse response = new CreateFolderResponse();
        response.setFolderName(createFolderRequest.getName());
        response.setAbsolutePath(createFolderRequest.getParentPath() + File.separator + createFolderRequest.getName());
        response.setParentPath(createFolderRequest.getParentPath());

        return response;
    }

    public static boolean isExists(Storage storage, CreateFolderRequest folderExistsRequest, String bucketName) throws DataConnectorException {
        try {
            Page<Blob> blobs;
            String prefix = folderExistsRequest.getParentPath();

            if (folderExistsRequest.getParentPath().startsWith(File.separator)) {
                prefix = folderExistsRequest.getParentPath().substring(1);
            }
            if (!prefix.isEmpty()) {
                prefix = prefix + File.separator;
            }
            if (folderExistsRequest.isFolder()) {
                prefix = prefix + folderExistsRequest.getName();
            } else {
                prefix = prefix + FilePathUtil.removeSlashIfEndsWith(folderExistsRequest.getName());
            }
            blobs = storage.list(bucketName, Storage.BlobListOption.prefix(prefix), Storage.BlobListOption.pageSize(1));
            return blobs.getValues().iterator().hasNext();
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    public static MoveFileResponse copyFile(MoveFileRequest copyFileRequest, Storage storage, String bucketName) throws DataConnectorException {

        if (!isExists(storage, new CreateFolderRequest(copyFileRequest.getSrcFileName(), copyFileRequest.getSrcParentPath(),
                copyFileRequest.getBucketName(), copyFileRequest.isFolder()), bucketName)) {
            throw new DataConnectorException(
                    "Source file " + copyFileRequest.getSrcFileName() + " doesn't exist in " + (StringUtils
                            .isNullorEmpty(copyFileRequest.getParentPath()) ?
                            "ROOT_FOLDER" :
                            copyFileRequest.getParentPath()));
        }
        if (StringUtils.isNullorEmpty(copyFileRequest.getDestFileName())) {
            throw new DataConnectorException("Destination file name cannot be empty");
        }
        if (isExists(storage, new CreateFolderRequest(copyFileRequest.getDestFileName(), copyFileRequest.getDestParentPath(),
                copyFileRequest.getBucketName(), copyFileRequest.isFolder()), bucketName) && !copyFileRequest
                .isForceful()) {
            throw new DataConnectorException(
                    "Destination file " + copyFileRequest.getSrcFileName() + " already exist in " + (StringUtils
                            .isNullorEmpty(copyFileRequest.getParentPath()) ?
                            "ROOT_FOLDER" :
                            copyFileRequest.getParentPath()) + ". Try forceful copy to replace it.");
        }

        try {
            if (copyFileRequest.isFolder()) {
                recursiveCopy(copyFileRequest, bucketName, storage);
            } else {
                String sourcePath = FilePathUtil.appendSlashIfNot(FilePathUtil.removeSlashIfStartsWith(copyFileRequest.getSrcParentPath())) + copyFileRequest.getSrcFileName();
                String destPath = "";
                if (copyFileRequest.getDestParentPath().equalsIgnoreCase(File.separator)) {
                    destPath = copyFileRequest.getDestFileName();
                } else {
                    destPath = FilePathUtil.removeSlashIfStartsWith(copyFileRequest.getDestParentPath()) + File.separator + copyFileRequest.getDestFileName();
                }
                Blob blob = storage.get(bucketName, sourcePath);
                blob.copyTo(bucketName, destPath);
            }
            MoveFileResponse response = new MoveFileResponse();

            response.setFolderName(copyFileRequest.getDestFileName());
            response.setAbsolutePath(
                    copyFileRequest.getSrcParentPath() + File.separator + copyFileRequest.getDestFileName());
            response.setParentPath(copyFileRequest.getParentPath());

            return response;
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    public static void recursiveCopy(MoveFileRequest copyFileRequest, String bucketName, Storage storage) throws DataConnectorException {

        Bucket bucket = storage.get(bucketName);
        String sourcePath = FilePathUtil.removeSlashIfStartsWith(FilePathUtil.appendSlashIfNot(copyFileRequest.getSrcParentPath())) + FilePathUtil.appendSlashIfNot(copyFileRequest.getSrcFileName());

        Page<Blob> blobList = bucket.list(
                Storage.BlobListOption.prefix(sourcePath)
        );

        for (Blob blob : blobList.iterateAll()) {
            if (blob.getName().equalsIgnoreCase(FilePathUtil.appendSlashIfNot(copyFileRequest.getSrcFileName()))) {
                continue;
            }
            String destPath = FilePathUtil.removeSlashIfStartsWith(copyFileRequest.getDestParentPath()) + File.separator + blob.getName().replaceAll(sourcePath, "");
            blob.copyTo(bucketName, destPath);
        }
    }

    public static boolean deleteFile(Storage storage, CreateFolderRequest deleteFileRequest, String bucketName) throws DataConnectorException {
        try {
            if (deleteFileRequest.isFolder()) {
                return deleteFileRecursively(storage, deleteFileRequest, bucketName);
            } else {
                String filePath = FilePathUtil.appendSlashIfNot(FilePathUtil.removeSlashIfStartsWith(deleteFileRequest.getParentPath())) + deleteFileRequest.getName();
                return storage.delete(bucketName, filePath);
            }
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    private static boolean deleteFileRecursively(Storage storage, CreateFolderRequest deleteFileRequest, String bucketName)
            throws DataConnectorException {
        try {
            boolean allDeleted = true;

            Bucket bucket = storage.get(bucketName);
            String folderPath = FilePathUtil.removeSlashIfStartsWith(FilePathUtil.appendSlashIfNot(deleteFileRequest.getParentPath()))
                    + FilePathUtil.appendSlashIfNot(deleteFileRequest.getName());

            Page<Blob> blobList = bucket.list(
                    Storage.BlobListOption.prefix(folderPath)
            );

            for (Blob blob : blobList.iterateAll()) {
               /* if (blob.getName().equalsIgnoreCase(FilePathUtil.appendSlashIfNot(deleteFileRequest.getName()))) {
                }*/
                if (!blob.delete()) {
                    allDeleted = false;
                }
            }

            return allDeleted;
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    public static ListFileResponse listFiles(CreateFileRequest listFileRequest, Storage storage, String bucketName) throws DataConnectorException {
        try {
            return listBlobContentsWithPagination(listFileRequest, storage, bucketName);
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    public static MoveFileResponse moveFile(MoveFileRequest moveFileRequest, Storage storage, String bucketName) throws DataConnectorException {
        MoveFileResponse response = new MoveFileResponse();
        try {
            if (moveFileRequest.isFolder()) {
                String destPath = moveFileRequest.getDestParentPath() + File.separator + moveFileRequest.getDestFileName();
                moveFileRequest.setDestParentPath(destPath);
            }
            if (copyFile(moveFileRequest, storage, bucketName) != null) {
                deleteFile(storage, new CreateFolderRequest(moveFileRequest.getSrcFileName(), moveFileRequest.getSrcParentPath(),
                        moveFileRequest.getBucketName(), moveFileRequest.isFolder()), bucketName);
            }
            response.setFolderName(moveFileRequest.getDestFileName());
            response.setAbsolutePath(moveFileRequest.getSrcParentPath() + File.separator + moveFileRequest.getDestFileName());
            response.setParentPath(moveFileRequest.getParentPath());

        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
        return response;
    }

    public static CreateFolderResponse uploadFile(Storage storage, UploadFileRequest uploadFileRequest, String bucketName) throws DataConnectorException {

        String filePath = FilePathUtil.removeSlashIfStartsWith(FilePathUtil.appendSlashIfNot(uploadFileRequest.getParentPath()))
                + uploadFileRequest.getName();

        try {
            byte[] bytes = IOUtils.toByteArray(uploadFileRequest.getInputStream());
            BlobId blobId = BlobId.of(bucketName, filePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, bytes);
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }

        CreateFolderResponse response = new CreateFolderResponse();
        response.setFolderName(uploadFileRequest.getName());
        response.setParentPath(uploadFileRequest.getParentPath());
        response.setAbsolutePath(uploadFileRequest.getBucketName() + filePath);

        return response;
    }

    public static byte[] downloadFile(Storage storage, CreateFileRequest downloadRequest, String bucketName) throws DataConnectorException {

        String filePath = FilePathUtil.appendSlashIfNot(FilePathUtil.removeSlashIfStartsWith(downloadRequest.getParentPath())) +
                FilePathUtil.removeSlashIfEndsWith(downloadRequest.getName());
        try {
            Blob blob = storage.get(BlobId.of(bucketName, filePath));
            long length = blob.getSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream((int) length);
            blob.downloadTo(bout);
            byte[] bytes = bout.toByteArray();

            return bytes;
        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    public static FilePreviewResponse previewFile(Storage storage, CreateFileRequest downloadRequest, FilePreviewRequest filePreviewRequestUI, String bucketName) throws DataConnectorException {
        try {

            byte[] inputStream = downloadFile(storage, downloadRequest, bucketName);
            InputStream targetStream = new ByteArrayInputStream(inputStream);

            return FileReadUtil.parseFileForPreview(targetStream, downloadRequest.getName(),
                    (long) (inputStream.length / 1024), filePreviewRequestUI);
        } catch (DataConnectorException e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        } catch (Exception e) {
        }
        return null;
    }

    public static ListFileResponse searchFiles(CreateFileRequest listFileRequest, Storage storage, String searchKey, String bucketName) throws DataConnectorException {
        try {
            ListFileResponse response = new ListFileResponse();
            int maxKeys = listFileRequest.getMaxKeys();

            MutableInt searchCount = new MutableInt();

            searchCount.setValue(0);
            if (maxKeys == 0) {
                maxKeys = 100;
            }
            List<ListFileResponse.File> files = new ArrayList<>();

            response.setTotalFolders(0);
            response.setTotalRegularFiles(0);

            String nextIndex = listFileRequest.getStartIndex();
            boolean hasMoreItems = true;
            while (hasMoreItems && listFileRequest.getMaxKeys() > searchCount.getValue()) {
                ListFileResponse objectList = listBlobContentsWithPagination(listFileRequest, storage, bucketName);

                MutableBoolean searchMore = new MutableBoolean();
                searchMore.setTrue();

                searchFiles(objectList, listFileRequest, searchKey, searchMore, searchCount, files, response);

                nextIndex = objectList.getNextItemIndex();

                hasMoreItems = objectList.isHasMoreItems();
                if (!searchMore.booleanValue()) {
                    break;
                }

            }
            // Set HasMoreItems for the search result...

            response.setHasMoreItems(searchCount.getValue() >= listFileRequest.getMaxKeys() && hasMoreItems);
            response.setNextItemIndex(nextIndex);
            response.setFiles(files);
            return response;

        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    public Bucket getBucket(String bucketName, Storage storage) throws DataConnectorException {
        Bucket bucket = storage.get(bucketName);
        if (!bucket.exists()) {
            throw new DataConnectorException("Bucket not present");
        }
        return bucket;
    }

    public static ListFileResponse listBlobContentsWithPagination(CreateFileRequest listFileRequest, Storage storage, String bucketName)
            throws DataConnectorException {
        try {
            Bucket bucket = storage.get(bucketName);
            List<ListFileResponse.File> files = new ArrayList<>();
            ListFileResponse response = new ListFileResponse();

            int totalFolders = 0;
            int totalRegularFiles = 0;

            if (listFileRequest.getMaxKeys() == 0)
                listFileRequest.setMaxKeys(100);

            long start = System.currentTimeMillis();

            String prefix = FilePathUtil.removeSlashIfStartsWith(FilePathUtil.appendSlashIfNot(listFileRequest.getParentPath()));
            String startIndex = "";

            if (!StringUtils.isNullorEmpty(listFileRequest.getStartIndex())) {
                startIndex = listFileRequest.getStartIndex();
            }

            Page<Blob> blobs = bucket.list(
                    Storage.BlobListOption.prefix(prefix),
                    Storage.BlobListOption.pageToken(startIndex),
                    Storage.BlobListOption.pageSize(listFileRequest.getMaxKeys()),
                    Storage.BlobListOption.currentDirectory()
            );

            for (Blob blob : blobs.getValues()) {
                if (blob.getName().equalsIgnoreCase(prefix)) {
                    continue;
                }

                if (blob.getName().endsWith(File.separator)) {
                    totalFolders += 1;

                    files.add(new ListFileResponse.File(
                            FilePathUtil.getFileName(FilePathUtil.removeSlashIfEndsWith(blob.getName())),
                            0, true, null));
                } else {
                    long modifiedDate = blob.getUpdateTime();
                    Date date = new Date(modifiedDate);

                    totalRegularFiles += 1;

                    files.add(new ListFileResponse.File(FilePathUtil.getFileName(blob.getName()),
                            blob.getSize(), false, date));
                }
            }
            sortFiles(listFileRequest.getSortOrder(), listFileRequest.getColumnType(), files);

            response.setTotalFolders(totalFolders);
            response.setTotalRegularFiles(totalRegularFiles);
            if (blobs.hasNextPage()) {
                response.setHasMoreItems(true);
                response.setNextItemIndex(blobs.getNextPageToken());
                listFileRequest.setStartIndex(blobs.getNextPageToken());
            }
            response.setFiles(files);

            long end = System.currentTimeMillis();
            return response;

        } catch (Exception e) {
            throw new DataConnectorException(ExceptionMessageUtil.parseMessage(e.getMessage()), e);
        }
    }

    private static void sortFiles(String sortValue, DSColumnType columnType, List<ListFileResponse.File> files) {
        if (!columnType.equals(DSColumnType.NONE)) {
            if (columnType.equals(DSColumnType.NAME)) {
                sortByName(sortValue, files);
            } else if (columnType.equals(DSColumnType.TYPE)) {
                sortByType(sortValue, files);
            } else if (columnType.equals(DSColumnType.MODIFIED_ON)) {
                sortByModifiedOn(sortValue, files);
            }
        }
    }

    private static void sortByName(String sortOrder, List<ListFileResponse.File> files) {
        if (sortOrder.equalsIgnoreCase(Sort.Direction.ASC.toString())) {
            Collections.sort(files, Comparator.comparing(ListFileResponse.File::getName));
        } else {
            Collections.sort(files, Comparator.comparing(ListFileResponse.File::getName).reversed());
        }
    }

    private static void sortByType(String sortOrder, List<ListFileResponse.File> files) {
        if (sortOrder.equalsIgnoreCase(Sort.Direction.ASC.toString())) {
            Collections.sort(files, GoogleCloudPlatform::compareAsc);
        } else {
            Collections.sort(files, GoogleCloudPlatform::compareDesc);
        }
    }

    private static int compareAsc(final Object o1, final Object o2) {
        String s1 = ((ListFileResponse.File) o1).getName().toLowerCase();
        String s2 = ((ListFileResponse.File) o2).getName().toLowerCase();

        boolean o1IsDir = ((ListFileResponse.File) o1).isFolder();
        boolean o2IsDir = ((ListFileResponse.File) o2).isFolder();

        final int s1Dot = s1.lastIndexOf('.');
        final int s2Dot = s2.lastIndexOf('.');

        if ((!o1IsDir && !o2IsDir) || (o1IsDir && o2IsDir)) { // both or neither
            s2 = s2.substring(s2Dot + 1);
            s1 = s1.substring(s1Dot + 1);
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        } else if (o1IsDir) { // only s2 has an extension, so s1 goes first
            return -1;
        } else { // only s1 has an extension, so s1 goes second
            return 1;
        }
    }

    private static int compareDesc(final Object o1, final Object o2) {
        String s1 = ((ListFileResponse.File) o1).getName().toLowerCase();
        String s2 = ((ListFileResponse.File) o2).getName().toLowerCase();

        boolean o1IsDir = ((ListFileResponse.File) o1).isFolder();
        boolean o2IsDir = ((ListFileResponse.File) o2).isFolder();

        final int s1Dot = s1.lastIndexOf('.');
        final int s2Dot = s2.lastIndexOf('.');

        if ((!o1IsDir && !o2IsDir) || (o1IsDir && o2IsDir)) { // both or neither
            s1 = s1.substring(s1Dot + 1);
            s2 = s2.substring(s2Dot + 1);
            return s2.toLowerCase().compareTo(s1.toLowerCase());
        } else if (o1IsDir) { // only s2 has an extension, so s1 goes first
            return -1;
        } else { // only s1 has an extension, so s1 goes second
            return 1;
        }
    }

    private static void sortByModifiedOn(String sortOrder, List<ListFileResponse.File> files) {
        if (sortOrder.equalsIgnoreCase(Sort.Direction.ASC.toString())) {
            files.sort((lhs, rhs) -> {
                if (lhs.getLastModified() == null && rhs.getLastModified() == null) {
                    return 0;
                } else if (lhs.getLastModified() != null && rhs.getLastModified() == null) {
                    return -1;
                } else if (lhs.getLastModified() == null && rhs.getLastModified() != null) {
                    return 999;
                } else {
                    return lhs.getLastModified().compareTo(rhs.getLastModified());
                }
            });
        } else {
            files.sort((lhs, rhs) -> {
                if (lhs.getLastModified() == null && rhs.getLastModified() == null) {
                    return 0;
                } else if (lhs.getLastModified() != null && rhs.getLastModified() == null) {
                    return 1;
                } else if (lhs.getLastModified() == null && rhs.getLastModified() != null) {
                    return 999;
                } else {
                    return -1 * (lhs.getLastModified().compareTo(rhs.getLastModified()));
                }
            });
        }
    }

    private static void searchFiles(ListFileResponse objectList, CreateFileRequest listFileRequest, String searchKey,
                                    MutableBoolean searchMore, MutableInt searchCount, List<ListFileResponse.File> files,
                                    ListFileResponse response) {

        for (ListFileResponse.File obj : objectList.getFiles()) {
            if (!obj.getName().equals(listFileRequest.getParentPath() + File.separator)) {
                String fileName = FilePathUtil.getFileName(FilePathUtil.removeSlashIfEndsWith(obj.getName()));
                if (fileName.contains(searchKey)) {
                    if (searchCount.getValue() >= listFileRequest.getMaxKeys()) {
                        searchMore.setFalse();
                        objectList.setNextItemIndex(fileName);
                        objectList.setHasMoreItems(true);
                        break;
                    }
                    searchCount.setValue(searchCount.getValue() + 1);
                    if (obj.isFolder()) {
                        response.setTotalFolders(response.getTotalFolders() + 1);
                        files.add(new ListFileResponse.File(fileName, 0, true, null));
                    } else {
                        response.setTotalRegularFiles(response.getTotalRegularFiles() + 1);
                        files.add(new ListFileResponse.File(fileName, obj.getSize(), false, obj.getLastModified()));
                    }
                }
            }
        }
    }
}