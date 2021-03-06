package com.ascba.rebate.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ascba.rebate.R;
import com.ascba.rebate.activities.base.BaseActivity;
import com.ascba.rebate.view.EditTextWithCustomHint;
import com.ascba.rebate.view.MoneyBar;

public class BusinessNameActivity extends BaseActivity {

    private EditTextWithCustomHint edName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_name);
        initViews();
    }

    private void initViews() {
        edName = ((EditTextWithCustomHint) findViewById(R.id.ed_business_data_name));
        ((MoneyBar) findViewById(R.id.mb_name)).setCallBack(new MoneyBar.CallBack() {
            @Override
            public void clickImage(View im) {

            }

            @Override
            public void clickComplete(View tv) {
                String s = edName.getText().toString();
                Intent intent=getIntent();
                intent.putExtra("business_data_name",s);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
