//package com.tc.bubblelayout;
//
//import android.graphics.Rect;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//
//public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {
//
//    private final int mColumnCount;
//    private final int mItemWidth;
//
//    /**
//     * 设置基本参数，以便计算间距
//     *
//     * @param columnCount 每列总数
//     * @param itemWidth   列表的单个item的宽度
//     */
//    public GridLayoutItemDecoration(int columnCount, int itemWidth) {
//        mColumnCount = columnCount;
//        mItemWidth = itemWidth;
//
//    }
//
//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        int childAdapterPosition = parent.getChildAdapterPosition(view);
//        int paddingLeft = parent.getPaddingLeft();
//        int paddingRight = parent.getPaddingRight();
//        //除去UI布局部分和内部padding，列表可以用来展示空白间距的部分的总共宽度
//        int mMarginWidthCount = parent.getMeasuredWidth() - mColumnCount * mItemWidth - paddingLeft - paddingRight;
//        int index = childAdapterPosition % mColumnCount;
//        //通过作图计算推到出左右间距的计算公式，把原有的列表空白空间从n等分分为n-1等分
//        float left = index * 1.0f / (mColumnCount * (mColumnCount - 1)) * mMarginWidthCount;
//        float right = (mColumnCount - (index + 1)) * 1.0f / (mColumnCount * (mColumnCount - 1)) * mMarginWidthCount;
//        outRect.set((int) left, 0, (int) right, 0);
////        LogUtil.d("childAdapterPosition 44:" + childAdapterPosition);
////        LogUtil.d("left:" + left + "  right:" + right);
////        LogUtil.d("view getWidth:" + view.getWidth() + "  getMeasuredWidth:" + view.getMeasuredWidth());
//
//    }
//
//}