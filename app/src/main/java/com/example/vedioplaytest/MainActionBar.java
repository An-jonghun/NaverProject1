package com.example.vedioplaytest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

public class MainActionBar extends AppCompatActivity {

    private Activity activity;
    private ActionBar actionBar;

    public MainActionBar(Activity act, ActionBar actBar) {
        this.activity = act;
        this.actionBar = actBar;
    }

    public void setActionBar(int layoutName) {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        View mActionView = LayoutInflater.from(activity).inflate(layoutName, null);

        actionBar.setCustomView(mActionView);

        Toolbar parent = (Toolbar) mActionView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mActionView, params);
    }
}
