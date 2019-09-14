package com.tc.bubblelayout.testrecylerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tc.bubblelayout.DensityUtil;

import java.util.List;

/**
 * author：   tc
 * date：     2019/9/14 & 16:29
 * version    1.0
 * description  列表根据不同数据类型，拆分为多个群组进行布局显示的布局间隔绘制类
 * modify by
 */
public class GroupCornerItemDecoratin extends AbstractGroupItemDecoration {

    private int mCornerRadius;

    public GroupCornerItemDecoratin(Context context, List list, int cornerRadius) {
        super(context, list);
        this.mCornerRadius = cornerRadius;
    }

    private void drawGroupCorner(Canvas canvas, View child, IGroupSort item, float[] corners) {
        Path srcPath = new Path();
        RectF rectF = new RectF(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        if (item != null) {
            mPaint.setColor(mContext.getResources().getColor(item.getGroupBackgroundColorId()));
        }
        //默认无圆角
        srcPath.addRoundRect(rectF, corners, Path.Direction.CCW);
        canvas.drawPath(srcPath, mPaint);
    }

    @Override
    public void onDrawWhenFirstGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State state,
                                         int adapterPosition, IGroupSort item) {
        float[] corners = {mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, 0, 0, 0, 0};
        drawGroupCorner(canvas, child, item, corners);
    }


    @Override
    public void onDrawWhenMiddleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State state,
                                          int adapterPosition, IGroupSort item) {
        float[] corners = {0, 0, 0, 0, 0, 0, 0, 0};
        drawGroupCorner(canvas, child, item, corners);
    }

    @Override
    public void onDrawWhenLastGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State state, int
            adapterPosition, IGroupSort item) {
        float[] corners = {0, 0, 0, 0, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius};
        drawGroupCorner(canvas, child, item, corners);
    }

    @Override
    public void onDrawWhenSingleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State state,
                                          int adapterPosition, IGroupSort item) {
        float[] corners = {mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius,
                mCornerRadius, mCornerRadius};
        drawGroupCorner(canvas, child, item, corners);
    }

    @Override
    public void onDrawOverWhenFirstGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State
            state, int adapterPosition, IGroupSort item) {

    }

    @Override
    public void onDrawOverWhenMiddleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State
            state, int adapterPosition, IGroupSort item) {

    }

    @Override
    public void onDrawOverWhenLastGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State state,
                                            int adapterPosition, IGroupSort item) {

    }

    @Override
    public void onDrawOverWhenSingleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView.State
            state, int adapterPosition, IGroupSort item) {

    }

    @Override
    public void getItemOffsetsWhenFirstGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView.State
            state, int adapterPosition, IGroupSort item) {

        if (adapterPosition != 0) {
            //第一个群组不加顶部间距
            outRect.set(0, DensityUtil.dip2px(mContext, item.getGroupDividerSize()), 0, 0);
        }
    }

    @Override
    public void getItemOffsetsWhenMiddleGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView.State
            state, int adapterPosition, IGroupSort item) {
        outRect.set(0, 0, 0, 0);
    }

    @Override
    public void getItemOffsetsWhenLastGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView.State
            state, int adapterPosition, IGroupSort item) {
        outRect.set(0, 0, 0, 0);
    }

    @Override
    public void getItemOffsetsWhenSingleGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView.State
            state, int adapterPosition, IGroupSort item) {
        outRect.set(0, DensityUtil.dip2px(mContext, item.getGroupDividerSize()), 0, 0);

    }
}
