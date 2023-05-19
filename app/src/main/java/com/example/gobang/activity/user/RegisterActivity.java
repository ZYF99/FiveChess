package com.example.gobang.activity.user;

import android.content.ContentValues;
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

public class RegisterActivity extends AppCompatActivity {

    private TextView tv_AhAbtL;
    private Button btn_register, btn_register_back;
    private EditText et_account,et_password,et_passwordConfirm;

    DBOpenHelper myDBOpenHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        myDBOpenHelper = new DBOpenHelper(this, null);

        btn_register.setOnClickListener(view ->  {
            String account = et_account.getText().toString();
            String password = et_password.getText().toString();
            String passwordConfirm = et_passwordConfirm.getText().toString();
            if (TextUtils.isEmpty(account)){
                Toast.makeText(RegisterActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(passwordConfirm)){
                Toast.makeText(RegisterActivity.this,"确认密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(password,passwordConfirm)){
                Toast.makeText(RegisterActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                return;
            }
            SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
            Cursor c = db.query("User_info",null,"account=?",new String[]{account},null,null,null);
            if(c!=null && c.getCount() >= 1){
                Toast.makeText(this, "该用户已存在", Toast.LENGTH_SHORT).show();
                c.close();
            }
            else{
                ContentValues values= new ContentValues();
                values.put("account",account);
                values.put("password",password);
                long rowid = db.insert("User_info",null,values);
                Toast.makeText(this, "注册成功，3秒后回到登录界面。。。", Toast.LENGTH_LONG).show();//提示信息
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toLogin(view);
                    }
                }, 3000);
            }
            db.close();
        });


        tv_AhAbtL.setOnClickListener(view -> {
            toLogin(view);
        });

        btn_register_back.setOnClickListener(view -> {
            toMain(view);
        });

    }

    //初始化
    private void initView(){
        btn_register = findViewById(R.id.btn_register);
        btn_register_back = findViewById(R.id.btn_register_back);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);
        et_passwordConfirm = findViewById(R.id.et_passwordConfirm);
        tv_AhAbtL = findViewById(R.id.tv_AhAbtL);
    }

    public void toMain(View view) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        RegisterActivity.this.finish();
    }


    public void toLogin(View view) {
        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        RegisterActivity.this.finish();
    }

}
