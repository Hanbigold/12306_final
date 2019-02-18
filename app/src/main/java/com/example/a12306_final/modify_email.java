package com.example.a12306_final;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a12306_final.javabean._User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class modify_email extends AppCompatActivity {
    private String User_name;
    private String objectId;
    private EditText modify_email_edit,Verification_Code_email;
    private Button Get_Verification_Code_email,submit_email;
    private Boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_email);
        Bmob.initialize(this,"22488e6103913f621999fda39c18cdec");
        modify_email_edit=(EditText)findViewById(R.id.modify_email);
        Verification_Code_email=(EditText)findViewById(R.id.Verification_Code_email);
        Get_Verification_Code_email=(Button)findViewById(R.id.Get_Verification_Code_email);
        submit_email=(Button)findViewById(R.id.submit_email);

        final _User muser=_User.getCurrentUser(_User.class);
        if(muser!=null){
            objectId= (String) BmobUser.getObjectByKey("objectId");
        }
        else {
            Intent intent = new Intent(modify_email.this,Login.class);
            startActivity(intent);
            finish();
        }
        //获取验证码
        Get_Verification_Code_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.requestSMSCode(muser.getMobilePhoneNumber(), "12306",new QueryListener<Integer>() {
                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        if(ex==null) {//验证码发送成功
                            Toast.makeText(getBaseContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getBaseContext(), "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        //确认
        submit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BmobSMS.verifySmsCode(muser.getMobilePhoneNumber(), Verification_Code_email.getText().toString(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            save();
                        }else {
                            Toast.makeText(getBaseContext(),"验证失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                SharedPreferences pref=getSharedPreferences("modify_password",MODE_PRIVATE);
                flag=pref.getBoolean("Yanzhengma",false);
                SharedPreferences.Editor editor=getSharedPreferences("modify_password",MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                if(isEmail(modify_email_edit.getText().toString())){
                    if (flag) {
                        _User newUser = new _User();
                        newUser.setEmail(modify_email_edit.getText().toString());
                        newUser.update(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(getBaseContext(), "邮箱修改成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(modify_email.this,My12306.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), "邮箱修改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getBaseContext(),"邮箱不符合格式",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void save(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor=getSharedPreferences("modify_email",MODE_PRIVATE).edit();
                editor.putBoolean("Yanzhengma",true);
                editor.apply();
            }
        });
    }
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }
}
