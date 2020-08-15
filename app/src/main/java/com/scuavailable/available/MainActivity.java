package com.scuavailable.available;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.scuavailable.available.util.CameraHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTestHere";
    private OpencvJni openCvJni;
    private CameraHelper cameraHelper;
    int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    // View Component
    ImageButton mSettingIb,mScanIb;
    TextView mTimeTv,mTimenameTv,mDateTv,mLocationTv,mWeatherTv;
    ImageView mWeathericonIv,mScrollupIv;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        openCvJni = new OpencvJni();
        initDrawer();
        initViews();
    }

    private void initDrawer() {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("123");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("12321321");

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName("enheng")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Log.i(TAG,"in drawer");
                        return true;
                    }
                })
                .build();
        
    }

    private void initViews() {
        mSettingIb = findViewById(R.id.ib_main_setting);
        mScanIb = findViewById(R.id.ib_main_scan);
        mTimeTv = findViewById(R.id.tv_main_time);
        mTimenameTv = findViewById(R.id.tv_main_timename);
        mDateTv = findViewById(R.id.tv_main_date);
        mLocationTv = findViewById(R.id.tv_main_location);
        mWeatherTv = findViewById(R.id.tv_main_weather);
        mWeathericonIv = findViewById(R.id.iv_main_weathericon);
        mScrollupIv = findViewById(R.id.iv_main_scrollup);

        //设置上滑动画
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.main_scan_anim);
        mScrollupIv.startAnimation(animation);

    }


}
