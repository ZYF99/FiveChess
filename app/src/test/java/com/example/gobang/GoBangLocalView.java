package com.example.gobang;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class GoBangLocalView extends View {
    public GoBangLocalView(Context context) {
        this(context, null);
    }

    public GoBangLocalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoBangLocalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
