package bean;

import org.springframework.util.StringUtils;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by antolivish on 06/05/20.
 */
public class FilePathUtil {

	private static final String NAME_REGEX = "^([a-zA-Z0-9 _-]|(\\.)){3,60}$";

	private static final Pattern PATTERN = Pattern.compile(NAME_REGEX);

	private static final String PATH_SEPERATOR = File.separator;

	private FilePathUtil()
	{
		/**
		 * Util class
		 */

	}

	public static boolean isValidFileName( String name ) throws DataConnectorException
	{

		if( name != null && !name.trim().isEmpty() && PATTERN.matcher(name).matches() )
		{
			return true;
		}
		throw new DataConnectorException("Invalid name given " + name
				+ ". Only Alphabets, Numbers, Space, _ and - are allowed. Name must have min 3 and maximum of 60 characters");
	}

	public static String appendSlashIfNot( String path )
	{
		if( StringUtils.isEmpty(path) )
		{
			return "";
		}
		if( path.endsWith(PATH_SEPERATOR) )
		{
			return path;
		}
		return path + PATH_SEPERATOR;
	}

	public static String removeSlashIfStartsWith( String path )
	{
		if( path ==null || StringUtils.isEmpty(path) )
		{
			return "";
		}
		if( path.startsWith(PATH_SEPERATOR) )
		{
			return path.substring(1);
		}
		return path;
	}

	public static String removeSlashIfEndsWith( String path )
	{
		if( path ==null || StringUtils.isEmpty(path) )
		{
			return "";
		}
		if( path.endsWith(PATH_SEPERATOR) )
		{
			return path.substring(0, path.length() - PATH_SEPERATOR.length());
		}
		return path;
	}

	public static String getFileName( String path )
	{
		int index = path.lastIndexOf(File.separator);
		return path.substring(index + 1);
	}
}
