package com.app.coldweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.coldweather.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Codpoe on 2016/2/4.
 */
public class ColdWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cold_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static ColdWeatherDB coldWeatherDB;

    private SQLiteDatabase db;

    /**
     * 将构造方式私有化
     */
    private ColdWeatherDB(Context context) {
        ColdWeatherOpenHelper dbHelper = new ColdWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取ColdWeatherDB的实例
     */
    public synchronized static ColdWeatherDB getInstance(Context context) {
        if(coldWeatherDB == null) {
            coldWeatherDB = new ColdWeatherDB(context);
        }
        return coldWeatherDB;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city) {
        if(city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     */
    public List<City> loadCities() {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
    }

    /**
     * 从数据库删除City
     */
    public void deleteCity(City city) {
        db.delete("City", "city_name = ?", new String[]{city.getCityName()});
    }

}
