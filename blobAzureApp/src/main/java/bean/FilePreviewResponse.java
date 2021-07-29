package bean;

import java.util.List;
import java.util.Map;

public class FilePreviewResponse {

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getEscapeCharacter() {
		return escapeCharacter;
	}

	public void setEscapeCharacter(String escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	public Integer getHeaderPosition() {
		return headerPosition;
	}

	public void setHeaderPosition(Integer headerPosition) {
		this.headerPosition = headerPosition;
	}

	public String getQuoteCharacter() {
		return quoteCharacter;
	}

	public void setQuoteCharacter(String quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}

	public boolean isHeaderEnabled() {
		return headerEnabled;
	}

	public void setHeaderEnabled(boolean headerEnabled) {
		this.headerEnabled = headerEnabled;
	}

	public Map<Integer, List<String>> getFileData() {
		return fileData;
	}

	public void setFileData(Map<Integer, List<String>> fileData) {
		this.fileData = fileData;
	}

	private Long fileSize;

	private String unit;

	private String delimiter;

	private String escapeCharacter;

	private Integer headerPosition;

	private String quoteCharacter;

	private boolean headerEnabled;

	private Map<Integer, List<String>> fileData;
}
