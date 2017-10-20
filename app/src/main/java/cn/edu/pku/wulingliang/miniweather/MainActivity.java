package cn.edu.pku.wulingliang.miniweather;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.pku.wuliangliang.bean.TodayWeather;
import cn.edu.pku.wuliangliang.util.NetUtil;

/**
 * Created by WLL on 2017/9/20.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mLocBtn;
    private ImageView mShareBtn;
    private ImageView mCitySelectBtn;
    private TextView timeTv, weekTv, pm25Tv, pmQualityTv, temperatureTv, temperatureTodayTv, climateTv, windTv, cityNameTv;
    private ImageView weatherImage, pmImage;

    private LocationManager locationManager;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
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

        mUpdateBtn = findViewById(R.id.title_updateBtn);
        mUpdateBtn.setOnClickListener(this);
        mCitySelectBtn = findViewById(R.id.title_cityManager);
        mCitySelectBtn.setOnClickListener(this);
        mShareBtn = findViewById(R.id.title_share);
        mShareBtn.setOnClickListener(this);
        mLocBtn = findViewById(R.id.title_location);
        mLocBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            queryWeatherCode(cityCode);
            Log.d("llWeather_netConnection", "Yes");
            Toast.makeText(MainActivity.this, "Network connected", Toast.LENGTH_LONG).show();
        } else {
            Log.d("llWeather_netConnection", "No");
            Toast.makeText(MainActivity.this, "Network unavalible", Toast.LENGTH_LONG).show();
        }

        initView();
        getLoc();
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

        cityNameTv.setText("N/A");
        timeTv.setText("N/A");
        temperatureTv.setText("N/A");
        weekTv.setText("N/A");
        pm25Tv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTodayTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_cityManager) {
            Intent i = new Intent(this, SelectCity.class);
//            startActivity(i);
            startActivityForResult(i, 1);
        }

        if (view.getId() == R.id.title_location) {
            getLoc();
        }

        if (view.getId() == R.id.title_updateBtn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("llWeather_cityCode", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("llWeather_clickNet", "Yes");
                queryWeatherCode(cityCode);
            } else {
                Log.d("llWeather_clickNet", "No");
                Toast.makeText(MainActivity.this, "Network unavalible", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("llWeather_cityCode", newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("llWeather_netConnection", "Yes");
                queryWeatherCode(newCityCode);

                SharedPreferences settings = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("main_city_code", newCityCode);
                editor.commit();
            } else {
                Log.d("llWeather_netConnection", "No");
                Toast.makeText(MainActivity.this, "Network unavalible", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("llWeather_URL", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setReadTimeout(4000);
                    httpURLConnection.setReadTimeout(4000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        response.append(str);
                        Log.d("llWeather_str", str);
                    }
                    String responseStr = response.toString();
                    Log.d("llWeather_responseStr", responseStr);
                    todayWeather = parseXml(responseStr);
                    if (todayWeather != null) {
                        Log.d("llWeather_todayWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("llWeather_exception", String.valueOf(e.getCause()));
                    if (String.valueOf(e.getCause()) == "libcore.io.ErrnoException: connect failed: ETIMEDOUT (Connection timed out)") {
                        Toast.makeText(MainActivity.this, "Cannot connect wthrcdn.etouch.cn", Toast.LENGTH_LONG).show();
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
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
            Log.d("llWeather_Loading", "parserXML");
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
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                                Log.d("llWeather_fengxiang", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                Log.d("llWeather_pm25", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                                Log.d("llWeather_quality", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                                Log.d("llWeather_date", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                                Log.d("llWeather_high", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                                Log.d("llWeather_low", xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                                Log.d("llWeather_type", xmlPullParser.getText());
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

    void updateTodayWeather(TodayWeather todayWeather) {
        cityNameTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdateTime() + "发布");
        temperatureTv.setText(todayWeather.getWendu() + "℃");
        weekTv.setText(todayWeather.getDate());

        pm25Tv.setText(todayWeather.getPm25());
        int pm25 = Integer.parseInt(todayWeather.getPm25());
        if (pm25 <= 50) {
            pmImage.setImageResource(R.drawable.biz_plugin_weather_0_50);
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

        pmQualityTv.setText(todayWeather.getQuality());
        temperatureTodayTv.setText(todayWeather.getLow().substring(2) + "~" + todayWeather.getHigh().substring(2));

        climateTv.setText(todayWeather.getType() + " · ");
        switch (todayWeather.getType()) {
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

        windTv.setText(todayWeather.getFengli() + "风");
        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_LONG).show();
    }

    private void getLoc() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, false);
        Log.d("llWeather_LocProvider", provider);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (provider == null || provider.equals("")) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                locationManager.requestLocationUpdates(provider, 0, 0,
                        locationListener);
            }
            updateLoc(location);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLoc(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("llWeather_LocInfo", "Provider now is enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateLoc(null);
            Log.d("llWeather_LocInfo", "Provider now is disabled");
        }
    };

    private void updateLoc(Location location) {
        String latLng;
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLng = "Latitude:" + lat + "  Longitude:" + lng;
        } else {
            latLng = "Can't access your location";
        }

        Log.d("llWeather_LocLatLng", latLng);
    }
}