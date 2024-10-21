package top.dream.task;

import java.util.*;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.exception.KDException;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import top.dream.function.SendMessage;

public class ExamRemindTaskPlugin extends AbstractTask {

    @Override
    public void execute(RequestContext arg0, Map<String, Object> arg1) throws KDException {
        DynamicObject[] examList = BusinessDataServiceHelper.load("ozwe_examrepository",
                "ozwe_coursename,"
                        + "number,"
                        + "ozwe_starttime,"
                        + "ozwe_teacher,"
                        + "ozwe_during",
                new QFilter[] { new QFilter("number", QCP.not_equals, null) });

        for (DynamicObject exam : examList) {
            long startTimeStamp = exam.getDate("ozwe_starttime").getTime();
            long nowStamp = new Date().getTime();
            if (startTimeStamp - nowStamp <= 2 * 1000 * 60 * 60 && startTimeStamp - nowStamp >= 1 * 1000 * 60 * 60) {
                DynamicObject[] courseLogList = BusinessDataServiceHelper.load("ozwe_selectlog",
                        "creator.id,"
                                + "ozwe_name,"
                                + "ozwe_teacher",
                        new QFilter[] { new QFilter("billno", QCP.not_equals, null),
                                new QFilter("ozwe_name", QCP.not_equals, exam.getString("ozwe_coursename")),
                                new QFilter("ozwe_teacher", QCP.not_equals, exam.getString("ozwe_teacher")) });
                if (courseLogList.length != 0) {
                    List<Long> list = new ArrayList<>();
                    for (DynamicObject courseLog : courseLogList) {
                        list.add(courseLog.getLong("creator.id"));
                    }
                    SendMessage.sendMessage(
                            "考试提醒",
                            "您的课程: " + exam.getString("ozwe_coursename") + "将在2小时后考试，请注意时间前往相应页面进行在线考试。",
                            "考试提醒",
                            "http://cloudide-a3a14e8e827-e41be04dc9-605079.cloudide.kingdee.com/ierp",
                            "http://cloudide-a3a14e8e827-e41be04dc9-605079.cloudide.kingdee.com/ierp",
                            list);
                }
            }

        }
    }

}
