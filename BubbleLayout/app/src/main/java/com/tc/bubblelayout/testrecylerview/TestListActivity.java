package com.tc.bubblelayout.testrecylerview;

import android.content.Context;
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
        final List<TestListGroupBean> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (i < 3) {
                list.add(new TestListGroupBean(String.valueOf(i), 1));
            } else if (i < 10) {
                list.add(new TestListGroupBean(String.valueOf(i), 2));
            } else {
                list.add(new TestListGroupBean(String.valueOf(i), 3));
            }
        }


        final NormalDecoration decoration = new NormalDecoration(this, true) {
            @Override
            public String getHeaderName(int pos) {

                return list.get(pos).getName();
            }
        };
        decoration.setTextSize(DensityUtil.dip2px(this, 17));
        recyclerView.addItemDecoration(decoration);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new GroupItemDecoration(this, list));
        recyclerView.setAdapter(new ListAdapter(list));
    }


    public class GroupItemDecoration extends AbstractCornerItemDecoration<TestListGroupBean> {

        public GroupItemDecoration(Context context, List<TestListGroupBean> list) {
            super(context, list);
        }
    }
}
