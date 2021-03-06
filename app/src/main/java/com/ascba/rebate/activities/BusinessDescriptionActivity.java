package com.ascba.rebate.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ascba.rebate.R;
import com.ascba.rebate.activities.base.BaseActivity;
import com.ascba.rebate.view.EditTextWithCustomHint;
import com.ascba.rebate.view.MoneyBar;

public class BusinessDescriptionActivity extends BaseActivity {

    private EditTextWithCustomHint tvDesc;
    private MoneyBar mbDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_description);
        initViews();
    }

    private void initViews() {
        tvDesc = ((EditTextWithCustomHint) findViewById(R.id.ed_desc));
        mbDesc = ((MoneyBar) findViewById(R.id.mb_desc));
        mbDesc.setCallBack(new MoneyBar.CallBack() {
            @Override
            public void clickImage(View im) {

            }

            @Override
            public void clickComplete(View tv) {
                String s = tvDesc.getText().toString();
                if(!"".equals(s)){
                    Intent intent = getIntent();
                    intent.putExtra("desc",s);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
}
