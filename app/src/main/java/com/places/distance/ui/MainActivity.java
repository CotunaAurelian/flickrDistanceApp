package com.places.distance.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.places.distance.Constants;
import com.places.distance.R;
import com.places.distance.domain.TrackingService;
import com.places.distance.presenter.MainPresenter;
import com.places.distance.presenter.MainView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Activity displaying a list of images, based on the distance the user walked
 */
public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    /**
     * Handles the permission needed for the app to work and be able to track the users movement
     */
    private RxPermissions mRxPermissions;

    private static int REQUEST_CODE = 1;

    @BindView(R.id.images_recycler_view)
    RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private BroadcastReceiver mTrackingStatusReceiver;
    private BroadcastReceiver mTrackingDataSetUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRxPermissions = new RxPermissions(this);

        // We use this to improve performance, because no other changes will be made on the layout size of
        // the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ImagesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mTrackingStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                invalidateOptionsMenu();
            }
        };

        mTrackingDataSetUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mAdapter != null){
                    ((ImagesAdapter)mAdapter).setData(TrackingService.getImageData());
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mTrackingDataSetUpdateReceiver, new IntentFilter(Constants.FILTER_TRACKING_IMAGESET_UPDATED));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mTrackingStatusReceiver, new IntentFilter(Constants.FILTER_TRACKING_STARTED));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mTrackingStatusReceiver, new IntentFilter(Constants.FILTER_TRACKING_STOPPED));

        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mTrackingStatusReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mTrackingStatusReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mTrackingDataSetUpdateReceiver);
    }

    @Override
    MainPresenter onCreatePresenter() {
        return new MainPresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        refreshMenuItem(menu.getItem(0));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        refreshMenuItem(item);
        if (item.getItemId() == R.id.action_start_stop) {
            mPresenter.handleStartStopTracking();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * If the menu item is start/stop action, handle the title changexz on it.
     * Otherwise just ignore it
     */
    private void refreshMenuItem(MenuItem item) {
        if (item.getItemId() == R.id.action_start_stop) {
            if (TrackingService.isRunning()) {
                item.setTitle(getResources().getString(R.string.lbl_stop));
            } else {
                item.setTitle(getResources().getString(R.string.lbl_start));
            }
        }
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void startLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(loginIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mPresenter.loginSuccessful();
        }
    }

    @Override
    public void handleTrackingStopped() {
        stopService(new Intent(MainActivity.this, TrackingService.class));
        invalidateOptionsMenu();
    }

    @Override
    public void handleTrackingStarted() {
        startService(new Intent(MainActivity.this, TrackingService.class));
        invalidateOptionsMenu();
    }

    @Override
    public void displayAgreementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.msg_tracking));
        builder.setPositiveButton(getResources().getString(R.string.lbl_agree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.userAgreeTracking();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.lbl_disagree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.userDisagreeTracking();
            }
        });
        builder.show();
    }

    @Override
    public boolean checkPermission(String permission) {
        return mRxPermissions.isGranted(permission);
    }

    @Override
    public void requestPermission(Consumer<Boolean> listener, String... permissions) {
        mRxPermissions.request(permissions)
                .subscribe(listener);
    }
}
