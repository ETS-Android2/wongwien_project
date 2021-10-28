package com.example.wongwien;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class EdittextV2 extends androidx.appcompat.widget.AppCompatEditText {

    public EdittextV2(Context context) {
        super(context);
    }

    public EdittextV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EdittextV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EdittextV2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP )
            this.clearFocus();
        return super.onKeyPreIme(keyCode, event);
    }
}
