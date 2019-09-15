package com.tc.bubblelayout.testrecylerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * author：   tc
 * date：      2019/9/15
 * version    1.0
 * description 根据不同数据类型，实现某一类聚合数据下显示相同展示风格的ItemDecoration类
 * modify by
 */
public abstract class AbstractGroupItemDecoration<T extends IGroupSort> extends RecyclerView.ItemDecoration {
    protected Paint mPaint;
    protected List<T> mList;
    protected Context mContext;

    @IntDef
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemLayoutType {
        int SINGLE_ITEM = 1;
        int TOP_LAYOUT_ITEM = 2;
        int MIDDLE_LAYOUT_ITEM = 3;
        int BOTTOM_LAYOUT_ITEM = 4;
    }


    public AbstractGroupItemDecoration(Context context, List<T> list) {
        this.mPaint = new Paint();
        mPaint.setAntiAlias(true);
        this.mList = list;
        this.mContext = context;
    }

    public int getItemCount() {
        if (mList == null || mList.size() <= 0) {
            return 0;
        }
        return mList.size();
    }

    public String getItemGroupIndex(int pos) {
        if (mList == null || pos < 0 || pos >= mList.size()) {
            return "-100";
        }
        T t = mList.get(pos);
        return t == null ? "-100" : t.getGroupSortType();
    }

    /**
     * 群组下的第一个item
     * 绘制背景图形，可能会被item内容遮挡
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawWhenFirstGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组下的中间item
     * 绘制背景图形，可能会被item内容遮挡
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawWhenMiddleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组下的最后一个item
     * 绘制背景图形，可能会被item内容遮挡
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawWhenLastGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组下只有唯一一个item
     * 绘制背景图形，可能会被item内容遮挡
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawWhenSingleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组下的第一个item
     * 在这个方法下可以做绘制顶部图形效果，不会被item内容覆盖
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawOverWhenFirstGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组下的中间item
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawOverWhenMiddleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组下的最后一个item
     * 在这个方法下可以做绘制顶部图形效果，不会被item内容覆盖
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawOverWhenLastGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组下只有唯一一个item
     * 在这个方法下可以做绘制顶部图形效果，不会被item内容覆盖
     *
     * @param canvas          画布
     * @param child           itemview
     * @param parent          父容器列表
     * @param state           布局状态
     * @param adapterPosition 当前item数据索引
     * @param item            item数据
     */
    public abstract void onDrawOverWhenSingleGroupItem(Canvas canvas, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 这里是群组中的第一个顶部item
     * 调整子布局item的左右上下位置
     *
     * @param outRect         布局左右上下间距
     * @param child           子布局
     * @param parent          父容器列表
     * @param state           布局装填
     * @param adapterPosition 数据索引
     * @param item            item数据
     */
    public abstract void getItemOffsetsWhenFirstGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 这里是群组中的中间部位item
     * 调整子布局item的左右上下位置
     *
     * @param outRect         布局左右上下间距
     * @param child           子布局
     * @param parent          父容器列表
     * @param state           布局装填
     * @param adapterPosition 数据索引
     * @param item            item数据
     */
    public abstract void getItemOffsetsWhenMiddleGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 这里是群组中的最后一个底部item
     * 调整子布局item的左右上下位置
     *
     * @param outRect         布局左右上下间距
     * @param child           子布局
     * @param parent          父容器列表
     * @param state           布局装填
     * @param adapterPosition 数据索引
     * @param item            item数据
     */
    public abstract void getItemOffsetsWhenLastGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);

    /**
     * 群组只有一个item
     * 调整子布局item的左右上下位置
     *
     * @param outRect         布局左右上下间距
     * @param child           子布局
     * @param parent          父容器列表
     * @param state           布局装填
     * @param adapterPosition 数据索引
     * @param item            item数据
     */
    public abstract void getItemOffsetsWhenSingleGroupItem(Rect outRect, View child, RecyclerView parent, RecyclerView
            .State state, int adapterPosition, T item);



    /**
     * 根据当前布局中的item view，获取它所属群组中的布局类型（群组只有一个元素，多个元素中（顶部、中间、底部item）这四种类型）
     *
     * @param child  item view
     * @param parent 列表父容器
     * @return 类型值
     */
    public Pair<Integer, Integer> getItemLayoutType(View child, RecyclerView parent) {
        int adapterPosition = parent.getChildAdapterPosition(child);
        if (adapterPosition < 0) {
            return null;
        }
        String lastGroupType = getItemGroupIndex(adapterPosition - 1);
        String curGroupType = getItemGroupIndex(adapterPosition);
        String nextGroupType = getItemGroupIndex(adapterPosition + 1);

        //是否群组中的第一个item
        boolean isTopItem = false;
        //是否最后一个item
        boolean isBottomItem = false;
        if (!lastGroupType.equals(curGroupType)) {
            isTopItem = true;
        }
        if (!nextGroupType.equals(curGroupType)) {
            isBottomItem = true;
        }
        //是第一个也是最后一个item，那么本群组只有一个item
        boolean isSingleItem = isTopItem && isBottomItem;
        //不是第一个也不是最后一个，那么就是中间的
        boolean isMiddle = !isTopItem && !isBottomItem;

        if (isSingleItem) {
            //此群组只有一个元素
            return new Pair<>(ItemLayoutType.SINGLE_ITEM, adapterPosition);
        } else if (isMiddle) {
            //群组多个元素时，位于中间的
            return new Pair<>(ItemLayoutType.MIDDLE_LAYOUT_ITEM, adapterPosition);
        } else if (isTopItem) {
            //多个元素的群组，位于最下面的元素
            return new Pair<>(ItemLayoutType.TOP_LAYOUT_ITEM, adapterPosition);
        } else {
            //群组下的最后一个元素
            return new Pair<>(ItemLayoutType.BOTTOM_LAYOUT_ITEM, adapterPosition);

        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        for (int i = 0, len = parent.getLayoutManager().getChildCount(); i < len; i++) {
            final View child = parent.getChildAt(i);
            Pair<Integer, Integer> pair = getItemLayoutType(child, parent);
            if (pair == null) {
                continue;
            }
            int itemLayoutType = pair.first;
            int adapterPosition = pair.second;
            T item = mList.get(adapterPosition);
            if (itemLayoutType == ItemLayoutType.SINGLE_ITEM) {
                onDrawWhenSingleGroupItem(canvas, child, parent, state, adapterPosition, item);
            } else if (itemLayoutType == ItemLayoutType.MIDDLE_LAYOUT_ITEM) {
                onDrawWhenMiddleGroupItem(canvas, child, parent, state, adapterPosition, item);
            } else if (itemLayoutType == ItemLayoutType.TOP_LAYOUT_ITEM) {
                onDrawWhenFirstGroupItem(canvas, child, parent, state, adapterPosition, item);
            } else {
                onDrawWhenLastGroupItem(canvas, child, parent, state, adapterPosition, item);
            }

        }

    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        for (int i = 0, len = parent.getLayoutManager().getChildCount(); i < len; i++) {
            final View child = parent.getChildAt(i);
            Pair<Integer, Integer> pair = getItemLayoutType(child, parent);
            if (pair == null) {
                continue;
            }
            int itemLayoutType = pair.first;
            int adapterPosition = pair.second;
            T item = mList.get(adapterPosition);
            if (itemLayoutType == ItemLayoutType.SINGLE_ITEM) {
                onDrawOverWhenSingleGroupItem(canvas, child, parent, state, adapterPosition, item);
            } else if (itemLayoutType == ItemLayoutType.MIDDLE_LAYOUT_ITEM) {
                onDrawOverWhenMiddleGroupItem(canvas, child, parent, state, adapterPosition, item);
            } else if (itemLayoutType == ItemLayoutType.TOP_LAYOUT_ITEM) {
                onDrawOverWhenFirstGroupItem(canvas, child, parent, state, adapterPosition, item);
            } else {
                onDrawOverWhenLastGroupItem(canvas, child, parent, state, adapterPosition, item);
            }

        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View child, RecyclerView parent, RecyclerView.State state) {
        Pair<Integer, Integer> pair = getItemLayoutType(child, parent);
        if (pair == null) {
            return;
        }
        int itemLayoutType = pair.first;
        int adapterPosition = pair.second;
        T item = mList.get(adapterPosition);
        if (itemLayoutType == ItemLayoutType.SINGLE_ITEM) {
            getItemOffsetsWhenSingleGroupItem(outRect, child, parent, state, adapterPosition, item);
        } else if (itemLayoutType == ItemLayoutType.MIDDLE_LAYOUT_ITEM) {
            getItemOffsetsWhenMiddleGroupItem(outRect, child, parent, state, adapterPosition, item);
        } else if (itemLayoutType == ItemLayoutType.TOP_LAYOUT_ITEM) {
            getItemOffsetsWhenFirstGroupItem(outRect, child, parent, state, adapterPosition, item);
        } else {
            getItemOffsetsWhenLastGroupItem(outRect, child, parent, state, adapterPosition, item);
        }

    }

}