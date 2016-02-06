package com.app.coldweather.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.coldweather.R;
import com.app.coldweather.db.ColdWeatherDB;
import com.app.coldweather.model.City;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {

    private ProgressDialog progressDialog;
    private AlertDialog.Builder dialog;
    private Button backBtn;
    private EditText addEdit;
    private Button addBtn;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ColdWeatherDB coldWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 城市列表
     */
    private List<City> cityList;

    /**
     * 选中的城市
     */
    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.choose_area);
        backBtn = (Button) findViewById(R.id.back_btn);
        addEdit = (EditText) findViewById(R.id.add_edit);
        addBtn = (Button) findViewById(R.id.add_btn);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coldWeatherDB = ColdWeatherDB.getInstance(this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCity = addEdit.getText().toString();
                int flag = 1;
                if (!TextUtils.isEmpty(inputCity)) {
                    for (City city : cityList) {
                        if (city.getCityName().equals(inputCity)) {
                            flag = 0;
                            Toast.makeText(ChooseAreaActivity.this, "已添加「" + inputCity + "」的天气信息，不必重复",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (flag == 1) {
                        dialog = new AlertDialog.Builder(ChooseAreaActivity.this);
                        dialog.setMessage("你确定要添加「" + inputCity + "」的天气信息吗？");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("我就要", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputCity = addEdit.getText().toString();
                                City city = new City();
                                city.setCityName(inputCity);
                                coldWeatherDB.saveCity(city);
                                queryCities();
                                Toast.makeText(ChooseAreaActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.setNegativeButton("我不要", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();
                    }
                }
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = cityList.get(position);
                Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                intent.putExtra("city_name", selectedCity.getCityName());
                startActivity(intent);
                finish();
            }
        });
        queryCities();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = cityList.get(position);
                dialog = new AlertDialog.Builder(ChooseAreaActivity.this);
                dialog.setMessage("你确定要删除该城市的天气信息吗？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("我就要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        coldWeatherDB.deleteCity(selectedCity);
                        queryCities();
                    }
                });
                dialog.setNegativeButton("我不要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void queryCities() {
        cityList = coldWeatherDB.loadCities();
        if(cityList.size() >= 0) {
            dataList.clear();
            for(City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在努力加载└(^o^)┘");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }
}
