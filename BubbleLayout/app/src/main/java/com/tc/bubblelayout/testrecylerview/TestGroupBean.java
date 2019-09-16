package com.tc.bubblelayout.testrecylerview;

import android.support.annotation.ColorRes;

/**
 * author：   tc
 * date：     2019/9/14 & 16:58
 * version    1.0
 * description  测试类
 * modify by
 */
public class TestGroupBean extends GroupSortItem {
    private String name;

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestGroupBean() {
    }

    public TestGroupBean(String groupType, @ColorRes int groupBackgroundColorId, int groupDividerSize, @ColorRes int
            groupPressColorId, String name) {
        super(groupType, groupBackgroundColorId, groupDividerSize, groupPressColorId);
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestGroupBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
