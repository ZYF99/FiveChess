package com.example.gobang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gobang.R;
import com.example.gobang.activity.user.LoginActivity;
import com.example.gobang.activity.user.RegisterActivity;
import com.example.gobang.activity.user.RuleActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStart, btnLogin, btnRegister, btnRule, btnSetting, btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.Button_start);
        btnLogin = findViewById(R.id.Button_Log);
        btnRegister = findViewById(R.id.Button_Register);
        btnRule = findViewById(R.id.Button_RULE);
        btnSetting = findViewById(R.id.Button_Setting);
        btnInfo = findViewById(R.id. Button_Info);

        btnStart.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PVPOLActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnRule.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RuleActivity.class);
            startActivity(intent);
        });

    }

}