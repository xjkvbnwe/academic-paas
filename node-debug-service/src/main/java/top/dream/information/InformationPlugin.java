package top.dream.information;

import java.util.*;
import kd.bos.base.AbstractBasePlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.RichTextEditor;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class InformationPlugin extends AbstractBasePlugIn{

    @Override
    public void afterBindData(EventObject e) {
        DynamicObject dObject = BusinessDataServiceHelper.loadSingle("ozwe_msg_information",
                    "number," +
                            "ozwe_msgcontent_tag,",
                    (new QFilter("number", QCP.equals, this.getModel().getValue("number").toString())).toArray());
        RichTextEditor edit = this.getView().getControl("ozwe_maincontent"); 
        if (dObject != null) {
            edit.setText(dObject.getString("ozwe_msgcontent_tag"));
        }
    }

    @Override
    public void beforeClosed(BeforeClosedEvent e) {
        // TODO Auto-generated method stub
        super.beforeClosed(e);
        e.setCheckDataChange(false);// 取消修改确认弹框，默认为true
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addItemClickListeners("titleapanel");
    }

    @Override
    //按钮生效前执行
    public void beforeItemClick(BeforeItemClickEvent e) {
        super.beforeItemClick(e);
        //获取点击控件的字段
        String itemKey = e.getItemKey();
        if (itemKey.equals("bar_submit")) {
            if ((this.getModel().getValue("name").toString() .length() <=0 ) || (this.getModel().getValue("number").toString() .length() <=0 )) {
                this.getView().showMessage("请将信息填写完整");
                e.setCancel(true);
            } else {
                RichTextEditor edit = this.getView().getControl("ozwe_maincontent");
                this.getModel().setValue("ozwe_msgcontent_tag", edit.getText());
            }
        }
    }

    // @Override
    // public void afterExecuteOperationTransaction(AfterOperationArgs e) {
    //     try {
    //         for (DynamicObject single : e.getDataEntities()) {
    //             DynamicObject dy = BusinessDataServiceHelper.loadSingle("ozwe_msg_information",
    //                     "number," +
    //                     "ozwe_msgcontent",
    //                     (new QFilter("number", QCP.equals, single.getString("number"))).toArray());
    //             RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
    //edit.setText("");
    //         }
    //     } catch (Exception ee) {

    //     }
    // }
}
