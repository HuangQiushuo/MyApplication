package com.example.a37925.myapplication;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * Created by Qiushuo Huang on 2017/5/2.
 */

public class ScalableLayout extends LinearLayout {
    public static final int STATUS_COLLAPSE = 0;
    public static final int STATUS_EXPAND = 1;
    public static final int STATUS_HIDE = 2;
    public static final int STATUS_SCROLL = 3;

    private int currentStatus = STATUS_COLLAPSE;
    private int lastX;
    private int lastY;
    private int lastRawY;
    private int lastXIntercept = 0;
    private int lastYIntercept = 0;
    private boolean scrolling = false;
    private int lastOffset;
    private int maxHeight = 1000;
    private int collapseHeight = 400;
    private int hideHeight = 80;
    private static final int vacility = 5;


    private static final int AUTO_THRESHOLD = 50;
    private int moveStart;
    private int moveCurrent;
    private OnStatusChangeListener onStatusChangeListener;
    int windowHeight = getResources().getDisplayMetrics().heightPixels;


    public ScalableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

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

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        boolean intercepted = false;
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN: {
//                scrolling = false;
//                lastXIntercept = x;
//                lastYIntercept = y;
//                lastX = x;
//                lastY = y;
//                intercepted = false;
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//                int deltaX = x - lastXIntercept;
//                int deltaY = y - lastYIntercept;
//                scrolling = true;
//                if (Math.abs(deltaY)<Math.abs(deltaX)) {
//                    intercepted = false;
//                }
//                break;
//            }
//            case MotionEvent.ACTION_UP: {
//                if(scrolling) {
//                    intercepted = true;
//                }
//                lastXIntercept = lastYIntercept = 0;
//                break;
//            }
//            default:
//                break;
//        }
//        return intercepted;
//    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                intercepted = false;
                moveStart = y;
                lastRawY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - lastXIntercept;
                int deltaY = y - lastYIntercept;
                scrolling = true;
                if (Math.abs(deltaY)>= Math.abs(deltaX)) {
                    intercepted = true;
                }else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
            default:
                break;
        }
        lastXIntercept = x;
        lastYIntercept = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                moveCurrent = y;
                Log.e("y", lastRawY+":"+y);
                doMove((lastRawY -y));
                lastRawY = y;
                break;

            case MotionEvent.ACTION_UP:
                if(currentStatus==STATUS_SCROLL) {
                    doUp();
                }
                break;
        }
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

        if(h<=hideHeight-collapseHeight){
            scrollTo(0, hideHeight-collapseHeight);
        } else if(h<=0) {
            scrollBy(0, y);
        } else if (h>0){
            scrollTo(0,0);
            scale(h);
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
        lastOffset = 0;
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
        lastOffset = 0;
        ObjectAnimator expandToHide = ObjectAnimator.ofInt(this, "expandToHide", 0, maxHeight-hideHeight).setDuration((maxHeight-hideHeight)/vacility);
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
        y = -y;
        int height = getHeight();
        int scroll = getScrollY();
        if(height>=collapseHeight && scroll==0){
            scale(y-lastOffset);
        }else{
            scroll(y-lastOffset);
        }
        lastOffset = y;
        Log.e("offset", lastOffset+":"+y);
    }

    private void setHideToExpand (int offset) {
        int height = getHeight();
        int scroll = getScrollY();
        int y = offset-lastOffset;
        if(scroll+y<=0){
            scroll(y);
        }else if(scroll<0 && scroll+y>0){
            scroll(0-scroll);
            scale(y+scroll);
        }else{
            scale(y);
        }
        lastOffset = offset;
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
