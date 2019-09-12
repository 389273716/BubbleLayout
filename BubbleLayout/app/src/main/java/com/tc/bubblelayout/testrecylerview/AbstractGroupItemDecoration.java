package com.tc.bubblelayout.testrecylerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tc.bubblelayout.DensityUtil;
import com.tc.bubblelayout.LogUtil;

import java.util.List;

public abstract class AbstractGroupItemDecoration<T extends IGroupSort> extends RecyclerView.ItemDecoration {
    private static final String TAG = "CornerItemDecoration";
    private Paint mPaint;
    private List<T> list;
    private Context context;

    public AbstractGroupItemDecoration(Context context, List<T> list) {
        mPaint = new Paint();
        ;
        this.list = list;
        this.context = context;
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(DensityUtil.dip2px(context, 1));
    }

    public int getItemGroupIndex(int pos) {
        if (list == null || pos < 0 || pos >= list.size()) {
            return -1;
        }
        T t = list.get(pos);
        return t == null ? -1 : t.getGroupSortIndex();
    }

    public abstract void onDrawWhenFirstGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void onDrawWhenMiddleGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void onDrawWhenLastGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void onDrawWhenSingleGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void onDrawOverWhenFirstGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void onDrawOverWhenMiddleGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void onDrawOverWhenLastGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void onDrawOverWhenSingleGroupItem(Canvas canvas, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void getItemOffsetsWhenFirstGroupItem(Rect outRect, View view, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void getItemOffsetsWhenMiddleGroupItem(Rect outRect, View view, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void getItemOffsetsWhenLastGroupItem(Rect outRect, View view, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    public abstract void getItemOffsetsWhenSingleGroupItem(Rect outRect, View view, RecyclerView parent, RecyclerView
            .State state, int adapterIndex, T item);

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        for (int i = 0, len = parent.getLayoutManager().getChildCount(); i < len; i++) {
            final View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);
            LogUtil.d(TAG, "i:" + i + "  pos:" + pos + "  child:" + child.getWidth());
            RectF rectF = new RectF(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            LogUtil.d(TAG, "rectF:" + rectF.toString());
            Path srcPath = new Path();

            int lastGroupIndex = getItemGroupIndex(pos - 1);
            int curGroupIndex = getItemGroupIndex(pos);
            int nextGroupIndex = getItemGroupIndex(pos + 1);

            T item = list.get(pos);

            //是否支持上圆角
            boolean isTopCorner = false;
            //是否支持下圆角
            boolean isBottomCorner = false;
            if (lastGroupIndex != curGroupIndex) {
                isTopCorner = true;
            }
            if (nextGroupIndex != curGroupIndex) {
                isBottomCorner = true;
            }
            boolean isSingleItem = isTopCorner && isBottomCorner;
            boolean isMiddle = !isTopCorner || !isBottomCorner;
            if (isSingleItem) {
                onDrawWhenSingleGroupItem();
            } else if (isMiddle) {
                onDrawWhenMiddleGroupItem();
            } else if (isTopCorner) {
                onDrawWhenFirstGroupItem();
            } else if (isBottomCorner) {
                onDrawWhenLastGroupItem();
            }


        }

    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
//        int paddingLeft = parent.getPaddingLeft();
//        int paddingRight = parent.getPaddingRight();

        int lastGroupIndex = getItemGroupIndex(pos - 1);
        int curGroupIndex = getItemGroupIndex(pos);
        int nextGroupIndex = getItemGroupIndex(pos + 1);

        T item = list.get(pos);
        if (lastGroupIndex != curGroupIndex && pos != 0) {
            outRect.set(0, DensityUtil.dip2px(context, item.getGroupDivider()), 0, 0);
        } else {
            outRect.set(0, 0, 0, 0);
        }

//        outRect.set(40, 0, 40, 0);

    }

}