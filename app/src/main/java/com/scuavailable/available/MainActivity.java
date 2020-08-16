package com.scuavailable.available;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.scuavailable.available.util.CameraHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTestHere";
    private OpencvJni openCvJni;
    private CameraHelper cameraHelper;
    int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    //Drawer
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private IProfile profile;
    private PrimaryDrawerItem academicOfficeItem;
    private PrimaryDrawerItem historyRecordItem;
    private PrimaryDrawerItem helpItem;
    private PrimaryDrawerItem shareItem;
    private PrimaryDrawerItem aboutItem;
    private PrimaryDrawerItem exitItem;


    // View Component
    ImageButton mSettingIb,mScanIb;
    TextView mTimeTv,mTimenameTv,mDateTv,mLocationTv,mWeatherTv;
    ImageView mWeathericonIv,mScrollupIv;

    private Context mContext;
    private SharedPreferences user_pref;
    private boolean login_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        openCvJni = new OpencvJni();

        //判断是否登陆，根据登陆情况呈现drawer页面
        user_pref = getSharedPreferences("user_pref", MODE_PRIVATE);
        login_status = user_pref.getBoolean("login_status", false);
        initDrawer(savedInstanceState);
        initViews();

    }

    private void initDrawer(Bundle savedInstanceState) {
//        用户信息栏
        // Create the AccountHeader
        buildHeader(false, savedInstanceState);
        academicOfficeItem =  new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_academic).withDescription(R.string.drawer_item_academic_desp).withIcon(getResources().getDrawable(R.drawable.ic_academic)).withDescriptionTextColorRes(R.color.drawer_item_desp_color).withSelectable(false);
        historyRecordItem =  new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_history).withDescription(R.string.drawer_item_history_desp).withIcon(getResources().getDrawable(R.drawable.ic_history)).withDescriptionTextColorRes(R.color.drawer_item_desp_color).withSelectable(false);
        helpItem =  new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_help).withDescription(R.string.drawer_item_help_desp).withIcon(getResources().getDrawable(R.drawable.ic_help)).withDescriptionTextColorRes(R.color.drawer_item_desp_color).withSelectable(false);
        shareItem =  new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_share).withDescription(R.string.drawer_item_share_desp).withIcon(getResources().getDrawable(R.drawable.ic_share)).withDescriptionTextColorRes(R.color.drawer_item_desp_color).withSelectable(false);
        aboutItem =  new PrimaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_about).withDescription(R.string.drawer_item_about_desp).withIcon(getResources().getDrawable(R.drawable.ic_about)).withDescriptionTextColorRes(R.color.drawer_item_desp_color).withSelectable(false);
        exitItem =  new PrimaryDrawerItem().withIdentifier(6).withName(R.string.drawer_item_exit).withDescription(R.string.drawer_item_exit_desp).withIcon(getResources().getDrawable(R.drawable.ic_exit)).withDescriptionTextColorRes(R.color.drawer_item_desp_color).withSelectable(false);


        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        academicOfficeItem,
                        historyRecordItem,
                        helpItem,
                        shareItem,
                        aboutItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Log.i(TAG,String.valueOf(drawerItem.getIdentifier()));
                        return true;
                    }
                })
                .addStickyDrawerItems(
                        exitItem
                )
                .withSelectedItem(-1)
                .build();
        if(login_status==false){
            result.removeAllStickyFooterItems();
        }
    }


    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        if(login_status == true){
            //获取数据赋值
            profile = new ProfileDrawerItem().withName("庄老师").withEmail("780891896@qq.com").withIcon(getResources().getDrawable(R.drawable.profile_icon)).withIdentifier(200);
        }else {
            profile = new ProfileDrawerItem().withName(R.string.drawer_info_nologin).withIcon(getResources().getDrawable(R.drawable.ic_icon_little)).withIdentifier(201);
        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCloseDrawerOnProfileListClick(true)
                .withHeaderBackground(R.drawable.bg_scu)
                .withCompactStyle(compact)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        profile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        Log.i(TAG,"Click");
                        if (profile.getIdentifier()==201) {
                            //跳转登陆注册页面

                        }
                        return false;
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

        //设置按键监听
        mSettingIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.openDrawer();
            }
        });
    }


}
