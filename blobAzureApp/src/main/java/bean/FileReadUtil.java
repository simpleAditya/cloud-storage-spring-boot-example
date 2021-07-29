package bean;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by antolivish on 06/05/20
 */

public class FileReadUtil {

    private static final String DELIMITER = ",";

    private static final String QUOTE_CHARACTER = String.valueOf(CSVParser.DEFAULT_QUOTE_CHARACTER);

    private static final String ESCAPE_CHARACTER = String.valueOf(CSVParser.DEFAULT_ESCAPE_CHARACTER);

    private static final Integer HEADER_POSITION = 1;

    private static final String UNNAMED_COLUMN = "Unnamed Column - ";

    private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    private FileReadUtil() {
    }

    public static FilePreviewResponse parseFileForPreview(InputStream inputStream, String fileName, Long fileSize,
                                                          FilePreviewRequest filePreviewRequestUI) throws DataConnectorException {
        final FilePreviewResponse filePreviewResponseUI = new FilePreviewResponse();
        try {
            String extension = FilenameUtils.getExtension(fileName).toLowerCase();

            validateFilePreviewRequest(filePreviewRequestUI, filePreviewResponseUI);
            final Map<Integer, List<String>> data = new LinkedHashMap<>();

            if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
                doXlsParsing(data, inputStream, extension);
            } else if (extension.equalsIgnoreCase("tsv")) {
                filePreviewResponseUI.setDelimiter("/t");
                doTsvParsing(data, inputStream);
            } else {
                doCsvParsing(data, inputStream, filePreviewResponseUI);
            }

            //set output filePreviewResponseUI
            filePreviewResponseUI.setFileName(fileName);
            filePreviewResponseUI.setFileSize(fileSize);
            filePreviewResponseUI.setUnit("KB");

            Integer headerPosition = filePreviewResponseUI.getHeaderPosition();
            //check header position and do respective reordering of data map
            final Map<Integer, List<String>> reorderedData = new LinkedHashMap<>();
            if (headerPosition > 1) {
                final Integer actualDataSize = data.size();

                if (headerPosition > actualDataSize) {
                    throw new DataConnectorException(
                            "Invalid header passed! Please provide header position between 1 - " + actualDataSize);
                }

                final List<String> headerValues = data.get(headerPosition);
                reorderedData.put(1, headerValues);

                int count = 2;
                for (int i = 1; i <= data.size(); i++) {
                    if (i != headerPosition) {
                        reorderedData.put(count, data.get(i));
                        count++;
                    }
                }

                setFilePreviewData(reorderedData, filePreviewRequestUI, filePreviewResponseUI);
            } else {
                setFilePreviewData(data, filePreviewRequestUI, filePreviewResponseUI);
            }
        } catch (final DataConnectorException e) {
            throw e;
        } catch (final UnsupportedOperationException e) {
            throw new DataConnectorException(e.getMessage(), e);
        } catch (final Exception e) {
            throw new DataConnectorException(INTERNAL_SERVER_ERROR, e);
        }
        return filePreviewResponseUI;
    }

    private static void setFilePreviewData(Map<Integer, List<String>> reorderedData,
                                           FilePreviewRequest filePreviewRequestUI, FilePreviewResponse filePreviewResponseUI) {
        //check for headerEnabled and set data accordingly
        if (null != filePreviewRequestUI && null != filePreviewRequestUI.getHeaderEnabled()) {
            dataWithHeader(reorderedData, filePreviewRequestUI.getHeaderEnabled());
            fillEmptyHeaderColumns(reorderedData, 2);
        }

        filePreviewResponseUI.setFileData(reorderedData);
    }

    private static void validateFilePreviewRequest(FilePreviewRequest filePreviewRequestUI,
                                                   FilePreviewResponse filePreviewResponseUI) throws DataConnectorException {
        //if null input then use default values
        if (null == filePreviewRequestUI
                || StringUtils.isNullorEmpty(filePreviewRequestUI.getDelimiter()) && StringUtils
                .isNullorEmpty(filePreviewRequestUI.getEscapeCharacter()) && StringUtils
                .isNullorEmpty(filePreviewRequestUI.getHeaderPosition()) && StringUtils
                .isNullorEmpty(filePreviewRequestUI.getQuoteCharacter())) {
            filePreviewResponseUI.setDelimiter(DELIMITER);
            filePreviewResponseUI.setEscapeCharacter(ESCAPE_CHARACTER);
            filePreviewResponseUI.setHeaderPosition(HEADER_POSITION);
            filePreviewResponseUI.setQuoteCharacter(QUOTE_CHARACTER);
            //set headerEnabled to true
            filePreviewResponseUI.setHeaderEnabled(true);
        }
        // if input is not null, validate all fields and do operation
        else if (!StringUtils.isNullorEmptyOrNullString(filePreviewRequestUI.getDelimiter()) && !StringUtils
                .isNullorEmptyOrNullString(filePreviewRequestUI.getEscapeCharacter()) && !StringUtils
                .isNullorEmptyOrNullString(filePreviewRequestUI.getQuoteCharacter()) && !StringUtils
                .isNullorEmpty(filePreviewRequestUI.getHeaderPosition()) && null != filePreviewRequestUI
                .getHeaderEnabled()) {
            filePreviewResponseUI.setDelimiter(filePreviewRequestUI.getDelimiter());
            filePreviewResponseUI.setEscapeCharacter(filePreviewRequestUI.getEscapeCharacter());
            filePreviewResponseUI.setQuoteCharacter(filePreviewRequestUI.getQuoteCharacter());

            if (filePreviewRequestUI.getHeaderEnabled() != null && filePreviewRequestUI.getHeaderEnabled()) {
                if (null != filePreviewRequestUI.getHeaderPosition() && filePreviewRequestUI.getHeaderPosition() < 1) {
                    throw new DataConnectorException("Invalid header value passed");
                } else {
                    filePreviewResponseUI.setHeaderPosition(filePreviewRequestUI.getHeaderPosition());
                }
                filePreviewResponseUI.setHeaderEnabled(true);
            } else {
                filePreviewResponseUI.setHeaderEnabled(false);
            }
        } else {
            throw new DataConnectorException("Invalid or empty request passed");
        }
    }

    private static void fillEmptyHeaderColumns(Map<Integer, List<String>> data, int key) {

        // Find the highest number columns.
        ArrayList<Integer> rowSizeList = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : data.entrySet()) {
            List<String> rowData = entry.getValue();
            final int rowSize = rowData.size();
            rowSizeList.add(rowSize);
        }

        int maxColumnNumber = Collections.max(rowSizeList);

        List<String> headerData = data.get(key);

        int headerSize = headerData.size();

        if (headerSize < maxColumnNumber) {
            for (int i = headerSize + 1; i <= maxColumnNumber; i++) {
                headerData.add(UNNAMED_COLUMN + i);
            }

            data.put(key, headerData);
        }

    }

    private static void dataWithHeader(Map<Integer, List<String>> data, Boolean headerEnabled) {
        if (headerEnabled != null && !headerEnabled) {
            fillDefaultHeaderColumns(data);
        } else {
            fillEmptyHeaderColumns(data, 1);
        }
    }

    private static void fillDefaultHeaderColumns(Map<Integer, List<String>> data) {
        // Find the highest number columns.
        ArrayList<Integer> rowSizeList = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : data.entrySet()) {
            rowSizeList.add(entry.getValue().size());
        }

        int maxColumnNumber = Collections.max(rowSizeList);

        List<String> defaultHeaderColumns = new ArrayList<>();

        for (int i = 1; i <= maxColumnNumber; i++) {
            defaultHeaderColumns.add(UNNAMED_COLUMN + i);
        }

        data.put(0, defaultHeaderColumns);
    }

    private static void doXlsParsing(Map<Integer, List<String>> data, InputStream inputStream, String extension)
            throws DataConnectorException {
        try {
            Workbook workbook;
            if (extension.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }

            final Sheet sheet = workbook.getSheetAt(0);

            // Iterate through each rows one by one
            final Iterator<Row> rowIterator = sheet.iterator();

            // Create a DataFormatter to format and get each cell's value as String
            final DataFormatter dataFormatter = new DataFormatter();

            int rowCount = 1;

            if (sheet.getPhysicalNumberOfRows() < 1) {
                return;
            }
            while (rowIterator.hasNext() && rowCount <= 500) {
                final Row row = rowIterator.next();

                //get total number of columns
                final short lastCellNum = row.getLastCellNum();

                // For each row, iterate through all the columns
                for (int cn = 0; cn < lastCellNum; cn++) {
                    final Cell c = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String cellValue;
                    if (c == null) {
                        // The spreadsheet is empty in this cell
                        cellValue = UNNAMED_COLUMN + (cn + 1);
                    } else {
                        // Do something useful with the cell's contents
                        cellValue = dataFormatter.formatCellValue(c);

                    }
                    if (data.containsKey(rowCount)) {
                        data.get(rowCount).add(cellValue);
                    } else {
                        data.put(rowCount, new ArrayList<>(Collections.singletonList(cellValue)));
                    }
                }
                rowCount++;
            }

        } catch (final IOException e) {
            throw new DataConnectorException("Something went wrong while reading xls/xlsx file");
        }
    }

    private static void doCsvParsing(Map<Integer, List<String>> data, InputStream inputStream,
                                     FilePreviewResponse filePreviewResponseUI) throws DataConnectorException {
        try {
            //openCSV logic
            final CSVParser parser = new CSVParserBuilder()
                    .withSeparator(filePreviewResponseUI.getDelimiter().charAt(0))
                    .withEscapeChar(filePreviewResponseUI.getEscapeCharacter().charAt(0))
                    .withQuoteChar(filePreviewResponseUI.getQuoteCharacter().charAt(0)).build();

            final CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(parser)
                    .build();

            String[] line;
            int rowCount = 1;

            while ((line = reader.readNext()) != null && rowCount < 501) {
                //add logic to store rows
                final List<String> rowData = new ArrayList<>(Arrays.asList(line));

                data.put(rowCount, rowData);
                rowCount++;
            }
        } catch (Exception e) {
            throw new DataConnectorException("Something went wrong while reading csv file");
        }
    }

    private static void doTsvParsing(Map<Integer, List<String>> data, InputStream inputStream) throws DataConnectorException {
        try {
            TsvParser parser = new TsvParser(new TsvParserSettings());
            List<String[]> parsedRows = parser.parseAll(inputStream);
            int rowCount = 1;

            for (String[] line : parsedRows) {
                //add logic to store rows
                final List<String> rowData = new ArrayList<>(Arrays.asList(line));

                data.put(rowCount, rowData);
                rowCount++;
                if (rowCount > 501) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new DataConnectorException("Something went wrong while reading csv file");
        }
    }

    public static byte[] createPDFPreviewFile(byte[] bytes, String tempFile) throws WebappException {
        String pdfFile = tempFile + File.separator + "temp.pdf";
        Path path = Paths.get(pdfFile);
        try {
            //Loading an existing PDF document
            PDDocument doc = PDDocument.load(bytes);

            //Instantiating Splitter class
            Splitter splitter = new Splitter();
            splitter.setEndPage(5);
            splitter.setSplitAtPage(5);

            //splitting the pages of a PDF document
            List<PDDocument> pages = splitter.split(doc);

            new File(tempFile).mkdirs();
            PDDocument pd = pages.listIterator().next();
            pd.save(pdfFile);

            return Files.readAllBytes(path);

        } catch (Exception e) {
            throw new WebappException("Error creating preview file");
        } finally {
            FileUtil.deleteFile(Paths.get(tempFile));
        }
    }

}
