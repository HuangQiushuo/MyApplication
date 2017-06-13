package com.example.a37925.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Qiushuo Huang on 2017/4/10.
 */

public class DialogActivity extends Activity{
    String hide = "hide";
    String show = "show";
    BottomSheetBehavior behavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        final TextView textView = (TextView) findViewById(R.id.bottom_header);
        // The View with the BottomSheetBehavior
        textView.setText(hide);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);
        View bottomSheet = coordinatorLayout.findViewById(R.id.mainLayout);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged( View bottomSheet, int newState) {
                //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画
                if(newState==BottomSheetBehavior.STATE_HIDDEN){
                    textView.setText(show);
                }

                if(newState==BottomSheetBehavior.STATE_EXPANDED){
                    textView.setText(hide);
                }
            }

            @Override
            public void onSlide( View bottomSheet, float slideOffset) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
            }
        });

        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView.getText().toString().equals(hide)) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }else{
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        initDialog();
    }

    public void initDialog() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.bottom_body);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String text) {
                Toast.makeText(DialogActivity.this, text, Toast.LENGTH_SHORT).show();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    public void onResume(){
        super.onResume();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private Adapter.OnItemClickListener mItemClickListener;

        public void setOnItemClickListener(Adapter.OnItemClickListener li) {
            mItemClickListener = li;
        }

        @Override
        public Adapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new Adapter.Holder(item);
        }

        @Override
        public void onBindViewHolder(final Adapter.Holder holder, int position) {
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
            return 3;
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
}
