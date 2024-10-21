package top.dream.work.course;

import java.util.*;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.list.plugin.AbstractMobListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import com.alibaba.nacos.api.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.list.IListView;
import kd.bos.mq.MessagePublisher;
import kd.bos.mq.MQFactory;

public class SelectCourseMob extends AbstractMobListPlugin{

    public void registerListener(EventObject e) {
		Button buttonBorrow = this.getView().getControl("button_quit");
		Button buttonAppointment = this.getView().getControl("button_select");
		buttonBorrow.addClickListener(this);
		buttonAppointment.addClickListener(this);
    }

    @Override
	public void click(EventObject e) {
        Control source = (Control)e.getSource();
        if (StringUtils.equals("button_quit", source.getKey())) {
            IListView listView = (IListView)this.getView();
            try {
                ListSelectedRow s = listView.getCurrentSelectedRowInfo();
                DynamicObject courseInfo = BusinessDataServiceHelper.loadSingle("ozwe_chooseclass",
					"billno,"
					+ "ozwe_number,"
					+ "ozwe_name,"
					+ "ozwe_classtime,"
					+ "ozwe_sum",
					new QFilter[]{new QFilter("billno",QCP.equals,s.getBillNo())});
                    MessagePublisher mp = MQFactory.get().createSimplePublisher("ozwe_StudentSystem", "queue_quit");
                    try {
                        mp.publish(courseInfo.getString("ozwe_name")+"&"+courseInfo.getString("ozwe_classtime")+"&"+courseInfo.getString("ozwe_number")+"&"+courseInfo.getString("ozwe_sum"));
                    } finally {
                        mp.close();
                    }
                    listView.showMessage("退课成功");
            } catch(NullPointerException npe) {
				listView.showMessage("选中状态的课程无法操作，已为您取消选中状态");
			}
        } else if (StringUtils.equals("button_select", source.getKey())) {
            IListView listView = (IListView)this.getView();
            try {
                ListSelectedRow s = listView.getCurrentSelectedRowInfo();
                DynamicObject courseInfo = BusinessDataServiceHelper.loadSingle("ozwe_chooseclass",
					"billno,"
					+ "ozwe_number,"
					+ "ozwe_name,"
					+ "ozwe_classtime,"
					+ "ozwe_sum,"
                    + "ozwe_location,"
                    + "ozwe_coursebegin,"
                    + "ozwe_courseend,"
                    + "ozwe_teacher,"
                    + "ozwe_full",
					new QFilter[]{new QFilter("billno",QCP.equals,s.getBillNo())});
                    if (courseInfo.getInt("ozwe_full") == 1) {
                        listView.showMessage("课程无容量");
                        return;
                    }
                    MessagePublisher mp = MQFactory.get().createSimplePublisher("ozwe_StudentSystem", "queue_select");
                    try {
                        mp.publish(courseInfo.getString("ozwe_name")+"&"+
                        courseInfo.getString("ozwe_classtime")+"&"+
                        courseInfo.getString("ozwe_number")+"&"+
                        courseInfo.getString("ozwe_sum")+"&"+
                        courseInfo.getString("ozwe_location")+"&"+
                        courseInfo.getString("ozwe_coursebegin")+"&"+
                        courseInfo.getString("ozwe_courseend")+"&"+
                        courseInfo.getString("ozwe_teacher")+"&"+
                        courseInfo.getString("ozwe_sum"));
                    } finally {
                        mp.close();
                    }
                    listView.showMessage("退课成功");
            } catch(NullPointerException npe) {
				listView.showMessage("选中状态的课程无法操作，已为您取消选中状态");
			}
        }
    }
}
