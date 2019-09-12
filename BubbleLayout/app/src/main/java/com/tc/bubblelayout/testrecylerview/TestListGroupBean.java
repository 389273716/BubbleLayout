package com.tc.bubblelayout.testrecylerview;

import com.tc.bubblelayout.R;

/**
 * author：   tc
 * date：      2019/9/12 & 15:11
 * version    1.0
 * description
 * modify by
 */
public class TestListGroupBean implements IGroupSort {


    private String name;
    private int groupIndex;

    public TestListGroupBean(String name, int groupIndex) {
        this.name = name;
        this.groupIndex = groupIndex;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    @Override
    public int getGroupSortIndex() {
        return groupIndex;
    }

    @Override
    public int getColorId() {
        if (groupIndex % 2 == 0) {
            return R.color.orange_ff6000;
        } else {
            return R.color.white;
        }
    }

    @Override
    public int getGroupDivider() {
        if (groupIndex % 2 == 0) {
            return 20;
        } else {
            return 10;
        }
    }
}
