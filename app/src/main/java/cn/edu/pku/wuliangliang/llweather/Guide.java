package cn.edu.pku.wuliangliang.llweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wuliangliang.bean.ViewPagerAdapter;

/**
 * Created by WLL on 2017/12/6.
 */

public class Guide extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.imageView1, R.id.imageView2, R.id.imageView3};

    private Button gotoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initDots();
    }

    public void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(layoutInflater.inflate(R.layout.page1, null));
        views.add(layoutInflater.inflate(R.layout.page2, null));
        views.add(layoutInflater.inflate(R.layout.page3, null));
        viewPagerAdapter = new ViewPagerAdapter(views, this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        gotoMain = (Button) findViewById(R.id.gotoMain);
        gotoMain.setOnClickListener(this);

        SharedPreferences settings = getSharedPreferences("openTime", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasOpened", true);
        editor.commit();
    }

    public void initDots() {
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i = 0; i < ids.length; i ++) {
            if (i == position) {
                dots[i].setImageResource(R.drawable.page_indicator_focused);
            } else {
                dots[i].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }

        if (position == 2) {
            gotoMain.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gotoMain) {
            Log.d("llWeather_gotoMain", "True");
            Intent i = new Intent(this, MainActivity.class);
            gotoMain.setText("启动中...");
            startActivity(i);
        }
    }
}

