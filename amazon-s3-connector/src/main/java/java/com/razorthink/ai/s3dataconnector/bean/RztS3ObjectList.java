package java.com.razorthink.ai.s3dataconnector.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antolivish on 06/05/20.
 */
@Data
public class RztS3ObjectList {

    private List<RztS3Object> objects = new ArrayList<>();

    private String nextItemIndex;

    private boolean hasMoreItems;

    private long timeTakenInMillis = 0L;

    public RztS3ObjectList( List<RztS3Object> objects, String nextItemIndex, long timeTakenInMillis )
    {
        this.objects = objects;
        this.nextItemIndex = nextItemIndex;
        this.timeTakenInMillis = timeTakenInMillis;
    }

    public RztS3ObjectList() {
    }
}
