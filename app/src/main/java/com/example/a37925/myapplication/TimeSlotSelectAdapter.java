package com.example.a37925.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Qiushuo Huang on 2017/4/12.
 */

class TimeSlotSelectAdapter extends RecyclerView.Adapter<TimeSlotSelectAdapter.Holder> {

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener li) {
        mItemClickListener = li;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new Holder(item);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.tv.setText("item " + position);
        if(mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(holder.getLayoutPosition(),
                            holder.tv.getText().toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 40;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv;
        public Holder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.text);
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position, String text);
    }
}
