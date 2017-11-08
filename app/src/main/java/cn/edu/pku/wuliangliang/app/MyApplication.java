package cn.edu.pku.wuliangliang.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wuliangliang.bean.City;
import cn.edu.pku.wuliangliang.db.CityDb;

/**
 * Created by WLL on 2017/11/1.
 */

public class MyApplication extends Application {

    private CityDb mCityDb;
    private List<City> mCityList;
    private static MyApplication mMyApplication;

    public List<City> getmCityList() {
        return mCityList;
    }
    private static MyApplication getInstance() {
        return mMyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("llWeather_Application", "MyApplication onCreate");
        mMyApplication = this;
        mCityDb = openCityDb();
        initCityList();
    }

    private CityDb openCityDb() {
        String dbPath = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator + CityDb.CITY_DB_NAME;
        File db = new File(dbPath);
        Log.d("llWeather_dbPath", dbPath);
        if (!db.exists()) {
            String folderPath = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(folderPath);
            Log.d("llWeather_folderPath", folderPath);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
                Log.d("llWeather_MyAppMkdirs", "true");
            }
            Log.d("llWeather_DbExists", "false");
            try {
                InputStream inputStream = getAssets().open("city.db");
                FileOutputStream fileOutputStream = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    fileOutputStream.flush();
                }
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDb(this, dbPath);
    }

    private void initCityList() {
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDb.getAllCity();
        int i = 0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
//            Log.d("llWeather_cityList", cityCode + " : " + cityName);
        }
        Log.d("llWeather_cityListNum", "i = " + i);
        return true;
    }

}
