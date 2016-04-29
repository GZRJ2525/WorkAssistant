package com.gzrijing.workassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.service.GetLeaderBusinessService;
import com.gzrijing.workassistant.service.GetWorkerBusinessService;
import com.gzrijing.workassistant.service.ListenerLeaderMachineApplyBillService;
import com.gzrijing.workassistant.service.ListenerLeaderMachineReturnBillService;
import com.gzrijing.workassistant.service.ListenerMachineApplyStateService;
import com.gzrijing.workassistant.service.ListenerMachineBackOkService;
import com.gzrijing.workassistant.service.ListenerMachineReceivedStateService;
import com.gzrijing.workassistant.service.ListenerMachineReturnStateService;
import com.gzrijing.workassistant.service.ListenerMachineSendOkService;
import com.gzrijing.workassistant.service.ListenerOrderStateService;
import com.gzrijing.workassistant.service.ListenerOrderTemInfoService;
import com.gzrijing.workassistant.service.ListenerProjectAmountIsCheckService;
import com.gzrijing.workassistant.service.ListenerReportInfoCompleteService;
import com.gzrijing.workassistant.service.ListenerReportInfoProblemService;
import com.gzrijing.workassistant.service.ListenerReportInfoProgressService;
import com.gzrijing.workassistant.service.ListenerReportInfoProjectAmountService;
import com.gzrijing.workassistant.service.ListenerReturnMachineOrderService;
import com.gzrijing.workassistant.service.ListenerSafetyInspectFailByDistributedService;
import com.gzrijing.workassistant.service.ListenerSafetyInspectFailByHandleService;
import com.gzrijing.workassistant.service.ListenerSafetyInspectFailService;
import com.gzrijing.workassistant.service.ListenerSendMachineOrderService;
import com.gzrijing.workassistant.service.ListenerSuppliesApplyStateService;
import com.gzrijing.workassistant.service.ListenerSuppliesReceivedStateService;
import com.gzrijing.workassistant.service.ListenerSuppliesReturnStateService;
import com.igexin.sdk.PushConsts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainReceiver extends BroadcastReceiver {
    /**
     * 推送入口
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences app = context.getSharedPreferences(
                "saveUser", context.MODE_PRIVATE);
        String userNo = app.getString("userNo", "");

        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    Log.e("data", data.toString());
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String user = jsonObject.getString("UserNo");
                            String cmd = jsonObject.getString("cmd");
                            if(userNo.equals(user)){
                                if(cmd.equals("getconstruction")){
                                    String sitLeader = jsonObject.getString("SitLeader");
                                    getLeaderBusiness(sitLeader);
                                }
                                if(cmd.equals("getmycons")){
                                    getWorkerBusiness();
                                }
                                if(cmd.equals("getmymaterialneedmain")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerSuppliesApplyState(user, orderId);
                                    }
                                }
                                if(cmd.equals("getmaterialsend")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String billNo = jsonObject.getString("BillNo");
                                    if(!orderId.equals("")){
                                        listenerSuppliesReceivedState(user, orderId, billNo);
                                    }
                                }
                                if(cmd.equals("getmaterialreturnstate")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String billNo = jsonObject.getString("BillNo");
                                    if(!orderId.equals("")){
                                        listenerSuppliesReturnState(user, orderId, billNo);
                                    }
                                }
                                if(cmd.equals("getmymachineneed")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String billNo = jsonObject.getString("BillNo");
                                    if(!orderId.equals("")){
                                        listenerMachineApplyState(user, orderId, billNo);
                                    }
                                }
                                if(cmd.equals("getmymachinereceived")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String billNo = jsonObject.getString("BillNo");
                                    if(!orderId.equals("")){
                                        listenerMachineReceivedState(user, orderId, billNo);
                                    }
                                }
                                if(cmd.equals("sendmachineok")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String billNo = jsonObject.getString("BillNo");
                                    String machineNo = jsonObject.getString("MachineNo");
                                    if(!orderId.equals("")){
                                        listenerMachineSendOk(user, orderId, billNo, machineNo);
                                    }
                                }
                                if(cmd.equals("getmymachinereturn")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String billNo = jsonObject.getString("BillNo");
                                    if(!orderId.equals("")){
                                        listenerMachineReturnState(user, orderId, billNo);
                                    }
                                }
                                if(cmd.equals("backmachinebillok")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String billNo = jsonObject.getString("BillNo");
                                    if(!orderId.equals("")){
                                        listenerMachineBackOk(user, orderId, billNo);
                                    }
                                }
                                if(cmd.equals("getneedsendmachinelist")){
                                    listenerSendMachineOrder();
                                }
                                if(cmd.equals("getneedbackmachinelist")){
                                    listenerReturnMachineOrder();
                                }
                                if(cmd.equals("getsomeinstalltask")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerReportInfoProgress(orderId);
                                    }
                                }
                                if(cmd.equals("getsomeinstallaccident")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerReportInfoProblem(orderId);
                                    }
                                }
                                if(cmd.equals("getsomeinstallconfirmmain")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerReportInfoProjectAmount(orderId);
                                    }
                                }
                                if(cmd.equals("getfinishconstruction")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerReportInfoComplete(orderId, userNo);
                                    }
                                }
                                if(cmd.equals("getmachineneedlist")){
                                    listenerLeaderMachineApplyBill();
                                }
                                if(cmd.equals("getmachinebacklist")){
                                    listenerLeaderMachineReturnBill();
                                }
                                if(cmd.equals("listenerFileState")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String state = jsonObject.getString("State");
                                    if(!orderId.equals("")){
                                        listenerOrderState(user, orderId, state);
                                    }
                                }
                                if(cmd.equals("getconstempmsg")){
                                    String orderId = jsonObject.getString("FileNo");
                                    String sitLeader = jsonObject.getString("SitLeader");
                                    if(!orderId.equals("")){
                                        listenerOrderTemInfo(userNo, orderId, sitLeader);
                                    }
                                }
                                if(cmd.equals("LoadSafeItemSaved")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerSafetyInspectFail(orderId);
                                    }
                                }
                                if(cmd.equals("handlegettask")){
                                    String orderId = jsonObject.getString("fileid");
                                    if(!orderId.equals("")){
                                        listenerSafetyInspectFailByDistributed(orderId);
                                    }
                                }
                                if(cmd.equals("handlegetinsys")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerSafetyInspectFailByHandle(orderId);
                                    }
                                }
                                if(cmd.equals("getmyconsconfirm")){
                                    String orderId = jsonObject.getString("FileNo");
                                    if(!orderId.equals("")){
                                        listenerProjectAmountIsCheck(orderId);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 获取Leader工程单
     */
    private void getLeaderBusiness(String sitLeader) {
        Intent intent = new Intent(MyApplication.getContext(), GetLeaderBusinessService.class);
        intent.putExtra("sitLeader", sitLeader);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 获取worker工程单
     */
    private void getWorkerBusiness() {
        Intent intent = new Intent(MyApplication.getContext(), GetWorkerBusinessService.class);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听材料申请单状态
     */
    private void listenerSuppliesApplyState(String userNo, String orderId){
        Intent intent = new Intent(MyApplication.getContext(), ListenerSuppliesApplyStateService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听哪些材料可以领用
     */
    private void listenerSuppliesReceivedState(String userNo, String orderId, String billNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerSuppliesReceivedStateService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("billNo", billNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听哪些材料可以退回
     */
    private void listenerSuppliesReturnState(String userNo, String orderId, String billNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerSuppliesReturnStateService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("billNo", billNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听机械申请单状态
     */
    private void listenerMachineApplyState(String userNo, String orderId, String billNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerMachineApplyStateService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("billNo", billNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听哪些机械可以领用
     */
    private void listenerMachineReceivedState(String userNo, String orderId, String billNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerMachineReceivedStateService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("billNo", billNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听哪些机械已经送达目的地
     */
    private void listenerMachineSendOk(String userNo, String orderId, String billNo, String machineNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerMachineSendOkService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("billNo", billNo);
        intent.putExtra("machineNo", machineNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听哪些机械可以退回
     */
    private void listenerMachineReturnState(String userNo, String orderId, String billNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerMachineReturnStateService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("billNo", billNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听退机申请单已全部退完
     */
    private void listenerMachineBackOk(String userNo, String orderId, String billNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerMachineBackOkService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("billNo", billNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的送机任务
     */
    private void listenerSendMachineOrder(){
        Intent intent = new Intent(MyApplication.getContext(), ListenerSendMachineOrderService.class);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的退机任务
     */
    private void listenerReturnMachineOrder(){
        Intent intent = new Intent(MyApplication.getContext(), ListenerReturnMachineOrderService.class);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的进度汇报信息
     */
    private void listenerReportInfoProgress(String orderId){
        Intent intent = new Intent(MyApplication.getContext(), ListenerReportInfoProgressService.class);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的问题汇报信息
     */
    private void listenerReportInfoProblem(String orderId){
        Intent intent = new Intent(MyApplication.getContext(), ListenerReportInfoProblemService.class);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的进度汇报信息
     */
    private void listenerReportInfoProjectAmount(String orderId){
        Intent intent = new Intent(MyApplication.getContext(), ListenerReportInfoProjectAmountService.class);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的完工汇报信息
     */
    private void listenerReportInfoComplete(String orderId, String userNo){
        Intent intent = new Intent(MyApplication.getContext(), ListenerReportInfoCompleteService.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("userNo", userNo);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的机械申请单（机械组长界面）
     */
    private void listenerLeaderMachineApplyBill(){
        Intent intent = new Intent(MyApplication.getContext(), ListenerLeaderMachineApplyBillService.class);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听是否有新的退机申请单（机械组长界面）
     */
    private void listenerLeaderMachineReturnBill(){
        Intent intent = new Intent(MyApplication.getContext(), ListenerLeaderMachineReturnBillService.class);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听工程状态
     */
    private void listenerOrderState(String userNo, String orderId, String state){
        Intent intent = new Intent(MyApplication.getContext(), ListenerOrderStateService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("state", state);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听工程临时信息
     */
    private void listenerOrderTemInfo(String userNo, String orderId, String sitLeader){
        Intent intent = new Intent(MyApplication.getContext(), ListenerOrderTemInfoService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("sitLeader", sitLeader);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听安全检查不合格项
     */
    private void listenerSafetyInspectFail(String orderId){
        Intent intent = new Intent(MyApplication.getContext(), ListenerSafetyInspectFailService.class);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听派发后安全检查不合格项
     */
    private void listenerSafetyInspectFailByDistributed(String orderId){
        Intent intent = new Intent(MyApplication.getContext(), ListenerSafetyInspectFailByDistributedService.class);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听处理后安全检查不合格项
     */
    private void listenerSafetyInspectFailByHandle(String orderId){
        Intent intent = new Intent(MyApplication.getContext(), ListenerSafetyInspectFailByHandleService.class);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

    /**
     * 监听工程量单审核后提醒
     */
    private void listenerProjectAmountIsCheck(String orderId) {
        Intent intent = new Intent(MyApplication.getContext(), ListenerProjectAmountIsCheckService.class);
        intent.putExtra("orderId", orderId);
        MyApplication.getContext().startService(intent);
    }

}
