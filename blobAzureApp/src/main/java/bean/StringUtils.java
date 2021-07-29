package bean;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Common util methods for generic null and empty checks
 *
 * @author karthik
 */
public final class StringUtils {

	/**
	 * Class contains static methods and variables, do not allow for class creation
	 */
	private StringUtils()
	{
		throw new IllegalStateException("Utility class cannot create instance of " + this.getClass().getName());
	}

	public static final String getFormattedString( String a, Object b )
	{
		return String.format("%s: %s", a, b);
	}

	public static boolean isNullorEmpty( final String val )
	{
		return isNull(val) || val.trim().isEmpty();
	}

	public static boolean isNullorEmpty( final List<?> val )
	{
		return isNull(val) || val.isEmpty();
	}


	public static boolean isNullorEmpty( final Object val )
	{
		return isNull(val) || val.toString().isEmpty();
	}


	public static boolean isNullorEmpty( final Collection<?> val )
	{
		return isNull(val) || val.isEmpty();
	}

	public static boolean isNullorEmpty( final Number val )
	{
		return isNull(val) || val.doubleValue() == 0 || val.intValue() == 0;
	}

	public static boolean isNullorEmpty( final Map<?, ?> val )
	{
		return isNull(val) || val.isEmpty();
	}

	public static boolean isNullorEmptyString( final String val )
	{
		return isNull(val) || val.isEmpty() || val.trim().equalsIgnoreCase("empty");
	}

	public static boolean isNullorEmptyOrNullString( final String val )
	{
		return isNull(val) || val.isEmpty() || val.trim().isEmpty() || val.trim().equalsIgnoreCase("empty") || val
				.trim().equalsIgnoreCase("null");
	}

	private static boolean isNull( final Object val )
	{
		return null == val;
	}

	public static String toTitleCase( String input )
	{
		final StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;

		for( char charecter : input.toCharArray() )
		{
			if( Character.isSpaceChar(charecter) )
			{
				nextTitleCase = true;
			}
			else if( nextTitleCase )
			{
				charecter = Character.toTitleCase(charecter);
				nextTitleCase = false;
			}

			titleCase.append(charecter);
		}

		return titleCase.toString();
	}
}
