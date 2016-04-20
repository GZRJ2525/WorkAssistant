package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;

public class ReportActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_progress;
    private Button btn_complete;
    private Button btn_projectAmount;
    private Button btn_problem;
    private String orderId;
    private String type;
    private FragmentManager fragmentManager;
    private ReportProgressFragment reportProgress;
    private ReportCompleteFragment reportComplete;
    private ReportProjectAmountFragment reportProjectAmount;
    private ReportProblemFragment reportProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initViews();
        initData();
        setListeners();
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_progress = (Button) findViewById(R.id.report_progress_btn);
        btn_problem = (Button) findViewById(R.id.report_problem_btn);
        btn_complete = (Button) findViewById(R.id.report_complete_btn);
        btn_projectAmount = (Button) findViewById(R.id.report_project_amount_btn);
    }

    private void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        type = intent.getStringExtra("type");

        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
    }

    private void setListeners() {
        btn_progress.setOnClickListener(this);
        btn_problem.setOnClickListener(this);
        btn_complete.setOnClickListener(this);
        btn_projectAmount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_progress_btn:
                setTabSelection(0);
                break;

            case R.id.report_problem_btn:
                setTabSelection(1);
                break;

            case R.id.report_complete_btn:
                setTabSelection(2);
                break;

            case R.id.report_project_amount_btn:
                setTabSelection(3);
                break;
        }
    }

    private void setTabSelection(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                btn_progress.setBackgroundResource(R.drawable.btn_paging_left_on);
                btn_progress.setTextColor(getResources().getColor(R.color.white));
                btn_complete.setBackgroundResource(R.drawable.btn_paging_middle_off);
                btn_complete.setTextColor(getResources().getColor(R.color.blue));
                btn_projectAmount.setBackgroundResource(R.drawable.btn_paging_right_off);
                btn_projectAmount.setTextColor(getResources().getColor(R.color.blue));
                btn_problem.setBackgroundResource(R.drawable.btn_paging_middle_off);
                btn_problem.setTextColor(getResources().getColor(R.color.blue));
                if (reportProgress == null) {
                    reportProgress = new ReportProgressFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderId);
                    reportProgress.setArguments(bundle);
                    transaction.add(R.id.fragment_report, reportProgress);
                } else {
                    transaction.show(reportProgress);
                }
                break;

            case 1:
                btn_progress.setBackgroundResource(R.drawable.btn_paging_left_off);
                btn_progress.setTextColor(getResources().getColor(R.color.blue));
                btn_complete.setBackgroundResource(R.drawable.btn_paging_middle_off);
                btn_complete.setTextColor(getResources().getColor(R.color.blue));
                btn_projectAmount.setBackgroundResource(R.drawable.btn_paging_right_off);
                btn_projectAmount.setTextColor(getResources().getColor(R.color.blue));
                btn_problem.setBackgroundResource(R.drawable.btn_paging_middle_on);
                btn_problem.setTextColor(getResources().getColor(R.color.white));
                if (reportProblem == null) {
                    reportProblem = new ReportProblemFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderId);
                    reportProblem.setArguments(bundle);
                    transaction.add(R.id.fragment_report, reportProblem);
                } else {
                    transaction.show(reportProblem);
                }
                break;

            case 2:
                btn_progress.setBackgroundResource(R.drawable.btn_paging_left_off);
                btn_progress.setTextColor(getResources().getColor(R.color.blue));
                btn_complete.setBackgroundResource(R.drawable.btn_paging_middle_on);
                btn_complete.setTextColor(getResources().getColor(R.color.white));
                btn_projectAmount.setBackgroundResource(R.drawable.btn_paging_right_off);
                btn_projectAmount.setTextColor(getResources().getColor(R.color.blue));
                btn_problem.setBackgroundResource(R.drawable.btn_paging_middle_off);
                btn_problem.setTextColor(getResources().getColor(R.color.blue));
                if (reportComplete == null) {
                    reportComplete = new ReportCompleteFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderId);
                    bundle.putString("type", type);
                    reportComplete.setArguments(bundle);
                    transaction.add(R.id.fragment_report, reportComplete);
                } else {
                    transaction.show(reportComplete);
                }
                break;

            case 3:
                btn_progress.setBackgroundResource(R.drawable.btn_paging_left_off);
                btn_progress.setTextColor(getResources().getColor(R.color.blue));
                btn_complete.setBackgroundResource(R.drawable.btn_paging_middle_off);
                btn_complete.setTextColor(getResources().getColor(R.color.blue));
                btn_projectAmount.setBackgroundResource(R.drawable.btn_paging_right_on);
                btn_projectAmount.setTextColor(getResources().getColor(R.color.white));
                btn_problem.setBackgroundResource(R.drawable.btn_paging_middle_off);
                btn_problem.setTextColor(getResources().getColor(R.color.blue));
                if (reportProjectAmount == null) {
                    reportProjectAmount = new ReportProjectAmountFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderId);
                    reportProjectAmount.setArguments(bundle);
                    transaction.add(R.id.fragment_report, reportProjectAmount);
                } else {
                    transaction.show(reportProjectAmount);
                }
                break;

        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (reportProgress != null) {
            transaction.hide(reportProgress);
        }
        if (reportProblem != null) {
            transaction.hide(reportProblem);
        }
        if (reportComplete != null) {
            transaction.hide(reportComplete);
        }
        if (reportProjectAmount != null) {
            transaction.hide(reportProjectAmount);
        }
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
