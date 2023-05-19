package com.example.gobang.activity.user;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gobang.activity.MainActivity;
import com.example.gobang.util.DBOpenHelper;
import com.example.gobang.R;

public class LoginActivity extends AppCompatActivity {

    private TextView tv_NoAgoR;
    private Button btn_login_back, btn_log;
    private EditText et_account,et_password;

    DBOpenHelper myDBOpenHelper;

    String account;
    String password;
    private int FLAG = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        myDBOpenHelper = new DBOpenHelper(this, null);

        btn_log.setOnClickListener(view ->  {
                //判断账号密码是否符合要求
                account = et_account.getText().toString().trim();
                password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(account)){
                    Toast.makeText(LoginActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)){
                    SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
                    Cursor c = db.query("User_info",null,"account=? and password=?",new String[]{account,password},null,null,null);
                    if(c!=null && c.getCount() >= 1){
                        c.close();
                        db.close();
                            Toast.makeText(LoginActivity.this,"登录成功，3秒后回到主界面。。。",Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toMain(view);
                                }
                            }, 3000);

                    } else {
                        Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
        });


        tv_NoAgoR.setOnClickListener(view -> {
            toRegister(view);
        });

        btn_login_back.setOnClickListener(view -> {
            toMain(view);
        });
    }

    //初始化
    private void initView(){
        btn_log = findViewById(R.id.btn_log);
        btn_login_back = findViewById(R.id.btn_login_back);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);
        tv_NoAgoR = findViewById(R.id.tv_noAgoR);
    }



    public void toMain(View view) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }


    public void toRegister(View view) {
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }


}


