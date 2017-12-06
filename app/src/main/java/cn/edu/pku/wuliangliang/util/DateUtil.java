package cn.edu.pku.wuliangliang.util;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.pku.wuliangliang.bean.Date;

/**
 * Created by WLL on 2017/11/30.
 */

public class DateUtil {
    private final String addressDate = "http://www.sojson.com/open/api/lunar/json.shtml";
    private Date mDate = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    mDate = (Date) msg.obj;
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
                    }

                    String responseStr = response.toString();
                    date = parseJson(responseStr);

                    if (date != null) {
                        Message message = new Message();
                        message.what = 2;
                        message.obj = date;

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Date parseJson(String jsonData) {
        Date date = new Date();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            if (jsonObject != null) {
                if (jsonObject.getInt("status") == 200) {
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
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return date;
    }
}

