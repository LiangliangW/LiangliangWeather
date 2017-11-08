package cn.edu.pku.wuliangliang.llweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by WLL on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = findViewById(R.id.city_back);
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_back:
                Intent i = new Intent();
                i.putExtra("cityCode", "101100101");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
