package top.dream.work.course;

import java.util.*;

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
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class WithdrawCoursePlugin extends AbstractReportFormPlugin {
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
                if (field.equalsIgnoreCase("ozwe_quit")) {
                    DynamicObject rowData = e.getRowData();
                    if (!rowData.getString("ozwe_situation").equalsIgnoreCase("已选")) {
                        view.showMessage("还未选择该课程，无法退课");
                        return;
                    }
                    //删除选课记录
                    DeleteServiceHelper.delete("ozwe_selectlog", new QFilter[] {
                        new QFilter("creator", QCP.equals, RequestContext.get().getCurrUserId()),
                        new QFilter("ozwe_name", QCP.equals, rowData.getString("ozwe_course"))
                    });
                    // 获取课表信息
                    String[] stringInfo = { "a1a2", "a3a4", "a5a6", "a7a8", "a9a10" };
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= 7; i++) {
                        for (String strSingle : stringInfo) {
                            sb.append("ozwe_" + i + strSingle + ",");
                        }
                    }
                    //删除课表记录
                    DynamicObject courseForm = BusinessDataServiceHelper.loadSingle("ozwe_courseform",
                            "creator," +
                                    "number," +
                                    "ozwe_year," +
                                    "ozwe_term," +
                                    "ozwe_number," +
                                    sb.toString().substring(0, sb.toString().length() - 1),
                            (new QFilter("creator", QCP.equals, RequestContext.get().getCurrUserId())).toArray());
                    String[] courseTimeArray = rowData.getString("ozwe_time").trim().split(";");
                    String[] timeArray = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
                    for (String strTime : courseTimeArray) {
                        // 分割时间字符串
                        String[] detail = strTime.split("-");
                        int node = Integer.parseInt(detail[1].split(",")[0]);
                        for (int index = 0; index < timeArray.length; index++) {
                            //星期数正确
                            if (detail[0].contains(timeArray[index])) {
                                String fieldName = "ozwe_" + (index + 1) + "a" + node + "a" + (node + 1);
                                String[] content = courseForm.getString(fieldName).split(";");
                                StringBuilder resultContentBuilder = new StringBuilder();
                                for (String singleContent : content) {
                                    if (!singleContent.contains(rowData.getString("ozwe_course"))) {
                                        resultContentBuilder.append(singleContent+";");
                                    }
                                }
                                courseForm.set(fieldName, resultContentBuilder.toString());
                                break;
                            }
                        }
                    }
                    //更改课程容量
                    DynamicObject courseInformation = BusinessDataServiceHelper.loadSingle("ozwe_chooseclass",
                            "ozwe_number," +
                            "ozwe_full,"+
                            "ozwe_already,",
                            (new QFilter("ozwe_number", QCP.equals, rowData.getString("ozwe_number"))).toArray());
                    courseInformation.set("ozwe_already", courseInformation.getInt("ozwe_already")-1);
                    if (courseInformation.getInt("ozwe_already") < rowData.getInt("ozwe_total")) {
                        courseInformation.set("ozwe_full", 0);
                    }
                    //保存业务对象
                    SaveServiceHelper.saveOperate("ozwe_chooseclass", new DynamicObject[] { courseInformation }, null);
                    SaveServiceHelper.saveOperate("ozwe_courseform", new DynamicObject[] { courseForm }, null);
                    view.showMessage("退课成功: "+rowData.getString("ozwe_course"));
                    view.refresh();
                }
            }
        });
    }
}
