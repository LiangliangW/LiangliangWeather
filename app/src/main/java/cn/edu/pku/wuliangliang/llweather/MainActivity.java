package cn.edu.pku.wuliangliang.llweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.edu.pku.wuliangliang.app.MyApplication;
import cn.edu.pku.wuliangliang.bean.City;
import cn.edu.pku.wuliangliang.bean.TodayWeather;
import cn.edu.pku.wuliangliang.util.NetUtil;
import cn.edu.pku.wuliangliang.util.OnGetLocEventListener;

/**
 * Created by WLL on 2017/9/20.
 */

public class MainActivity extends Activity implements View.OnClickListener{

    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn_ed, mLocBtn, mLocIngBtn, mShareBtn, mCitySelectBtn;
    private ProgressBar mUpdateBtn_ing;
    private TextView timeTv, weekTv, pm25Tv, pmQualityTv, temperatureTv, temperatureTodayTv, climateTv, windTv, cityNameTv;
    private TextView date1Tv, highLow1Tv, type1Tv, fengli1Tv;
    private TextView date2Tv, highLow2Tv, type2Tv, fengli2Tv;
    private TextView date3Tv, highLow3Tv, type3Tv, fengli3Tv;
    private ImageView type1PicTv, type2PicTv, type3PicTv;
    private ImageView weatherImage, pmImage;
    private ImageView mFace;
    private long[] mHints = new long[5];
    private boolean pm25OkFlag = false;
    private MyApplication myApplication;
    private List<City> mCityList;
    private String locCityCode;
    private cn.edu.pku.wuliangliang.util.Location mLocation = new cn.edu.pku.wuliangliang.util.Location();

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    TodayWeather[] todayWeathersList = (TodayWeather[]) msg.obj;
                    TodayWeather todayWeather1 = todayWeathersList[0];
                    TodayWeather todayWeather2 = todayWeathersList[1];
                    TodayWeather todayWeather3 = todayWeathersList[2];
                    updateTodayWeather(todayWeather1, todayWeather2, todayWeather3);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn_ed = findViewById(R.id.title_updateBtn_ed);
        mUpdateBtn_ed.setOnClickListener(this);
        mUpdateBtn_ing = findViewById(R.id.title_updateBtn_ing);
        mCitySelectBtn = findViewById(R.id.title_cityManager);
        mCitySelectBtn.setOnClickListener(this);
        mShareBtn = findViewById(R.id.title_share);
        mShareBtn.setOnClickListener(this);
        mLocBtn = findViewById(R.id.title_location);
        mLocBtn.setOnClickListener(this);
        mLocIngBtn = findViewById(R.id.title_location_ing);
        mLocIngBtn.setOnClickListener(this);
        mFace = findViewById(R.id.pm25_pic);
        mFace.setOnClickListener(this);

        initView();

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101100101");
            queryWeatherCode(cityCode);
            Log.d("llWeather_netConnection", "Yes");
        } else {
            Log.d("llWeather_netConnection", "No");
            Toast.makeText(MainActivity.this, "Network unavalible", Toast.LENGTH_LONG).show();
        }
    }

    void initView() {
        cityNameTv = findViewById(R.id.title_cityName);
        timeTv = findViewById(R.id.time);
        temperatureTv = findViewById(R.id.temperature);
        weekTv = findViewById(R.id.date);
        pm25Tv = findViewById(R.id.pm25_num);
        pmQualityTv = findViewById(R.id.pm25_degree);
        pmImage = findViewById(R.id.pm25_pic);
        temperatureTodayTv = findViewById(R.id.temperature_today);
        climateTv = findViewById(R.id.climate_today);
        windTv = findViewById(R.id.wind_today);
        weatherImage = findViewById(R.id.weather_pic);

        date1Tv = findViewById(R.id.date1);
        type1PicTv = findViewById(R.id.type1Pic);
        highLow1Tv = findViewById(R.id.highLow1);
        type1Tv = findViewById(R.id.type1);
        fengli1Tv = findViewById(R.id.fengli1);

        date2Tv = findViewById(R.id.date2);
        type2PicTv = findViewById(R.id.type2Pic);
        highLow2Tv = findViewById(R.id.highLow2);
        type2Tv = findViewById(R.id.type2);
        fengli2Tv = findViewById(R.id.fengli2);

        date3Tv = findViewById(R.id.date3);
        type3PicTv = findViewById(R.id.type3Pic);
        highLow3Tv = findViewById(R.id.highLow3);
        type3Tv = findViewById(R.id.type3);
        fengli3Tv = findViewById(R.id.fengli3);

        cityNameTv.setText("N/A");
        timeTv.setText("N/A");
        temperatureTv.setText("N/A");
        weekTv.setText("N/A");
        pm25Tv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTodayTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

        date1Tv.setText("N/A");
        highLow1Tv.setText("N/A");
        type1Tv.setText("N/A");
        fengli1Tv.setText("N/A");

        date2Tv.setText("N/A");
        highLow2Tv.setText("N/A");
        type2Tv.setText("N/A");
        fengli2Tv.setText("N/A");

        date3Tv.setText("N/A");
        highLow3Tv.setText("N/A");
        type3Tv.setText("N/A");
        fengli3Tv.setText("N/A");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_cityManager) {
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i, 1);
        }

        if (view.getId() == R.id.title_location) {
            getLoc();
        }

        if (view.getId() == R.id.title_updateBtn_ed) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101100101");
            Log.d("llWeather_cityCode", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("llWeather_clickNet", "Yes");
                queryWeatherCode(cityCode);
            } else {
                Log.d("llWeather_clickNet", "No");
                Toast.makeText(MainActivity.this, "Network unavalible", Toast.LENGTH_LONG).show();
            }
        }

        if (view.getId() == R.id.pm25_pic) {
            System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
            mHints[mHints.length - 1] = SystemClock.uptimeMillis();
            if (SystemClock.uptimeMillis() - mHints[0] < 1000) {
                if (pm25OkFlag == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("俍俍提醒")
                            .setMessage("虽然空气不好，但还是要有好心情！")
                            .setCancelable(false)
                            .setPositiveButton("笑一个吧", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pmImage.setImageResource(R.drawable.biz_plugin_weather_0_50);
                                    try {
                                        java.lang.reflect.Field field = dialog.getClass()
                                                .getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog,true);
                                        dialog.dismiss();
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("心情不好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        java.lang.reflect.Field field = dialog.getClass()
                                                .getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog,false);
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, "o(*￣3￣)o，心情不好选项已被禁用", Toast.LENGTH_LONG).show();
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("俍俍提醒")
                            .setMessage("晴天要有好心情！")
                            .setCancelable(false)
                            .setPositiveButton("笑一个吧", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pmImage.setImageResource(R.drawable.biz_plugin_weather_0_50);
                                    try {
                                        java.lang.reflect.Field field = dialog.getClass()
                                                .getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog,true);
                                        dialog.dismiss();
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("心情不好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        java.lang.reflect.Field field = dialog.getClass()
                                                .getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog,false);
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, "o(*￣3￣)o，该选项已被禁用，请重新选择", Toast.LENGTH_LONG).show();
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    builder.show();
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("llWeather_cityCode", newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("llWeather_netConnection", "Yes");
                if (!newCityCode.equals("-1")) {
                    queryWeatherCode(newCityCode);

                    SharedPreferences settings = getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("main_city_code", newCityCode);
                    editor.commit();
                } else {
                    Toast.makeText(MainActivity.this, "城市选择失败", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("llWeather_netConnection", "No");
                Toast.makeText(MainActivity.this, "Network unavalible", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode) {
        mUpdateBtn_ed.setVisibility(View.INVISIBLE);
        mUpdateBtn_ing.setVisibility(View.VISIBLE);

        final String address1 = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("llWeather_URL1", address1);

        String cityCode2 = cityCode.substring(0, 5) + "0100";
        final String address2 = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode2;
        Log.d("llWeather_URL2", address2);

        String cityCode3 = cityCode.substring(0, 5) + "0101";
        final String address3 = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode3;
        Log.d("llWeather_URL3", address3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection1 = null;
                HttpURLConnection httpURLConnection2 = null;
                HttpURLConnection httpURLConnection3 = null;
                TodayWeather todayWeather1 = null;
                TodayWeather todayWeather2 = null;
                TodayWeather todayWeather3 = null;

                try {
                    URL url1 = new URL(address1);
                    httpURLConnection1 = (HttpURLConnection) url1.openConnection();
                    httpURLConnection1.setRequestMethod("GET");
                    httpURLConnection1.setReadTimeout(4000);
                    InputStream inputStream1 = httpURLConnection1.getInputStream();
                    BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1));
                    StringBuilder response1 = new StringBuilder();
                    String str1;
                    while ((str1 = bufferedReader1.readLine()) != null) {
                        response1.append(str1);
                        Log.d("llWeather_str1", str1);
                    }
                    String responseStr1 = response1.toString();
                    Log.d("llWeather_responseStr1", responseStr1);
                    todayWeather1 = parseXml(responseStr1);


                    URL url2 = new URL(address2);
                    httpURLConnection2 = (HttpURLConnection) url2.openConnection();
                    httpURLConnection2.setRequestMethod("GET");
                    httpURLConnection2.setReadTimeout(1000);
                    InputStream inputStream2 = httpURLConnection2.getInputStream();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
                    StringBuilder response2 = new StringBuilder();
                    String str2;
                    while ((str2 = bufferedReader2.readLine()) != null) {
                        response2.append(str2);
                        Log.d("llWeather_str2", str2);
                    }
                    String responseStr2 = response2.toString();
                    Log.d("llWeather_responseStr2", responseStr2);
                    todayWeather2 = parseXmlPm25(responseStr2);

                    URL url3 = new URL(address3);
                    httpURLConnection3 = (HttpURLConnection) url3.openConnection();
                    httpURLConnection3.setRequestMethod("GET");
                    httpURLConnection3.setReadTimeout(1000);
                    InputStream inputStream3 = httpURLConnection3.getInputStream();
                    BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(inputStream3));
                    StringBuilder response3 = new StringBuilder();
                    String str3;
                    while ((str3 = bufferedReader3.readLine()) != null) {
                        response3.append(str3);
                        Log.d("llWeather_str3", str3);
                    }
                    String responseStr3 = response3.toString();
                    Log.d("llWeather_responseStr3", responseStr3);
                    todayWeather3 = parseXmlPm25(responseStr3);

                    TodayWeather[] todayWeathersList = {todayWeather1, todayWeather2, todayWeather3};

                    if (todayWeather1 != null && (todayWeather2 != null || todayWeather3 != null)) {
                        Log.d("llWeather_todayWeather1", todayWeather1.toString());
                        if (todayWeather2 != null) {
                            Log.d("llWeather_todayWeather2", todayWeather2.toString());
                        } else {
                            Log.d("llWeather_todayWeather2", "NULL");
                        }
                        if (todayWeather3 != null) {
                            Log.d("llWeather_todayWeather3", todayWeather3.toString());
                        } else {
                            Log.d("llWeather_todayWeather3", "NULL");
                        }

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = todayWeathersList;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("llWeather_exception", String.valueOf(e.getCause()));
                    if (String.valueOf(e.getCause()) == "libcore.io.ErrnoException: connect failed: ETIMEDOUT (Connection timed out)") {
                        Toast.makeText(MainActivity.this, "Cannot connect wthrcdn.etouch.cn", Toast.LENGTH_LONG).show();
                    }
                } finally {
                    if (httpURLConnection1 != null) {
                        httpURLConnection1.disconnect();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                }
            }
        }).start();


    }

    private TodayWeather parseXml(String xmlData) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            Log.d("llWeather_Loading", "parserXml");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                                Log.d("llWeather_city", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdateTime(xmlPullParser.getText());
                                Log.d("llWeather_updatetime", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                                Log.d("llWeather_wendu", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                                Log.d("llWeather_fengli", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli1(xmlPullParser.getText());
                                fengliCount++;
                                Log.d("llWeather_fengli1", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli2(xmlPullParser.getText());
                                fengliCount++;
                                Log.d("llWeather_fengli2", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli3(xmlPullParser.getText());
                                fengliCount++;
                                Log.d("llWeather_fengli3", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                                Log.d("llWeather_fengxiang", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                Log.d("llWeather_pm25_1", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                                Log.d("llWeather_quality_1", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                                Log.d("llWeather_date", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate1(xmlPullParser.getText());
                                dateCount++;
                                Log.d("llWeather_date1", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate2(xmlPullParser.getText());
                                dateCount++;
                                Log.d("llWeather_date2", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate3(xmlPullParser.getText());
                                dateCount++;
                                Log.d("llWeather_date3", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                                Log.d("llWeather_high", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh1(xmlPullParser.getText());
                                highCount++;
                                Log.d("llWeather_high1", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh2(xmlPullParser.getText());
                                highCount++;
                                Log.d("llWeather_high2", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh3(xmlPullParser.getText());
                                highCount++;
                                Log.d("llWeather_high32", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                                Log.d("llWeather_low", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow1(xmlPullParser.getText());
                                lowCount++;
                                Log.d("llWeather_low1", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow2(xmlPullParser.getText());
                                lowCount++;
                                Log.d("llWeather_low2", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow3(xmlPullParser.getText());
                                lowCount++;
                                Log.d("llWeather_low3", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                                Log.d("llWeather_type", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType1(xmlPullParser.getText());
                                typeCount++;
                                Log.d("llWeather_type1", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType2(xmlPullParser.getText());
                                typeCount++;
                                Log.d("llWeather_type2", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType3(xmlPullParser.getText());
                                typeCount++;
                                Log.d("llWeather_type3", xmlPullParser.getText());
                            }
                            break;
                        }


                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return todayWeather;
    }

    private TodayWeather parseXmlPm25(String xmlData) {
        TodayWeather todayWeather = null;
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            Log.d("llWeather_Loading", "parserXmlPm25");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                Log.d("llWeather_pm25_2", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                                Log.d("llWeather_quality_2", xmlPullParser.getText());
                            }
                            break;
                        }


                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather1, TodayWeather todayWeather2, TodayWeather todayWeather3) {
        if (todayWeather1.getCity() != null) {
            cityNameTv.setText(todayWeather1.getCity());
            timeTv.setText(todayWeather1.getUpdateTime() + "发布");
            temperatureTv.setText(todayWeather1.getWendu() + "℃");
            weekTv.setText(todayWeather1.getDate());
            temperatureTodayTv.setText(todayWeather1.getLow().substring(2) + "~" + todayWeather1.getHigh().substring(2));
            climateTv.setText(todayWeather1.getType() + " · ");
            switch (todayWeather1.getType()) {
                case "暴雪":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "沙尘暴":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                case "特大暴雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "阴":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "阵雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "中雨":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                case "晴":
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                default:
                    weatherImage.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            windTv.setText(todayWeather1.getFengli() + "风");

            date1Tv.setText(todayWeather1.getDate1().substring(todayWeather1.getDate1().length() - 3));
            highLow1Tv.setText(todayWeather1.getLow1().substring(2) + "~" + todayWeather1.getHigh1().substring(2));
            type1Tv.setText(todayWeather1.getType1());
            fengli1Tv.setText(todayWeather1.getFengli1());

            date2Tv.setText(todayWeather1.getDate2().substring(todayWeather1.getDate2().length() - 3));
            highLow2Tv.setText(todayWeather1.getLow2().substring(2) + "~" + todayWeather1.getHigh2().substring(2));
            type2Tv.setText(todayWeather1.getType2());
            fengli2Tv.setText(todayWeather1.getFengli2());

            date3Tv.setText(todayWeather1.getDate3().substring(todayWeather1.getDate3().length() - 3));
            highLow3Tv.setText(todayWeather1.getLow3().substring(2) + "~" + todayWeather1.getHigh3().substring(2));
            type3Tv.setText(todayWeather1.getType3());
            fengli3Tv.setText(todayWeather1.getFengli3());

            switch (todayWeather1.getType1()) {
                case "暴雪":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "沙尘暴":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                case "特大暴雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "阴":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "阵雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "中雨":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                case "晴":
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                default:
                    type1PicTv.setImageResource(R.drawable.biz_plugin_weather_qing);
            }

            switch (todayWeather1.getType2()) {
                case "暴雪":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "沙尘暴":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                case "特大暴雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "阴":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "阵雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "中雨":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                case "晴":
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                default:
                    type2PicTv.setImageResource(R.drawable.biz_plugin_weather_qing);
            }

            switch (todayWeather1.getType3()) {
                case "暴雪":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "沙尘暴":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                case "特大暴雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "阴":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "阵雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "中雨":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                case "晴":
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                default:
                    type3PicTv.setImageResource(R.drawable.biz_plugin_weather_qing);
            }


            if (todayWeather2.getPm25() != null) {
                pm25Tv.setText(todayWeather2.getPm25());
                int pm25 = Integer.parseInt(todayWeather2.getPm25());
                if (pm25 <= 50) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_0_50);
                    pm25OkFlag = true;
                } else if (pm25 > 50 && pm25 <= 100) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_51_100);
                } else if (pm25 > 100 && pm25 <= 150) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_101_150);
                } else if (pm25 > 150 && pm25 <= 200) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_151_200);
                } else if (pm25 > 250 && pm25 <= 300) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_201_300);
                } else if (pm25 > 300) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                }

                pmQualityTv.setText(todayWeather2.getQuality());
            } else if (todayWeather3.getPm25() != null) {
                pm25Tv.setText(todayWeather3.getPm25());
                int pm25 = Integer.parseInt(todayWeather3.getPm25());
                if (pm25 <= 50) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_0_50);
                    pm25OkFlag = true;
                } else if (pm25 > 50 && pm25 <= 100) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_51_100);
                } else if (pm25 > 100 && pm25 <= 150) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_101_150);
                } else if (pm25 > 150 && pm25 <= 200) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_151_200);
                } else if (pm25 > 250 && pm25 <= 300) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_201_300);
                } else if (pm25 > 300) {
                    pmImage.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                }

                pmQualityTv.setText(todayWeather3.getQuality());
            } else {
                Toast.makeText(MainActivity.this, "抱歉，无法获得该地区PM2.5数据", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(MainActivity.this, "已更新最新数据", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(MainActivity.this, "对不起，无本地天气信息，请尝试搜索附近城市", Toast.LENGTH_SHORT).show();
        }

        mUpdateBtn_ed.setVisibility(View.VISIBLE);
        mUpdateBtn_ing.setVisibility(View.INVISIBLE);
    }

    private void getLoc() {
        mLocBtn.setVisibility(View.INVISIBLE);
        mLocIngBtn.setVisibility(View.VISIBLE);
        mUpdateBtn_ed.setVisibility(View.INVISIBLE);
        mUpdateBtn_ing.setVisibility(View.VISIBLE);

        mLocation.startLocation(getApplicationContext());

        mLocation.setOnGetLocEventListener(new OnGetLocEventListener() {
            @Override
            public void onGetLocEvent(String locCityName) {
                Log.d("llWeather_LocCity_Main", locCityName);
                String locOkName = locCityName.substring(0, locCityName.length() - 1);
                Log.d("llWeather_LcCtOk_Main", locOkName);
                myApplication = (MyApplication) getApplication();
                mCityList = myApplication.getmCityList();
                for (City city: mCityList) {
//                    Log.d("llWeather_LcCtList", city.getCity());
                    if (city.getCity().equals(locOkName)) {
                        locCityCode = city.getNumber();
                        break;
                    }
                }

                if (locCityCode != null) {
                    Log.d("llWeather_LcCtCode_Main", locCityCode);
                    if (NetUtil.getNetworkState(getApplicationContext()) != NetUtil.NETWORK_NONE) {
                        queryWeatherCode(locCityCode);
                        SharedPreferences settings = getSharedPreferences("config", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("main_city_code", locCityCode);
                        editor.commit();
                    } else {
                        Toast.makeText(MainActivity.this, "Network unavalible", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "定位失败，请重试", Toast.LENGTH_LONG).show();
                }
            }
        });
        mUpdateBtn_ed.setVisibility(View.VISIBLE);
        mUpdateBtn_ing.setVisibility(View.INVISIBLE);
        mLocBtn.setVisibility(View.VISIBLE);
        mLocIngBtn.setVisibility(View.INVISIBLE);
    }
}