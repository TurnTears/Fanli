package com.ascba.rebate.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ascba.rebate.R;
import com.ascba.rebate.activities.base.BaseNetWork3Activity;
import com.ascba.rebate.activities.main.MainActivity;
import com.ascba.rebate.activities.password_loss.PasswordLossActivity;
import com.ascba.rebate.activities.register.RegisterInputNumberActivity;
import com.ascba.rebate.appconfig.AppConfig;
import com.ascba.rebate.utils.UrlEncodeUtils;
import com.ascba.rebate.utils.UrlUtils;
import com.yolanda.nohttp.rest.Request;
import org.json.JSONObject;

/**
 * 登录页面
 */

public class LoginActivity extends BaseNetWork3Activity {
    private EditText edPhone;
    private EditText edPassword;
    private String loginPhone;
    private String loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        initViews();
        //autoLogin();
        backFirstPhone();//传回注册成功的手机账号
        backLossPhone();//密码找回成功
    }

    private void autoLogin() {
        int uuid = AppConfig.getInstance().getInt("uuid",-1000);
        if (uuid != -1000) {//如果有登录数据，则自动跳转到主界面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void backLossPhone() {
        String loss_password = getIntent().getStringExtra("loss_password");
        if (loss_password != null) {
            Toast.makeText(this, "密码找回成功", Toast.LENGTH_SHORT).show();
            edPassword.setText("");
            AppConfig.getInstance().putString("login_password","");
        }
    }


    private void backFirstPhone() {
        String phone_number = getIntent().getStringExtra("phone_number");
        if (phone_number != null) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            edPhone.setText(phone_number);
            edPassword.requestFocusFromTouch();
        }
    }

    private void initViews() {
        edPhone = (EditText) findViewById(R.id.login_phone_ed);
        edPassword = (EditText) findViewById(R.id.login_password_ed);
        String login_phone = AppConfig.getInstance().getString("login_phone", "");
        if(!"".equals(login_phone)){
            edPhone.setText(login_phone);
            edPassword.requestFocusFromTouch();
        }
    }


    //点击登录按钮
    public void goMain(View view) {
        loginRequest();
    }


    //进入密码找回页面
    public void goForgetPassword(View view) {
        Intent intent = new Intent(this, PasswordLossActivity.class);
        startActivity(intent);
    }

    //进入注册页面
    public void goRegister(View view) {
        Intent intent = new Intent(this, RegisterInputNumberActivity.class);
        startActivity(intent);
    }
    private void loginRequest(){
        loginPhone = edPhone.getText().toString();
        loginPassword = edPassword.getText().toString();
        if (loginPhone.equals("") || loginPassword.equals("")) {
            Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Request<JSONObject> jsonRequest = buildNetRequest(UrlUtils.login, 0, false);
        jsonRequest.add("sign",UrlEncodeUtils.createSign(UrlUtils.login));
        jsonRequest.add("loginId",loginPhone);
        jsonRequest.add("password",loginPassword);
        executeNetWork(jsonRequest,"请稍后");
        setCallback(new Callback() {
            @Override
            public void handle200Data(JSONObject dataObj, String message) {

                int uuid = dataObj.optInt("uuid");
                String token = dataObj.optString("token");
                Long exTime = dataObj.optLong("expiring_time");

                AppConfig.getInstance().putInt("uuid",uuid);
                AppConfig.getInstance().putString("token",token);
                AppConfig.getInstance().putLong("expiring_time",exTime);
                AppConfig.getInstance().putString("login_phone",loginPhone);
                AppConfig.getInstance().putString("login_password","");

                setResult(RESULT_OK,new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void handle404(String message) {
                setResult(RESULT_CANCELED,getIntent());
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*setResult(RESULT_CANCELED,getIntent());
        finish();*/
    }
}
