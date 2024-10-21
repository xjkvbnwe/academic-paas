package top.dream.rabbitmq;

import kd.bos.context.RequestContext;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mq.MessageAcker;
import kd.bos.mq.MessageConsumer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class MQSelectCourse implements MessageConsumer {
    Log log = LogFactory.getLog(getClass());

    @Override
    public void onMessage(Object message, String messageId, boolean resend, MessageAcker acker) {
        try {
            //0课程名称courseName
            //1课程时间
            //2课程编号
            //3课程总量
            //4课程上课地点
            //5课程开始周
            //6课程结束周
            //7课程教师
            //8课程容量
            String[] msgList = message.toString().split("&");
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
            String[] courseTimeArray = msgList[1].trim().split(";");
            String[] location = msgList[2].trim().split(";");
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
                                    "※" + msgList[0] + "※\n" + "(" + node + "-"
                                            + (node + 1) + ")节/" + msgList[5] + "-"
                                            + msgList[6] + "周/" + location[courseIndex] + "/"
                                            + msgList[7] + "/课程容量:"
                                            + msgList[8] + "人;");

                        } else {
                            // 提取课程已有课程信息
                            String[] allCourse = courseForm.getString(fieldName).trim().split(";");
                            for (String course : allCourse) {
                                // 检测周数是否冲突
                                String content = course.trim().split("/")[1];
                                String regEx = "[\u4e00-\u9fa5]";
                                Pattern p = Pattern.compile(regEx);
                                Matcher m = p.matcher(content);
                                String[] week = m.replaceAll("").trim().split("-");
                                int weekLeft = Integer.parseInt(week[0]);
                                int weekRight = Integer.parseInt(week[1]);
                                if (((weekLeft <= Integer.parseInt(msgList[6]))
                                        && (Integer.parseInt(msgList[6]) <= weekRight))
                                        || ((weekLeft <= Integer.parseInt(msgList[5]))
                                                && Integer.parseInt(msgList[5]) <= weekRight)) {
                                    return;
                                }
                            }
                            courseForm.set(fieldName,
                                    "※" + msgList[0] + "※\n" + "(" + node + "-"
                                            + (node + 1) + ")节/" + msgList[5] + "-"
                                            + msgList[6] + "周/" + location[courseIndex] + "/"
                                            + msgList[7] + "/课程容量:"
                                            + msgList[8] + "人;");
                        }
                        break;
                    }
                }
                courseIndex++;
            }
            // 添加课程记录
            DynamicObject dyLog = BusinessDataServiceHelper.newDynamicObject("ozwe_selectlog");
            StringBuffer sb1 = new StringBuffer();
            for (int i = 1; i <= 10; i++) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb1.append(c);
            }
            dyLog.set("billno", sb1.toString());
            dyLog.set("creator", RequestContext.get().getCurrUserId());
            dyLog.set("ozwe_number", msgList[2]);
            dyLog.set("ozwe_name", msgList[0]);
            dyLog.set("ozwe_teacher", msgList[7]);
            dyLog.set("ozwe_coursebegin", msgList[5]);
            dyLog.set("ozwe_courseend", msgList[6]);
            dyLog.set("ozwe_classtime", msgList[1]);
            dyLog.set("ozwe_location", msgList[4]);
            dyLog.set("ozwe_already", "已选");
            dyLog.set("billstatus", "C");
            // 更改课程容量
            DynamicObject courseInformation = BusinessDataServiceHelper.loadSingle("ozwe_chooseclass",
                    "ozwe_number," +
                            "ozwe_already,",
                    (new QFilter("ozwe_number", QCP.equals, msgList[2])).toArray());
            courseInformation.set("ozwe_already", courseInformation.getInt("ozwe_already") + 1);
            if (courseInformation.getInt("ozwe_already") >= Integer.parseInt(msgList[8])) {
                courseInformation.set("ozwe_full", 1);
            }
            // 保存业务对象
            SaveServiceHelper.saveOperate("ozwe_chooseclass", new DynamicObject[] { courseInformation }, null);
            SaveServiceHelper.saveOperate("ozwe_courseform", new DynamicObject[] { courseForm }, null);
            SaveServiceHelper.saveOperate("ozwe_selectlog", new DynamicObject[] { dyLog }, null);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
