package com.example.gobang.model;

public class Point {
    //在数组中位置
    private int x;
    private int y;
    //该点的优先级（AI根据优先级落子）
    private int priority = 0;

    public Point() {
        this.x = 0;
        this.y = 0;
    }


    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", priority=" + priority +
                '}';
    }

}

