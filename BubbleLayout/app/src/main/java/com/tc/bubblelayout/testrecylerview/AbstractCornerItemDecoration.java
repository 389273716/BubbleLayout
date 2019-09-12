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

public abstract class AbstractCornerItemDecoration extends AbstractGroupItemDecoration {
    private static final String TAG = "CornerItemDecoration";

    public AbstractCornerItemDecoration(Context context, List list) {
        super(context, list);
    }



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
            if (item != null) {
                mPaint.setColor(context.getResources().getColor(item.getColorId()));
            }
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
            //默认无圆角
            float[] corners = {0, 0, 0, 0, 0, 0, 0, 0};
            if (isTopCorner) {
                //添加上圆角
                for (int j = 0; j < 4; j++) {
                    corners[j] = 50;
                }
            }
            if (isBottomCorner) {
                //添加下圆角
                for (int j = 4; j < 8; j++) {
                    corners[j] = 50;
                }
            }
            srcPath.addRoundRect(rectF, corners, Path.Direction.CCW);
            canvas.drawPath(srcPath, mPaint);
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