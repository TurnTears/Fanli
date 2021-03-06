package com.ascba.rebate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ascba.rebate.R;
import com.ascba.rebate.activities.base.NetworkBaseActivity;
import com.ascba.rebate.view.EditTextWithCustomHint;
import com.ascba.rebate.view.MoneyBar;

public class MessageActivity extends NetworkBaseActivity {
    private EditTextWithCustomHint edMsg;
    private MoneyBar mbMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initViews();
    }

    private void initViews() {
        edMsg = ((EditTextWithCustomHint) findViewById(R.id.ed_msg));
        mbMsg = ((MoneyBar) findViewById(R.id.mb_msg));
        mbMsg.setCallBack(new MoneyBar.CallBack() {
            @Override
            public void clickImage(View im) {

            }

            @Override
            public void clickComplete(View tv) {
                String msg = edMsg.getText().toString();
                if("".equals(msg)){
                    finish();
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra("message",msg);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
