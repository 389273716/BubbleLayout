package com.tc.bubblelayout.testrecylerview;

/**
 * author：   tc
 * date：      2019/9/12 & 15:52
 * version    1.0
 * description
 * modify by
 */
public abstract class GroupSortItem implements IGroupSort {
    private String groupType;
    private int groupBackgroundColorId;
    private int groupDividerSize;

    public GroupSortItem(String groupType, int groupBackgroundColorId, int groupDividerSize) {
        this.groupType = groupType;
        this.groupBackgroundColorId = groupBackgroundColorId;
        this.groupDividerSize = groupDividerSize;
    }


    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public void setGroupBackgroundColorId(int groupBackgroundColorId) {
        this.groupBackgroundColorId = groupBackgroundColorId;
    }

    public void setGroupDividerSize(int groupDividerSize) {
        this.groupDividerSize = groupDividerSize;
    }

    @Override
    public String getGroupSortType() {
        return groupType;
    }

    @Override
    public int getGroupBackgroundColorId() {
        return groupBackgroundColorId;
    }

    @Override
    public int getGroupDividerSize() {
        return groupDividerSize;
    }

}
