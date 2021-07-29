package java.com.razorthink.ai.s3dataconnector.util;

/**
 * Created by antolivish on 06/05/20.
 */
public class ExceptionMessageUtil {

    private static final String START_POINT = "(Service:";

    private ExceptionMessageUtil()
    {
        //static Util class
    }

    public static String parseMessage( String originalMessage )
    {
        if( originalMessage.contains(START_POINT) )
        {
            return originalMessage.substring(0, originalMessage.indexOf(START_POINT) - 1);
        }
        return originalMessage;
    }
}
