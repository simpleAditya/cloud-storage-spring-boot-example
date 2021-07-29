package bean;
;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    private FileUtil() {
        //private
    }

    public static File saveUploadedFile(MultipartFile file, String tempDir ) throws WebappException
    {
        byte[] bytes;
        try
        {
            bytes = file.getBytes();
        }
        catch( Exception e )
        {
            throw new WebappException(e);
        }
        File dir = new File(tempDir);
        if( !dir.exists() && !dir.mkdirs() )
        {
            throw new WebappException("Error creating directory", dir.getAbsoluteFile());
        }
        try
        {
            Path path = Paths.get(dir + File.separator + file.getOriginalFilename());
            Files.write(path, bytes);
            return path.toFile();
        }
        catch( Exception e )
        {
            throw new WebappException(e);
        }
    }

    public static void deleteFile(Path pathToDelete) {

        File file = new File(pathToDelete.toString());
        try {
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(pathToDelete.toFile());
            }
            else
            {
                boolean delete = file.delete();
            }
        } catch (IOException e) {}
    }
}
