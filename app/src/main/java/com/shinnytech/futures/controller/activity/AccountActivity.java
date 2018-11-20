package com.shinnytech.futures.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;

import com.shinnytech.futures.R;
import com.shinnytech.futures.application.BaseApplication;
import com.shinnytech.futures.databinding.ActivityAccountBinding;
import com.shinnytech.futures.model.bean.accountinfobean.AccountEntity;
import com.shinnytech.futures.model.bean.accountinfobean.UserEntity;
import com.shinnytech.futures.model.engine.DataManager;

import static com.shinnytech.futures.constants.CommonConstants.ACCOUNT;
import static com.shinnytech.futures.constants.CommonConstants.ACTIVITY_TYPE;
import static com.shinnytech.futures.constants.CommonConstants.TD_MESSAGE;
import static com.shinnytech.futures.model.receiver.NetworkReceiver.NETWORK_STATE;
import static com.shinnytech.futures.model.service.WebSocketService.TD_BROADCAST_ACTION;

/**
 * date: 7/7/17
 * author: chenli
 * description: 账户信息页，实时刷新用户的账户信息，用户不登录的情况下看不到本页
 * version:
 * state: done
 */
public class AccountActivity extends BaseActivity {

    private BroadcastReceiver mReceiver;
    private BroadcastReceiver mReceiver1;
    private DataManager sDataManager = DataManager.getInstance();
    private Context sContext;
    private ActivityAccountBinding mBinding;

    /**
     * date: 7/7/17
     * author: chenli
     * description: 刷新账户信息
     */
    private void refreshUI(String mDataString) {
        switch (mDataString) {
            case TD_MESSAGE:
                UserEntity userEntity = sDataManager.getTradeBean().getUsers().get(sDataManager.USER_ID);
                if (userEntity == null) return;
                AccountEntity accountEntity = userEntity.getAccounts().get("CNY");
                ((ActivityAccountBinding) mViewDataBinding).setAccount(accountEntity);
                break;
            default:
                break;
        }
    }

    /**
     * date: 7/7/17
     * author: chenli
     * description: 注册账户广播，监听账户实时信息
     */
    private void registerBroaderCast() {
        mReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int networkStatus = intent.getIntExtra("networkStatus", 0);
                switch (networkStatus) {
                    case 0:
                        mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.off_line));
                        mToolbarTitle.setTextColor(Color.BLACK);
                        mToolbarTitle.setText("交易、行情网络未连接！");
                        mToolbarTitle.setTextSize(20);
                        break;
                    case 1:
                        mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.black_dark));
                        mToolbarTitle.setTextColor(Color.WHITE);
                        mToolbarTitle.setText("资金账户");
                        mToolbarTitle.setTextSize(25);
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(mReceiver1, new IntentFilter(NETWORK_STATE));

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mDataString = intent.getStringExtra("msg");
                refreshUI(mDataString);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(TD_BROADCAST_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayoutID = R.layout.activity_account;
        mTitle = ACCOUNT;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        sContext = BaseApplication.getContext();
        mBinding = (ActivityAccountBinding) mViewDataBinding;
    }

    @Override
    protected void initEvent() {
        mBinding.buttonIdLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sDataManager.IS_LOGIN = false;
                AccountActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolbarFromNetwork(sContext, "资金账户");
        if (!sDataManager.IS_LOGIN) {
            Intent intent = new Intent(this, LoginActivity.class);
            //判断从哪个页面跳到登录页，登录页的销毁方式不一样
            intent.putExtra(ACTIVITY_TYPE, "MainActivity");
            startActivity(intent);
        }else {
            mBinding.buttonIdLogout.setVisibility(View.VISIBLE);
        }
        refreshUI(TD_MESSAGE);
        registerBroaderCast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        unregisterReceiver(mReceiver1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
