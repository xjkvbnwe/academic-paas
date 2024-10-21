package top.dream.rabbitmq;

import kd.bos.context.RequestContext;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mq.MessageAcker;
import kd.bos.mq.MessageConsumer;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class MQQuitCourse implements MessageConsumer {
    Log log = LogFactory.getLog(getClass());

    @Override
    public void onMessage(Object message, String messageId, boolean resend, MessageAcker acker) {
        try {
            //0课程名称courseName
            //1课程时间
            //2课程编号
            //3课程总量
            String[] msgList = message.toString().split("&");
            // 删除选课记录
            DeleteServiceHelper.delete("ozwe_selectlog", new QFilter[] {
                    new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId()),
                    new QFilter("ozwe_name", QCP.equals, msgList[0])
            });
            // 获取课表信息
            String[] stringInfo = { "a1a2", "a3a4", "a5a6", "a7a8", "a9a10" };
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= 7; i++) {
                for (String strSingle : stringInfo) {
                    sb.append("ozwe_" + i + strSingle + ",");
                }
            }
            // 删除课表记录
            DynamicObject courseForm = BusinessDataServiceHelper.loadSingle("ozwe_courseform",
                    "creator.id," +
                            "number," +
                            "ozwe_year," +
                            "ozwe_term," +
                            "ozwe_number," +
                            sb.toString().substring(0, sb.toString().length() - 1),
                    (new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId())).toArray());
            String[] courseTimeArray = msgList[1].trim().split(";");
            String[] timeArray = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
            for (String strTime : courseTimeArray) {
                // 分割时间字符串
                String[] detail = strTime.split("-");
                int node = Integer.parseInt(detail[1].split(",")[0]);
                for (int index = 0; index < timeArray.length; index++) {
                    // 星期数正确
                    if (detail[0].contains(timeArray[index])) {
                        String fieldName = "ozwe_" + (index + 1) + "a" + node + "a" + (node + 1);
                        String[] content = courseForm.getString(fieldName).split(";");
                        StringBuilder resultContentBuilder = new StringBuilder();
                        for (String singleContent : content) {
                            if (!singleContent.contains(msgList[0])) {
                                System.out.println(singleContent);
                                resultContentBuilder.append(singleContent + ";");
                            }
                        }
                        courseForm.set(fieldName, resultContentBuilder.toString());
                        break;
                    }
                }
            }
            // 更改课程容量
            DynamicObject courseInformation = BusinessDataServiceHelper.loadSingle("ozwe_chooseclass",
                    "ozwe_number," +
                            "ozwe_full," +
                            "ozwe_already,",
                    (new QFilter("ozwe_number", QCP.equals, msgList[2])).toArray());
            courseInformation.set("ozwe_already", courseInformation.getInt("ozwe_already") - 1);
            if (courseInformation.getInt("ozwe_already") < Integer.parseInt(msgList[3])) {
                courseInformation.set("ozwe_full", 0);
            }
            // 保存业务对象
            SaveServiceHelper.saveOperate("ozwe_chooseclass", new DynamicObject[] { courseInformation }, null);
            SaveServiceHelper.saveOperate("ozwe_courseform", new DynamicObject[] { courseForm }, null);
            
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
