package com.example.a12306_final;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class modify_password extends AppCompatActivity {
    private String User_name,password;
    private Button confirm,Get_Verification_Code;
    private EditText  newPassword,passwordConfirm,Edit_yanzhengma,oldPassword;
    private String objectId;
    private Boolean flag;
    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);
        Bmob.initialize(this,"22488e6103913f621999fda39c18cdec");
        final _User muser=_User.getCurrentUser(_User.class);
        newPassword=(EditText)findViewById(R.id.NewPassward);
        oldPassword = (EditText)findViewById(R.id.OldPassward);
        passwordConfirm=(EditText)findViewById(R.id.Password_confirmation);
        Edit_yanzhengma=(EditText)findViewById(R.id.Verification_Code);
        if(muser!=null){
            objectId= (String) BmobUser.getObjectByKey("objectId");
            phone_number = (String) BmobUser.getObjectByKey("mobilePhoneNumber");
        }
        else {
            Intent intent = new Intent(modify_password.this,Login.class);
            startActivity(intent);
            finish();
        }
        //获取验证码
        Get_Verification_Code=(Button)findViewById(R.id.Get_Verification_Code);
        Get_Verification_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.requestSMSCode(phone_number, "12306",new QueryListener<Integer>() {
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
        confirm=(Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.verifySmsCode(phone_number, Edit_yanzhengma.getText().toString(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            Toast.makeText(getBaseContext(),"验证成功",Toast.LENGTH_SHORT).show();
                            save(muser);
                        }else {

                            Toast.makeText(getBaseContext(),"验证失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void save(final _User mmuser){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String password = (String)BmobUser.getObjectByKey("password_config");
                if(newPassword.getText().toString().length()<6){
                    Toast.makeText(getBaseContext(),"密码长度小于6，请重新输入密码",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(oldPassword.getText().toString().equals(password)) {
                        if (newPassword.getText().toString().equals(passwordConfirm.getText().toString())) {
                            _User newUser = new _User();
                            newUser.setPassword_config(newPassword.getText().toString());
                            newUser.setPassword(newPassword.getText().toString());
                            newUser.update(objectId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getBaseContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
                                        mmuser.logOut();
                                        mmuser.getCurrentUser();
                                        Intent intent = new Intent(modify_password.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getBaseContext(), "密码修改失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getBaseContext(), "两次密码输入不相符", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getBaseContext(),"与原密码不符合",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
