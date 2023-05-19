package com.example.gobang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gobang.R;
import com.example.gobang.view.GoBangLocalView;
import com.example.gobang.view.ChessBoardView;

public class PVPLocalActivity extends AppCompatActivity implements ChessBoardView.BoardListener, View.OnClickListener {

    //五子棋UI
    private GoBangLocalView fiveChessLocalView;
    //显示得分
    private TextView userAScoreTv, userBScoreTv;
    //显示玩家/ai执子
    private ImageView userAChessIv, userBChessIv;
    //玩家/ai回合标识
    private ImageView userATimeIv, userBTimeIv;
    //平局数
    private int draw_game = 0;

    //PopUpWindow选择玩家执子
    private PopupWindow chooseChess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_pvp_lcoal);
        //初始化控件
        initView();
        //view加载完成
        fiveChessLocalView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            //初始化PopupWindow
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            initPop(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
        });
    }

    private void initView() {
        //五子棋UI
        fiveChessLocalView = findViewById(R.id.GobangLocal_view);
        fiveChessLocalView.setCallBack(this);
        //显示用户得分
        userAScoreTv = findViewById(R.id.userA_score_tv);
        userBScoreTv = findViewById(R.id.userB_score_tv);
        //显示玩家执子
        userAChessIv = findViewById(R.id.userA_chess_iv);
        userBChessIv = findViewById(R.id.userB_chess_iv);
        //玩家回合标识
        userATimeIv = findViewById(R.id.userA_think_iv);
        userBTimeIv = findViewById(R.id.userB_think_iv);
        //重开游戏设置点击事件
        findViewById(R.id.restart_game).setOnClickListener(this);
        //返回主页设置点击事件
        findViewById(R.id.btn_local_back).setOnClickListener(this);
    }


    //初始化PopupWindow
    private void initPop(int width, int height) {
        if (chooseChess == null) {
            View view = View.inflate(this, R.layout.pop_choose_chess, null);
            ImageButton white = view.findViewById(R.id.choose_white);
            ImageButton black = view.findViewById(R.id.choose_black);
            white.setOnClickListener(this);
            black.setOnClickListener(this);
            chooseChess = new PopupWindow(view, width, height);
            chooseChess.setOutsideTouchable(false);
            chooseChess.showAtLocation(fiveChessLocalView, Gravity.CENTER, 0, 0);
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
            case GoBangLocalView.BLACK_WIN:
                showToast("黑棋胜利！");
                break;
            case GoBangLocalView.NO_WIN:
                showToast("平局！");
                draw_game++;
                break;
            case GoBangLocalView.WHITE_WIN:
                showToast("白棋胜利！");
                break;
        }
    }

    //更新游戏胜利局数
    private void updateWinInfo() {
        userAScoreTv.setText("分数 : " + fiveChessLocalView.getUserAScore() + " ");
        userBScoreTv.setText("分数 : " + fiveChessLocalView.getUserBScore() + " ");
    }


    @Override
    public void onChangeGamer(boolean isWhite) {
        if (fiveChessLocalView.getUserAChess() == 1) {
            if (isWhite) {
                userBTimeIv.setVisibility(View.VISIBLE);
                userATimeIv.setVisibility(View.GONE);
                fiveChessLocalView.postInvalidate();
            } else {
                userBTimeIv.setVisibility(View.GONE);
                userATimeIv.setVisibility(View.VISIBLE);
                fiveChessLocalView.postInvalidate();
            }
        } else {
            if (isWhite) {
                userBTimeIv.setVisibility(View.GONE);
                userATimeIv.setVisibility(View.VISIBLE);
                fiveChessLocalView.postInvalidate();
            } else {
                userBTimeIv.setVisibility(View.VISIBLE);
                userATimeIv.setVisibility(View.GONE);
                fiveChessLocalView.postInvalidate();
            }
        }
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart_game:
                if (!fiveChessLocalView.GameResult()) {
                    showAlterDialog_reset();
                } else {
                    //显示PopupWindow
                    chooseChess.showAtLocation(fiveChessLocalView, Gravity.CENTER, 0, 0);
                    //重新开始游戏
                    fiveChessLocalView.resetGame();
                }
                break;
            case R.id.btn_local_back:
                if (!fiveChessLocalView.GameResult()) {
                    showAlterDialog_back();
                } else {
                    Intent intent = new Intent(PVPLocalActivity.this, MainActivity.class);
                    startActivity(intent);
                    PVPLocalActivity.this.finish();
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
    private void changeUI(boolean isUserAWhite) {
        if (isUserAWhite) {
            //玩家选择白棋
            fiveChessLocalView.setUserAChess(GoBangLocalView.WHITE_CHESS);
            fiveChessLocalView.setUserBChess(GoBangLocalView.BLACK_CHESS);
            //玩家先手
            fiveChessLocalView.setUserABout(true);
            //更改当前落子
            userAChessIv.setBackgroundResource(R.drawable.white_chess);
            userBChessIv.setBackgroundResource(R.drawable.black_chess);
            userBTimeIv.setVisibility(View.GONE);
            userATimeIv.setVisibility(View.VISIBLE);
        } else {
            //玩家选择黑棋
            fiveChessLocalView.setUserAChess(GoBangLocalView.BLACK_CHESS);
            fiveChessLocalView.setUserBChess(GoBangLocalView.WHITE_CHESS);
            fiveChessLocalView.setUserABout(false);
            //更改当前落子
            userAChessIv.setBackgroundResource(R.drawable.black_chess);
            userBChessIv.setBackgroundResource(R.drawable.white_chess);
            userBTimeIv.setVisibility(View.VISIBLE);
            userATimeIv.setVisibility(View.GONE);
        }
    }


    private void showAlterDialog_reset() {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(PVPLocalActivity.this);
        alterDiaglog.setIcon(R.drawable.icon);//图标
        alterDiaglog.setTitle("提示：");//文字
        alterDiaglog.setMessage("游戏尚未结束，是否选择重新开始？");//提示消息
        //积极的选择
        alterDiaglog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //显示PopupWindow
                chooseChess.showAtLocation(fiveChessLocalView, Gravity.CENTER, 0, 0);
                //重新开始游戏
                fiveChessLocalView.resetGame();
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


    private void showAlterDialog_back() {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(PVPLocalActivity.this);
        alterDiaglog.setIcon(R.drawable.icon);//图标
        alterDiaglog.setTitle("提示：");//文字
        alterDiaglog.setMessage("游戏尚未结束，是否退出对局？");//提示消息
        //积极的选择
        alterDiaglog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PVPLocalActivity.this, MainActivity.class);
                startActivity(intent);
                PVPLocalActivity.this.finish();
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


