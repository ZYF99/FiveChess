package com.example.gobang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gobang.R;
import com.example.gobang.ai.AI;
import com.example.gobang.ai.AICallBack;
import com.example.gobang.view.GoBangView;
import com.example.gobang.view.ChessBoardView;


public class PVEActivity extends AppCompatActivity implements ChessBoardView.BoardListener, AICallBack, View.OnClickListener {

    //五子棋UI
    private GoBangView fiveChessView;
    //显示用户以及ai得分
    private TextView userScoreTv, aiScoreTv;
    //显示玩家/ai执子
    private ImageView userChessIv, aiChessIv;
    //玩家/ai回合标识
    private ImageView userTimeIv, aiTimeIv;
    //游戏ai
    private AI ai; //ai1 ai2
    //平局数
    private int draw_game = 0;
    //PopUpWindow选择玩家执子
    private PopupWindow chooseChess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_pve);
        //初始化控件
        initView();
        //初始化ai
        ai = new AI(fiveChessView.getChessArray(), this);
        //view加载完成
        fiveChessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //初始化PopupWindow
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                initPop(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
            }
        });
    }

    private void initView() {
        //五子棋UI
        fiveChessView = findViewById(R.id.Gobang_view);
        fiveChessView.setBoardListener(this);
        //显示用户以及ai得分
        userScoreTv = (TextView) findViewById(R.id.user_score_tv);
        aiScoreTv = (TextView) findViewById(R.id.ai_score_tv);
        //显示玩家/ai执子
        userChessIv = (ImageView) findViewById(R.id.user_chess_iv);
        aiChessIv = (ImageView) findViewById(R.id.ai_chess_iv);
        //玩家/ai回合标识
        userTimeIv = (ImageView) findViewById(R.id.user_think_iv);
        aiTimeIv = (ImageView) findViewById(R.id.ai_think_iv);
        //重开游戏设置点击事件
        findViewById(R.id.restart_game).setOnClickListener(this);
        //返回主页设置点击事件
        findViewById(R.id.btn_pve_back).setOnClickListener(this);
    }


    //初始化PopupWindow
    private void initPop(int width, int height) {
        if (chooseChess == null) {
            View view = View.inflate(this, R.layout.pop_choose_chess, null);
            ImageButton white = (ImageButton) view.findViewById(R.id.choose_white);
            ImageButton black = (ImageButton) view.findViewById(R.id.choose_black);
            white.setOnClickListener(this);
            black.setOnClickListener(this);
            chooseChess = new PopupWindow(view, width, height);
            chooseChess.setOutsideTouchable(false);
            chooseChess.showAtLocation(fiveChessView, Gravity.CENTER, 0, 0);
        }
    }


    @Override
    public void onMyChessDown(int x, int y) {

    }

    @Override
    public void onGameOver(int winner) {
        //更新游戏胜利局数
        updateWinInfo();
        switch (winner) {
            case GoBangView.BLACK_WIN:
                showToast("黑棋胜利！");
                break;
            case GoBangView.NO_WIN:
                showToast("平局！");
                draw_game++;
                break;
            case GoBangView.WHITE_WIN:
                showToast("白棋胜利！");
                break;
        }
    }

    //更新游戏胜利局数
    private void updateWinInfo() {
        userScoreTv.setText("分数 : " + fiveChessView.getUserScore() + " ");
        aiScoreTv.setText("分数 : " + fiveChessView.getAiScore() + " ");
    }

    @Override
    public void onChangeGamer(boolean isWhite) {
        //ai回合
        ai.aiBout();
        //更改当前落子
        aiTimeIv.setVisibility(View.VISIBLE);
        userTimeIv.setVisibility(View.GONE);
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aiAtTheBell() {
        runOnUiThread(() -> {
            //更新UI
            if((!fiveChessView.GameResult()) && (!fiveChessView.getUserBout())){
                fiveChessView.postInvalidate();
            }
            //检查游戏是否结束
            fiveChessView.checkAiGameOver();
            //设置为玩家回合
            fiveChessView.setUserBout(true);
            //更改当前落子
            aiTimeIv.setVisibility(View.GONE);
            userTimeIv.setVisibility(View.VISIBLE);
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart_game:
                if(!fiveChessView.GameResult()) {
                    showAlterDialog_reset();
                }
                else{
                    //显示PopupWindow
                    chooseChess.showAtLocation(fiveChessView, Gravity.CENTER, 0, 0);
                    //重新开始游戏
                    fiveChessView.resetGame();
                }
                break;
            case R.id.btn_pve_back:
                if(!fiveChessView.GameResult()) {
                    showAlterDialog_back();
                }
                else{
                    Intent intent = new Intent(PVEActivity.this, MainActivity.class);
                    startActivity(intent);
                    PVEActivity.this.finish();
                }
                break;
            case R.id.choose_black:
                changeUI(false);
                chooseChess.dismiss();
                break;
            case R.id.choose_white:
                changeUI(true);
                chooseChess.dismiss();
                break;
        }
    }

    //根据玩家选择执子，更新UI
    private void changeUI(boolean isUserWhite) {
        if (isUserWhite) {
            //玩家选择白棋
            fiveChessView.setUserChess(GoBangView.WHITE_CHESS);
            ai.setAiChess(GoBangView.BLACK_CHESS);
            //玩家先手
            fiveChessView.setUserBout(true);
            //更改当前落子
            userChessIv.setBackgroundResource(R.drawable.white_chess);
            aiChessIv.setBackgroundResource(R.drawable.black_chess);
            aiTimeIv.setVisibility(View.GONE);
            userTimeIv.setVisibility(View.VISIBLE);
        } else {
            //玩家选择黑棋
            fiveChessView.setUserChess(GoBangView.BLACK_CHESS);
            fiveChessView.setUserBout(false);
            //ai先手
            ai.setAiChess(GoBangView.WHITE_CHESS);
            ai.aiBout();
            //更改当前落子
            userChessIv.setBackgroundResource(R.drawable.black_chess);
            aiChessIv.setBackgroundResource(R.drawable.white_chess);
            aiTimeIv.setVisibility(View.VISIBLE);
            userTimeIv.setVisibility(View.GONE);
        }
    }



    private void showAlterDialog_reset(){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(PVEActivity.this);
        alterDiaglog.setIcon(R.drawable.icon);//图标
        alterDiaglog.setTitle("提示：");//文字
        alterDiaglog.setMessage("游戏尚未结束，是否选择重新开始？");//提示消息
        //积极的选择
        alterDiaglog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //显示PopupWindow
                chooseChess.showAtLocation(fiveChessView, Gravity.CENTER, 0, 0);
                //重新开始游戏
                fiveChessView.resetGame();
            }
        });
        //消极的选择
        alterDiaglog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alterDiaglog.show();
    }


    private void showAlterDialog_back(){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(PVEActivity.this);
        alterDiaglog.setIcon(R.drawable.icon);//图标
        alterDiaglog.setTitle("提示：");//文字
        alterDiaglog.setMessage("游戏尚未结束，是否退出对局？");//提示消息
        //积极的选择
        alterDiaglog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PVEActivity.this, MainActivity.class);
                startActivity(intent);
                PVEActivity.this.finish();
            }
        });
        //消极的选择
        alterDiaglog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alterDiaglog.show();
    }


}


