package com.app.coldweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.coldweather.R;
import com.app.coldweather.util.HttpCallbackListener;
import com.app.coldweather.util.HttpUtil;
import com.app.coldweather.util.Utility;


public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;

    /**
     * 用于显示城市名
     */
    private TextView cityNameText;

    /**
     * 用于显示日期
     */
    private TextView dateText;

    /**
     * 用于显示更新时间
     */
    private TextView timeText;

    /**
     * 用于显示温度
     */
    private TextView temperatureText;

    /**
     * 用于显示湿度
     */
    private TextView humidityText;

    /**
     * 用于显示描述
     */
    private TextView infoText;

    /**
     * 用于显示风向和风力
     */
    private TextView windText;

    /**
     * 用于显示未来几天的天气数据
     */
    private TextView week0Text;
    private TextView desp0Text;
    private TextView temperature0Text;

    private TextView week1Text;
    private TextView desp1Text;
    private TextView temperature1Text;

    private TextView week2Text;
    private TextView desp2Text;
    private TextView temperature2Text;

    private TextView week3Text;
    private TextView desp3Text;
    private TextView temperature3Text;

    private TextView week4Text;
    private TextView desp4Text;
    private TextView temperature4Text;

    /**
     * PM2.5
     */
    private TextView pm25Text;

    /**
     * 切换城市按钮
     */
    private Button switchCityBtn;

    /**
     * 更新天气按钮
     */
    private Button refreshWeatherBtn;

    private static final String KEY = "7c993db932e6c6f7e3ccccfc4c1a3976";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name_text);
        dateText = (TextView) findViewById(R.id.date_text);
        timeText = (TextView) findViewById(R.id.time_text);
        temperatureText = (TextView) findViewById(R.id.temperature_text);
        humidityText = (TextView) findViewById(R.id.humidity_text);
        infoText = (TextView) findViewById(R.id.info_text);
        windText = (TextView) findViewById(R.id.wind_text);

        week0Text = (TextView) findViewById(R.id.week_0_text);
        desp0Text = (TextView) findViewById(R.id.desp_0_text);
        temperature0Text = (TextView) findViewById(R.id.temperature_0_text);

        week1Text = (TextView) findViewById(R.id.week_1_text);
        desp1Text = (TextView) findViewById(R.id.desp_1_text);
        temperature1Text = (TextView) findViewById(R.id.temperature_1_text);

        week2Text = (TextView) findViewById(R.id.week_2_text);
        desp2Text = (TextView) findViewById(R.id.desp_2_text);
        temperature2Text = (TextView) findViewById(R.id.temperature_2_text);

        week3Text = (TextView) findViewById(R.id.week_3_text);
        desp3Text = (TextView) findViewById(R.id.desp_3_text);
        temperature3Text = (TextView) findViewById(R.id.temperature_3_text);

        week4Text = (TextView) findViewById(R.id.week_4_text);
        desp4Text = (TextView) findViewById(R.id.desp_4_text);
        temperature4Text = (TextView) findViewById(R.id.temperature_4_text);

        pm25Text = (TextView) findViewById(R.id.pm25_text);

        switchCityBtn = (Button) findViewById(R.id.switch_city_btn);
        refreshWeatherBtn = (Button) findViewById(R.id.refresh_weather_btn);
        String cityName = getIntent().getStringExtra("city_name");
        if(!TextUtils.isEmpty(cityName)) {
            dateText.setVisibility(View.INVISIBLE);
            timeText.setText("更新中...");
            cityNameText.setVisibility(View.INVISIBLE);
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            String address = "http://op.juhe.cn/onebox/weather/query?cityname="
                    + cityName + "&key=" +KEY;
            queryWeather(address);
        }else {
            showWeather();
        }
        switchCityBtn.setOnClickListener(this);
        refreshWeatherBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city_btn:
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather_btn:
                timeText.setText("更新中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String cityName = prefs.getString("city_name", "");
                if (!TextUtils.isEmpty(cityName)) {
                    String address = "http://op.juhe.cn/onebox/weather/query?cityname="
                            + cityName + "&key=" +KEY;
                    queryWeather(address);
                }
                break;
            default:
                break;
        }
    }

    private void queryWeather(String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (!TextUtils.isEmpty(response)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeText.setText("更新失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        dateText.setText(prefs.getString("date", ""));
        timeText.setText(prefs.getString("time", "") + "更新");

        temperatureText.setText(prefs.getString("temperature", ""));
        humidityText.setText("湿度：" + prefs.getString("humidity", "") + "%");
        infoText.setText(prefs.getString("info", "") + "   ");
        windText.setText(prefs.getString("direct", "") + prefs.getString("power", ""));

        week0Text.setText("周" + prefs.getString("week_0", ""));
        desp0Text.setText(prefs.getString("future_desp_0", ""));
        temperature0Text.setText(prefs.getString("night_temperature_0", "") + "~" +
                prefs.getString("day_temperature_0", ""));

        week1Text.setText("周" + prefs.getString("week_1", ""));
        desp1Text.setText(prefs.getString("future_desp_1", ""));
        temperature1Text.setText(prefs.getString("night_temperature_1", "") + "~" +
                prefs.getString("day_temperature_1", ""));

        week2Text.setText("周" + prefs.getString("week_2", ""));
        desp2Text.setText(prefs.getString("future_desp_2", ""));
        temperature2Text.setText(prefs.getString("night_temperature_2", "") + "~" +
                prefs.getString("day_temperature_2", ""));

        week3Text.setText("周" + prefs.getString("week_3", ""));
        desp3Text.setText(prefs.getString("future_desp_3", ""));
        temperature3Text.setText(prefs.getString("night_temperature_3", "") + "~" +
                prefs.getString("day_temperature_3", ""));

        week4Text.setText("周" + prefs.getString("week_4", ""));
        desp4Text.setText(prefs.getString("future_desp_4", ""));
        temperature4Text.setText(prefs.getString("night_temperature_4", "") + "~" +
                prefs.getString("day_temperature_4", ""));

        pm25Text.setText(prefs.getString("pm25", "") + " 空气质量" +
                prefs.getString("quality", ""));

        cityNameText.setVisibility(View.VISIBLE);
        dateText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
