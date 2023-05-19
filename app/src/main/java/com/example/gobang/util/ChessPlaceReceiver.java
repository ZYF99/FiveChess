package com.example.gobang.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ChessPlaceReceiver extends BroadcastReceiver {
    static String place = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        place = intent.getStringExtra("CP_cp");
        Log.i("test","广播接受者CP："+place);
    }
    public static String getChessPlace() {
        return place;
    }
}