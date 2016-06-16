package com.example.haoshul.qqslidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.menu_listview)
    ListView menuListview;
    @InjectView(R.id.main_listview)
    ListView mainListview;

    @InjectView(R.id.iv_head)
    ImageView ivHead;
    @InjectView(R.id.SlideMenu)
    com.example.haoshul.qqslidemenu.SlideMenu slideMenu;
    @InjectView(R.id.MyLineerLayout)
    MyLinearLayout myLineerLayout;

    String[] name = new String[]{"李光洙", "刘在石", "宋智孝", "姜Gary", "haha", "金钟国", "池石镇"
            , "李光洙", "刘在石", "宋智孝", "姜Gary", "haha", "金钟国", "池石镇"};

    String[] menu = new String[]{"aaaaaaaaaaaaaaaaaaaaa", "bbbbbbbbbbbbbbbb", "cccccccccccccc", "ddddddddddddddddd"
            , "eeeeeeeeeeeeeeeeeee", "ffffffffffffffffffff", "ggggggggggggggggggg"
            , "李光洙", "刘在石", "宋智孝", "姜Gary", "haha", "金钟国", "池石镇"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        mainListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name));
        menuListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });

        slideMenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
                menuListview.smoothScrollToPosition(new Random().nextInt(menu.length));
            }

            @Override
            public void onClose() {
                ViewPropertyAnimator.animate(ivHead)
                        .translationX(10)
                        .setInterpolator(new CycleInterpolator(4))
                        .setDuration(500)
                        .start();
            }

            @Override
            public void onDragging(float fraction) {
                ViewHelper.setAlpha(ivHead, 1 - fraction);
            }
        });

        myLineerLayout.bindSlideMenu(slideMenu);
    }
}
