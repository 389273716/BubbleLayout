package com.tc.bubblelayout.testrecylerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private List<TestGroupBean> mList;
    private Context context;

    public ListAdapter(Context context, List<TestGroupBean> list) {
        this.context = context;
        mList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_list, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();
        TestGroupBean item = getItem(adapterPosition);
        if (item == null) {
            return;
        }
        holder.tv.setText(item.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemRemoved(adapterPosition);
                mList.remove(adapterPosition);
                notifyItemRangeChanged(adapterPosition, mList.size());
                Toast.makeText(context, "delete index:" + adapterPosition, Toast.LENGTH_LONG).show();
            }
        });
    }

    private TestGroupBean getItem(int pos) {
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
