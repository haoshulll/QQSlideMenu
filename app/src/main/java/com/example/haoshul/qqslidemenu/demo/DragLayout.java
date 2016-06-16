package com.example.haoshul.qqslidemenu.demo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by haoshul on 2016/6/15.
 */
public class DragLayout extends FrameLayout {

    private View blueView;
    private View redView;
    private ViewDragHelper viewDragHelper;
    private int horizontalRange;
    private int verticalRange;


    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        viewDragHelper = ViewDragHelper.create(this,callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView = getChildAt(0);
        blueView = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int l = getMeasuredWidth()/2-redView.getMeasuredWidth()/2;
        int t = 0;
        redView.layout(l,t,l+redView.getMeasuredWidth(),t+redView.getMeasuredHeight());
        blueView.layout(l,t+redView.getBottom(),redView.getRight(),redView.getBottom()+blueView.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    //在onMeasure()方法之后，可以得到控件的长宽
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        horizontalRange = getMeasuredWidth() - blueView.getMeasuredWidth();
        verticalRange = getMeasuredHeight() - blueView.getMeasuredHeight();

    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == blueView || child == redView;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
//            Log.d("tag", "onViewCaptured()");
        }
        @Override
        public int getViewHorizontalDragRange(View child) {
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return super.getViewVerticalDragRange(child);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left<0) left = 0;
            if (left>horizontalRange) left = horizontalRange;
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top<0) top = 0;
            if (top>verticalRange) top = verticalRange;
            return top;
        }

        /**
         * 当view位置发生改变时调用，一般用于做伴随运动
         * @param changedView
         * @param left view当前的left
         * @param top view当前的top
         * @param dx 本次水平移动的距离
         * @param dy 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == blueView) {
//                redView.layout(redView.getLeft()+dx,redView.getTop()+dy,redView.getRight()+dx,redView.getBottom()+dy);
                redView.offsetLeftAndRight(dx);
                redView.offsetTopAndBottom(dy);
            }else if(changedView == redView){
                blueView.offsetLeftAndRight(dx);
                blueView.offsetTopAndBottom(dy);
            }
        }

        /**
         * 手指抬起时调用
         * @param releasedChild
         * @param xvel 水平移动的速度
         * @param yvel 垂直移动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth()/2 - releasedChild.getMeasuredHeight()/2;
            //在左半边，缓慢滑到左边
            if (releasedChild.getLeft()<centerLeft){
                viewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }else{
                viewDragHelper.smoothSlideViewTo(releasedChild,horizontalRange,releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    };


    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }
}
