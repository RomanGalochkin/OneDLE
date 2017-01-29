package com.yoggo.dleandroidclient.tools;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class SelectableMovementMethod extends LinkMovementMethod {
    @Override
    public boolean canSelectArbitrarily () {
        return true;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.setSelection(text, text.length());
    }

    @Override
    public void onTakeFocus(TextView view, Spannable text, int dir) {
       if ((dir & (View.FOCUS_FORWARD | View.FOCUS_DOWN)) != 0) {
           if (view.getLayout() == null) {
               Selection.setSelection(text, text.length());
           }
       } else {
           Selection.setSelection(text, text.length());
       }
    }
}