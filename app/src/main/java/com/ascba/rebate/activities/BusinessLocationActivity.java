package com.ascba.rebate.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ascba.rebate.R;
import com.ascba.rebate.activities.base.BaseActivity;
import com.ascba.rebate.view.EditTextWithCustomHint;
import com.ascba.rebate.view.MoneyBar;

public class BusinessLocationActivity extends BaseActivity {
    private EditTextWithCustomHint edLocate;
    private EditTextWithCustomHint edCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_location);
        initViews();
    }

    private void initViews() {
        edCity = ((EditTextWithCustomHint) findViewById(R.id.ed_city));
        edLocate = ((EditTextWithCustomHint) findViewById(R.id.ed_locate));
        ((MoneyBar) findViewById(R.id.mb_location)).setCallBack(new MoneyBar.CallBack() {
            @Override
            public void clickImage(View im) {

            }

            @Override
            public void clickComplete(View tv) {
                String s = edCity.getText().toString();
                Intent intent=getIntent();
                intent.putExtra("location",s);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
