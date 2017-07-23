package com.places.distance.presenter;

import android.content.Context;

import io.reactivex.functions.Consumer;


/**
 * Interface for all the callbacks between the Main Activity and it's presenter
 * Created by Aurelian Cotuna
 */

public interface MainView {

    Context getContext();

    void startLogin();

    void handleTrackingStopped();

    void handleTrackingStarted();

    void displayAgreementDialog();

    boolean checkPermission(String permission);

    void requestPermission(Consumer<Boolean> listener, String... permissions);

}
