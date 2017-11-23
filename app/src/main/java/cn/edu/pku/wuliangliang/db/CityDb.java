package cn.edu.pku.wuliangliang.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wuliangliang.bean.City;

/**
 * Created by WLL on 2017/11/1.
 */

public class CityDb {
    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDb(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    public List<City> getAllCity() {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String province = cursor.getString(cursor.getColumnIndex("province"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String firstPy = cursor.getString(cursor.getColumnIndex("firstpy"));
            String allPy = cursor.getString(cursor.getColumnIndex("allpy"));
            String allFirstPy = cursor.getString(cursor.getColumnIndex("allfirstpy"));

            City item = new City(province, city, number, firstPy, allPy, allFirstPy);
            list.add(item);
        }

        return list;
    }

    public List<City> getLocCity(String locOkName) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.rawQuery("SELECT * from " + CITY_TABLE_NAME + " WHERE city = \'" + locOkName + "\'", null);
        while (cursor.moveToNext()) {
            String province = cursor.getString(cursor.getColumnIndex("province"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String firstPy = cursor.getString(cursor.getColumnIndex("firstpy"));
            String allPy = cursor.getString(cursor.getColumnIndex("allpy"));
            String allFirstPy = cursor.getString(cursor.getColumnIndex("allfirstpy"));

            City item = new City(province, city, number, firstPy, allPy, allFirstPy);
            list.add(item);
        }

        return list;
    }
}
