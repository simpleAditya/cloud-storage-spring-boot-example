package bean;

public interface MessageBundleResource {

	String getMessage(final String key);

	String getMessage(final String key, Object... objects);

}
