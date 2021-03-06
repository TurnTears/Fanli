package com.ascba.rebate.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ascba.rebate.R;
import com.ascba.rebate.activities.base.BaseActivity;
import com.ascba.rebate.activities.login.LoginActivity;
import com.ascba.rebate.handlers.CheckThread;
import com.ascba.rebate.handlers.PhoneHandler;
import com.ascba.rebate.utils.LogUtils;
import com.ascba.rebate.utils.NetUtils;
import com.ascba.rebate.utils.UrlEncodeUtils;
import com.ascba.rebate.utils.UrlUtils;
import com.ascba.rebate.view.EditTextWithCustomHint;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

public class AddCardActivity extends BaseActivity {

    private EditTextWithCustomHint edName;
    private EditTextWithCustomHint edCardNumber;
    private PhoneHandler phoneHandler;
    private CheckThread checkThread;
    private RequestQueue requestQueue;
    private SharedPreferences sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crad);
        initViews();
    }

    private void initViews() {
        sf=getSharedPreferences("first_login_success_name_password",MODE_PRIVATE);
        edName = ((EditTextWithCustomHint) findViewById(R.id.ed_add_card_name));
        edCardNumber = ((EditTextWithCustomHint) findViewById(R.id.ed_add_card_number));
    }

    public void next(View view) {
        sendMsgToSevr(UrlUtils.getBankCard);
    }

    private void sendMsgToSevr(String baseUrl) {
        boolean netAva = NetUtils.isNetworkAvailable(this);
        if(!netAva){
            Toast.makeText(this, "请打开网络", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = edName.getText().toString();
        String cardNumber = edCardNumber.getText().toString();
        if(name.equals("")||cardNumber.equals("")){
            Toast.makeText(this, "请输入姓名或卡号", Toast.LENGTH_SHORT).show();
        }
        int uuid = sf.getInt("uuid", -1000);
        String token = sf.getString("token", "");
        long expiring_time = sf.getLong("expiring_time", -2000);
        requestQueue = NoHttp.newRequestQueue();
        final ProgressDialog dialog = new ProgressDialog(this, R.style.dialog);
        dialog.setMessage("请稍后");
        Request<JSONObject> objRequest = NoHttp.createJsonObjectRequest(baseUrl + "?", RequestMethod.POST);
        objRequest.add("sign", UrlEncodeUtils.createSign(baseUrl));
        objRequest.add("uuid", uuid);
        objRequest.add("token", token);
        objRequest.add("expiring_time", expiring_time);
        objRequest.add("bank_card", cardNumber);

        phoneHandler = new PhoneHandler(this);
        phoneHandler.setCallback(new PhoneHandler.Callback() {
            @Override
            public void getMessage(Message msg) {
                dialog.dismiss();
                JSONObject jObj = (JSONObject) msg.obj;
                LogUtils.PrintLog("123AddCardActivity", jObj.toString());
                try {
                    int status = jObj.optInt("status");
                    String message = jObj.getString("msg");
                    if (status == 200) {
                        JSONObject dataObj = jObj.optJSONObject("data");
                        int update_status = dataObj.optInt("update_status");
                        JSONObject bankObj = dataObj.optJSONObject("bankCardInfo");
                        String realname = bankObj.optString("realname");
                        String cardid = bankObj.optString("cardid");
                        String bank_card = bankObj.optString("bank_card");
                        String bank = bankObj.optString("bank");
                        String type = bankObj.optString("type");
                        String nature = bankObj.optString("nature");
                        String kefu = bankObj.optString("kefu");
                        String logo = bankObj.optString("logo");
                        String info = bankObj.optString("info");
                        if (update_status == 1) {
                            sf.edit()
                                    .putString("token", dataObj.optString("token"))
                                    .putLong("expiring_time", dataObj.optLong("expiring_time"))
                                    .apply();
                        }
                        Intent intent = new Intent(AddCardActivity.this,BankCardActivity.class);
                        intent.putExtra("realname",realname);
                        intent.putExtra("cardid",cardid);
                        intent.putExtra("bank_card",bank_card);
                        intent.putExtra("bank",bank);
                        intent.putExtra("type",type);
                        intent.putExtra("nature",nature);
                        intent.putExtra("kefu",kefu);
                        intent.putExtra("logo",logo);
                        intent.putExtra("info",info);
                        startActivity(intent);
                        finish();
                    } else if(status==1||status==2||status==3||status == 4||status==5){//缺少sign参数
                        Intent intent = new Intent(AddCardActivity.this, LoginActivity.class);
                        sf.edit().putInt("uuid", -1000).apply();
                        startActivity(intent);
                        finish();
                    } else if(status==404){
                        Toast.makeText(AddCardActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else if(status==500){
                        Toast.makeText(AddCardActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        checkThread = new CheckThread(requestQueue, phoneHandler, objRequest);
        checkThread.start();
        dialog.show();
    }

    public void goCardProtocol(View view) {
        Intent intent=new Intent(this,CardProtocolActivity.class);
        startActivity(intent);
    }
}
