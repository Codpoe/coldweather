package com.app.coldweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.app.coldweather.db.ColdWeatherDB;
import com.app.coldweather.model.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Codpoe on 2016/2/4.
 */
public class Utility {

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
     */
    public static void handleWeatherResponse(Context context, String response) {
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("result").getJSONObject("data");                                        //数据
            JSONObject realTime = data.getJSONObject("realtime");   //实时
            JSONArray future = data.getJSONArray("weather");     //未来几天天气预报
            JSONObject pm25 = data.getJSONObject("pm25");           //PM2.5

            /**
             * 实时/当天天气数据
             */
            String cityName = realTime.getString("city_name");      //城市
            String date = realTime.getString("date");               //日期
            String time = realTime.getString("time");               //更新时间
            JSONObject weather = realTime.getJSONObject("weather"); //当前实况天气
            String temperature = weather.getString("temperature");  //温度
            String humidity = weather.getString("humidity");        //湿度
            String info = weather.getString("info");                //描述
            JSONObject wind = realTime.getJSONObject("wind");       //风
            String direct = wind.getString("direct");               //风向
            String power = wind.getString("power");                 //风力

            /**
             * 未来几天天气预报
             */
            String[] week = new String[5];                          //周几
            String[] futureDesp = new String[5];                    //未来几天天气的描述
            String[] dayTemperature = new String[5];                //白天温度
            String[] nightTemperature = new String[5];              //夜间温度
            for(int i = 0; i < 5; i ++) {                           //取未来五天的天气数据
                JSONObject futureJSONObject = future.getJSONObject(i);
                JSONObject futureInfo = futureJSONObject.getJSONObject("info");
                JSONArray day = futureInfo.getJSONArray("day");     //白天天气
                futureDesp[i] = day.getString(1);                   //描述
                dayTemperature[i] = day.getString(2);               //白天温度
                JSONArray night = futureInfo.getJSONArray("night"); //夜间天气
                nightTemperature[i] = night.getString(2);           //夜间温度
                week[i] = futureJSONObject.getString("week");       //周几
            }

            /**
             * PM2.5
             */
            JSONObject pm25_1 = pm25.getJSONObject("pm25");
            String pm25_2 = pm25_1.getString("pm25");
            String quality = pm25_1.getString("quality");

            saveWeather(context, cityName, date, time, temperature, humidity,
                    info, direct, power, week, futureDesp, dayTemperature,
                    nightTemperature, pm25_2, quality);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中
     */
    public static void saveWeather(Context context, String cityName, String date,
                                   String time, String temperature, String humidity,
                                   String info, String direct, String power,
                                   String[] week, String[] futureDesp, String[] dayTemperature,
                                   String[] nightTemperature, String pm25_2, String quality) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_celected", true);
        editor.putString("city_name", cityName);
        editor.putString("date", date);
        editor.putString("time", time);
        editor.putString("temperature", temperature);
        editor.putString("humidity", humidity);
        editor.putString("info", info);
        editor.putString("direct", direct);
        editor.putString("power", power);
        editor.putString("week_0", week[0]);
        editor.putString("week_1", week[1]);
        editor.putString("week_2", week[2]);
        editor.putString("week_3", week[3]);
        editor.putString("week_4", week[4]);
        editor.putString("future_desp_0", futureDesp[0]);
        editor.putString("future_desp_1", futureDesp[1]);
        editor.putString("future_desp_2", futureDesp[2]);
        editor.putString("future_desp_3", futureDesp[3]);
        editor.putString("future_desp_4", futureDesp[4]);
        editor.putString("day_temperature_0", dayTemperature[0]);
        editor.putString("day_temperature_1", dayTemperature[1]);
        editor.putString("day_temperature_2", dayTemperature[2]);
        editor.putString("day_temperature_3", dayTemperature[3]);
        editor.putString("day_temperature_4", dayTemperature[4]);
        editor.putString("night_temperature_0", nightTemperature[0]);
        editor.putString("night_temperature_1", nightTemperature[1]);
        editor.putString("night_temperature_2", nightTemperature[2]);
        editor.putString("night_temperature_3", nightTemperature[3]);
        editor.putString("night_temperature_4", nightTemperature[4]);
        editor.putString("pm25", pm25_2);
        editor.putString("quality", quality);
        editor.commit();

    }
}
