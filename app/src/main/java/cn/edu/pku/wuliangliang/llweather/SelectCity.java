package cn.edu.pku.wuliangliang.llweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wuliangliang.app.MyApplication;
import cn.edu.pku.wuliangliang.bean.City;

/**
 * Created by WLL on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView mCityListView;
    private MyApplication mMyApplication;
    private List<City> mCityList;
    private ArrayList<String> mArrayList;
    private String updateCityCode = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = findViewById(R.id.city_back);
        mBackBtn.setOnClickListener(this);
        mCityListView = findViewById(R.id.city_list);
        mMyApplication = (MyApplication) getApplication();
        mCityList = mMyApplication.getmCityList();
        mArrayList = new ArrayList<String>();
        mCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this, "你点击了" + mArrayList.get(position), Toast.LENGTH_SHORT).show();
                updateCityCode = mCityList.get(position).getNumber();
            }
        });

        for (int i = 0; i < mCityList.size(); i++) {
            mArrayList.add(mCityList.get(i).getCity());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, mArrayList);
        mCityListView.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_back:
                Intent i = new Intent();
                i.putExtra("cityCode", updateCityCode);
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
