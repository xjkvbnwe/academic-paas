package top.dream.work.course;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.report.IReportView;
import kd.bos.report.ReportList;
import kd.bos.report.plugin.AbstractReportFormPlugin;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class SelectCoursePlugin extends AbstractReportFormPlugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        ReportList reportList = getControl("reportlistap");
        IReportView view = (IReportView) this.getView();
        reportList.addHyperClickListener(new HyperLinkClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void hyperLinkClick(HyperLinkClickEvent e) {
                String field = e.getFieldName();
                if (field.equalsIgnoreCase("ozwe_choose")) {
                    DynamicObject rowData = e.getRowData();
                    if (rowData.getString("ozwe_situation").equalsIgnoreCase("已选")) {
                        view.showMessage("已经选择该课程，无需重复选择");
                        return;
                    }

                    if (rowData.getInt("ozwe_already") == rowData.getInt("ozwe_total")) {
                        view.showMessage("该课程已经满员");
                        return;
                    }

                    // 获取课表信息
                    String[] stringInfo = { "a1a2", "a3a4", "a5a6", "a7a8", "a9a10" };
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= 7; i++) {
                        for (String strSingle : stringInfo) {
                            sb.append("ozwe_" + i + strSingle + ",");
                        }
                    }
                    DynamicObject courseForm = BusinessDataServiceHelper.loadSingle("ozwe_courseform",
                            "creator," +
                                    "number," +
                                    "ozwe_year," +
                                    "ozwe_term," +
                                    "ozwe_number," +
                                    sb.toString().substring(0, sb.toString().length() - 1),
                            (new QFilter("creator", QCP.equals, RequestContext.get().getCurrUserId())).toArray());
                    if (courseForm == null) {
                        // 创建一个课表对象
                        courseForm = BusinessDataServiceHelper.newDynamicObject("ozwe_courseform");
                        StringBuffer sb1 = new StringBuffer();
                        for (int i = 1; i <= 10; i++) {
                            int ascii = 48 + (int) (Math.random() * 9);
                            char c = (char) ascii;
                            sb1.append(c);
                        }
                        courseForm.set("number", sb1.toString());
                        courseForm.set("createtime", new Date());
                        courseForm.set("ozwe_year", "2022-2023");
                        courseForm.set("ozwe_term", 1);
                        courseForm.set("status", "C");
                        courseForm.set("enable", 1);
                        courseForm.set("creator", RequestContext.get().getCurrUserId());
                    }
                    String[] courseTimeArray = rowData.getString("ozwe_time").trim().split(";");
                    String[] location = rowData.getString("ozwe_area").trim().split(";");
                    String[] timeArray = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
                    int courseIndex = 0;
                    for (String strTime : courseTimeArray) {
                        // 分割时间字符串
                        String[] detail = strTime.split("-");
                        int node = Integer.parseInt(detail[1].split(",")[0]);
                        for (int index = 0; index < timeArray.length; index++) {
                            // 星期数正确
                            if (detail[0].contains(timeArray[index])) {
                                // 判断是否有空，直接添加
                                String fieldName = "ozwe_" + (index + 1) + "a" + node + "a" + (node + 1);

                                if (courseForm.getString(fieldName).length() == 0) {
                                    courseForm.set(fieldName,
                                            "※" + rowData.getString("ozwe_course") + "※\n" + "(" + node + "-"
                                                    + (node + 1) + ")节/" + rowData.getString("ozwe_begin") + "-"
                                                    + rowData.getString("ozwe_end") + "周/" + location[courseIndex] + "/"
                                                    + rowData.getString("ozwe_teacher") + "/课程容量:"
                                                    + rowData.getString("ozwe_total") + "人;");
                                    
                                } else {
                                    // 提取课程已有课程信息
                                    String[] allCourse = courseForm.getString(fieldName).trim().split(";");
                                    for (String course : allCourse) {
                                        //检测周数是否冲突
                                        String content =course.trim().split("/")[1];
                                        String regEx = "[\u4e00-\u9fa5]";
                                        Pattern p = Pattern.compile(regEx);
                                        Matcher m = p.matcher(content);
                                        String[] week = m.replaceAll("").trim().split("-");
                                        int weekLeft = Integer.parseInt(week[0]);
                                        int weekRight = Integer.parseInt(week[1]);
                                        if (((weekLeft <= rowData.getInt("ozwe_end")) && (rowData.getInt("ozwe_end") <= weekRight))
                                         || ((weekLeft <= rowData.getInt("ozwe_begin")) && rowData.getInt("ozwe_begin") <= weekRight) ) {
                                            view.showMessage("该课程与现有课程有冲突，无法选课");
                                            return;
                                        }
                                    }
                                    courseForm.set(fieldName,courseForm.getString(fieldName)+"\n"+
                                            "※" + rowData.getString("ozwe_course") + "※\n" + "(" + node + "-"
                                                    + (node + 1) + ")节/" + rowData.getString("ozwe_begin") + "-"
                                                    + rowData.getString("ozwe_end") + "周/" + location[courseIndex] + "/"
                                                    + rowData.getString("ozwe_teacher") + "/课程容量:"
                                                    + rowData.getString("ozwe_total") + "人;");
                                }
                                break;
                            }
                        }
                        courseIndex++;
                    }
                    //添加课程记录
                    DynamicObject dyLog = BusinessDataServiceHelper.newDynamicObject("ozwe_selectlog");
                        StringBuffer sb1 = new StringBuffer();
                        for (int i = 1; i <= 10; i++) {
                            int ascii = 48 + (int) (Math.random() * 9);
                            char c = (char) ascii;
                            sb1.append(c);
                        }
                        dyLog.set("billno", sb1.toString());
                        dyLog.set("creator", RequestContext.get().getCurrUserId());
                        dyLog.set("ozwe_number", rowData.getString("ozwe_number"));
                        dyLog.set("ozwe_name", rowData.getString("ozwe_course"));
                        dyLog.set("ozwe_teacher", rowData.getString("ozwe_teacher"));
                        dyLog.set("ozwe_coursebegin", rowData.getString("ozwe_begin"));
                        dyLog.set("ozwe_courseend", rowData.getString("ozwe_end"));
                        dyLog.set("ozwe_classtime", rowData.getString("ozwe_time"));
                        dyLog.set("ozwe_location", rowData.getString("ozwe_area"));
                        dyLog.set("ozwe_already", "已选");
                        dyLog.set("billstatus", "C");
                    //更改课程容量
                    DynamicObject courseInformation = BusinessDataServiceHelper.loadSingle("ozwe_chooseclass",
                            "ozwe_number," +
                                    "ozwe_already,",
                            (new QFilter("ozwe_number", QCP.equals, rowData.getString("ozwe_number"))).toArray());
                    courseInformation.set("ozwe_already", courseInformation.getInt("ozwe_already")+1);
                    if (courseInformation.getInt("ozwe_already") >= rowData.getInt("ozwe_total")) {
                        courseInformation.set("ozwe_full", 1);
                    }
                    //保存业务对象
                    SaveServiceHelper.saveOperate("ozwe_chooseclass", new DynamicObject[] { courseInformation }, null);
                    SaveServiceHelper.saveOperate("ozwe_courseform", new DynamicObject[] { courseForm }, null);
                    SaveServiceHelper.saveOperate("ozwe_selectlog", new DynamicObject[] { dyLog }, null);
                    view.showMessage("选课成功: "+rowData.getString("ozwe_course"));
                    view.refresh();
                }
            }

        });
    }
}
