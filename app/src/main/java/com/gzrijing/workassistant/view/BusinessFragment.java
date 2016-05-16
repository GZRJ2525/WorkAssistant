package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.DetailedInfoData;
import com.gzrijing.workassistant.db.ImageData;
import com.gzrijing.workassistant.db.MachineData;
import com.gzrijing.workassistant.db.MachineNoData;
import com.gzrijing.workassistant.db.SuppliesData;
import com.gzrijing.workassistant.db.SuppliesNoData;
import com.gzrijing.workassistant.db.TimeData;
import com.gzrijing.workassistant.entity.BusinessByLeader;
import com.gzrijing.workassistant.entity.BusinessByWorker;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.entity.MachineNo;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.DeleteFolderUtil;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.util.VoiceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class BusinessFragment extends Fragment {

    private View layoutView;
    private LeaderFragment leaderFragment;
    private WorkerFragment workerFragment;
    private DirectorFragment directorFragment;
    private InspectionStationFragment inspectionStationFragment;
    private String userRank;
    private String userNo;
    private Handler handler = new Handler();
    private ImageLoader mImageLoader;
    private int count = 0;//【dialog何时关闭的标识]
    private ProgressDialog pDialog;

    public BusinessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        SharedPreferences sp = getActivity().getSharedPreferences(
                "saveUser", Context.MODE_PRIVATE);
        userRank = sp.getString("userRank", "");
        userNo = sp.getString("userNo", "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_business, container, false);

        if (savedInstanceState == null) {
            Log.e("userRank", userRank);
            Log.e("userNo", userNo);
            if(userRank.equals("3")){//水表检定站主任
                Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);//【
                if (fragment == null) {
                    setTabSelection(Integer.valueOf(userRank));
                }
            }else{
                List<TimeData> timeData = DataSupport.where("userNo = ?", userNo).find(TimeData.class);//【timeData
                if (timeData.size() > 0) {//非首次登陆该部手机
                    Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);//【
                    if (fragment == null) {
                        setTabSelection(Integer.valueOf(userRank));
                    }
                } else {//首次登陆该部手机
                    DataSupport.deleteAll(BusinessData.class);//[清除掉该部手机中记录的其他账号的工程资料]
                    DataSupport.deleteAll(TimeData.class);//[清除掉该部手机中记录的其他账号的获取工程资料的时间记录]
                    DeleteFolderUtil.deleteFolder(Environment.getExternalStorageDirectory()+"/GZRJWorkassistant");//[清除掉该部手机中记录的其他账号的获取的图片等资料在SD卡中文件夹]
                    mImageLoader = ImageLoader.getInstance();
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("请保持网络连接，正在加载数据...");//[检查站的不在这儿，在InspectionStationFragment中。它没有工程类型和状态的筛选]
                    pDialog.show();
                    if (userRank.equals("0")) {
                        getWorkerBusiness();
                    }
                    if(userRank.equals("1")){
                        getLeaderBusiness();
                    }
                    if(userRank.equals("2")){
                        getDirectorBusiness();
                    }
                }
            }

        }

        return layoutView;
    }

    private void getDirectorBusiness() {
        String time = "2016-1-10 10:00:00";//【
        TimeData timeData = new TimeData();
        timeData.setTime(time);
        timeData.setUserNo(userNo);
        timeData.save();

        String url = null;
        try {//卢工接口2. 获取工程项目  【begindate
            url = "?cmd=getconstruction&userno=" + URLEncoder.encode(userNo, "UTF-8") + "&begindate=" + URLEncoder.encode(time, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                saveDirectorBusinessData(response);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void saveDirectorBusinessData(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String time = sdf.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put("time", time);
        DataSupport.updateAll(TimeData.class, values, "userNo = ?", userNo);//[储存获得工程数据的时间，做什么用？]

        List<BusinessByLeader> list = JsonParseUtils.getLeaderBusiness(data);
        if(list.size() == 0){
            pDialog.dismiss();
            Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
            if (fragment == null) {
                setTabSelection(Integer.valueOf(userRank));
            }
        }
        for (final BusinessByLeader order : list) {
            BusinessData data1 = new BusinessData();
            data1.setUser(userNo);
            data1.setOrderId(order.getOrderId());
            data1.setUrgent(order.isUrgent());
            data1.setType(order.getType());
            data1.setState(order.getState());
            data1.setReceivedTime(order.getReceivedTime());
            data1.setDeadline(order.getDeadline());
            data1.setFlag(order.getFlag());
            data1.setTemInfoNum(order.getTemInfoNum());
            List<DetailedInfo> infos = order.getDetailedInfos();
            for (DetailedInfo info : infos) {
                DetailedInfoData data2 = new DetailedInfoData();
                data2.setKey(info.getKey());
                data2.setValue(info.getValue());
                data2.save();
                data1.getDetailedInfoList().add(data2);
            }
            List<PicUrl> picUrls = order.getPicUrls();
            for (final PicUrl picUrl : picUrls) {
                Log.e("picUrl", HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl());
                mImageLoader.loadImage(HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        try {
                            File path = ImageUtils.getImagePath(getActivity(), userNo, order.getOrderId());
                            ImageUtils.saveFile(getActivity(), loadedImage, picUrl.getPicUrl(), path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ImageData data3 = new ImageData();
                data3.setUrl(picUrl.getPicUrl());
                data3.save();
                data1.getImageDataList().add(data3);
            }
            data1.save();
        }
        pDialog.dismiss();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
        if (fragment == null) {
            setTabSelection(Integer.valueOf(userRank));
        }
    }


    private void getLeaderBusiness() {
        String time = "2016-1-10 10:00:00";
        TimeData timeData = new TimeData();
        timeData.setTime(time);
        timeData.setUserNo(userNo);
        timeData.save();

        String url = null;
        try {//卢工接口2. 获取工程项目  【State = 状态（已派工/未派工）
            url = "?cmd=getconstruction&userno=" + URLEncoder.encode(userNo, "UTF-8") + "&begindate=" + URLEncoder.encode(time, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("卢2response", response);
                saveLeaderBusinessData(response);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });

    }

    private void saveLeaderBusinessData(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String time = sdf.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put("time", time);
        DataSupport.updateAll(TimeData.class, values, "userNo = ?", userNo);//[ 获取工程的时间 记入数据库]

        List<BusinessByLeader> list = JsonParseUtils.getLeaderBusiness(data);
        if(list.size() == 0){//无工程信息
            pDialog.dismiss();
            Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
            if (fragment == null) {
                setTabSelection(Integer.valueOf(userRank));
            }
        }
        for (final BusinessByLeader order : list) {
            count+=2;
            BusinessData data1 = new BusinessData();
            data1.setUser(userNo);
            data1.setOrderId(order.getOrderId());
            data1.setUrgent(order.isUrgent());
            data1.setType(order.getType());
            data1.setState(order.getState());
            data1.setReceivedTime(order.getReceivedTime());
            data1.setDeadline(order.getDeadline());
            data1.setFlag(order.getFlag());//[也就是服务器返回的State]
            data1.setTemInfoNum(order.getTemInfoNum());
            List<DetailedInfo> infos = order.getDetailedInfos();
            for (DetailedInfo info : infos) {
                DetailedInfoData data2 = new DetailedInfoData();
                data2.setKey(info.getKey());
                data2.setValue(info.getValue());
                data2.save();
                data1.getDetailedInfoList().add(data2);//[保存工程的详情]
            }
            List<PicUrl> picUrls = order.getPicUrls();
            for (final PicUrl picUrl : picUrls) {
                Log.e("picUrl", HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl());
                mImageLoader.loadImage(HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        try {
                            File path = ImageUtils.getImagePath(getActivity(), userNo, order.getOrderId());//[创建一个存储图片的路径]
                            ImageUtils.saveFile(getActivity(), loadedImage, picUrl.getPicUrl(), path);//[保存下载的图片]
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ImageData data3 = new ImageData();
                data3.setUrl(picUrl.getPicUrl());
                data3.save();
                data1.getImageDataList().add(data3);//[保存图片的Url]
            }
            data1.save();
            getMachine(order.getOrderId(), data1);
        }
    }

    private void getMachine(String orderId, BusinessData businessData) {
        getMachineApply(orderId, businessData);
        getMachineReceived(orderId, businessData);

    }

    private void getMachineApply(String orderId, final BusinessData businessData) {
        String url = null;
        try {//卢工接口54．属于自己制单，但还没有接收完的机械申请单【接收完的机械单资料如何获得？
            url = "?cmd=getmymachineneed&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                    "&billno=&fileno=" + URLEncoder.encode(orderId, "UTF-8")+"&checkdate=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                ArrayList<MachineNo> list = JsonParseUtils.getMachineApplyNo(response);
                for(MachineNo machineNo : list){
                    MachineNoData machineNoData = new MachineNoData();
                    machineNoData.setApplyId(machineNo.getApplyId());
                    machineNoData.setReturnId(machineNo.getReturnId());
                    machineNoData.setApplyTime(machineNo.getApplyTime());
                    machineNoData.setUseTime(machineNo.getUseTime());
                    machineNoData.setReturnTime(machineNo.getReturnTime());
                    machineNoData.setUseAddress(machineNo.getUseAddress());
                    machineNoData.setRemarks(machineNo.getRemarks());
                    machineNoData.setApprovalTime(machineNo.getApprovalTime());
                    machineNoData.setApplyState(machineNo.getApplyState());
                    machineNoData.setReturnState(machineNo.getReturnState());
                    machineNoData.setReturnType(machineNo.getReturnType());
                    machineNoData.setReturnApplyTime(machineNo.getReturnApplyTime());
                    machineNoData.setReturnAddress(machineNo.getReturnAddress());
                    machineNoData.setReason(machineNo.getReason());
                    machineNoData.save();
                    businessData.getMachineNoList().add(machineNoData);

                    for(Machine machine : machineNo.getMachineList()){
                        MachineData machineData = new MachineData();
                        machineData.setApplyId(machineNo.getApplyId());
                        machineData.setReturnId(machineNo.getReturnId());
                        machineData.setNo(machine.getId());
                        machineData.setName(machine.getName());
                        machineData.setUnit(machine.getUnit());
                        machineData.setApplyNum(machine.getApplyNum());
                        machineData.setSendNum(machine.getSendNum());
                        machineData.setReceivedState("");
                        machineData.save();
                        businessData.getMachineDataList().add(machineData);
                    }
                }
                businessData.save();
                count--;
                if(count == 0){
                    if(pDialog != null){
                        pDialog.dismiss();
                    }
                    Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
                    if (fragment == null) {
                        setTabSelection(Integer.valueOf(userRank));
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(pDialog != null){
                            pDialog.dismiss();
                        }
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        DataSupport.deleteAll(BusinessData.class);
                        DataSupport.deleteAll(TimeData.class);
                    }
                });
            }
        });
    }

    private void getMachineReceived(String orderId, final BusinessData businessData) {
        String url = null;
        try {//卢工接口55．获取某工程项目已经领出去的机械
            url = "?cmd=gethavesendmachine&fileno=" + URLEncoder.encode(orderId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                ArrayList<Machine> list = JsonParseUtils.getMachineByReceived(response);
                for(Machine machine : list){
                    MachineData machineData = new MachineData();
                    machineData.setNo(machine.getId());
                    machineData.setName(machine.getName());
                    machineData.setUnit(machine.getUnit());
                    machineData.setSendNum(machine.getSendNum());
                    machineData.setReceivedState(machine.getState());
                    machineData.save();
                    businessData.getMachineDataList().add(machineData);
                }
                businessData.save();
                count--;
                if(count == 0){
                    if(pDialog != null){
                        pDialog.dismiss();
                    }
                    Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
                    if (fragment == null) {
                        setTabSelection(Integer.valueOf(userRank));
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(pDialog != null){
                            pDialog.dismiss();
                        }
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        DataSupport.deleteAll(BusinessData.class);
                        DataSupport.deleteAll(TimeData.class);
                    }
                });
            }
        });
    }

    private void getWorkerBusiness() {
        String time = "2016-1-10 10:00:00";
        TimeData timeData = new TimeData();
        timeData.setTime(time);
        timeData.setUserNo(userNo);
        timeData.save();

        String url = null;
        try {//卢工接口13. 获取我的工单
            url = "?cmd=getmycons&userno=" + URLEncoder.encode(userNo, "UTF-8") + "&fileno=&begindate=" + URLEncoder.encode(time, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                saveWorkerBusinessData(response);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                        DataSupport.deleteAll(BusinessData.class);
                        DataSupport.deleteAll(TimeData.class);
                    }
                });
            }
        });
    }

    private void saveWorkerBusinessData(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String time = sdf.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put("time", time);
        DataSupport.updateAll(TimeData.class, values, "userNo = ?", userNo);

        List<BusinessByWorker> list = JsonParseUtils.getWorkerBusiness(data);//服务器返回的工程数据
        if(list.size() == 0){
            pDialog.dismiss();
            Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
            if (fragment == null) {
                setTabSelection(Integer.valueOf(userRank));
            }
        }
        for (final BusinessByWorker order : list) {
            count+=2;
            BusinessData data1 = new BusinessData();
            data1.setUser(userNo);
            data1.setOrderId(order.getOrderId());
            data1.setUrgent(order.isUrgent());
            data1.setType(order.getType());
            data1.setState(order.getState());
            data1.setReceivedTime(order.getReceivedTime());
            data1.setDeadline(order.getDeadline());
            data1.setFlag(order.getFlag());
            data1.setTemInfoNum(order.getTemInfoNum());
            data1.setRecordFileName(order.getRecordFileName());
            if (order.getRecordFileName() != null && !order.getRecordFileName().equals("")) {//【
                String url = HttpUtils.voiceURLPath + order.getRecordFileName();
                File file = VoiceUtil.getVoicePath(getActivity(), userNo, order.getOrderId());
                HttpUtils.downloadFile(url, file, new HttpCallbackListener() {
                    @Override
                    public void onFinish(final String response) {
                        Log.e("voice", response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!response.equals("ok")) {
                                    ToastUtil.showToast(getActivity(), order.getOrderId() + "下载录音文件失败", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                                DataSupport.deleteAll(BusinessData.class);
                                DataSupport.deleteAll(TimeData.class);
                            }
                        });
                    }
                });
            }
            List<DetailedInfo> infos = order.getDetailedInfos();
            for (DetailedInfo info : infos) {
                DetailedInfoData data2 = new DetailedInfoData();
                data2.setKey(info.getKey());
                data2.setValue(info.getValue());
                data2.save();
                data1.getDetailedInfoList().add(data2);
            }
            List<PicUrl> picUrls = order.getPicUrls();
            for (final PicUrl picUrl : picUrls) {
                mImageLoader.loadImage(HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        try {
                            File path = ImageUtils.getImagePath(getActivity(), userNo, order.getOrderId());
                            ImageUtils.saveFile(getActivity(), loadedImage, picUrl.getPicUrl(), path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ImageData data3 = new ImageData();
                data3.setUrl(picUrl.getPicUrl());
                data3.save();
                data1.getImageDataList().add(data3);
            }
            data1.save();
            getSupplies(order.getOrderId(), data1);
        }

    }

    private void getSupplies(String orderId, BusinessData businessData) {
        getSuppliesApply(orderId, businessData);
        getSuppliesReceived(orderId, businessData);
    }

    private void getSuppliesApply(String orderId, final BusinessData businessData) {
        String url = null;
        try {//卢工接口17. 获取属于自己制单的材料申请单
            url = "?cmd=getmymaterialneedmain&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                    "&checkdate=&fileno=" + URLEncoder.encode(orderId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<SuppliesNo> list = JsonParseUtils.getMyOrderSuppliesNoByApply(response);

                        for (SuppliesNo suppliesNo : list) {
                            SuppliesNoData suppliesNoData = new SuppliesNoData();
                            suppliesNoData.setApplyId(suppliesNo.getApplyId());
                            suppliesNoData.setApplyState(suppliesNo.getApplyState());
                            suppliesNoData.setApplyTime(suppliesNo.getApplyTime());
                            suppliesNoData.setUseTime(suppliesNo.getUseTime());
                            suppliesNoData.setReturnId(suppliesNo.getReturnId());
                            suppliesNoData.setReturnState(suppliesNo.getReturnState());
                            suppliesNoData.setReturnTime(suppliesNo.getReturnTime());
                            suppliesNoData.setApprovalTime(suppliesNo.getApprovalTime());
                            suppliesNoData.setReason(suppliesNo.getReason());
                            suppliesNoData.setRemarks(suppliesNo.getRemarks());
                            suppliesNoData.save();
                            businessData.getSuppliesNoList().add(suppliesNoData);

                            for (Supplies supplies : suppliesNo.getSuppliesList()) {
                                SuppliesData suppliesData = new SuppliesData();
                                suppliesData.setApplyId(suppliesNo.getApplyId());
                                suppliesData.setReturnId(suppliesNo.getReturnId());
                                suppliesData.setNo(supplies.getId());
                                suppliesData.setName(supplies.getName());
                                suppliesData.setSpec(supplies.getSpec());
                                suppliesData.setUnit(supplies.getUnit());
                                suppliesData.setApplyNum(supplies.getApplyNum());
                                suppliesData.setSendNum(supplies.getSendNum());
                                suppliesData.save();
                                businessData.getSuppliesDataList().add(suppliesData);
                            }
                        }
                        businessData.save();
                        count--;
                        if(count == 0){
                            if(pDialog != null){
                                pDialog.dismiss();
                            }
                            Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
                            if (fragment == null) {
                                setTabSelection(Integer.valueOf(userRank));
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(pDialog != null){
                            pDialog.dismiss();
                        }
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        DataSupport.deleteAll(BusinessData.class);
                        DataSupport.deleteAll(TimeData.class);
                    }
                });
            }
        });
    }

    private void getSuppliesReceived(String orderId, final BusinessData businessData) {
        String url = null;
        try {//卢工接口49．监听哪些材料可以领用
            url = "?cmd=getmaterialsend&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                    "&checkdate=&fileno=" + URLEncoder.encode(orderId, "UTF-8") +
                    "&billno=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<SuppliesNo> list = JsonParseUtils.getMyOrderSuppliesNoBySend(response);
                        for(SuppliesNo suppliesNo : list){
                            SuppliesNoData suppliesNoData = new SuppliesNoData();
                            suppliesNoData.setApplyId(suppliesNo.getApplyId());//【]
                            suppliesNoData.setReceivedId(suppliesNo.getReceivedId());
                            suppliesNoData.setReceivedState(suppliesNo.getReceivedState());
                            suppliesNoData.setReceivedTime(suppliesNo.getReceivedTime());
                            suppliesNoData.save();
                            businessData.getSuppliesNoList().add(suppliesNoData);

                            for(Supplies supplies : suppliesNo.getSuppliesList()){
                                SuppliesData suppliesData = new SuppliesData();
                                suppliesData.setApplyId(suppliesNo.getApplyId());
                                suppliesData.setReceivedId(suppliesNo.getReceivedId());
                                suppliesData.setNo(supplies.getId());
                                suppliesData.setName(supplies.getName());
                                suppliesData.setSpec(supplies.getSpec());
                                suppliesData.setUnit(supplies.getUnit());
                                suppliesData.setSendNum(supplies.getSendNum());
                                suppliesData.save();
                                businessData.getSuppliesDataList().add(suppliesData);
                            }
                        }
                        businessData.save();
                        count--;
                        if(count == 0){
                            if(pDialog != null){
                                pDialog.dismiss();
                            }
                            Fragment fragment = getChildFragmentManager().findFragmentByTag(userRank);
                            if (fragment == null) {
                                setTabSelection(Integer.valueOf(userRank));
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(pDialog != null){
                            pDialog.dismiss();
                        }
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        DataSupport.deleteAll(BusinessData.class);
                        DataSupport.deleteAll(TimeData.class);
                    }
                });
            }
        });
    }

    private void setTabSelection(int index) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (workerFragment == null) {
                    workerFragment = new WorkerFragment();
                    transaction.add(R.id.fragment_business, workerFragment, "0");
                } else {
                    transaction.show(workerFragment);
                }
                break;
            case 1:
                if (leaderFragment == null) {
                    leaderFragment = new LeaderFragment();
                    transaction.add(R.id.fragment_business, leaderFragment, "1");
                } else {
                    transaction.show(leaderFragment);
                }
                break;
            case 2:
                if (directorFragment == null) {
                    directorFragment = new DirectorFragment();
                    transaction.add(R.id.fragment_business, directorFragment, "2");
                } else {
                    transaction.show(directorFragment);
                }
                break;
            case 3:
                if (inspectionStationFragment == null) {
                    inspectionStationFragment = new InspectionStationFragment();
                    transaction.add(R.id.fragment_business, inspectionStationFragment, "3");
                } else {
                    transaction.show(inspectionStationFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (leaderFragment != null) {
            transaction.hide(leaderFragment);
        }
        if (workerFragment != null) {
            transaction.hide(workerFragment);
        }
        if (directorFragment != null) {
            transaction.hide(directorFragment);
        }
        if (inspectionStationFragment != null) {
            transaction.hide(inspectionStationFragment);
        }
    }

}
