package com.example.a37925.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Qiushuo Huang on 2017/4/12.
 */

public class MultiBottomSheetLayout extends CoordinatorLayout {
    public static final int STATE_EXPANDED = 0;
    public static final int STATE_COLLAPSE = 1;

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_BODY = 2;
    public static final int TYPE_FOOTER = 3;

    private String hide = "hide";
    private String show = "show";
    private View bodyView;
    private View footerView;
    private View headerView;
    private TimeSlotSelectBehavior listBehavior;
    private OnStateChangeListener listener;
    private int footerHeight;
    private boolean isModal = false;
    private int backColor;
    private boolean isInit = false;

    private int currentState = TimeSlotSelectBehavior.STATE_EXPANDED;

    private View backGroundView;

    public MultiBottomSheetLayout(Context context) {
        super(context);

    }

    public MultiBottomSheetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MultiBottomSheetLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void onLayout( boolean changed, int left, int top, int right,
                             int bottom) {
        super.onLayout(changed,left,top,right,bottom);
        Log.e("test", "onLayout");
        initChildren();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("test", "onMeasure");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e("test", "finishInflate");

    }

    private void init(Context context, AttributeSet attrs){
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.MultiBottomSheetLayout);
//        int type = a.getInteger(
//                android.support.design.R.styleable.CoordinatorLayout_Layout_android_layout_gravity,
//                Gravity.NO_GRAVITY);
        isModal = a.getBoolean(
                R.styleable.MultiBottomSheetLayout_multi_bottom_sheet_modal,
                false);
        backColor = a.getColor(R.styleable.MultiBottomSheetLayout_multi_bottom_sheet_backcolor,
                getResources().getColor(android.R.color.transparent));
        a.recycle();
        initBackGround();

    }

    private void initBackGround(){
        if(isModal){
            backGroundView = new View(getContext());
            backGroundView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            backGroundView.setBackgroundColor(backColor);
            backGroundView.setOnTouchListener(new OnTouchListener() {
                private float mPosX;
                private float mPosY;
                private float mCurPosX;
                private float mCurPosY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mPosX = event.getX();
                            mPosY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mCurPosX = event.getX();
                            mCurPosY = event.getY();

                            break;
                        case MotionEvent.ACTION_UP:
                            if (mCurPosY - mPosY > 0
                                    && (Math.abs(mCurPosY - mPosY) > 25)) {
                                hide();
                            } else if (mCurPosY - mPosY < 0
                                    && (Math.abs(mCurPosY - mPosY) > 25)) {
                            }
                            break;
                    }
                    return true;
                }
            });
        }
        addView(backGroundView);

    }

    private void initChildren(){
        if(isInit){
            return;
        }
        isInit = true;
        if(headerView==null&&bodyView==null&&footerView==null) {
            for(int i=0;i<getChildCount();i++){
                View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final Behavior behavior = lp.getBehavior();
                switch (child.getId()){
                    case R.id.bottom_body:
                        bodyView = child;
                        break;
                    case R.id.bottom_footer:
                        footerView = child;
                        if(currentState==STATE_COLLAPSE){
                            ViewCompat.offsetTopAndBottom(footerView, footerView.getHeight());
                            backGroundView.setVisibility(GONE);
                        }
                        break;
                    case R.id.bottom_header:
                        headerView = child;
                        break;
                }
                if (behavior != null ) {
                    initBehavior((TimeSlotSelectBehavior) behavior);
                }
            }
            footerHeight = footerView.getHeight();
            if (headerView != null && bodyView != null && footerView != null) {
                CoordinatorLayout.LayoutParams bodyParams = (CoordinatorLayout.LayoutParams) bodyView.getLayoutParams();
                bodyParams.setMargins(bodyParams.leftMargin, bodyParams.topMargin,
                        bodyParams.rightMargin, bodyParams.bottomMargin+footerView.getHeight()+((MarginLayoutParams)footerView.getLayoutParams()).topMargin);
                bodyParams.setBehavior(listBehavior);

                CoordinatorLayout.LayoutParams footerParams = (CoordinatorLayout.LayoutParams) footerView.getLayoutParams();
                footerParams.gravity = Gravity.BOTTOM;
            }
        }
    }

//    @Override
//    public CoordinatorLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
//        MarginLayoutParams tmpParams = new MarginLayoutParams(getContext(), attrs);
//        LayoutParams params = new LayoutParams(tmpParams);
//
//        final TypedArray a = getContext().obtainStyledAttributes(attrs,
//                R.styleable.MultiBottomSheetLayout_Layout);
////        int type = a.getInteger(
////                android.support.design.R.styleable.CoordinatorLayout_Layout_android_layout_gravity,
////                Gravity.NO_GRAVITY);
//        int type = a.getInteger(
//                R.styleable.MultiBottomSheetLayout_Layout_layout_type,
//                -1);
//        params.type = type;
//        if(type==TYPE_BODY){
//            initBehavior(getContext(), attrs);
//        }
//        a.recycle();
//        return params;
//    }


    private void initBehavior(TimeSlotSelectBehavior behavior){
        listBehavior = behavior;
        listBehavior.setBottomSheetCallback(new TimeSlotSelectBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged( View bottomSheet, int newState) {
                //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画

                switch (newState){
                    case TimeSlotSelectBehavior.STATE_HIDDEN:
                            currentState = STATE_COLLAPSE;
                            if(listener!=null) {
                                listener.onStateChange(STATE_COLLAPSE);
                            }
                        if(backGroundView != null){
                            backGroundView.setVisibility(INVISIBLE);
                        }
                        break;
                    default:
                        if(currentState!=STATE_EXPANDED) {
                            currentState = STATE_EXPANDED;
                            if(backGroundView != null){
                                backGroundView.setVisibility(VISIBLE);
                            }
                            if(listener!=null) {
                                listener.onStateChange(STATE_EXPANDED);
                            }
                        }
                }
            }

            @Override
            public void onSlide( View bottomSheet, float slideOffset) {

                if(footerView==null){
                    return;
                }
                int windowHeight = MultiBottomSheetLayout.this.getHeight();

                if((slideOffset+footerView.getTop())>=bodyView.getTop()) {
                    if(slideOffset<0){
                        if(bodyView.getTop()<windowHeight-listBehavior.getPeekHeight()+footerView.getHeight()){
                            ViewCompat.offsetTopAndBottom(footerView, (int) slideOffset);
                        }
                    }
                    if(slideOffset>0){
                        if(bodyView.getTop()>windowHeight-listBehavior.getPeekHeight()){
                            ViewCompat.offsetTopAndBottom(footerView, (int) slideOffset);
                        }
                    }
                }

                if(footerView.getTop() > windowHeight){
                    ViewCompat.offsetTopAndBottom(footerView,windowHeight- footerView.getTop());
                }
                if(footerView.getTop()<(windowHeight- footerHeight)){
                    ViewCompat.offsetTopAndBottom(footerView,windowHeight- footerHeight- footerView.getTop());
                }
            }
        });
    }

    public void show(){
        listBehavior.setState(TimeSlotSelectBehavior.STATE_COLLAPSED);
        currentState = STATE_EXPANDED;
    }

    public void hide(){
        listBehavior.setState(TimeSlotSelectBehavior.STATE_HIDDEN);
        currentState = STATE_COLLAPSE;

    }

    interface OnStateChangeListener{
        void onStateChange(int newState);
    }

    public int getCurrentState() {
        return currentState;
    }

    public void toggle(){
        if(currentState==STATE_EXPANDED){
            hide();
        }else{
            show();
        }
    }

    public OnStateChangeListener getOnStateChangeListener() {
        return listener;
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.listener = listener;
    }

}
