package com.tc.bubblelayout.testrecylerview;

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

    public TestGroupBean(String groupType, int groupBackgroundColorId, int groupDividerSize, int groupPressColorId,
                         String name) {
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
