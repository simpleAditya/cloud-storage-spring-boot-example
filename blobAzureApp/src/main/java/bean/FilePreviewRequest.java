package bean;

public class FilePreviewRequest {

	private String delimiter;

	private String escapeCharacter;

	private Integer headerPosition;

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

	public Boolean getHeaderEnabled() {
		return headerEnabled;
	}

	public void setHeaderEnabled(Boolean headerEnabled) {
		this.headerEnabled = headerEnabled;
	}

	private String quoteCharacter;

	private Boolean headerEnabled;
}
