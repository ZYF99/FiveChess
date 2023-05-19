package com.example.gobang.activity.user;


import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gobang.R;
import com.example.gobang.activity.MainActivity;


public class WelcomeActivity extends AppCompatActivity {

    private ImageView tv_welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        tv_welcome = findViewById(R.id.tv_welcome);

        final ScaleAnimation animation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(3000);
        tv_welcome.setAnimation(animation);
        animation.startNow();

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                animation.cancel();
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }
}

