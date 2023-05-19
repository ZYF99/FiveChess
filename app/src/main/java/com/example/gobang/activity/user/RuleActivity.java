package com.example.gobang.activity.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gobang.R;

public class RuleActivity extends AppCompatActivity {

    private TextView et_rule;
    private TextView tv_rule_page;
    private ImageView iv_rule_image;
    private ImageView btn_rule_left, btn_rule_right;
    private Button btn_rule_back;
    String rule1 = "        1、对局双方各执一色棋子。";
    String rule2 = "        2、空棋盘开局。";
    String rule3 = "        3、白子先手、黑子后手，交替下子，每次只能下一子。";
    String rule4 = "        4、白方的第一枚棋子可下在棋盘任意交叉点上。";
    String rule5 = "        5、棋子下在棋盘的空白点上，棋子下定后，不得向其它点移动，不得从棋盘上拿掉或拿起另落别处。";
    String rule6 = "        6、轮流下子是双方的权利，但允许任何一方放弃下子权，先形成5子连线者获胜。";
    private int x = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        initView();
        et_rule.setText(rule1);
        tv_rule_page.setText("1/6");
        iv_rule_image.setBackgroundResource(R.drawable.rule_image1);
        btn_rule_left.setOnClickListener(view -> {
            if (x > 1 && x <= 6) {
                switch (x) {
                    case 2:
                        et_rule.setText(rule1);
                        tv_rule_page.setText("1/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image1);
                        x--;
                        break;
                    case 3:
                        et_rule.setText(rule2);
                        tv_rule_page.setText("2/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image2);
                        x--;
                        break;
                    case 4:
                        et_rule.setText(rule3);
                        tv_rule_page.setText("3/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image3);
                        x--;
                        break;
                    case 5:
                        et_rule.setText(rule4);
                        tv_rule_page.setText("4/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image4);
                        x--;
                        break;
                    case 6:
                        et_rule.setText(rule5);
                        tv_rule_page.setText("5/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image5);
                        x--;
                        break;
                }
            }
        });
        btn_rule_right.setOnClickListener(view -> {
            if (x >= 1 && x < 6) {
                switch (x) {
                    case 1:
                        et_rule.setText(rule2);
                        tv_rule_page.setText("2/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image2);
                        x++;
                        break;
                    case 2:
                        et_rule.setText(rule3);
                        tv_rule_page.setText("3/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image3);
                        x++;
                        break;
                    case 3:
                        et_rule.setText(rule4);
                        tv_rule_page.setText("4/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image4);
                        x++;
                        break;
                    case 4:
                        et_rule.setText(rule5);
                        tv_rule_page.setText("5/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image5);
                        x++;
                        break;
                    case 5:
                        et_rule.setText(rule6);
                        tv_rule_page.setText("6/6");
                        iv_rule_image.setBackgroundResource(R.drawable.rule_image6);
                        x++;
                        break;
                }
            }
        });
        btn_rule_back.setOnClickListener(view -> finish());
    }

    //初始化
    private void initView() {
        iv_rule_image = findViewById(R.id.iv_rule_image);
        et_rule = findViewById(R.id.et_rule);
        btn_rule_left = findViewById(R.id.btn_rule_left);
        btn_rule_right = findViewById(R.id.btn_rule_right);
        tv_rule_page = findViewById(R.id.tv_rule_page);
        btn_rule_back = findViewById(R.id.btn_back);
    }
}

