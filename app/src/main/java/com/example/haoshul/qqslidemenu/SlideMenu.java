package com.example.haoshul.qqslidemenu;


import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by haoshul on 2016/6/16.
 */
public class SlideMenu extends FrameLayout {

    private ViewDragHelper dragHelper;
    private View mainView;
    private View menuView;
    private int mainWidth,mainHeight;
    private int menuWidth,menuHeight;
    private float dragRange;
    private FloatEvaluator floatEvaluator;


    //保存拖拽状态常量
    enum DragState{
        Open,Close,Dragging
    }

    private DragState mState = DragState.Close;

    public DragState getState(){
        return mState;
    }

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        dragHelper = ViewDragHelper.create(this,callback);
        floatEvaluator = new FloatEvaluator();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount()>2){
            throw  new IllegalArgumentException("SlideView must only 2 children!");
        }
        mainView = getChildAt(1);
        menuView = getChildAt(0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mainWidth = getMeasuredWidth();
        mainHeight = getMeasuredHeight();
        menuWidth = getMeasuredWidth();
        menuHeight = getMeasuredHeight();
        dragRange = (int) (getMeasuredWidth()*0.65);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mainView || child == menuView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView){
                left = fixLeft(left);
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menuView){
                //先固定住menuview
                menuView.layout(0,menuView.getTop(),menuWidth,menuView.getBottom());
                //再让mainview移动
                int newleft = fixLeft(mainView.getLeft() + dx);
                mainView.layout(newleft,mainView.getTop(),newleft+mainView.getMeasuredWidth(),mainView.getBottom());
            }
            //1.计算移动的百分比
            float fraction = mainView.getLeft()/dragRange;
            //2.根据移动的百分比执行伴随动画
            exeCuteAnim(fraction);

            //3.进行state状态改变的逻辑判断
            if (mainView.getLeft() == 0 && mState != DragState.Close){
                //说明关闭状态
                mState = DragState.Close;
                //回调监听器方法
                if (listener != null){
                    listener.onClose();
                }
            }else if (mainView.getLeft() == dragRange && mState != DragState.Open){
                //说明打开状态
                mState = DragState.Open;
                //回调监听器方法
                if (listener != null){
                    listener.onOpen();
                }
            }

            if (listener != null){
                //回调监听器方法
                listener.onDragging(fraction);
            }

        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mainView.getLeft() < dragRange/2){
                //在左半边应该close
                close();
            }else{
                //在右半边应该open
                open();
            }

            if (xvel < -100){
                close();
            }else if (xvel > 100){
                open();
            }
        }
    };

    public void open() {
        dragHelper.smoothSlideViewTo(mainView, (int) dragRange,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    public void close() {
        dragHelper.smoothSlideViewTo(mainView,0,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * 设置拖动动画
     * @param fraction
     */
    private void exeCuteAnim(float fraction) {
//            float scaleValue = 0.85f+(1-fraction)*0.15f;
//            ViewCompat.setScaleX(mainView,scaleValue);
//            ViewCompat.setScaleY(mainView,scaleValue);
        ViewHelper.setScaleX(mainView,floatEvaluator.evaluate(fraction,1f,0.85f));
        ViewHelper.setScaleY(mainView,floatEvaluator.evaluate(fraction,1f,0.85f));
        ViewHelper.setTranslationX(menuView,floatEvaluator.evaluate(fraction,-menuWidth/2,0));
        ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1f));
        getBackground().setColorFilter(ColorUtils.calculateMinimumAlpha(Color.TRANSPARENT,Color.BLACK,fraction)
                , PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    /**
     * 限制left值的范围
     * @param left
     * @return
     */
    private int fixLeft(int left){
        if (left < 0) left = 0;
        if (left > dragRange) left = (int) dragRange;
        return left;
    }


    private OnDragStateChangeListener listener;
    public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
        this.listener = listener;
    }

    /**
     * SlideMenu拖拽状态改变的监听器
     */
    public interface OnDragStateChangeListener{
        void onOpen();
        void onClose();
        void onDragging(float fraction);
    }

}
