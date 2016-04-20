package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.BusinessHaveSendByAll;
import com.gzrijing.workassistant.entity.Subordinate;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BusinessHaveSendByAllActivity extends BaseActivity {

    private String userNo;
    private ArrayList<BusinessHaveSendByAll> businessList = new ArrayList<BusinessHaveSendByAll>();
    private ArrayList<Subordinate> subordinateList = new ArrayList<Subordinate>();
    private Handler handler = new Handler();
    private int index;
    private FragmentManager fragmentManager;
    private SortByDeadlineFragment sortByDeadlineFragment;
    private SortByWorkerNameFragment sortByWorkerNameFragment;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_have_send_by_all);

        initData();
        initViews();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        fragmentManager = getSupportFragmentManager();

        getData();
        getSubordinate();
    }

    private void getData() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getmyappointinstallid&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response--b", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<BusinessHaveSendByAll> list = JsonParseUtils.getBusinessHaveSendByAll(response);
                        businessList.addAll(list);
                        pDialog.dismiss();
                        setTabSelection(index);
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessHaveSendByAllActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getSubordinate() {
        String url = null;
        try {
            url = "?cmd=getsitusergl&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response--s", response);
                List<Subordinate> list = JsonParseUtils.getSubordinate(response);
                subordinateList.addAll(list);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessHaveSendByAllActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });

            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_business_have_send_by_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_select) {
            selectSort();
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectSort() {
        String[] item = {"按期限排序", "按施工员名字排序"};
        final int flag = index;
        new android.support.v7.app.AlertDialog.Builder(this).setTitle("选择排序方式：").setSingleChoiceItems(
                item, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTabSelection(index);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = flag;
                    }
                }).show();
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     */
    private void setTabSelection(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (sortByDeadlineFragment == null) {
                    sortByDeadlineFragment = new SortByDeadlineFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("businessList", businessList);
                    sortByDeadlineFragment.setArguments(bundle);
                    transaction.add(R.id.business_have_send_by_all_fragment_content, sortByDeadlineFragment);
                } else {
                    transaction.show(sortByDeadlineFragment);
                }
                break;
            case 1:
                if (sortByWorkerNameFragment == null) {
                    sortByWorkerNameFragment = new SortByWorkerNameFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("businessList", businessList);
                    bundle.putParcelableArrayList("subordinateList", subordinateList);
                    sortByWorkerNameFragment.setArguments(bundle);
                    transaction.add(R.id.business_have_send_by_all_fragment_content, sortByWorkerNameFragment);
                } else {
                    transaction.show(sortByWorkerNameFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (sortByDeadlineFragment != null) {
            transaction.hide(sortByDeadlineFragment);
        }
        if (sortByWorkerNameFragment != null) {
            transaction.hide(sortByWorkerNameFragment);
        }
    }
}
