package com.example.a37925.myapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Qiushuo Huang on 2017/5/2.
 */

public class ScalableLayout extends LinearLayout {
    public static final int STATUS_COLLAPSE = 0;
    public static final int STATUS_EXPAND = 1;
    public static final int STATUS_HIDE = 2;
    public static final int STATUS_SCROLL = 3;

    private int currentStatus = STATUS_COLLAPSE;
    private float lastY;
    private int maxHeight = 1000;
    private int collapseHeight = 400;
    private int hideHeight = 80;
    private static final int vacility = 5;

//    private ObjectAnimator collapseToExpandAnimator;
//    private ObjectAnimator expandToCollapseAnimator;
//    private ObjectAnimator collapseToHideAnimator;
//    private ObjectAnimator hideToCollapseAnimator;
//    private AnimatorSet hideToExpandAnimator;
//    private AnimatorSet expandToHideAnimator;


    private static final int AUTO_THRESHOLD = 50;
    private int moveStart;
    private int moveCurrent;
    private OnStatusChangeListener onStatusChangeListener;
    int windowHeight = getResources().getDisplayMetrics().heightPixels;


    public ScalableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    private void initAnimators(){
//        int start, end, time;
//        collapseToExpandAnimator = getExpandAnimator();
//        expandToCollapseAnimator = getCollapseAnimator();
//        collapseToHideAnimator = getHideAnimator();
//        start = hideHeight-collapseHeight;
//        end = 0;
//        time = (end-start)/vacility;
//        hideToCollapseAnimator = ObjectAnimator.ofInt(this, "scroll", start, end).setDuration(time);
//
//        hideToExpandAnimator = new AnimatorSet();
//        ObjectAnimator hideToExpand = ObjectAnimator.ofInt(this, "hideToExpand", hideHeight, maxHeight).setDuration((maxHeight-hideHeight)/vacility);
//        hideToExpandAnimator.play(hideToExpand);
//        hideToExpandAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                setInternalStatus(STATUS_SCROLL);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                setInternalStatus(STATUS_EXPAND);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//
//        expandToHideAnimator = new AnimatorSet();
////        ObjectAnimator expandToCollapse = getCollapseAnimator();
////        expandToCollapse.removeAllListeners();
////        ObjectAnimator collapseToHide = getHideAnimator();
////        collapseToHide.removeAllListeners();
//        time = (maxHeight-hideHeight)/vacility;
//        ObjectAnimator expandToHide = ObjectAnimator.ofInt(this, "expandToHide", maxHeight, hideHeight, time);
////        expandToHideAnimator.playSequentially(expandToCollapse, collapseToHide);
//        expandToHideAnimator.play(expandToHide);
//        expandToHideAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                setInternalStatus(STATUS_SCROLL);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                setInternalStatus(STATUS_HIDE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//
//    }

    public ScalableLayout(Context context) {
        super(context);
        init();
    }

    public ScalableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setOrientation(LinearLayout.VERTICAL);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getRawY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                moveStart = (int) y;
                break;

            case MotionEvent.ACTION_MOVE:
                moveCurrent = (int) y;
                doMove((int) (lastY-y));
                break;

            case MotionEvent.ACTION_UP:
                if(currentStatus==STATUS_SCROLL) {
                    doUp();
                }
                break;
        }
        lastY = y;
        return true;
    }

    private void doMove(int y){
        //处于collapse状态
        setInternalStatus(STATUS_SCROLL);
        int height = getHeight();
        int scroll = getScrollY();
        if(height>collapseHeight){
            scale(y);
        }else if(height == collapseHeight && y>0 && scroll==0){
            scale(y);
        }else if(height == collapseHeight && y<0 && scroll==0){
            scroll(y);
        }else if(scroll<0){
            scroll(y);
        }
    }

    private void scale(int y){
        int h = getHeight()+y;
        if(h>=maxHeight){
            getLayoutParams().height = maxHeight;
        } else if(h>collapseHeight) {
            getLayoutParams().height += y;
        } else if (h<collapseHeight){
            getLayoutParams().height = collapseHeight;
            scroll(h-collapseHeight);
        } else if(h == collapseHeight) {
            getLayoutParams().height = collapseHeight;
        }
        requestLayout();
    }

    private void scroll(int y) {
        int h = getScrollY() + y;
        if(h>0){
            scrollTo(0,0);
            scale(h);
        } else if(h<=hideHeight - collapseHeight) {
            scrollTo(0, hideHeight-collapseHeight);
//            setInternalStatus(STATUS_HIDE);
        } else{
            scrollBy(0, y);
//            setInternalStatus(STATUS_HIDE);
        }
    }

    public void setInternalStatus(int status){
        int old = currentStatus;
        currentStatus = status;
        if(onStatusChangeListener!=null){
            onStatusChangeListener.onStatusChange(old, currentStatus);
        }
    }

    private void doUp(){
        //如果不是hide
        if(getLayoutParams().height>collapseHeight){
            autoScale();
        }
        //如果是hide
        else{
            autoTransition();
        }
    }

    private void autoTransition() {
        int move = moveStart-moveCurrent;
        if(move>0){
            if(move>=AUTO_THRESHOLD){
                getShowAnimator().start();
//                setInternalStatus(STATUS_COLLAPSE);
            } else{
                getHideAnimator().start();
//                setInternalStatus(STATUS_HIDE);
            }
        }else{
            if(-move>=AUTO_THRESHOLD){
                getHideAnimator().start();
//                setInternalStatus(STATUS_HIDE);
            }else{
                getShowAnimator().start();
//                setInternalStatus(STATUS_COLLAPSE);
            }
        }
    }

    private void autoScale(){
        int move = moveStart-moveCurrent;
        if(move>0){
            if(move>=AUTO_THRESHOLD){
                getExpandAnimator().start();
            } else{
                getCollapseAnimator().start();
//                setInternalStatus(STATUS_COLLAPSE);
            }
        }else{
            if(-move>=AUTO_THRESHOLD){
                getCollapseAnimator().start();
//                setInternalStatus(STATUS_COLLAPSE);
            }else{
                getExpandAnimator().start();
//                setInternalStatus(STATUS_EXPAND);
            }
        }
    }

    private ObjectAnimator getExpandAnimator(){
        int start = getLayoutParams().height;
        int end = maxHeight;
        int time = (end-start)/vacility;
        ObjectAnimator animator =  ObjectAnimator.ofInt(this, "height", start, end).setDuration(time);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setInternalStatus(STATUS_SCROLL);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setInternalStatus(STATUS_EXPAND);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return animator;
    }

    private ObjectAnimator getCollapseAnimator(){
        int start = getLayoutParams().height;
        int end = collapseHeight;
        int time = (start-end)/vacility;
        ObjectAnimator animator =  ObjectAnimator.ofInt(this, "height", start, end).setDuration(time);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setInternalStatus(STATUS_SCROLL);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setInternalStatus(STATUS_COLLAPSE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return animator;
    }

    private ObjectAnimator getShowAnimator(){
        int start = getScrollY();
        int end = 0;
        int time = (end-start)/vacility;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "scroll", start, end).setDuration(time);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setInternalStatus(STATUS_SCROLL);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setInternalStatus(STATUS_COLLAPSE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return animator;
    }

    private ObjectAnimator getHideAnimator(){
        int start = getScrollY();
        int end = -(collapseHeight-hideHeight);
        int time = (start-end)/vacility;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "scroll", start, end).setDuration(time);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setInternalStatus(STATUS_SCROLL);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setInternalStatus(STATUS_HIDE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return animator;
    }

    public void setScroll(int y){
        this.scrollTo(0, y);
    }

    public void setHeight(int height){
        getLayoutParams().height = height;
        this.requestLayout();
    }

    public void expand(){
        switch (currentStatus){
            case STATUS_EXPAND:
                break;
            case STATUS_COLLAPSE:
//                getExpandAnimator().start();
                getExpandAnimator().start();
                break;
            case STATUS_HIDE:
                getHideToExpandAnimator().start();
                break;
        }
    }

    private ObjectAnimator getHideToExpandAnimator(){
        ObjectAnimator hideToExpand = ObjectAnimator.ofInt(this, "hideToExpand", 0, maxHeight-hideHeight).setDuration((maxHeight-hideHeight)/vacility);
        hideToExpand.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setInternalStatus(STATUS_SCROLL);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setInternalStatus(STATUS_EXPAND);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return hideToExpand;
    }

    private ObjectAnimator getExpandToHideAnimator(){
        ObjectAnimator expandToHide = ObjectAnimator.ofInt(this, "expandToHide", 0, hideHeight-maxHeight, (maxHeight-hideHeight)/vacility);
//        expandToHideAnimator.playSequentially(expandToCollapse, collapseToHide);
        expandToHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setInternalStatus(STATUS_SCROLL);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setInternalStatus(STATUS_HIDE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return expandToHide;
    }

    private void setExpandToHide(int y){
        int height = getHeight();
        int scroll = getScrollY();
        if(height>=collapseHeight && scroll==0){
            scale(y);
        }else{
            scroll(y);
        }
    }

    private void setHideToExpand (int y) {
        int height = getHeight();
        int scroll = getScrollY();
        if(scroll+y<=0){
            scroll(y);
        }else if(scroll<0 && scroll+y>0){
            scroll(0-scroll);
            scale(y+scroll);
        }else{
            scale(y);
        }
    }


    public void collapse(){
        switch (currentStatus){
            case STATUS_COLLAPSE:
                break;
            case STATUS_EXPAND:
//                getCollapseAnimator().start();
                getCollapseAnimator().start();
                break;
            case STATUS_HIDE:
//                getShowAnimator().start();
                getShowAnimator().start();
                break;

        }
    }

    public void hide(){
        switch (currentStatus){
            case STATUS_HIDE:
                break;
            case STATUS_COLLAPSE:
                getHideAnimator().start();
                break;
            case STATUS_EXPAND:
                getExpandToHideAnimator().start();
                break;
        }
    }

    public int getStatus(){
        return currentStatus;
    }

    public void toggle(){
        if(currentStatus!=STATUS_HIDE){
            hide();
        }else {
            expand();
        }
    }

    public void setOnStatusChangeListener(OnStatusChangeListener listener){
        onStatusChangeListener = listener;
    }

    public interface OnStatusChangeListener{
        void onStatusChange(int oldStatus, int newStatus);
    }

}
