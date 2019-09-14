package com.tc.bubblelayout.testrecylerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tc.bubblelayout.DensityUtil;
import com.tc.bubblelayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author：   tc
 * date：      2019/9/11 & 10:57
 * version    1.0
 * description
 * modify by
 */
public class TestListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        final List<TestGroupBean> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (i < 4) {
                list.add(new TestGroupBean(String.valueOf(i), String.valueOf(1), R.color.color_999999, 20));
            } else if (i == 4) {
                list.add(new TestGroupBean(String.valueOf(i), String.valueOf(2), R.color.orange_ff6000, 15));
            } else if (i == 5) {
                list.add(new TestGroupBean(String.valueOf(i), String.valueOf(3), R.color.colorAccent, 15));
            } else if (i < 20) {
                list.add(new TestGroupBean(String.valueOf(i), String.valueOf(4), R.color.colorPrimaryDark, 10));
            } else {
                list.add(new TestGroupBean(String.valueOf(i), String.valueOf(5), R.color.color_ffffff, 30));
            }
        }
        //测试群组间的间距为0的情况
        for (TestGroupBean testGroupBean : list) {
            testGroupBean.setGroupDividerSize(0);
        }
//        数据要事先排好序
//        Collections.sort(list, new Comparator<TestGroupBean>() {
//            @Override
//            public int compare(TestGroupBean o1, TestGroupBean o2) {
//                return 0;
//            }
//        });

        final NormalDecoration decoration = new NormalDecoration(this, true) {
            @Override
            public String getHeaderName(int pos) {

                return list.get(pos).getGroupSortType();
            }
        };
        decoration.setTextSize(DensityUtil.dip2px(this, 17));
        recyclerView.addItemDecoration(decoration);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new GroupCornerItemDecoratin(this, list,25));
        recyclerView.setAdapter(new ListAdapter(list));
    }


}
