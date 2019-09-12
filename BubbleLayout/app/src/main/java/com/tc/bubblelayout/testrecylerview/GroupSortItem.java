package com.tc.bubblelayout.testrecylerview;

/**
 * author：   tc
 * date：      2019/9/12 & 15:52
 * version    1.0
 * description
 * modify by
 */
public class GroupSortItem implements IGroupSort {
    private int groupIndex;
    private int groupColorId;

    public GroupSortItem(int groupIndex, int groupColorId) {
        this.groupIndex = groupIndex;
        this.groupColorId = groupColorId;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getGroupColorId() {
        return groupColorId;
    }

    public void setGroupColorId(int groupColorId) {
        this.groupColorId = groupColorId;
    }

    @Override
    public int getGroupSortIndex() {
        return groupIndex;
    }

    @Override
    public int getColorId() {
        return groupColorId;
    }

    @Override
    public int getGroupDivider() {
        return 10;
    }

}
