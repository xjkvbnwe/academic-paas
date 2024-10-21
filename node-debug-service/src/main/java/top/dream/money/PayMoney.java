package top.dream.money;

import java.util.*;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class PayMoney extends AbstractBillPlugIn {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addItemClickListeners("ozwe_pay");
    }

    public void beforeItemClick(BeforeItemClickEvent e) {
        super.beforeItemClick(e);
        String itemKey = e.getItemKey();
        // 如果点击的字段为缴费
        if ("ozwe_pay".equals(itemKey)) {
            if (Double.parseDouble(this.getModel().getValue("ozwe_amounttask").toString()) > 0) {
                if (Double.parseDouble(this.getModel().getValue("ozwe_amountend").toString()) >= 0) {
                    // 获取校园卡信息并且获取更改余额
                    DynamicObject dy = BusinessDataServiceHelper.loadSingle("ozwe_schoolcard",
                            "number," +
                                    "ozwe_amount",
                            (new QFilter("number", QCP.equals, this.getModel().getValue("ozwe_card").toString()))
                                    .toArray());
                    dy.set("ozwe_amount", Double.parseDouble(this.getModel().getValue("ozwe_amountend").toString()));

                    // 增加金额管理记录
                    DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("ozwe_moneymanage");
                    StringBuffer sb1 = new StringBuffer();
                    for (int i = 1; i <= 10; i++) {
                        int ascii = 48 + (int) (Math.random() * 9);
                        char c = (char) ascii;
                        sb1.append(c);
                    }
                    dynamicObject.set("billno", sb1.toString());
                    dynamicObject.set("ozwe_card", this.getModel().getValue("ozwe_card").toString());
                    dynamicObject.set("ozwe_amount", Double.parseDouble(this.getModel().getValue("ozwe_amount").toString()));
                    dynamicObject.set("ozwe_operation", Double.parseDouble(this.getModel().getValue("ozwe_amounttask").toString()));
                    dynamicObject.set("ozwe_typevalue", -2);
                    dynamicObject.set("ozwe_amountend", Double.parseDouble(this.getModel().getValue("ozwe_amountend").toString()));
                    dynamicObject.set("billstatus", "C");
                    dynamicObject.set("ozwe_textarea", "任务编号:"+this.getModel().getValue("ozwe_taskno")+",任务名称:"+this.getModel().getValue("ozwe_taskname"));
                    dynamicObject.set("createtime", new Date());
                    // 保存单据
                    SaveServiceHelper.saveOperate("ozwe_schoolcard", new DynamicObject[] { dy }, null);
                    SaveServiceHelper.saveOperate("ozwe_moneymanage", new DynamicObject[] {dynamicObject}, null);
                    this.getView().showMessage("缴费成功");
                } else {
                    this.getView().showMessage("余额不足");
                    e.setCancel(true);
                }
            } else {
                this.getView().showMessage("请选择缴费项目");
                e.setCancel(true);
            }
        }
    }
}
