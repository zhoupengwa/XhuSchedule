package weily.com.schedule.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by peng on 2017/9/11.
 * this is a class to give the space to recycleview in order to guarantee the there is some space betwween items ,look beautiful!
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    public SpaceItemDecoration(int space){
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left=space;
        outRect.bottom=space+1;
    }
}
