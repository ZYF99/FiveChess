package com.example.gobang.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.gobang.R;

public class ChessBoardView extends View implements View.OnTouchListener {

    //画笔
    private Paint paint;
    //棋子数组
    private int[][] chessArray;
    //当前下棋顺序(默认白棋先下)
    private boolean isWhite = true;
    //游戏是否结束
    private boolean isGameOver = false;

    //我的（本家）的棋子颜色
    private int myChessColor;
    //对家的棋子颜色
    private int opponentChessColor;

    //bitmap
    private Bitmap whiteChess;
    private Bitmap blackChess;
    //Rect矩形
    private Rect rect;
    //棋盘宽高
    private float len;
    //棋盘格数
    private int GRID_NUMBER = 15;
    //每格之间的距离
    private float preWidth;
    //边距
    private float offset;

    //回调
    private BoardListener boardListener;

    //当前黑白棋胜利次数
    private int whiteChessCount, blackChessCount;

    //是否是玩家A的回合
    private boolean isMyBout;

    //玩家执子颜色
    private int userAChess = WHITE_CHESS;
    private int userBChess = BLACK_CHESS;

    //玩家胜利次数
    private int userAScore = 0;
    private int userBScore = 0;

    /**
     * 一些常量
     */
    //白棋
    public static final int WHITE_CHESS = 1;
    //黑棋
    public static final int BLACK_CHESS = 2;
    //无棋
    public static final int NO_CHESS = 0;
    //白棋赢
    public static final int WHITE_WIN = 101;
    //黑棋赢
    public static final int BLACK_WIN = 102;
    //平局
    public static final int NO_WIN = 103;

    public ChessBoardView(Context context) {
        this(context, null);
    }

    public ChessBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化Paint
        paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        //初始化chessArray
        chessArray = new int[GRID_NUMBER][GRID_NUMBER];
        //初始化棋子图片bitmap
        whiteChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_chess);
        blackChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_chess);
        //初始化胜利局数
        whiteChessCount = 0;
        blackChessCount = 0;
        //初始化Rect
        rect = new Rect();
        //设置点击监听
        setOnTouchListener(this);
        //重置棋盘状态
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                chessArray[i][j] = 0;
            }
        }
    }

    public void initMyChessColor(int myChessColor) {
        this.myChessColor = myChessColor;
        if (myChessColor == WHITE_CHESS) {
            opponentChessColor = BLACK_CHESS;
            isMyBout = false;
        } else {
            opponentChessColor = WHITE_CHESS;
            isMyBout = true;
        }
    }

    /**
     * 重新测量宽高，确保宽高一样
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取高宽值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //获取宽高中较小的值
        int len = width > height ? height : width;
        //重新设置宽高
        setMeasuredDimension(len, len);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //棋盘为一个GRID_NUMBER*GRID_NUMBER的正方形，所有棋盘宽高必须一样
        len = getWidth() > getHeight() ? getHeight() : getWidth();
        preWidth = len / GRID_NUMBER;
        //边距
        offset = preWidth / 2;
        //棋盘线条
        for (int i = 0; i < GRID_NUMBER; i++) {
            //开始位置
            float start = i * preWidth + offset;
            //startX x轴起始坐标;startY y轴起始坐标;stopX	x轴终止坐标;stopY y轴终止坐标
            //横线
            canvas.drawLine(offset, start, len - offset, start, paint);
            //竖线
            canvas.drawLine(start, offset, start, len - offset, paint);
        }
        //绘制棋子
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                //rect中点坐标，带边距
                float rectX = offset + i * preWidth;
                float rectY = offset + j * preWidth;
                //设置rect位置
                rect.set((int) (rectX - offset), (int) (rectY - offset),
                        (int) (rectX + offset), (int) (rectY + offset));
                //遍历chessArray
                switch (chessArray[i][j]) {
                    case WHITE_CHESS:
                        //绘制白棋
                        canvas.drawBitmap(whiteChess, null, rect, paint);
                        break;
                    case BLACK_CHESS:
                        //绘制黑棋
                        canvas.drawBitmap(blackChess, null, rect, paint);
                        break;
                }
            }
        }
        //判断是否结束
        checkGameOver();
    }

    /**
     * 判断是否结束
     */
    private void checkGameOver() {
        //获取落子的颜色(如果当前是白棋先手，则落子是黑棋)
        int chess;
        //棋盘是否填满
        boolean isFull = true;
        //遍历chessArray
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                //判断棋盘是否填满
                if (chessArray[i][j] != BLACK_CHESS && chessArray[i][j] != WHITE_CHESS) {
                    isFull = false;
                }
                //只需要判断落子是否五连即可
                if (chessArray[i][j] == (chess = isWhite ? BLACK_CHESS : WHITE_CHESS) || chessArray[i][j] == (chess = isWhite ? WHITE_CHESS : BLACK_CHESS)) {
                    //判断五子相连
                    if (isFiveSame(i, j) && !isGameOver) {
                        //五子相连游戏结束
                        isGameOver = true;
                        if (boardListener != null) {
                            //判断黑白棋胜利
                            if (chess == WHITE_CHESS) {
                                whiteChessCount++;
                            } else {
                                blackChessCount++;
                            }
                            //判断哪位玩家胜利
                            if (userAChess == chess) {
                                userAScore++;
                            } else {
                                userBScore++;
                            }
                            boardListener.onGameOver(chess == WHITE_CHESS ? WHITE_WIN : BLACK_WIN);
                        }
                        return;
                    }
                }
            }
        }
        //如果棋盘填满，平局结束
        if (isFull) {
            isGameOver = true;
            if (boardListener != null) {
                boardListener.onGameOver(NO_WIN);
            }
        }
    }

    /**
     * 重置棋盘
     */
    public void resetBoard() {
        isGameOver = false;
        //重置棋盘状态
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                chessArray[i][j] = 0;
            }
        }
        //更新UI
        postInvalidate();
    }

    /**
     * 判断是否存在五子相连
     *
     * @return
     */
    private boolean isFiveSame(int x, int y) {
        //判断横向
        if (x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y] && chessArray[x][y] == chessArray[x + 2][y]
                    && chessArray[x][y] == chessArray[x + 3][y] && chessArray[x][y] == chessArray[x + 4][y]) {
                return true;
            }
        }
        //判断纵向
        if (y + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x][y + 1] && chessArray[x][y] == chessArray[x][y + 2]
                    && chessArray[x][y] == chessArray[x][y + 3] && chessArray[x][y] == chessArray[x][y + 4]) {
                return true;
            }
        }
        //判断斜向(左上到右下)
        if (y + 4 < GRID_NUMBER && x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y + 1] && chessArray[x][y] == chessArray[x + 2][y + 2]
                    && chessArray[x][y] == chessArray[x + 3][y + 3] && chessArray[x][y] == chessArray[x + 4][y + 4]) {
                return true;
            }
        }
        //判断斜向(左下到右上)
        if (y - 4 > 0 && x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y - 1] && chessArray[x][y] == chessArray[x + 2][y - 2]
                    && chessArray[x][y] == chessArray[x + 3][y - 3] && chessArray[x][y] == chessArray[x + 4][y - 4]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isMyBout) {
                    if (!isGameOver) {

                        //获取按下时的位置
                        float downX = event.getX();
                        float downY = event.getY();

                        //点击的位置在棋盘上
                        if (downX >= offset / 2 && downX <= len - offset / 2 && downY >= offset / 2 && downY <= len - offset / 2) {

                            //获取棋子对应的位置
                            int x = (int) (downX / preWidth);
                            int y = (int) (downY / preWidth);

                            //判断当前位置是否已经有子
                            if (chessArray[x][y] != WHITE_CHESS && chessArray[x][y] != BLACK_CHESS) {
                                Log.i("TEST ONLINE", "本家棋子位置：" + x + "/" + "y");
                                boardListener.onMyChessDown(x, y);
                                setChess(x, y, myChessColor);
                                isWhite = false;
                                //修改当前为玩家B执子
                                isMyBout = false;
                                //更新棋盘
                                postInvalidate();
                                //回调当前执子
                                if (boardListener != null) {
                                    boardListener.onChangeGamer(isWhite);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "游戏已经结束，请重新开始！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return false;
    }

    //画对家的棋子
    public void drawOpponentChess(String info) {
        String[] temp;
        temp = info.split("/");// 分割字符串 [x,y,color]
        int x = Integer.parseInt(temp[0]);
        int y = Integer.parseInt(temp[1]);
        int color = Integer.parseInt(temp[2]);
        setChess(x, y, opponentChessColor);
        postInvalidate();
        isMyBout = true;
    }

    public interface BoardListener {

        //落子
        void onMyChessDown(int x, int y);

        //游戏结束回调
        void onGameOver(int winner);

        //游戏更换执子回调
        void onChangeGamer(boolean isWhite);
    }

    public void setBoardListener(BoardListener boardListener) {
        this.boardListener = boardListener;
    }

    private void setChess(int x, int y, int color) {
        chessArray[x][y] = color;
    }

    public boolean GameResult() {
        return isGameOver;
    }

    public void setMyBout(boolean myBout) {
        isMyBout = myBout;
    }

    public int getUserAChess() {
        return userAChess;
    }

    public void setUserAChess(int userAChess) {
        this.userAChess = userAChess;
    }

    public void setUserBChess(int userBChess) {
        this.userBChess = userBChess;
    }

    public int getUserAScore() {
        return userAScore;
    }

    public int getUserBScore() {
        return userBScore;
    }

}

