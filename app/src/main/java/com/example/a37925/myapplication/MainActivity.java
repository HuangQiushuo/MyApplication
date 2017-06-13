package com.example.a37925.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String hide = "hide";
    String show = "show";
    TimeSlotSelectBehavior behavior;
    ScalableLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDialog();
        final TextView showButton = (TextView) findViewById(R.id.bottom_header);
        layout = (ScalableLayout) findViewById(R.id.multibottom);
        showButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        layout.toggle();
                    }
                }
        );
        layout.setOnStatusChangeListener(new ScalableLayout.OnStatusChangeListener() {
            @Override
            public void onStatusChange(int oldStatus, int newStatus) {
                if(newStatus == ScalableLayout.STATUS_HIDE){
                    showButton.setText(show);
                }else if(newStatus == ScalableLayout.STATUS_COLLAPSE || newStatus == ScalableLayout.STATUS_EXPAND){
                    showButton.setText(hide);
                }
            }
        });
    }

    public void onResume(){
        super.onResume();
//        layout.show();
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
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, DialogActivity.class));
            }
        });
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
            return 50;
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
