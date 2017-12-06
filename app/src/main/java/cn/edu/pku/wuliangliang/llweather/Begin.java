package cn.edu.pku.wuliangliang.llweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.wuliangliang.bean.Date;

/**
 * Created by WLL on 2017/12/6.
 */

public class Begin extends AppCompatActivity {

    private final String addressDate = "http://www.sojson.com/open/api/lunar/json.shtml";
    private TextView begin_date, begin_title;
    private RelativeLayout begin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        int waitTime = 5000;
        Handler handler1 = new Handler();

        SharedPreferences sharedPreferences = getSharedPreferences("openTime", MODE_PRIVATE);
        Boolean hasOpened = sharedPreferences.getBoolean("hasOpened", false);
        if (hasOpened == false) {
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Begin.this, Guide.class));
                    Begin.this.finish();
                }
            }, waitTime);
        } else {
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Begin.this, MainActivity.class));
                    Begin.this.finish();
                }
            }, waitTime);
        }

        begin_date = (TextView) findViewById(R.id.begin_date);
        begin_title = (TextView) findViewById(R.id.begin_title);
        begin = (RelativeLayout) findViewById(R.id.begin);
        getDate();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    Date mDate;
                    mDate = (Date) msg.obj;
                    updateDate(mDate);
                    break;
                default:
                    break;
            }
        }
    };

    public void getDate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                Date date = null;
                try {
                    URL url = new URL(addressDate);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(4000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        response.append(str);
                        Log.d("viewPager_str", str);
                    }

                    String responseStr = response.toString();
                    date = parseJson(responseStr);

                    if (date != null) {
                        Message message = new Message();
                        message.what = 2;
                        message.obj = date;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("viewPager_exception", String.valueOf(e.getCause()));
                }
            }
        }).start();
    }

    public Date parseJson(String jsonData) {
        Date date = new Date();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            if (jsonObject != null) {
                Log.d("viewPager", "有json数据");
                if (jsonObject.getInt("status") == 200) {
                    Log.d("viewPager", "正在解析");
                    JSONObject data = jsonObject.getJSONObject("data");
                    date.setYear(data.getInt("year"));
                    date.setMonth(data.getInt("month"));
                    date.setDay(data.getInt("day"));
                    date.setCnYear(data.getString("cnyear"));
                    date.setCnMonth(data.getString("cnmonth"));
                    date.setCnDay(data.getString("cnday"));
                    date.setLunarYear(data.getInt("lunarYear"));
                    date.setLunarMonth(data.getInt("lunarMonth"));
                    date.setLunarDay(data.getInt("lunarDay"));
                    date.setAnimal(data.getString("animal"));
                    date.setLeap(data.getBoolean("leap"));
                    Log.d("viewPager_子线程", "阳历" + date.getMonth() + "月" + date.getDay() + "日" + " 农历" + date.getLunarMonth() + "月" + date.getLunarDay() + "日");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return date;
    }

    public void updateDate(Date date) {
        begin_date.setText("阳历" + date.getMonth() + "月" + date.getDay() + "日" + " 农历" + date.getLunarMonth() + "月" + date.getLunarDay() + "日");
        Log.d("viewPager", "阳历" + date.getMonth() + "月" + date.getDay() + "日" + " 农历" + date.getLunarMonth() + "月" + date.getLunarDay() + "日");
        if (!date.isLeap()) {
            if (date.getLunarMonth() == 1 && date.getLunarDay() == 1) {
                begin.setBackgroundResource(R.drawable.chunjie);
            } else if (date.getLunarMonth() == 1 && date.getLunarDay() == 10) {
                begin.setBackgroundResource(R.drawable.birthday);
                begin_title.setText("LL爸生日快乐！");
                begin_title.setVisibility(View.VISIBLE);
            } else if (date.getLunarMonth() == 3 && date.getLunarDay() == 3) {
                begin.setBackgroundResource(R.drawable.birthday);
                begin_title.setText("LL妈生日快乐！");
                begin_title.setVisibility(View.VISIBLE);
            } else if (date.getMonth() == 9 && date.getDay() == 8) {
                begin.setBackgroundResource(R.drawable.birthday);
                begin_title.setText("豆豆小仙女生日快乐！");
                begin_title.setVisibility(View.VISIBLE);
            } else if (date.getMonth() == 5 && date.getDay() == 8) {
                begin.setBackgroundResource(R.drawable.birthday);
                begin_title.setText("LL生日快乐！");
                begin_title.setVisibility(View.VISIBLE);
            } else if (date.getLunarMonth() == 1 && date.getLunarDay() == 15) {
                begin.setBackgroundResource(R.drawable.yuanxiaojie);
            } else if (date.getLunarMonth() == 8 && date.getLunarDay() == 15) {
                begin.setBackgroundResource(R.drawable.zhongqiujie);
            } else if (date.getLunarMonth() == 5 && date.getLunarDay() == 5) {
                begin.setBackgroundResource(R.drawable.duanwujie);
            } else if (date.getLunarMonth() == 7 && date.getLunarDay() == 7) {
                begin.setBackgroundResource(R.drawable.qingrenjie);
            } else if (date.getMonth() == 12 && date.getDay() == 25) {
                begin.setBackgroundResource(R.drawable.shengdanjie);
                begin_title.setText("圣诞快乐");
                begin_title.setVisibility(View.VISIBLE);
            }
            else if (date.getMonth() == 11 && date.getDay() == 30) {
                begin.setBackgroundResource(R.drawable.shengdanjie);
                begin_title.setText("测试");
                begin_title.setVisibility(View.VISIBLE);
            }
        }

    }
}
