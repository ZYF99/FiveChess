package com.example.gobang.ai;


import android.util.Log;

import com.example.gobang.model.Point;
import com.example.gobang.view.GoBangView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 简单的AI
 */

public class AI implements Runnable {
    //棋盘信息
    private int[][] chessArray;
    //电脑执子（默认黑子）
    private int aiChess = GoBangView.BLACK_CHESS;
    //所有无子位置的信息集合
    private List<Point> pointList;
    //ai落子结束回调
    private AICallBack callBack;
    //棋盘宽高（panelLength）
    private int panelLength;

    public AI(int[][] chessArray, AICallBack callBack) {
        pointList = new ArrayList<>();
        this.chessArray = chessArray;
        this.callBack = callBack;
        this.panelLength = chessArray.length;
    }

    //ai开始落子
    public void aiBout() {
        new Thread(this).start();
    }

    //判断落子的优先级评分
    private void checkPriority(Point p) {
        int aiPriority = checkSelf(p.getX(), p.getY());
        Log.i("test", "广播接受者ai：" + aiPriority);
        int userPriority = checkUser(p.getX(), p.getY());
        Log.i("test", "广播接受者user：" + userPriority);
        p.setPriority(aiPriority >= userPriority ? aiPriority : userPriority);
    }

    //获取当前点，ai优先级评分
    private int checkSelf(int x, int y) {
        return getPriority(x, y, aiChess);
    }

    //获取当前点，玩家优先级评分
    private int checkUser(int x, int y) {
        int userChess;
        if (aiChess == GoBangView.WHITE_CHESS) {
            userChess = GoBangView.BLACK_CHESS;
        } else {
            userChess = GoBangView.WHITE_CHESS;
        }
        return getPriority(x, y, userChess);
    }

    //通过线程选择最佳落点
    @Override
    public void run() {
        //清空pointList，每次ai落子都会清空
        pointList.clear();
        int blankCount = 0;
        //将所有无子位置加入list
        for (int i = 0; i < panelLength; i++)
            for (int j = 0; j < panelLength; j++) {
                if (chessArray[i][j] == GoBangView.NO_CHESS) {
                    Point p = new Point(i, j);
                    checkPriority(p);
                    pointList.add(p);
                    blankCount++;
                }
            }
        //遍历pointList，找到优先级最高的Point
        Point max = pointList.get(0);
        for (Point point : pointList) {
            if (max.getPriority() < point.getPriority()) {
                max = point;
                Log.i("test", "广播接受者pppppp：" + max.getPriority());
            }
        }
        //AI先手或者用户先手第一次落子时
        if (blankCount >= panelLength * panelLength - 1) {
            max = getStartPoint();
        }
        //如果棋盘上所有棋子的优先级均小于等于1，则随机落点
        for (Point point : pointList) {
            if (max.getPriority() <= 1) {
                max = getStartPoint();
            }
        }
        //休眠2秒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //落子，并将结果回调
        chessArray[max.getX()][max.getY()] = aiChess;
        Log.i("test", "广播接受者ppppppmax：" + max.getPriority());
        callBack.aiAtTheBell();
    }

    public void setAiChess(int aiChess) {
        this.aiChess = aiChess;
    }

    //AI先手或者用户先手第一次落子时，随机获取一个点落子
    private Point getStartPoint() {
        //AI先手或者用户先手第一次落子时，随机获取一个点落子
        //该点是否可用标识
        boolean isUse = true;
        //在中间位置随机生成一个点
        Random random = new Random();
        int x = random.nextInt(15);
        int y = random.nextInt(15);
        //确保周围不存在其他棋子
        if (chessArray[x][y] != GoBangView.NO_CHESS) {
            isUse = false;
        }
        if (isUse) {
            return new Point(x, y);
        } else {
            return getStartPoint();
        }
    }

    /**
     * 判断指定点chessArray[x][y]横向优先级
     *
     * @param x 数组下标
     * @param y 数组下标
     * @return 该点优先级评分
     */
    private int getPriority(int x, int y, int chess) {
        if ((x >= 2 && x <= 12) && (y >= 2 && y <= 12)) {
            return calcPriority(x, y, chess);
        }
        else if((x == 1 || x== 13) && (y == 1 || y == 13)) {
            return calcotherPriority(x, y, chess);
        }
        else return 0;
    }


    /**
     * 根据相连数以及开始结束是否被堵住计算优先级评分
     *
     * @return 优先级评分
     */
    private int calcPriority(int x, int y, int chess) {
        //优先级评分
        int priority = 0;
        for (int i = x - 2; i <= x + 2; i++)
            for (int j = y - 2; j <= y + 2; j++) {
                if (chessArray[i][j] != chess) {
                    priority = priority + 1;
                } else {
                    priority = priority + 2;
                }
            }
        return priority;
    }

    private int calcotherPriority(int x, int y, int chess) {
        //优先级评分
        int priority = 0;
        for (int i = x - 1; i <= x + 1; i++)
            for (int j = y - 1; j <= y + 1; j++) {
                if (chessArray[i][j] != chess) {
                    priority = priority + 1;
                } else {
                    priority = priority + 2;
                }
            }
        return priority;
    }

}





