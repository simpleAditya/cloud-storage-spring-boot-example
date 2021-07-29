package bean;

import java.util.ArrayList;
import java.util.List;

public class RztAzureObjectList
{
    private List<RztAzureObject> objects = new ArrayList<>();

    private String nextItemIndex;

    public List<RztAzureObject> getObjects() {
        return objects;
    }

    public void setObjects(List<RztAzureObject> objects) {
        this.objects = objects;
    }

    public String getNextItemIndex() {
        return nextItemIndex;
    }

    public void setNextItemIndex(String nextItemIndex) {
        this.nextItemIndex = nextItemIndex;
    }

    public boolean isHasMoreItems() {
        return hasMoreItems;
    }

    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    public long getTimeTakenInMillis() {
        return timeTakenInMillis;
    }

    public void setTimeTakenInMillis(long timeTakenInMillis) {
        this.timeTakenInMillis = timeTakenInMillis;
    }

    private boolean hasMoreItems;

    private long timeTakenInMillis = 0L;

    public RztAzureObjectList( List<RztAzureObject> objects, String nextItemIndex,
                               long timeTakenInMillis )
    {
        this.objects = objects;
        this.nextItemIndex = nextItemIndex;
        this.timeTakenInMillis = timeTakenInMillis;
    }

    public RztAzureObjectList() {
    }
}