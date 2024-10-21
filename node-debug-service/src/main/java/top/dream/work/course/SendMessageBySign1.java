package top.dream.work.course;

import java.text.SimpleDateFormat;
import java.util.*;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import top.dream.function.SendMessage;

public class SendMessageBySign1 extends AbstractOperationServicePlugIn {
    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {
        List<Long> receivers = new ArrayList<>();
        for (DynamicObject single : e.getDataEntities()) {
            DynamicObject course = single.getDynamicObject("ozwe_class");
            DynamicObject[] dyList = BusinessDataServiceHelper.load("ozwe_chooseclass",
                        "ozwe_number," +
                        "creator.id",
                        (new QFilter("ozwe_number", QCP.equals, course.getString("number"))).toArray());
            for (DynamicObject dySingle : dyList) {
                receivers.add(dySingle.getLong("creator.id"));
            }
            SendMessage.sendMessage(
						"课程签到提醒", 
						"您的课程: "+course.getString("name")+"正在签到，请在"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(single.getDate("ozwe_end"))+"前往相应界面签到", 
						"课程签到", 
						"前往微信小程序签到",
						"前往微信小程序签到",
						receivers);
        }
    }
}
