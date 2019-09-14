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

    public TestGroupBean(String name, String groupType, int groupBackgroundColorId, int groupDividerSize) {
        super(groupType, groupBackgroundColorId, groupDividerSize);
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestGroupBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
