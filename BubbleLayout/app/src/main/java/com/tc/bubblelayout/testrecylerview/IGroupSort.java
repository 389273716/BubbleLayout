package com.tc.bubblelayout.testrecylerview;

/**
 * author：   tc
 * date：      2019/9/12 & 15:10
 * version    1.0
 * description  实现本接口，以便完成ItemDecoration的分组逻辑处理以及绘制
 * modify by
 */
public interface IGroupSort {
    /**
     * 当前数据所在分组索引
     *
     * @return
     */
    String getGroupSortType();

    /**
     * 获取颜色id，用于
     *
     * @return
     */
    int getGroupBackgroundColorId();

    /**
     * item按压时的背景颜色
     *
     * @return
     */
    int getGroupPressColorId();

    /**
     * 获取item上下间距,单位为dp
     *
     * @return
     */
    int getGroupDividerSize();


}
