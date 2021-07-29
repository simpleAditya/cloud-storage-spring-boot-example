package bean;

/**
 * Created by antolivish on 06/05/20.
 */
public class DataConnectorException extends Exception {

	private final String message;

	public DataConnectorException()
	{
		this.message = null;
	}

	public DataConnectorException(String message )
	{
		this.message = message;
	}

	public DataConnectorException(String message, Throwable t )
	{
		super(t);
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
