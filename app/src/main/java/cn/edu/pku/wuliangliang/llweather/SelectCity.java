package cn.edu.pku.wuliangliang.llweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.edu.pku.wuliangliang.app.MyApplication;
import cn.edu.pku.wuliangliang.bean.City;

/**
 * Created by WLL on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn, mDeleteTxt;
    private EditText mEditText;
    private ListView mCityListView;
    private MyApplication mMyApplication;
    private List<City> mCityList;
    private ArrayList<City> mCityList_selected;
    private ArrayList<String> mArrayList;
    private String updateCityCode = "-1";
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = findViewById(R.id.city_back);
        mBackBtn.setOnClickListener(this);
        mDeleteTxt = findViewById(R.id.city_search_delete);
        mDeleteTxt.setOnClickListener(this);
        mEditText = findViewById(R.id.city_search_edit);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mDeleteTxt.setVisibility(View.GONE);
                } else {
                    mDeleteTxt.setVisibility(View.VISIBLE);
                }

                refreshMArrayList(s.toString());
            }
        });
        mCityListView = findViewById(R.id.city_list);
        mMyApplication = (MyApplication) getApplication();
        mCityList = mMyApplication.getmCityList();
        mCityList_selected = new ArrayList<City>();
        for (City city : mCityList) {
            mCityList_selected.add(city);
        }
        mArrayList = new ArrayList<String>();
        mCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this, "你点击了" + mArrayList.get(position), Toast.LENGTH_SHORT).show();
                updateCityCode = mCityList_selected.get(position).getNumber();
                Intent i = new Intent();
                i.putExtra("cityCode", updateCityCode);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        for (int i = 0; i < mCityList_selected.size(); i++) {
            mArrayList.add(mCityList_selected.get(i).getCity() + "-" + mCityList_selected.get(i).getProvince());
        }

        arrayAdapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, mArrayList);
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
            case R.id.city_search_delete:
                mEditText.setText("");
            default:
                break;
        }
    }

    public void refreshMArrayList (String s) {
        if (TextUtils.isEmpty(s)) {
            for (City city : mCityList) {
                mCityList_selected.add(city);
            }
        } else {
            mCityList_selected.clear();
            for (City city : mCityList) {
                if (city.getCity().contains(s) || city.getProvince().contains(s)) {
                    mCityList_selected.add(city);
                }
            }
        }

        mArrayList.clear();
        for (City city : mCityList_selected) {
            mArrayList.add(city.getCity() + "-" + city.getProvince());
        }

        arrayAdapter.notifyDataSetChanged();
    }
}
