package com.gzrijing.workassistant.util;

import android.util.Log;

import com.gzrijing.workassistant.entity.Acceptance;
import com.gzrijing.workassistant.entity.Accident;
import com.gzrijing.workassistant.entity.BusinessByLeader;
import com.gzrijing.workassistant.entity.BusinessByWorker;
import com.gzrijing.workassistant.entity.BusinessHaveSend;
import com.gzrijing.workassistant.entity.BusinessHaveSendByAll;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.Inspection;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBillByMachine;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBill;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBillByMachine;
import com.gzrijing.workassistant.entity.LeaderMachineState;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.entity.MachineNo;
import com.gzrijing.workassistant.entity.MachineVerify;
import com.gzrijing.workassistant.entity.MachineVerifyInfo;
import com.gzrijing.workassistant.entity.Notice;
import com.gzrijing.workassistant.entity.OrderTypeSupplies;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.ProblemResult;
import com.gzrijing.workassistant.entity.ProblemType;
import com.gzrijing.workassistant.entity.Progress;
import com.gzrijing.workassistant.entity.QueryProjectAmount;
import com.gzrijing.workassistant.entity.ReportInfo;
import com.gzrijing.workassistant.entity.ReportInfoProjectAmount;
import com.gzrijing.workassistant.entity.ReturnMachine;
import com.gzrijing.workassistant.entity.SafetyInspectFail;
import com.gzrijing.workassistant.entity.SafetyInspectFailItem;
import com.gzrijing.workassistant.entity.SafetyInspectFailReport;
import com.gzrijing.workassistant.entity.SafetyInspectFirstItem;
import com.gzrijing.workassistant.entity.SafetyInspectForm;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecord;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecordFailItem;
import com.gzrijing.workassistant.entity.SafetyInspectSecondItem;
import com.gzrijing.workassistant.entity.SafetyInspectTask;
import com.gzrijing.workassistant.entity.SendMachine;
import com.gzrijing.workassistant.entity.Subordinate;
import com.gzrijing.workassistant.entity.SubordinateLocation;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;
import com.gzrijing.workassistant.entity.SuppliesVerify;
import com.gzrijing.workassistant.entity.SuppliesVerifyInfo;
import com.gzrijing.workassistant.entity.TemInfo;
import com.gzrijing.workassistant.entity.User;
import com.gzrijing.workassistant.entity.WorkGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析和处理服务器返回的数据
 */
public class JsonParseUtils {
    /**
     * 登录解析
     *
     * @param jsonData
     * @return User
     */
    public static User getUser(String jsonData) {
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String userNo = jsonObject.getString("UserNo");
            String userName = jsonObject.getString("UserName");
            String userDept = jsonObject.getString("UserDept");
            String userSit = jsonObject.getString("UserSit");
            String userRank = jsonObject.getString("SitLeader");
            user = new User(userNo, userName, userDept, userSit, userRank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Leader获取工程解析
     *
     * @param jsonData
     * @return List<BusinessByLeader>
     */
    public static List<BusinessByLeader> getLeaderBusiness(String jsonData) {
        List<BusinessByLeader> list = new ArrayList<BusinessByLeader>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderId = jsonObject.getString("FileNo");
                String type = jsonObject.getString("ConsTypeName");
                String state = jsonObject.getString("State");
                String receivedTime = jsonObject.getString("CheckDate").replace("/", "-");
                String deadline = jsonObject.getString("EstimateFinishDate").replace("/", "-");
                boolean urgent = jsonObject.getBoolean("IsUrgent");
                int temInfoNum = Integer.valueOf(jsonObject.getString("TempMsgQty"));

                BusinessByLeader businessByLeader = new BusinessByLeader();
                businessByLeader.setOrderId(orderId);
                businessByLeader.setType(type);
                businessByLeader.setState(state);
                businessByLeader.setReceivedTime(receivedTime);
                businessByLeader.setDeadline(deadline);
                businessByLeader.setUrgent(urgent);
                businessByLeader.setTemInfoNum(temInfoNum);
                if (state.equals("未派工")) {
                    businessByLeader.setFlag("确认收到");
                } else {
                    businessByLeader.setFlag("派工");
                }

                List<DetailedInfo> infos = new ArrayList<DetailedInfo>();
                JSONArray jsonArray1 = jsonObject.getJSONArray("Detail");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String key = jsonObject1.getString("key");
                    String value = jsonObject1.getString("value");
                    DetailedInfo detailedInfo = new DetailedInfo(key, value);
                    infos.add(detailedInfo);
                }

                if (!jsonObject.getString("PicUri").equals("")) {
                    JSONArray jsonArray2 = jsonObject.getJSONArray("PicUri");
                    ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
                    for (int k = 0; k < jsonArray2.length(); k++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                        String url = jsonObject2.getString("PicUri");
                        int index = url.lastIndexOf("/");
                        url = url.substring(index + 1);
                        PicUrl picUrl = new PicUrl();
                        picUrl.setPicUrl(url);
                        picUrls.add(picUrl);
                    }
                    businessByLeader.setPicUrls(picUrls);
                }
                businessByLeader.setDetailedInfos(infos);
                list.add(businessByLeader);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取被派工岗位的人员列表
     */
    public static List<Subordinate> getSubordinate(String jsonData) {
        List<Subordinate> subList = new ArrayList<Subordinate>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String userNo = jsonObject.getString("UserNo");
                String userName = jsonObject.getString("UserName");
                Subordinate subordinate = new Subordinate();
                subordinate.setUserNo(userNo);
                subordinate.setName(userName);
                subList.add(subordinate);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return subList;
    }

    /**
     * 获取所有工作组
     */
    public static List<WorkGroup> getWorkGroup(String jsonData) {
        List<WorkGroup> list = new ArrayList<WorkGroup>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String groupNo = jsonObject.getString("WorkSitNo");
                String groupName = jsonObject.getString("WorkSit");
                WorkGroup workGroup = new WorkGroup();
                workGroup.setGroupNo(groupNo);
                workGroup.setGroupName(groupName);
                list.add(workGroup);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Worker获取工程解析
     *
     * @param jsonData
     * @return List<BusinessByWorker>
     */
    public static List<BusinessByWorker> getWorkerBusiness(String jsonData) {
        List<BusinessByWorker> list = new ArrayList<BusinessByWorker>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderId = jsonObject.getString("FileNo");
                String type = jsonObject.getString("ConsTypeName");
                String accident = jsonObject.getString("Accident").trim();
                String state;
                if (accident.equals("")) {
                    state = "正常";
                } else {
                    state = accident;
                }
                String flag = jsonObject.getString("State").trim();
                if (flag.equals("未接受")) {
                    flag = "确认收到";
                }
                if (flag.equals("已接受")) {
                    flag = "处理";
                }
                if (flag.equals("正在处理")) {
                    flag = "汇报";
                }
                String receivedTime = jsonObject.getString("CheckDate").replace("/", "-");
                String deadline = jsonObject.getString("EstimateFinishDate").replace("/", "-");
                boolean urgent = jsonObject.getBoolean("IsUrgent");
                int temInfoNum = Integer.valueOf(jsonObject.getString("TempMsgQty"));

                BusinessByWorker businessByWorker = new BusinessByWorker();
                businessByWorker.setOrderId(orderId);
                businessByWorker.setType(type);
                businessByWorker.setState(state);
                businessByWorker.setReceivedTime(receivedTime);
                businessByWorker.setDeadline(deadline);
                businessByWorker.setUrgent(urgent);
                businessByWorker.setFlag(flag);
                businessByWorker.setTemInfoNum(temInfoNum);

                ArrayList<DetailedInfo> infos = new ArrayList<DetailedInfo>();
                JSONArray jsonArray1 = jsonObject.getJSONArray("Detail");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String key = jsonObject1.getString("key");
                    String value = jsonObject1.getString("value");
                    DetailedInfo detailedInfo = new DetailedInfo(key, value);
                    infos.add(detailedInfo);
                }
                String content = jsonObject.getString("InstallContent");
                DetailedInfo detailedInfo = new DetailedInfo("施工内容", content);
                infos.add(detailedInfo);

                if (!jsonObject.getString("PicUri").equals("")) {
                    JSONArray jsonArray2 = jsonObject.getJSONArray("PicUri");
                    ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
                    for (int k = 0; k < jsonArray2.length(); k++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                        String url = jsonObject2.getString("PicUri");
                        int index = url.lastIndexOf("/");
                        url = url.substring(index + 1);
                        PicUrl picUrl = new PicUrl();
                        picUrl.setPicUrl(url);
                        picUrls.add(picUrl);
                    }

                    businessByWorker.setPicUrls(picUrls);
                }
                if (!jsonObject.getString("SoundUri").equals("")) {
                    JSONArray jsonArray2 = jsonObject.getJSONArray("SoundUri");
                    for (int k = 0; k < jsonArray2.length(); k++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                        String url = jsonObject2.getString("SoundUri");
                        int index = url.lastIndexOf("/");
                        url = url.substring(index + 1);
                        Log.e("url", url);
                        businessByWorker.setRecordFileName(url);
                    }
                }
                businessByWorker.setDetailedInfos(infos);
                list.add(businessByWorker);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取关键字查询材料列表
     *
     * @param jsonDate
     * @return List<Supplies>
     */
    public static List<Supplies> getSuppliesQueries(String jsonDate) {
        List<Supplies> suppliesList = new ArrayList<Supplies>();
        try {
            JSONArray jsonArray = new JSONArray(jsonDate);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("MakingNo").trim();
                String name = jsonObject.getString("MakingName").trim();
                String spec = jsonObject.getString("MakingSpace").trim();
                String unit = jsonObject.getString("MakingUnit").trim();
                Supplies supplies = new Supplies();
                supplies.setId(id);
                supplies.setName(name);
                supplies.setSpec(spec);
                supplies.setUnit(unit);
                suppliesList.add(supplies);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return suppliesList;
    }

    /**
     * 获取关键字查询机械列表
     *
     * @param jsonDate
     * @return List<Machine>
     */
    public static List<Machine> getMachineQueries(String jsonDate) {
        List<Machine> machines = new ArrayList<Machine>();
        try {
            JSONArray jsonArray = new JSONArray(jsonDate);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("MachineNo").trim();
                String name = jsonObject.getString("MachineName").trim();
                String unit = jsonObject.getString("MachineUnit").trim();
                String state = jsonObject.getString("State").trim();
                Machine machine = new Machine();
                machine.setId(id);
                machine.setName(name);
                machine.setUnit(unit);
                machine.setState(state);
                machines.add(machine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return machines;
    }

    /**
     * 获取工程所有图片Url
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<PicUrl> getImageUrl(String jsonData) {
        ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("PicUri");
                PicUrl picUrl = new PicUrl();
                picUrl.setPicUrl(url);
                picUrls.add(picUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return picUrls;
    }

    /**
     * 获取主工程进度
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<Progress> getProgress(String jsonData) {
        ArrayList<Progress> list = new ArrayList<Progress>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String time = jsonObject.getString("BeginDateTime");
                String content = jsonObject.getString("TaskDetail");
                Progress progress = new Progress();
                progress.setTime(time);
                progress.setContent(content);
                list.add(progress);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取巡检计划
     *
     * @param jsonData
     * @return
     */
    public static List<BusinessByWorker> getInspection(String jsonData) {
        List<BusinessByWorker> list = new ArrayList<BusinessByWorker>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String areaNo = jsonObject.getString("AreaNo");
                String areaName = jsonObject.getString("AreaName");
                String type = jsonObject.getString("PlanClass");
                String state = jsonObject.getString("State");
                String beginTime = jsonObject.getString("BeginTime").replace("/", "-");
                String endTime = jsonObject.getString("StopTime").replace("/", "-");
                String deadline = endTime;

                BusinessByWorker businessByWorker = new BusinessByWorker();
                businessByWorker.setOrderId(areaNo + "/" + areaName);
                businessByWorker.setType(type);
                businessByWorker.setState(state);
                businessByWorker.setReceivedTime(beginTime);
                businessByWorker.setDeadline(deadline);
                businessByWorker.setFlag("巡检");

                ArrayList<Inspection> inspectionList = new ArrayList<Inspection>();
                JSONArray jsonArray1 = jsonObject.getJSONArray("DelatInf");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String No = jsonObject1.getString("FileNo");
                    String name = jsonObject1.getString("FileNm");
                    String model = jsonObject1.getString("FileClass");
                    String address = jsonObject1.getString("FileAddress");
                    String gps = jsonObject1.getString("FileGps");
                    String longitude = "0";
                    String latitude = "0";
                    if (!gps.equals("")) {
                        longitude = gps.split("，")[0];
                        latitude = gps.split("，")[1];
                    }
                    String valveNo = jsonObject1.getString("ValveNo");
                    String valveGNo = jsonObject1.getString("ValveGNo");
                    String vfType = jsonObject1.getString("VfClass");
                    String checkFlag = jsonObject1.getString("CheckFlag");
                    Inspection inspection = new Inspection();
                    inspection.setNo(No);
                    inspection.setName(name);
                    inspection.setModel(model);
                    inspection.setAddress(address);
                    inspection.setLongitude(Double.valueOf(longitude));
                    inspection.setLatitude(Double.valueOf(latitude));
                    inspection.setValveNo(valveNo);
                    inspection.setValveGNo(valveGNo);
                    inspection.setType(vfType);
                    inspection.setCheckFlag(checkFlag);
                    inspectionList.add(inspection);
                }

                businessByWorker.setInspectionInfos(inspectionList);
                list.add(businessByWorker);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取机械审核信息
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<MachineVerify> getMachineVerify(String jsonData) {
        ArrayList<MachineVerify> list = new ArrayList<MachineVerify>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("BillNo");
                String applicant = jsonObject.getString("SaveName");
                String useTime = jsonObject.getString("BeginDate");
                String returnTime = jsonObject.getString("EstimateFinishDate");
                String useAdress = jsonObject.getString("ProAddress");
                String remarks = jsonObject.getString("Remark");
                String state = jsonObject.getString("State");

                MachineVerify machineVerify = new MachineVerify();
                machineVerify.setId(id);
                machineVerify.setApplicant(applicant);
                machineVerify.setUseTime(useTime);
                machineVerify.setReturnTime(returnTime);
                machineVerify.setUseAdress(useAdress);
                machineVerify.setRemarks(remarks);
                machineVerify.setState(state);

                ArrayList<MachineVerifyInfo> infos = new ArrayList<MachineVerifyInfo>();
                JSONArray jsonArray1 = jsonObject.getJSONArray("MachineNeedDetail");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String name = jsonObject1.getString("MachineName");
                    String applyNum = jsonObject1.getString("Qty");
                    String sendNum = jsonObject1.getString("SendQty");
                    MachineVerifyInfo info = new MachineVerifyInfo();
                    info.setName(name);
                    info.setApplyNum(applyNum);
                    info.setSendNum(sendNum);
                    infos.add(info);
                }

                machineVerify.setMachineVerifyInfoList(infos);
                list.add(machineVerify);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取材料审核信息
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<SuppliesVerify> getSuppliesVerify(String jsonData) {
        ArrayList<SuppliesVerify> list = new ArrayList<SuppliesVerify>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String No = jsonObject.getString("BillNo");
                String applicant = jsonObject.getString("SaveName");
                String useTime = jsonObject.getString("UseDateTime");
                String remarks = jsonObject.getString("Remark");
                String state = jsonObject.getString("State");
                String billType = jsonObject.getString("BillType");
                if (billType.equals("工程材料")) {
                    SuppliesVerify suppliesVerify = new SuppliesVerify();
                    suppliesVerify.setNo(No);
                    suppliesVerify.setApplicant(applicant);
                    suppliesVerify.setUseTime(useTime);
                    suppliesVerify.setRemarks(remarks);
                    suppliesVerify.setState(state);

                    if (!jsonObject.getString("MaterialDetail").equals("")) {
                        ArrayList<SuppliesVerifyInfo> infos = new ArrayList<SuppliesVerifyInfo>();
                        JSONArray jsonArray1 = jsonObject.getJSONArray("MaterialDetail");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                            String name = jsonObject1.getString("MakingName");
                            String spec = jsonObject1.getString("MakingSpace");
                            String unit = jsonObject1.getString("MakingUnit");
                            String applyNum = jsonObject1.getString("NeedQty");
                            String sendNum = jsonObject1.getString("SendQty");
                            SuppliesVerifyInfo info = new SuppliesVerifyInfo();
                            info.setName(name);
                            info.setSpec(spec);
                            info.setUnit(unit);
                            info.setApplyNum(applyNum);
                            info.setSendNum(sendNum);
                            infos.add(info);
                        }
                        suppliesVerify.setSuppliesVerifyInfoList(infos);
                    }

                    list.add(suppliesVerify);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取某工程已派工单
     *
     * @param jsonData
     * @return
     */

    public static ArrayList<BusinessHaveSend> getBusinessHaveSend(String jsonData) {
        ArrayList<BusinessHaveSend> list = new ArrayList<BusinessHaveSend>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String content = jsonObject.getString("Content");
                String state = jsonObject.getString("State");
                String deadline = jsonObject.getString("EstimateFinishDate");
                String executors = jsonObject.getString("InstallUser");

                BusinessHaveSend businessHaveSend = new BusinessHaveSend();
                businessHaveSend.setId(id);
                businessHaveSend.setContent(content);
                businessHaveSend.setState(state);
                businessHaveSend.setDeadline(deadline);
                businessHaveSend.setExecutors(executors);
                list.add(businessHaveSend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取问题汇报信息
     *
     * @param jsonData
     * @return
     */
    public static List<ReportInfo> getProblemReportInfo(String jsonData) {
        List<ReportInfo> list = new ArrayList<ReportInfo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String fileNo = jsonObject.getString("FileNo");
                String content = jsonObject.getString("Reason");
                String state = jsonObject.getString("FileState").trim();
                String picNum = jsonObject.getString("PicQty");
                String reportor = jsonObject.getString("SaveName");
                String reportTime = jsonObject.getString("SaveDate");
                String handleResult = jsonObject.getString("HandleResult");
                String handleName = jsonObject.getString("HandleName");

                ReportInfo info = new ReportInfo();
                info.setId(id);
                info.setFileNo(fileNo);
                info.setContent(content);
                info.setPicNum(picNum);
                info.setReportor(reportor);
                info.setReportTime(reportTime);
                info.setHandleName(handleName);
                info.setHandleResult(handleResult);
                if (state.equals("停止")) {
                    info.setState("0");
                }
                if (state.equals("继续")) {
                    info.setState("1");
                }

                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取进度汇报信息
     *
     * @param jsonData
     * @return
     */
    public static List<ReportInfo> getProgressReportInfo(String jsonData) {
        List<ReportInfo> list = new ArrayList<ReportInfo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String fileNo = jsonObject.getString("FileNo");
                String content = jsonObject.getString("TaskDetail");
                String picNum = jsonObject.getString("PicQty");
                String reportor = jsonObject.getString("SaveName");
                String reportTime = jsonObject.getString("SaveDate");

                ReportInfo info = new ReportInfo();
                info.setId(id);
                info.setFileNo(fileNo);
                info.setContent(content);
                info.setPicNum(picNum);
                info.setReportor(reportor);
                info.setReportTime(reportTime);

                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查看已派工单工程量信息-施工内容
     *
     * @param jsonData
     * @return
     */
    public static List<ReportInfoProjectAmount> getProjectAmountReportInfo(String jsonData) {
        List<ReportInfoProjectAmount> list = new ArrayList<ReportInfoProjectAmount>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String content = jsonObject.getString("ConsContent");
                String civil = jsonObject.getString("EarthWorkContent");
                String feeType = jsonObject.getString("Receivables");
                String state = jsonObject.getString("State");
                if (state.equals("保存")) {
                    state = "未审核";
                }
                if (state.equals("审核")) {
                    state = "已审核";
                }
                String reportName = jsonObject.getString("SaveName");
                String reportData = jsonObject.getString("SaveDate");
                String checkDate = jsonObject.getString("CheckDate");

                ReportInfoProjectAmount info = new ReportInfoProjectAmount();
                info.setId(id);
                info.setContent(content);
                info.setCivil(civil);
                info.setFeeType(feeType);
                info.setState(state);
                info.setReportName(reportName);
                info.setReportDate(reportData);
                info.setCheckData(checkDate);
                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查看已派工单工程量信息—材料
     *
     * @param jsonData
     * @return
     */
    public static List<Supplies> getProjectAmountReportInfoSupplies(String jsonData) {
        List<Supplies> list = new ArrayList<Supplies>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("MakingNo");
                String name = jsonObject.getString("MakingName");
                String spec = jsonObject.getString("MakingSpace");
                String unit = jsonObject.getString("MakingUnit");
                String num = jsonObject.getString("MakingQty");

                Supplies supplies = new Supplies();
                supplies.setId(id);
                supplies.setName(name);
                supplies.setSpec(spec);
                supplies.setUnit(unit);
                supplies.setNum(num);
                list.add(supplies);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取监听材料申请单状态
     *
     * @param jsonData
     * @return
     */
    public static List<SuppliesNo> getLitenerSuppliesApplyState(String jsonData) {
        List<SuppliesNo> list = new ArrayList<SuppliesNo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String applyId = jsonObject.getString("BillNo");
                String state = jsonObject.getString("State");
                if (state.equals("保存")) {
                    state = "申请中";
                }
                if (state.equals("审核")) {
                    state = "已审批";
                }
                if (state.equals("不通过")) {
                    state = "不批准";
                }
                String approvalTime = jsonObject.getString("CheckDate");
                String reason = jsonObject.getString("UnPassReason");

                SuppliesNo suppliesNo = new SuppliesNo();
                suppliesNo.setApplyId(applyId);
                suppliesNo.setApplyState(state);
                suppliesNo.setApprovalTime(approvalTime);
                suppliesNo.setReason(reason);
                list.add(suppliesNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取我的工单的材料申请单
     *
     * @param jsonData
     * @return
     */
    public static List<SuppliesNo> getMyOrderSuppliesNoByApply(String jsonData) {
        List<SuppliesNo> list = new ArrayList<SuppliesNo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String billType = jsonObject.getString("BillType");
                String applyId = "";
                String returnId = "";
                String applyState = "";
                String returnState = "";
                String applyTime = "";
                String returnTime = "";
                if (billType.equals("工程材料")) {
                    applyId = jsonObject.getString("BillNo");
                    applyState = jsonObject.getString("State");
                    applyTime = jsonObject.getString("SaveDate");
                    if (applyState.equals("保存")) {
                        applyState = "申请中";
                    }
                    if (applyState.equals("审核")) {
                        applyState = "已审批";
                    }
                    if (applyState.equals("不通过")) {
                        applyState = "不批准";
                    }
                }
                if (billType.equals("工程退料")) {
                    returnId = jsonObject.getString("BillNo");
                    returnState = jsonObject.getString("State");
                    returnTime = jsonObject.getString("SaveDate");
                    if (returnState.equals("保存")) {
                        returnState = "申请中";
                    }
                    if (returnState.equals("审核")) {
                        returnState = "可退回";
                    }
                    if (returnState.equals("不通过")) {
                        returnState = "不批准";
                    }
                }

                String approvalTime = jsonObject.getString("CheckDate");
                String useTime = jsonObject.getString("UseDateTime");
                String reason = jsonObject.getString("UnPassReason");
                String remark = jsonObject.getString("Remark");

                ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
                JSONArray jsonArray1 = jsonObject.getJSONArray("MaterialDetail");
                int count = 0;
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String no = jsonObject1.getString("MakingNo");
                    String name = jsonObject1.getString("MakingName");
                    String spec = jsonObject1.getString("MakingSpace");
                    String unit = jsonObject1.getString("MakingUnit");
                    String applyNum = jsonObject1.getString("NeedQty");
                    String sendNum = jsonObject1.getString("SendQty");
                    String surplusNum = jsonObject1.getString("DiffQty");
                    if (surplusNum.equals("0.00")) {
                        count++;
                    }

                    Supplies supplies = new Supplies();
                    supplies.setId(no);
                    supplies.setName(name);
                    supplies.setSpec(spec);
                    supplies.setUnit(unit);
                    supplies.setApplyNum(applyNum);
                    supplies.setSendNum(sendNum);
                    suppliesList.add(supplies);
                }

                if (billType.equals("工程退料") && count > 0 && count == jsonArray1.length()) {
                    returnState = "已退回";
                }

                SuppliesNo suppliesNo = new SuppliesNo();
                suppliesNo.setApplyId(applyId);
                suppliesNo.setApplyState(applyState);
                suppliesNo.setApplyTime(applyTime);
                suppliesNo.setUseTime(useTime);
                suppliesNo.setReturnId(returnId);
                suppliesNo.setReturnState(returnState);
                suppliesNo.setReturnTime(returnTime);
                suppliesNo.setApprovalTime(approvalTime);
                suppliesNo.setReason(reason);
                suppliesNo.setRemarks(remark);
                suppliesNo.setSuppliesList(suppliesList);
                list.add(suppliesNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取我的工单的材料发放单
     *
     * @param jsonData
     * @return
     */
    public static List<SuppliesNo> getMyOrderSuppliesNoBySend(String jsonData) {
        List<SuppliesNo> list = new ArrayList<SuppliesNo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String billType = jsonObject.getString("BillType");
                if (billType.equals("工程材料")) {
                    String applyId = jsonObject.getString("MaterialNeedBillNo");
                    String receivedState = jsonObject.getString("State");
                    if (receivedState.equals("审核")) {
                        receivedState = "可领用";
                    } else if (receivedState.equals("执行")) {
                        receivedState = "已领出";
                    } else {
                        continue;
                    }
                    String billNo = jsonObject.getString("BillNo");
                    String receivedTime = jsonObject.getString("FinishDate");
                    String str = jsonObject.getString("MaterialDetail");
                    ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
                    if (!str.equals("")) {
                        JSONArray jsonArray1 = jsonObject.getJSONArray("MaterialDetail");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                            String no = jsonObject1.getString("MakingNo");
                            String name = jsonObject1.getString("MakingName");
                            String spec = jsonObject1.getString("MakingSpace");
                            String unit = jsonObject1.getString("MakingUnit");
                            String sendNum = jsonObject1.getString("Qty");

                            Supplies supplies = new Supplies();
                            supplies.setId(no);
                            supplies.setName(name);
                            supplies.setSpec(spec);
                            supplies.setUnit(unit);
                            supplies.setSendNum(sendNum);
                            suppliesList.add(supplies);
                        }
                    }
                    SuppliesNo suppliesNo = new SuppliesNo();
                    suppliesNo.setApplyId(applyId);
                    suppliesNo.setReceivedId(billNo);
                    suppliesNo.setReceivedState(receivedState);
                    suppliesNo.setReceivedTime(receivedTime);
                    suppliesNo.setSuppliesList(suppliesList);
                    list.add(suppliesNo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取监听哪些材料可以领用-发放单
     *
     * @param jsonData
     * @return
     */
    public static List<SuppliesNo> getLitenerSuppliesNoReceivedState(String jsonData) {
        List<SuppliesNo> list = new ArrayList<SuppliesNo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String receivedId = jsonObject.getString("BillNo");
                String applyId = jsonObject.getString("MaterialNeedBillNo");
                String state = jsonObject.getString("State");
                if (state.equals("审核")) {
                    state = "可领用";
                }
                if (state.equals("执行")) {
                    state = "已领出";
                }
                SuppliesNo suppliesNo = new SuppliesNo();
                suppliesNo.setReceivedId(receivedId);
                suppliesNo.setApplyId(applyId);
                suppliesNo.setReceivedState(state);
                list.add(suppliesNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取监听哪些材料可以领用-材料
     *
     * @param jsonData
     * @return
     */
    public static List<Supplies> getLitenerSuppliesReceivedState(String jsonData) {
        List<Supplies> list = new ArrayList<Supplies>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject.getJSONArray("MaterialDetail");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String id = jsonObject1.getString("MakingNo");
                    String name = jsonObject1.getString("MakingName");
                    String spec = jsonObject1.getString("MakingSpace");
                    String unit = jsonObject1.getString("MakingUnit");
                    String applyNum = jsonObject1.getString("NeedQty");
                    String sendNum = jsonObject1.getString("Qty");

                    Supplies supplies = new Supplies();
                    supplies.setId(id);
                    supplies.setName(name);
                    supplies.setSpec(spec);
                    supplies.setUnit(unit);
                    supplies.setApplyNum(applyNum);
                    supplies.setSendNum(sendNum);
                    list.add(supplies);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取工程量材料--（已领材料）
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<Supplies> getReportProjectAmountSupplies(String jsonData) {
        ArrayList<Supplies> list = new ArrayList<Supplies>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("MakingNo");
                String name = jsonObject.getString("MakingName");
                String spec = jsonObject.getString("MakingSpace");
                String unit = jsonObject.getString("MakingUnit");
                String num = jsonObject.getString("NeedQty");

                Supplies supplies = new Supplies();
                supplies.setId(id);
                supplies.setName(name);
                supplies.setSpec(spec);
                supplies.setUnit(unit);
                supplies.setNum(num);
                list.add(supplies);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取某工程的机械申请单
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<MachineNo> getMachineApplyNo(String jsonData) {
        ArrayList<MachineNo> list = new ArrayList<MachineNo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String billType = jsonObject.getString("BillType");
                String applyId = "";
                String returnId = "";
                String applyState = "";
                String returnState = "";
                String applyTime = "";
                String returnApplyTime = "";
                String useAddress = "";
                String returnAddress = "";
                ArrayList<Machine> machineList = new ArrayList<Machine>();
                if (billType.equals("工程机械申请")) {
                    applyId = jsonObject.getString("BillNo");
                    applyState = jsonObject.getString("State");
                    applyTime = jsonObject.getString("SaveDate");
                    useAddress = jsonObject.getString("ProAddress");
                    if (applyState.equals("保存")) {
                        applyState = "申请中";
                    }
                    if (applyState.equals("审核")) {
                        applyState = "已审批";
                    }
                    if (applyState.equals("停止")) {
                        applyState = "不批准";
                    }

                    JSONArray jsonArray1 = jsonObject.getJSONArray("MachineNeedDetail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name = jsonObject1.getString("MachineName");
                        String unit = jsonObject1.getString("MachineUnit");
                        String applyNum = jsonObject1.getString("Qty");
                        String sendNum = jsonObject1.getString("SendQty");

                        Machine machine = new Machine();
                        machine.setName(name);
                        machine.setUnit(unit);
                        machine.setApplyNum(Integer.valueOf(applyNum));
                        machine.setSendNum(Integer.valueOf(sendNum));
                        machineList.add(machine);
                    }

                }

                if (billType.equals("正常机械退还") || billType.equals("损坏机械退还")) {
                    returnId = jsonObject.getString("BillNo");
                    returnState = jsonObject.getString("State");
                    returnApplyTime = jsonObject.getString("SaveDate");
                    returnAddress = jsonObject.getString("ProAddress");
                    if (returnState.equals("保存")) {
                        returnState = "申请中";
                    }
                    if (returnState.equals("审核")) {
                        returnState = "可退回";
                    }
                    if (returnState.equals("停止")) {
                        returnState = "不批准";
                    }
                    if (billType.equals("正常机械退还")) {
                        billType = "正常";
                    }
                    if (billType.equals("损坏机械退还")) {
                        billType = "损坏";
                    }

                    JSONArray jsonArray1 = jsonObject.getJSONArray("MachineSendDetail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String no = jsonObject1.getString("MachineNo");
                        String name = jsonObject1.getString("MachineName");
                        String unit = jsonObject1.getString("MachineUnit");
                        String applyNum = jsonObject1.getString("Qty");

                        Machine machine = new Machine();
                        machine.setId(no);
                        machine.setName(name);
                        machine.setUnit(unit);
                        machine.setApplyNum(Integer.valueOf(applyNum));
                        machineList.add(machine);
                    }
                }

                String approvalTime = jsonObject.getString("CheckDate");
                String useTime = jsonObject.getString("BeginDate");
                String returnTime = jsonObject.getString("EstimateFinishDate");
                String reason = jsonObject.getString("UnPassReason");
                String remark = jsonObject.getString("Remark");

                MachineNo machineNo = new MachineNo();
                machineNo.setApplyId(applyId);
                machineNo.setReturnId(returnId);
                machineNo.setApplyTime(applyTime);
                machineNo.setUseTime(useTime);
                machineNo.setReturnTime(returnTime);
                machineNo.setUseAddress(useAddress);
                machineNo.setRemarks(remark);
                machineNo.setApprovalTime(approvalTime);
                machineNo.setApplyState(applyState);
                machineNo.setReturnState(returnState);
                machineNo.setReturnType(billType);
                machineNo.setReturnApplyTime(returnApplyTime);
                machineNo.setReturnAddress(returnAddress);
                machineNo.setReason(reason);
                machineNo.setMachineList(machineList);
                list.add(machineNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取某工程的已发放的机械
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<Machine> getMachineByReceived(String jsonData) {
        ArrayList<Machine> list = new ArrayList<Machine>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String no = jsonObject.getString("MachineNo");
                String name = jsonObject.getString("MachineName");
                String unit = jsonObject.getString("MachineUnit");
                String sendNum = jsonObject.getString("Qty");
                String state = jsonObject.getString("State");
                if(state.equals("送机中")){
                    state = "已安排";
                }
                if(state.equals("已送到")){
                    state = "已送达";
                }
                Machine machine = new Machine();
                machine.setId(no);
                machine.setName(name);
                machine.setUnit(unit);
                machine.setSendNum(Integer.valueOf(sendNum));
                machine.setState(state);
                list.add(machine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取监听机械申请单状态
     *
     * @param jsonData
     * @return
     */
    public static List<MachineNo> getLitenerMachineApplyState(String jsonData) {
        List<MachineNo> list = new ArrayList<MachineNo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("BillNo");
                String state = jsonObject.getString("State");
                if (state.equals("保存")) {
                    state = "申请中";
                }
                if (state.equals("审核")) {
                    state = "已审批";
                }
                if (state.equals("停止")) {
                    state = "不批准";
                }
                String approvalTime = jsonObject.getString("CheckDate");
                String reason = jsonObject.getString("UnPassReason");

                MachineNo machineNo = new MachineNo();
                machineNo.setApplyId(id);
                machineNo.setApplyState(state);
                machineNo.setApprovalTime(approvalTime);
                machineNo.setReason(reason);
                list.add(machineNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取监听哪些机械可以领用
     *
     * @param jsonData
     * @return
     */
    public static List<Machine> getLitenerMachineReceivedState(String jsonData) {
        List<Machine> list = new ArrayList<Machine>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String applyId = jsonObject.getString("BillNo");
                if (!jsonObject.getString("MachineSendDetail").equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("MachineSendDetail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String id = jsonObject1.getString("MachineNo");
                        String name = jsonObject1.getString("MachineName");
                        String unit = jsonObject1.getString("MachineUnit");
                        String sendNum = jsonObject1.getString("Qty");

                        Machine machine = new Machine();
                        machine.setApplyId(applyId);
                        machine.setId(id);
                        machine.setName(name);
                        machine.setUnit(unit);
                        machine.setSendNum(Integer.valueOf(sendNum));
                        machine.setState("已安排");
                        list.add(machine);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取监听机械安排数量变化
     *
     * @param jsonData
     * @return
     */
    public static List<Machine> getLitenerMachineSendNum(String jsonData) {
        List<Machine> list = new ArrayList<Machine>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String applyId = jsonObject.getString("BillNo");
                if (!jsonObject.getString("MachineNeedDetail").equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("MachineNeedDetail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name = jsonObject1.getString("MachineName");
                        String unit = jsonObject1.getString("MachineUnit");
                        String applyNum = jsonObject1.getString("Qty");
                        String sendNum = jsonObject1.getString("SendQty");

                        Machine machine = new Machine();
                        machine.setApplyId(applyId);
                        machine.setName(name);
                        machine.setUnit(unit);
                        machine.setApplyNum(Integer.valueOf(applyNum));
                        machine.setSendNum(Integer.valueOf(sendNum));
                        list.add(machine);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取待送机械列表
     *
     * @param jsonData
     * @return
     */
    public static List<SendMachine> getSendMachine(String jsonData) {
        List<SendMachine> list = new ArrayList<SendMachine>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String sendId = jsonObject.getString("Sendid");
                String orderId = jsonObject.getString("FileNo");
                String billNo = jsonObject.getString("BillNo");
                String sendAddress = jsonObject.getString("ProAddress");
                String getAddress = jsonObject.getString("MachineAddress");
                String machineNo = jsonObject.getString("MachineNo");
                String machineName = jsonObject.getString("MachineName");
                String sendData = jsonObject.getString("SendDate");
                String applyName = jsonObject.getString("SaveName");
                String machineNum = jsonObject.getString("Qty");

                SendMachine sendMachine = new SendMachine();
                sendMachine.setSendId(sendId);
                sendMachine.setOrderId(orderId);
                sendMachine.setBillNo(billNo);
                sendMachine.setSendAddress(sendAddress);
                sendMachine.setGetAddress(getAddress);
                sendMachine.setMachineNo(machineNo);
                sendMachine.setMachineName(machineName);
                sendMachine.setApplyName(applyName);
                sendMachine.setSendData(sendData);
                sendMachine.setMachineNum(machineNum);
                list.add(sendMachine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取待退机械列表
     *
     * @param jsonData
     * @return
     */
    public static List<ReturnMachine> getReturnMachine(String jsonData) {
        List<ReturnMachine> list = new ArrayList<ReturnMachine>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String sendId = jsonObject.getString("Sendid");
                String orderId = jsonObject.getString("FileNo");
                String billNo = jsonObject.getString("BillNo");
                String sendAddress = jsonObject.getString("MachineAddress");
                String address = jsonObject.getString("ProAddress");
                String machineNo = jsonObject.getString("MachineNo");
                String machineName = jsonObject.getString("MachineName");
                String machineNum = jsonObject.getString("Qty");
                String returnName = jsonObject.getString("SaveName");
                String returnTime = jsonObject.getString("SendDate");
                String type = jsonObject.getString("BillType");

                ReturnMachine returnMachine = new ReturnMachine();
                returnMachine.setSendId(sendId);
                returnMachine.setOrderId(orderId);
                returnMachine.setBillNo(billNo);
                returnMachine.setSendAddress(sendAddress);
                returnMachine.setAddress(address);
                returnMachine.setMachineNo(machineNo);
                returnMachine.setMachineName(machineName);
                returnMachine.setMachineNum(machineNum);
                returnMachine.setReturnName(returnName);
                returnMachine.setReturnTiem(returnTime);
                returnMachine.setType(type);
                list.add(returnMachine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取完工信息
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<Acceptance> getReportCompleteInfo(String jsonData) {
        ArrayList<Acceptance> list = new ArrayList<Acceptance>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderId = jsonObject.getString("FileNo");
                String orderType = jsonObject.getString("ConsTypeName");

                String KHMaking = jsonObject.getString("KHMaking");
                ArrayList<Supplies> suppliesByClient = new ArrayList<Supplies>();
                if (!KHMaking.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("KHMaking");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String id = jsonObject1.getString("MakingNo");
                        String name = jsonObject1.getString("MakingName");
                        String spec = jsonObject1.getString("MakingSpace");
                        String unit = jsonObject1.getString("MakingUnit");
                        String num = jsonObject1.getString("MakingQty");

                        Supplies supplies = new Supplies();
                        supplies.setId(id);
                        supplies.setName(name);
                        supplies.setSpec(spec);
                        supplies.setUnit(unit);
                        supplies.setNum(num);
                        suppliesByClient.add(supplies);
                    }
                }

                String SWMaking = jsonObject.getString("SWMaking");
                ArrayList<Supplies> suppliesByWater = new ArrayList<Supplies>();
                if (!SWMaking.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("SWMaking");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String id = jsonObject1.getString("MakingNo");
                        String name = jsonObject1.getString("MakingName");
                        String spec = jsonObject1.getString("MakingSpace");
                        String unit = jsonObject1.getString("MakingUnit");
                        String num = jsonObject1.getString("MakingQty");

                        Supplies supplies = new Supplies();
                        supplies.setId(id);
                        supplies.setName(name);
                        supplies.setSpec(spec);
                        supplies.setUnit(unit);
                        supplies.setNum(num);
                        suppliesByWater.add(supplies);
                    }
                }

                String detail = jsonObject.getString("Detail");
                ArrayList<DetailedInfo> detailedInfos = new ArrayList<DetailedInfo>();
                if (!detail.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("Detail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String key = jsonObject1.getString("key");
                        String value = jsonObject1.getString("value");

                        if (!key.equals("报事照片数") && !key.equals("任务照片数") && !key.equals("工程确认照片数")
                                && !key.equals("完工照片数") && !key.equals("定位照片数") && !key.equals("意外情况照片数")) {
                            DetailedInfo detailedInfo = new DetailedInfo();
                            detailedInfo.setKey(key);
                            detailedInfo.setValue(value);
                            detailedInfos.add(detailedInfo);
                        }

                    }
                }

                Acceptance acceptance = new Acceptance();
                acceptance.setOrderId(orderId);
                acceptance.setOrderType(orderType);
                acceptance.setSuppliesByClient(suppliesByClient);
                acceptance.setSuppliesByWater(suppliesByWater);
                acceptance.setDetailedInfos(detailedInfos);
                list.add(acceptance);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取完工图片
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<PicUrl> getReportCompletePicUrl(String jsonData) {
        ArrayList<PicUrl> list = new ArrayList<PicUrl>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("PicUri");
                PicUrl picUrl = new PicUrl();
                picUrl.setPicUrl(url);
                list.add(picUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取安全检查工程
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<SafetyInspectTask> getSafetyInspectTask(String jsonData) {
        ArrayList<SafetyInspectTask> list = new ArrayList<SafetyInspectTask>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("TaskNo");
                String name = jsonObject.getString("TaskName");
                String type = jsonObject.getString("TaskClass");
                String gps = jsonObject.getString("TaskGps");
                String formId = jsonObject.getString("TaskRID");
                String longitude = "0";
                String latitude = "0";
                if (!gps.equals("")) {
                    longitude = gps.split("，")[0];
                    latitude = gps.split("，")[1];
                }

                SafetyInspectTask task = new SafetyInspectTask();
                task.setId(id);
                task.setName(name);
                task.setType(type);
                task.setLongitude(Double.valueOf(longitude));
                task.setLatitude(Double.valueOf(latitude));
                task.setFormId(formId);
                list.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取安全检查不合格工程
     * @param jsonData
     * @return
     */
    public static ArrayList<SafetyInspectFail> getSafetyInspectFail(String jsonData) {
        ArrayList<SafetyInspectFail> list = new ArrayList<SafetyInspectFail>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderId = jsonObject.getString("ErroeProNo");
                String name = jsonObject.getString("ErrorProName");
                String type = jsonObject.getString("ErroeProClass");

                SafetyInspectFail fail = new SafetyInspectFail();
                fail.setOrderId(orderId);
                fail.setName(name);
                fail.setType(type);
                list.add(fail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取安全检查表项目
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<SafetyInspectFirstItem> getSafetyInspectFormItem(String jsonData) {
        ArrayList<SafetyInspectFirstItem> list = new ArrayList<SafetyInspectFirstItem>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("ItemId");
                String name = jsonObject.getString("ItemName");

                JSONArray jsonArray1 = jsonObject.getJSONArray("ItemDel");
                ArrayList<SafetyInspectSecondItem> childList = new ArrayList<SafetyInspectSecondItem>();
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    String childId = jsonObject1.getString("ItemId");
                    String childName = jsonObject1.getString("ItemName");

                    SafetyInspectSecondItem child = new SafetyInspectSecondItem();
                    child.setId(childId);
                    child.setName(childName);
                    childList.add(child);
                }

                SafetyInspectFirstItem group = new SafetyInspectFirstItem();
                group.setId(id);
                group.setName(name);
                group.setChildList(childList);
                list.add(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取已检查的安全检查表项目
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<SafetyInspectSecondItem> getSafetyInspectFormItemByChecked(String jsonData) {
        ArrayList<SafetyInspectSecondItem> list = new ArrayList<SafetyInspectSecondItem>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("ItemId");
                String name = jsonObject.getString("ItemName");

                SafetyInspectSecondItem item = new SafetyInspectSecondItem();
                item.setId(id);
                item.setName(name);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取安全检查表
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<SafetyInspectForm> getSafetyInspectForm(String jsonData) {
        ArrayList<SafetyInspectForm> list = new ArrayList<SafetyInspectForm>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String situation = jsonObject.getString("TaskCi");
                String process = jsonObject.getString("TaskSi");
                String checkDate = jsonObject.getString("TaskUt");
                String flag = jsonObject.getString("TaskSf");

                String failure = jsonObject.getString("TaskDel");
                ArrayList<SafetyInspectSecondItem> failureList = new ArrayList<SafetyInspectSecondItem>();
                if (!failure.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("TaskDel");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String id = jsonObject1.getString("ItemId");
                        String name = jsonObject1.getString("ItemName");
                        String isHandle = jsonObject1.getString("HandleFlag");
                        SafetyInspectSecondItem failureItem = new SafetyInspectSecondItem();
                        failureItem.setId(id);
                        failureItem.setName(name);
                        failureItem.setIsHandle(isHandle);
                        failureList.add(failureItem);
                    }
                }

                String picurlStr = jsonObject.getString("TaskPicD");
                ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
                if (!picurlStr.equals("")) {
                    JSONArray jsonArray2 = jsonObject.getJSONArray("TaskPicD");
                    for (int k = 0; k < jsonArray2.length(); k++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                        String picUri = jsonObject2.getString("PicUri");
                        PicUrl picUrl = new PicUrl();
                        picUrl.setPicUrl(picUri);
                        picUrls.add(picUrl);
                    }
                }

                SafetyInspectForm form = new SafetyInspectForm();
                form.setSituation(situation);
                form.setProcess(process);
                form.setCheckDate(checkDate);
                form.setFlag(flag);
                form.setFailure(failureList);
                form.setPicUrls(picUrls);
                list.add(form);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取机械申请单列表（机械组长界面）
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<LeaderMachineApplyBill> getLeaderMachineApplyBill(String jsonData) {
        ArrayList<LeaderMachineApplyBill> list = new ArrayList<LeaderMachineApplyBill>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String billNo = jsonObject.getString("BillNo");
                String billType = jsonObject.getString("BillType");
                String orderId = jsonObject.getString("FileNo");
                String useAddress = jsonObject.getString("ProAddress");
                String useDate = jsonObject.getString("BeginDate");
                String finishDate = jsonObject.getString("EstimateFinishDate");
                String applyName = jsonObject.getString("SaveName");
                String applyDate = jsonObject.getString("SaveDate");
                String remark = jsonObject.getString("Remark");
                String state = jsonObject.getString("State");
                if (state.equals("保存")) {
                    state = "未审核";
                }
                if (state.equals("审核")) {
                    state = "已审核";
                }

                String machineNeedDetail = jsonObject.getString("MachineNeedDetail");
                ArrayList<LeaderMachineApplyBillByMachine> machineList = new ArrayList<LeaderMachineApplyBillByMachine>();
                if (!machineNeedDetail.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("MachineNeedDetail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name = jsonObject1.getString("MachineName");
                        String unit = jsonObject1.getString("MachineUnit");
                        String applyNum = jsonObject1.getString("Qty");
                        String sendNum = jsonObject1.getString("SendQty");

                        LeaderMachineApplyBillByMachine machine = new LeaderMachineApplyBillByMachine();
                        machine.setName(name);
                        machine.setUnit(unit);
                        machine.setApplyNum(applyNum);
                        machine.setSendNum(sendNum);
                        machineList.add(machine);
                    }
                }

                LeaderMachineApplyBill bill = new LeaderMachineApplyBill();
                bill.setBillNo(billNo);
                bill.setBillType(billType);
                bill.setOrderId(orderId);
                bill.setUseAddress(useAddress);
                bill.setUseDate(useDate);
                bill.setFinishDate(finishDate);
                bill.setApplyName(applyName);
                bill.setApplyDate(applyDate);
                bill.setRemark(remark);
                bill.setState(state);
                bill.setMachineList(machineList);
                list.add(bill);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询某机械信息
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<LeaderMachineState> getLeaderMachineState(String jsonData) {
        ArrayList<LeaderMachineState> list = new ArrayList<LeaderMachineState>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String machineNo = jsonObject.getString("MachineNo");
                String machineName = jsonObject.getString("MachineName");
                String machineUnit = jsonObject.getString("MachineUnit");
                String state = jsonObject.getString("State");
                String address = jsonObject.getString("MachineAddress");

                LeaderMachineState machineState = new LeaderMachineState();
                machineState.setMachineNo(machineNo);
                machineState.setMachineName(machineName);
                machineState.setMachineUnit(machineUnit);
                machineState.setState(state);
                machineState.setAddress(address);
                list.add(machineState);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取机械退回单列表（机械组长界面）
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<LeaderMachineReturnBill> getLeaderMachineReturnBill(String jsonData) {
        ArrayList<LeaderMachineReturnBill> list = new ArrayList<LeaderMachineReturnBill>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String billNo = jsonObject.getString("BillNo");
                String billType = jsonObject.getString("BillType");
                String orderId = jsonObject.getString("FileNo");
                String returnAddress = jsonObject.getString("ProAddress");
                String returnDate = jsonObject.getString("EstimateFinishDate");
                String applyName = jsonObject.getString("SaveName");
                String applyDate = jsonObject.getString("SaveDate");
                String remark = jsonObject.getString("Remark");
                String state = jsonObject.getString("State");
                if (state.equals("保存")) {
                    state = "未审核";
                }
                if (state.equals("审核")) {
                    state = "已审核";
                }

                String machineNeedDetail = jsonObject.getString("MachineSendDetail");
                ArrayList<LeaderMachineReturnBillByMachine> machineList = new ArrayList<LeaderMachineReturnBillByMachine>();
                if (!machineNeedDetail.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("MachineSendDetail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String machineNo = jsonObject1.getString("MachineNo");
                        String name = jsonObject1.getString("MachineName");
                        String unit = jsonObject1.getString("MachineUnit");
                        String num = jsonObject1.getString("NeedQty");
                        String flag = jsonObject1.getString("SendName");
                        if (flag.equals("")) {
                            flag = "0";
                        } else {
                            flag = "1";
                        }

                        LeaderMachineReturnBillByMachine machine = new LeaderMachineReturnBillByMachine();
                        machine.setMachineNo(machineNo);
                        machine.setName(name);
                        machine.setUnit(unit);
                        machine.setNum(num);
                        machine.setFlag(flag);
                        machineList.add(machine);
                    }
                }

                LeaderMachineReturnBill bill = new LeaderMachineReturnBill();
                bill.setBillNo(billNo);
                bill.setBillType(billType);
                bill.setOrderId(orderId);
                bill.setReturnAddress(returnAddress);
                bill.setReturnDate(returnDate);
                bill.setApplyName(applyName);
                bill.setApplyDate(applyDate);
                bill.setRemark(remark);
                bill.setState(state);
                bill.setMachineList(machineList);
                list.add(bill);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取工程验收单
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<Acceptance> getAcceptanceInfo(String jsonData) {
        ArrayList<Acceptance> list = new ArrayList<Acceptance>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderId = jsonObject.getString("FileNo");
                String orderType = jsonObject.getString("ConsTypeName");
                String KHContent = jsonObject.getString("KHConsContent");
                String SWContent = jsonObject.getString("SWConsContent");
                String KHCivil = jsonObject.getString("KHEarthWorkContent");
                String SWCivil = jsonObject.getString("SWEarthWorkContent");

                String KHMaking = jsonObject.getString("KHMaking");
                ArrayList<Supplies> suppliesByClient = new ArrayList<Supplies>();
                if (!KHMaking.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("KHMaking");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String id = jsonObject1.getString("MakingNo");
                        String name = jsonObject1.getString("MakingName");
                        String spec = jsonObject1.getString("MakingSpace");
                        String unit = jsonObject1.getString("MakingUnit");
                        String num = jsonObject1.getString("MakingQty");

                        Supplies supplies = new Supplies();
                        supplies.setId(id);
                        supplies.setName(name);
                        supplies.setSpec(spec);
                        supplies.setUnit(unit);
                        supplies.setNum(num);
                        suppliesByClient.add(supplies);
                    }
                }

                String SWMaking = jsonObject.getString("SWMaking");
                ArrayList<Supplies> suppliesByWater = new ArrayList<Supplies>();
                if (!SWMaking.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("SWMaking");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String id = jsonObject1.getString("MakingNo");
                        String name = jsonObject1.getString("MakingName");
                        String spec = jsonObject1.getString("MakingSpace");
                        String unit = jsonObject1.getString("MakingUnit");
                        String num = jsonObject1.getString("MakingQty");

                        Supplies supplies = new Supplies();
                        supplies.setId(id);
                        supplies.setName(name);
                        supplies.setSpec(spec);
                        supplies.setUnit(unit);
                        supplies.setNum(num);
                        suppliesByWater.add(supplies);
                    }
                }

                String detail = jsonObject.getString("Detail");
                ArrayList<DetailedInfo> detailedInfos = new ArrayList<DetailedInfo>();
                if (!detail.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("Detail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String key = jsonObject1.getString("key");
                        String value = jsonObject1.getString("value");

                        if (!key.equals("报事照片数") && !key.equals("任务照片数") && !key.equals("工程确认照片数")
                                && !key.equals("完工照片数") && !key.equals("定位照片数") && !key.equals("意外情况照片数")) {
                            DetailedInfo detailedInfo = new DetailedInfo();
                            detailedInfo.setKey(key);
                            detailedInfo.setValue(value);
                            detailedInfos.add(detailedInfo);
                        }

                    }
                }

                Acceptance acceptance = new Acceptance();
                acceptance.setOrderId(orderId);
                acceptance.setOrderType(orderType);
                acceptance.setKHContent(KHContent);
                acceptance.setKHCivil(KHCivil);
                acceptance.setSWContent(SWContent);
                acceptance.setSWCivil(SWCivil);
                acceptance.setSuppliesByClient(suppliesByClient);
                acceptance.setSuppliesByWater(suppliesByWater);
                acceptance.setDetailedInfos(detailedInfos);
                list.add(acceptance);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取意外情况的原因类型
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<ProblemType> getProblemType(String jsonData) {
        ArrayList<ProblemType> list = new ArrayList<ProblemType>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.getString("AccidentReason");
                String userNo = jsonObject.getString("UserNo");

                ProblemType problemType = new ProblemType();
                problemType.setType(type);
                problemType.setUserNo(userNo);
                list.add(problemType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取组长确认的工程量表格 - 内容
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<QueryProjectAmount> getQueryProjectAmount(String jsonData) {
        ArrayList<QueryProjectAmount> list = new ArrayList<QueryProjectAmount>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String content = jsonObject.getString("ConsContent");
                String civil = jsonObject.getString("EarthWorkContent");
                String approvalName = jsonObject.getString("SaveName");
                String approvalTime = jsonObject.getString("CheckDate");
                String feeType = jsonObject.getString("Receivables");
                String state = jsonObject.getString("State");
                if (state.equals("保存")) {
                    state = "未审核";
                }
                if (state.equals("审核")) {
                    state = "已审核";
                }

                QueryProjectAmount info = new QueryProjectAmount();
                info.setId(id);
                info.setApprovalName(approvalName);
                info.setApprovalTime(approvalTime);
                info.setFeeType(feeType);
                info.setContent(content);
                info.setCivil(civil);
                info.setState(state);

                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取意外情况
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<Accident> getAccidents(String jsonData) {
        ArrayList<Accident> list = new ArrayList<Accident>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String orderId = jsonObject.getString("FileNo");
                String type = jsonObject.getString("Reason");
                String state = jsonObject.getString("FileState");
                String reason = jsonObject.getString("HandleReason");
                String handleName = jsonObject.getString("HandleName");
                String problemName = jsonObject.getString("SaveName");
                String problemTime = jsonObject.getString("SaveDate");

                Accident accident = new Accident();
                accident.setId(id);
                accident.setOrderId(orderId);
                accident.setType(type);
                accident.setState(state);
                accident.setReason(reason);
                accident.setHandleName(handleName);
                accident.setProblemName(problemName);
                accident.setProblemtime(problemTime);

                list.add(accident);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取做过的工程类型的口径
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<String> getCaliber(String jsonData) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String caliber = jsonObject.getString("DrainSize");
                list.add(caliber);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取某工程类型所用材料
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<OrderTypeSupplies> getOrderTypeSupplies(String jsonData) {
        ArrayList<OrderTypeSupplies> list = new ArrayList<OrderTypeSupplies>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderId = jsonObject.getString("FileNo");
                String suppliesJson = jsonObject.getString("MaterialDetail");
                ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
                if (!suppliesJson.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("MaterialDetail");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String no = jsonObject1.getString("MakingNo");
                        String name = jsonObject1.getString("MakingName");
                        String spec = jsonObject1.getString("MakingSpace");
                        String unit = jsonObject1.getString("MakingUnit");
                        String applyNum = jsonObject1.getString("NeedQty");

                        Supplies supplies = new Supplies();
                        supplies.setId(no);
                        supplies.setName(name);
                        supplies.setSpec(spec);
                        supplies.setUnit(unit);
                        supplies.setApplyNum(applyNum);
                        suppliesList.add(supplies);
                    }
                }

                OrderTypeSupplies orderTypeSupplies = new OrderTypeSupplies();
                orderTypeSupplies.setOrderId(orderId);
                orderTypeSupplies.setSuppliesList(suppliesList);
                list.add(orderTypeSupplies);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取代班人员列表
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<User> getReplaceLeader(String jsonData) {
        ArrayList<User> list = new ArrayList<User>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String userNo = jsonObject.getString("UserNo");
                String userName = jsonObject.getString("UserName");

                User user = new User();
                user.setUserNo(userNo);
                user.setUserName(userName);
                list.add(user);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取日常通知信息
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<Notice> getNotice(String jsonData) {
        ArrayList<Notice> list = new ArrayList<Notice>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("NoticeTitle");
                String content = jsonObject.getString("NoticeDetail");
                String promulgator = jsonObject.getString("SaveName");
                String date = jsonObject.getString("SaveDate");
                String department = jsonObject.getString("UserSit");

                Notice notice = new Notice();
                notice.setTitle(title);
                notice.setContent(content);
                notice.setPromulgator(promulgator);
                notice.setDate(date);
                notice.setDepartment(department);
                list.add(notice);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取下属位置信息
     *
     * @param jsonData
     * @return
     */
    public static ArrayList<SubordinateLocation> getSubordinateLocationInfo(String jsonData) {
        ArrayList<SubordinateLocation> list = new ArrayList<SubordinateLocation>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String gps = jsonObject.getString("GpsNo");
                double latitude = 0;
                double longitude = 0;
                if (!gps.equals("")) {
                    String[] str = gps.split(",");
                    latitude = Double.valueOf(str[0]);
                    longitude = Double.valueOf(str[1]);
                }

                String userNo = jsonObject.getString("UserNo");
                String name = jsonObject.getString("UserName");
                String tel = jsonObject.getString("UserTel");
                String lastTime = jsonObject.getString("GpsNoTime");
                String doingTask = jsonObject.getString("UserTask");

                SubordinateLocation subordinateLocation = new SubordinateLocation();
                subordinateLocation.setLatitude(latitude);
                subordinateLocation.setLongitude(longitude);
                subordinateLocation.setUserNo(userNo);
                subordinateLocation.setName(name);
                subordinateLocation.setTel(tel);
                subordinateLocation.setLastTime(lastTime);
                subordinateLocation.setDoingTask(doingTask);
                list.add(subordinateLocation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取所有已派发的工程
     */
    public static ArrayList<BusinessHaveSendByAll> getBusinessHaveSendByAll(String jsonData) {
        ArrayList<BusinessHaveSendByAll> list = new ArrayList<BusinessHaveSendByAll>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderId = jsonObject.getString("FileNo");
                String branchId = jsonObject.getString("AppointInstallID");
                String workerName = jsonObject.getString("InstallUser");
                String workerContent = jsonObject.getString("InstallContent");
                String deadline = jsonObject.getString("EstimateFinishDate").replace("/", "-");

                BusinessHaveSendByAll business = new BusinessHaveSendByAll();
                business.setOrderId(orderId);
                business.setBranchId(branchId);
                business.setWorkerName(workerName);
                business.setWorkContent(workerContent);
                business.setDeadline(deadline);
                list.add(business);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取工程临时信息
     */
    public static ArrayList<TemInfo> getTemInfo(String jsonData) {
        ArrayList<TemInfo> list = new ArrayList<TemInfo>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String message = jsonObject.getString("Message");

                TemInfo temInfo = new TemInfo();
                temInfo.setMessage(message);
                list.add(temInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取工程问题处理结果
     */
    public static ArrayList<ProblemResult> getProblemProcessResult(String jsonData) {
        ArrayList<ProblemResult> list = new ArrayList<ProblemResult>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String reason = jsonObject.getString("Reason");
                String result = jsonObject.getString("HandleResult");

                ProblemResult problemResult = new ProblemResult();
                problemResult.setReason(reason);
                problemResult.setResult(result);
                list.add(problemResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取某一工程不合格项
     */
    public static ArrayList<SafetyInspectFailItem> getSafetyInspectFailItem(String jsonData) {
        ArrayList<SafetyInspectFailItem> list = new ArrayList<SafetyInspectFailItem>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String content = jsonObject.getString("ItemName");
                String recordId = jsonObject.getString("RecordId");
                String isHandle = jsonObject.getString("Flag");
                String isDistributed = jsonObject.getString("ChargeFlag");
                String remark = jsonObject.getString("ChargeInf");
                String worker = jsonObject.getString("ChargeUserName");

                SafetyInspectFailItem item = new SafetyInspectFailItem();
                item.setContent(content);
                item.setRecordId(recordId);
                item.setIsHandle(isHandle);
                item.setWorker(worker);
                item.setIsDistributed(isDistributed);
                item.setRemark(remark);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取某一工程不合格项处理信息
     */
    public static ArrayList<SafetyInspectFailReport> getSafetyInspectFailReport(String jsonData) {
        ArrayList<SafetyInspectFailReport> list = new ArrayList<SafetyInspectFailReport>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String recordId = jsonObject.getString("HandleRecId");
                String content = jsonObject.getString("HandleSafeItem");
                String time = jsonObject.getString("HandleTime");
                String worker = jsonObject.getString("HandleUserName");
                String remark = jsonObject.getString("HandleInf");

                SafetyInspectFailReport item = new SafetyInspectFailReport();
                item.setRecordId(recordId);
                item.setContent(content);
                item.setWorker(worker);
                item.setTime(time);
                item.setRemark(remark);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取某一工程安全检查历史记录
     */
    public static ArrayList<SafetyInspectHistoryRecord> getSafetyInspectHistoryRecord(String jsonData) {
        ArrayList<SafetyInspectHistoryRecord> list = new ArrayList<SafetyInspectHistoryRecord>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String situation = jsonObject.getString("TaskCi");
                String process = jsonObject.getString("TaskSi");
                String submitTime = jsonObject.getString("TaskUt");
                String flag = jsonObject.getString("TaskSf");

                String failure = jsonObject.getString("TaskDel");
                ArrayList<SafetyInspectHistoryRecordFailItem> failureList = new ArrayList<SafetyInspectHistoryRecordFailItem>();
                if (!failure.equals("")) {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("TaskDel");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String failContent = jsonObject1.getString("ItemName");
                        String handleName = jsonObject1.getString("handlename");
                        String handleTime = jsonObject1.getString("handletime");
                        SafetyInspectHistoryRecordFailItem failureItem = new SafetyInspectHistoryRecordFailItem();
                        failureItem.setFailContent(failContent);
                        failureItem.setHandleName(handleName);
                        failureItem.setHandleTime(handleTime);
                        failureList.add(failureItem);
                    }
                }

                String picUrlStr = jsonObject.getString("TaskPicD");
                ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
                if (!picUrlStr.equals("")) {
                    JSONArray jsonArray2 = jsonObject.getJSONArray("TaskPicD");
                    for (int k = 0; k < jsonArray2.length(); k++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                        String picUri = jsonObject2.getString("PicUri");
                        PicUrl picUrl = new PicUrl();
                        picUrl.setPicUrl(picUri);
                        picUrls.add(picUrl);
                    }
                }

                SafetyInspectHistoryRecord item = new SafetyInspectHistoryRecord();
                item.setSituation(situation);
                item.setProcess(process);
                item.setSubmitDate(submitTime);
                item.setFlag(flag);
                item.setFailure(failureList);
                item.setPicUrls(picUrls);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
