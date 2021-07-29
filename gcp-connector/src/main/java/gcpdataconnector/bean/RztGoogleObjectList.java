package gcpdataconnector.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RztGoogleObjectList
{
    private List<RztGoogleObject> objects = new ArrayList<>();

    private String nextItemIndex;

    private boolean hasMoreItems;

    private long timeTakenInMillis = 0L;

    public RztGoogleObjectList( List<RztGoogleObject> objects, String nextItemIndex,
                               long timeTakenInMillis )
    {
        this.objects = objects;
        this.nextItemIndex = nextItemIndex;
        this.timeTakenInMillis = timeTakenInMillis;
    }

    public RztGoogleObjectList() {
    }
}