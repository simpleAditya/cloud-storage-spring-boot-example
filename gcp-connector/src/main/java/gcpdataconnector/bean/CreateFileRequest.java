package gcpdataconnector.bean;

import com.razorthink.ai.dataconnector.enums.DSColumnType;
import lombok.Data;

@Data
public class CreateFileRequest
{
        private String name;

        private String parentPath;

        private String bucketName;

        private String fileDelimiter;

        private int maxKeys;

        private String startIndex;

        private String sortOrder;

        private DSColumnType columnType;

    public DSColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(DSColumnType columnType) {
        this.columnType = columnType;
    }

    public CreateFileRequest(String name, String parentPath, String bucketName, String fileDelimiter, int maxKeys,
                             String startIndex, String sortOrder, DSColumnType columnType)
    {
        this.name = name;
        this.parentPath = parentPath;
        this.bucketName = bucketName;
        this.fileDelimiter = fileDelimiter;
        this.maxKeys = maxKeys;
        this.startIndex = startIndex;
        this.sortOrder = sortOrder;
        this.columnType = columnType;
    }

        public CreateFileRequest(String name, String parentPath, String bucketName )
        {
            this.name = name;
            this.parentPath = parentPath;
            this.bucketName = bucketName;
        }

        public CreateFileRequest() {}
}