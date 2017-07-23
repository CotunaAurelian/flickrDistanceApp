package com.places.distance.presenter;

import android.Manifest;

import com.places.distance.domain.TrackingService;
import com.places.distance.ui.MainActivity;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Presenter handling all the business logic for {@link MainActivity}
 * Created by Aurelian Cotuna
 */

public class MainPresenter extends BasePresenter {

    private MainView mMainView;

    private boolean mUserAgreedTracking;

    public MainPresenter(MainView view) {
        this.mMainView = view;
        this.mUserAgreedTracking = false;
    }

    public void handleStartStopTracking() {
        if (!TrackingService.isRunning()) {
            mMainView.displayAgreementDialog();
        } else {
            mMainView.handleTrackingStopped();
        }
    }

    private void handleAcceptedTracking() {
        if (TrackingService.isRunning()) {
            mMainView.handleTrackingStopped();
        } else {
            mMainView.handleTrackingStarted();
        }
    }

    public void loginSuccessful(){
        if (mUserAgreedTracking) {
            handleAcceptedTracking();
        } else {
            userDisagreeTracking();
        }

        TrackingService.startLoadingImages(mMainView.getContext());
    }

    public void userAgreeTracking() {
        mMainView.requestPermission(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean accepted) throws Exception {
                mUserAgreedTracking = accepted;

                if (TrackingService.isAuthenticated(mMainView.getContext())) {
                    if (accepted) {
                        handleAcceptedTracking();
                    } else {
                        userDisagreeTracking();
                    }
                    TrackingService.startLoadingImages(mMainView.getContext());
                }else {
                    mMainView.startLogin();
                }


            }
        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void userDisagreeTracking() {
        mMainView.handleTrackingStopped();

    }


}
