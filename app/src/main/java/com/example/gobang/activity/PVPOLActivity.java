package com.example.gobang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gobang.R;
import com.example.gobang.SocketClient;
import com.example.gobang.view.ChessBoardView;

public class PVPOLActivity extends AppCompatActivity implements ChessBoardView.BoardListener, View.OnClickListener {

    //五子棋UI
    private ChessBoardView fiveChessOnlineView;
    //显示得分
    private TextView userAScoreTv, userBScoreTv;
    //显示玩家/ai执子
    private ImageView userAChessIv, userBChessIv;
    //玩家/ai回合标识
    private ImageView userATimeIv, userBTimeIv;

    //PopUpWindow选择玩家执子
    //private PopupWindow chooseChess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_pvp_ol);
        //初始化控件
        initView();
        initSocket();
    }

    private void initView() {
        //五子棋UI
        fiveChessOnlineView = findViewById(R.id.GobangOnline_view);
        fiveChessOnlineView.setFocusable(true);
        fiveChessOnlineView.setBoardListener(this);
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

    //初始化Socket客户端
    private void initSocket() {
        SocketClient.getInstance().setServerMessageListener(new SocketClient.ServerMessageListener() {
            @Override
            public void onOpponentChessDownMessage(String str) {
                //对手下棋
                Log.i("Actestol", "对家下棋：" + str);
                fiveChessOnlineView.drawOpponentChess(str);
            }

            @Override
            public void onChessColorMessage(int myChessColor) {
                String chessColor = "";
                if (myChessColor == ChessBoardView.BLACK_CHESS) {
                    chessColor = "黑色";
                } else {
                    chessColor = "白色";
                }
                fiveChessOnlineView.initMyChessColor(myChessColor);
                showToast("您的棋子颜色为" + chessColor);
            }
        });
        fiveChessOnlineView.setBoardListener(this);
    }

    @Override
    public void onMyChessDown(int x, int y) {
        //自己下棋
        SocketClient.getInstance().downChess(x, y, ChessBoardView.WHITE_CHESS);
    }

    @Override
    public void onGameOver(int winner) {
        //更新游戏胜利局数
        updateWinInfo();
        switch (winner) {
            case ChessBoardView.BLACK_WIN:
                showToast("黑棋胜利！");
                break;
            case ChessBoardView.NO_WIN:
                showToast("平局！");
                break;
            case ChessBoardView.WHITE_WIN:
                showToast("白棋胜利！");
                break;
        }
        //SocketClient.getInstance().release();
    }

    //更新游戏胜利局数
    private void updateWinInfo() {
        userAScoreTv.setText("分数 : " + fiveChessOnlineView.getUserAScore() + " ");
        userBScoreTv.setText("分数 : " + fiveChessOnlineView.getUserBScore() + " ");
    }

    @Override
    public void onChangeGamer(boolean isWhite) {
        userBTimeIv.setVisibility(View.VISIBLE);
        userATimeIv.setVisibility(View.GONE);
        fiveChessOnlineView.postInvalidate();
    }

    private void showToast(String str) {
        runOnUiThread(() -> Toast.makeText(PVPOLActivity.this, str, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart_game:

                if (!fiveChessOnlineView.GameResult()) {
                    showResetDialog();
                } else {
                    //initSocket();
                    fiveChessOnlineView.resetBoard();
                }
                break;
            case R.id.btn_local_back:
                if (!fiveChessOnlineView.GameResult()) {
                    showBackDialog();
                } else {
                    Intent intent = new Intent(PVPOLActivity.this, MainActivity.class);
                    startActivity(intent);
                    PVPOLActivity.this.finish();
                }
        }
    }

    //根据玩家选择执子，更新UI
    private void changeUI(boolean isUserAWhite) {
        if (isUserAWhite) {
            //玩家选择白棋
            fiveChessOnlineView.setUserAChess(ChessBoardView.WHITE_CHESS);
            fiveChessOnlineView.setUserBChess(ChessBoardView.BLACK_CHESS);
            //玩家先手
            fiveChessOnlineView.setMyBout(true);
            //更改当前落子
            userAChessIv.setBackgroundResource(R.drawable.white_chess);
            userBChessIv.setBackgroundResource(R.drawable.black_chess);
            userBTimeIv.setVisibility(View.GONE);
            userATimeIv.setVisibility(View.VISIBLE);
        } else {
            //玩家选择黑棋
            fiveChessOnlineView.setUserAChess(ChessBoardView.BLACK_CHESS);
            fiveChessOnlineView.setUserBChess(ChessBoardView.WHITE_CHESS);
            fiveChessOnlineView.setMyBout(false);
            //更改当前落子
            userAChessIv.setBackgroundResource(R.drawable.black_chess);
            userBChessIv.setBackgroundResource(R.drawable.white_chess);
            userBTimeIv.setVisibility(View.VISIBLE);
            userATimeIv.setVisibility(View.GONE);
        }
    }

    private void showResetDialog() {
        new AlertDialog.Builder(PVPOLActivity.this)
                .setIcon(R.drawable.icon)//图标
                .setTitle("提示：")//文字
                .setMessage("游戏尚未结束，是否选择重新开始？")
                .setNegativeButton("确定", (dialog, which) -> {
                    //显示PopupWindow
                    //chooseChess.showAtLocation(fiveChessOnlineView, Gravity.CENTER, 0, 0);
                    //重新开始游戏
                    fiveChessOnlineView.resetBoard();
                })
                .setPositiveButton("取消", (dialog, which) -> {

                })
                .show();
    }

    private void showBackDialog() {
        new AlertDialog.Builder(PVPOLActivity.this)
                .setIcon(R.drawable.icon)//图标
                .setTitle("提示：")//文字
                .setMessage("游戏尚未结束，是否退出对局？")
                .setNegativeButton("确定", (dialog, which) -> {
                    finish();
                })
                .setPositiveButton("取消", (dialog, which) -> {

                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketClient.getInstance().release();
    }
}



