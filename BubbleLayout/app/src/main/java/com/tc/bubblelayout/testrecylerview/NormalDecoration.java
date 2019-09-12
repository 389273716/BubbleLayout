//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tc.bubblelayout.testrecylerview;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.tc.bubblelayout.DensityUtil;
import com.tc.bubblelayout.LogUtil;

public abstract class NormalDecoration extends ItemDecoration {
    private static final String TAG = "NormalDecoration";
    //是否关系排序
    private final boolean isSalutationSort;
    private Paint mHeaderTxtPaint = new Paint(1);
    private Paint mHeaderContentPaint;
    private int headerHeight;
    //不是字母排序是，Recycle距离顶部的距离
    private int noLetterHeaderHeight;
    private int textPaddingLeft;
    private int textSize = 34;
    private int textColor = Color.parseColor("#000000");
    private final float txtYAxis;
    private RecyclerView mRecyclerView;
    private SparseArray stickyHeaderPosArray = new SparseArray();
    private GestureDetector gestureDetector;
    private OnGestureListener gestureListener = new OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    protected NormalDecoration(Activity activity, boolean isSalutationSort) {
        this.isSalutationSort = isSalutationSort;
        this.mHeaderTxtPaint.setColor(this.textColor);
        this.mHeaderTxtPaint.setTextSize((float) this.textSize);
        this.mHeaderTxtPaint.setTextAlign(Align.LEFT);
        this.mHeaderContentPaint = new Paint(1);
        int headerContentColor = Color.parseColor("#f5f5f5");
        this.mHeaderContentPaint.setColor(headerContentColor);
        FontMetrics fontMetrics = this.mHeaderTxtPaint.getFontMetrics();
        float total = -fontMetrics.ascent + fontMetrics.descent;
        this.txtYAxis = total / 2.0F - fontMetrics.descent;

        headerHeight = DensityUtil.dip2px(activity, 25);
        noLetterHeaderHeight = DensityUtil.dip2px(activity, 10);
        textPaddingLeft = DensityUtil.dip2px(activity, 25);
    }

    @Override
    public void getItemOffsets(Rect outRect, View itemView, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, itemView, parent, state);
        if (this.mRecyclerView == null) {
            this.mRecyclerView = parent;
        }

        int currentCount;
        int pos = parent.getChildAdapterPosition(itemView);
        int itemCount = parent.getAdapter().getItemCount();
        LogUtil.d(TAG, "itemCount--" + itemCount + ",getItemPos: " + pos);
        int applyContactCount = 0;
        String curHeaderName = this.getHeaderName(pos);
        if (curHeaderName != null) {
            if (pos == 0 || !curHeaderName.equals(this.getHeaderName(pos - 1))) {
                outRect.top = this.headerHeight;
            }
        }
        //字母排序联系人都有headName,除了好友申请item；
        //关系排序都没有headName
        if (curHeaderName != null) {
//            if (pos == 0 || !curHeaderName.equals(this.getHeaderName(pos - 1))) {
//                if (!curHeaderName.equals(this.getHeaderName(pos + 1))) {
//                    itemView.setBackgroundResource(R.drawable.bg_list_item_corner_all_selector);
//                } else {
//                    itemView.setBackgroundResource(R.drawable.bg_list_item_corner_top_selector);
//                }
//            } else if (!curHeaderName.equals(this.getHeaderName(pos + 1))) {
//                itemView.setBackgroundResource(R.drawable.bg_list_item_corner_bottom_selector);
//            } else {
//                itemView.setBackgroundResource(R.drawable.bg_list_item_corner_none_selector);
//            }
        } else {
            if (pos == 0) {
                outRect.top = this.noLetterHeaderHeight;
            }
            if (isSalutationSort) {
                currentCount = itemCount;
            } else {
                currentCount = applyContactCount;
            }
//            if (currentCount == 1) {
//                itemView.setBackgroundResource(R.drawable.bg_list_item_corner_all_selector);
//            } else if (pos == 0) {
//                itemView.setBackgroundResource(R.drawable.bg_list_item_corner_top_selector);
//            } else if (pos == currentCount - 1) {
//                itemView.setBackgroundResource(R.drawable.bg_list_item_corner_bottom_selector);
//            } else {
//                itemView.setBackgroundResource(R.drawable.bg_list_item_corner_none_selector);
//            }
        }

    }

    public abstract String getHeaderName(int var1);

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
        super.onDrawOver(canvas, recyclerView, state);
        if (this.mRecyclerView == null) {
            this.mRecyclerView = recyclerView;
        }

        if (this.gestureDetector == null) {
            this.gestureDetector = new GestureDetector(recyclerView.getContext(), this.gestureListener);
            recyclerView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return NormalDecoration.this.gestureDetector.onTouchEvent(event);
                }
            });
        }

        this.stickyHeaderPosArray.clear();
        int childCount = recyclerView.getChildCount();
        int left = recyclerView.getLeft() + recyclerView.getPaddingLeft();
        int right = recyclerView.getRight() - recyclerView.getPaddingRight();
        String firstHeaderName = null;
        int translateTop = 0;

        for (int i = 0; i < childCount; ++i) {
            View childView = recyclerView.getChildAt(i);
            int pos = recyclerView.getChildAdapterPosition(childView);
            String curHeaderName = this.getHeaderName(pos);
            if (i == 0) {
                firstHeaderName = curHeaderName;
            }

            if (curHeaderName != null) {
                int viewTop = childView.getTop() + recyclerView.getPaddingTop();
                if (pos == 0 || !curHeaderName.equals(this.getHeaderName(pos - 1))) {
                    canvas.drawRect((float) left, (float) (viewTop - this.headerHeight), (float) right,
                            (float) viewTop, this.mHeaderContentPaint);
                    canvas.drawText(curHeaderName, (float) (left + this.textPaddingLeft),
                            (float) (viewTop - this.headerHeight / 2) + this.txtYAxis, this.mHeaderTxtPaint);
                    if (this.headerHeight < viewTop && viewTop <= 2 * this.headerHeight) {
                        translateTop = viewTop - 2 * this.headerHeight;
                    }

                    this.stickyHeaderPosArray.put(pos, viewTop);
                }
            }
        }

        if (firstHeaderName != null) {
            canvas.save();
            canvas.translate(0.0F, (float) translateTop);
            canvas.drawRect((float) left, 0.0F, (float) right, (float) this.headerHeight, this.mHeaderContentPaint);
            canvas.drawText(firstHeaderName, (float) (left + this.textPaddingLeft),
                    (float) (this.headerHeight / 2) + this.txtYAxis, this.mHeaderTxtPaint);
            canvas.restore();
        }
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        this.mHeaderTxtPaint.setTextSize((float) textSize);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.mHeaderTxtPaint.setColor(textColor);
    }
}
