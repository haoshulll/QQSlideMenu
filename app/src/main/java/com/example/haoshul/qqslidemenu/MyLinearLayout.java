package com.example.haoshul.qqslidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by haoshul on 2016/6/16.
 */
public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slideMenu;
    public void bindSlideMenu(SlideMenu slideMenu){
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slideMenu != null && slideMenu.getState() == SlideMenu.DragState.Open){
            //当slideMenu处于打开状态，需要拦截触摸事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideMenu != null && slideMenu.getState() == SlideMenu.DragState.Open){
            //当slideMenu处于打开状态，需要拦截触摸事件，并且消费触摸事件
            if (event.getAction() == MotionEvent.ACTION_UP){
                slideMenu.close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
