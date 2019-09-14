package com.tc.bubblelayout.testrecylerview;

/**
 * author：   tc
 * date：      2019/9/12 & 15:10
 * version    1.0
 * description
 * modify by
 */
public interface IGroupSort {
    /**
     * 当前数据所在分组索引
     * @return
     */
    String getGroupSortType();

    /**
     * 获取颜色id，用于
     * @return
     */
    int getGroupBackgroundColorId();

    /**
     * 获取item上下间距
     * @return
     */
    int getGroupDividerSize();


}
