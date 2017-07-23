package com.places.distance.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.places.distance.presenter.BasePresenter;


/**
 * Base activity class for all the activities in the application
 * Created by Aurelian Cotuna
 */

public abstract class BaseActivity<T extends BasePresenter>  extends AppCompatActivity {

    T mPresenter;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mPresenter = onCreatePresenter();

    }

    abstract T onCreatePresenter();
}
