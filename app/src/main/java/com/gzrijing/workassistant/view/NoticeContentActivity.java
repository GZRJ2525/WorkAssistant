package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Notice;

public class NoticeContentActivity extends BaseActivity {

    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_promulgator;
    private TextView tv_date;
    private Notice notice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_content);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        notice = intent.getParcelableExtra("notice");
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_title = (TextView) findViewById(R.id.notice_content_title_tv);
        tv_title.setText(notice.getTitle());
        tv_content = (TextView) findViewById(R.id.notice_content_content_tv);
        tv_content.setText(notice.getContent());
        tv_promulgator = (TextView) findViewById(R.id.notice_content_promulgator_tv);
        tv_promulgator.setText(notice.getDepartment() + "：" + notice.getPromulgator());
        tv_date = (TextView) findViewById(R.id.notice_content_date_tv);
        tv_date.setText(notice.getDate());
    }

    private void setListeners() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
