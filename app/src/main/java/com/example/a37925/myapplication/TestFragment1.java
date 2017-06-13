package com.example.a37925.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Qiushuo Huang on 2017/5/6.
 */

public class TestFragment1 extends Fragment {
    String hide = "hide";
    String show = "show";
    TimeSlotSelectBehavior behavior;
    MultiBottomSheetLayout layout;
    View content;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content = inflater.inflate(R.layout.activity_main, null);
        initDialog();
        final TextView showButton = (TextView) content.findViewById(R.id.bottom_header);
        layout = (MultiBottomSheetLayout) content.findViewById(R.id.multibottom);
        content.findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                TestFragment2 testFragment = new TestFragment2();
                FragmentTransaction t = manager.beginTransaction().replace(R.id.content, testFragment, testFragment.getClass().getSimpleName());
                t.addToBackStack(testFragment.getClass().getSimpleName());
                t.commit();
            }
        });
        showButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        layout.toggle();
                    }
                }
        );
        layout.setOnStateChangeListener(new MultiBottomSheetLayout.OnStateChangeListener() {
            @Override
            public void onStateChange(int newState) {
                if(newState == MultiBottomSheetLayout.STATE_EXPANDED){
                    showButton.setText(hide);
                }else{
                    showButton.setText(show);
                }
            }
        });
        return content;
    }



    public void onResume(){
        super.onResume();
//        layout.show();
    }

    public void initDialog() {
        RecyclerView recyclerView = (RecyclerView) content.findViewById(R.id.bottom_body);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        MainActivity.Adapter adapter = new MainActivity.Adapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MainActivity.Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String text) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                TestFragment2 testFragment = new TestFragment2();
                FragmentTransaction t = manager.beginTransaction().replace(R.id.content, testFragment, testFragment.getClass().getSimpleName());
                t.addToBackStack(testFragment.getClass().getSimpleName());
                t.commit();
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
