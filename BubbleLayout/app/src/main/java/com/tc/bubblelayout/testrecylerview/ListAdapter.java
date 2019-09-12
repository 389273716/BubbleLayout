package com.tc.bubblelayout.testrecylerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tc.bubblelayout.R;

import java.util.List;

/**
 * author：   tc
 * date：      2019/9/11 & 10:58
 * version    1.0
 * description
 * modify by
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.BaseViewHolder> {
    private List<TestListGroupBean> mList;

    public ListAdapter(List<TestListGroupBean> list) {
        mList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_list, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        TestListGroupBean item = getItem(adapterPosition);
        if (item == null) {
            return;
        }
        holder.tv.setText(item.getName());
//        if (adapterPosition % 2 == 0) {
//            holder.tv.setBackgroundColor(Color.RED);
//        } else {
//            holder.tv.setBackgroundColor(Color.GREEN);
//        }
    }

    private TestListGroupBean getItem(int pos) {
        if (pos <= -1 || pos > getItemCount()) {
            return null;
        }
        return mList.get(pos);
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv;

        public BaseViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_test_list);
        }
    }

}
