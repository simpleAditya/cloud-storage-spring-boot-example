package gcpdataconnector.enums;

/**
 * Created by antolivish on 29/06/20.
 */
public enum DSColumnType {

    MODIFIED_ON("lastModified"), TYPE("type"), NAME("name"), NONE("none");

    private String type;

    DSColumnType(String type )
    {
        this.type = type;
    }

    public static DSColumnType getSortType( String type )
    {
        for( DSColumnType extension : DSColumnType.values() )
        {
            if( extension.getType().equalsIgnoreCase(type) )
            {
                return extension;
            }
        }
        return NONE;
    }

    public String getType()
    {
        return type;
    }
}
