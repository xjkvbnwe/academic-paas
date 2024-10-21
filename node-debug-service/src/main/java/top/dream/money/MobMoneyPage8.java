package top.dream.money;

import java.text.SimpleDateFormat;
import java.util.*;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Label;
import kd.bos.form.plugin.AbstractMobFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class MobMoneyPage8 extends AbstractMobFormPlugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        Label name = getView().getControl("ozwe_labelap1");
        name.setText(RequestContext.get().getUserName());

        DynamicObject card = BusinessDataServiceHelper.loadSingle("ozwe_schoolcard",
				"ozwe_user," +
                "ozwe_amount",
				new QFilter[]{new QFilter("ozwe_user",QCP.equals,RequestContext.get().getCurrUserId())});
        Label balanceLabel = getView().getControl("ozwe_labelap2");
        balanceLabel.setText(String.format("%.2f", Double.parseDouble(card.getString("ozwe_amount"))));

        DynamicObject[] money = BusinessDataServiceHelper.load("ozwe_moneymanage",
				"ozwe_schoolcard," +
                "createtime," +
                "ozwe_typevalue," +
                "ozwe_operation",
				new QFilter[]{new QFilter("ozwe_schoolcard",QCP.equals,card.getPkValue())});
        Date today = new Date();
        //计算今日消费
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        double amountAll = 0;
        for (DynamicObject moneySingle:money) {
            if (sdf.format(moneySingle.getDate("createtime")).equalsIgnoreCase(sdf.format(today))) {
                if (moneySingle.getInt("ozwe_typevalue") <0) {
                    amountAll += Double.parseDouble(moneySingle.getString("ozwe_operation"));
                }
            }
        }
        Label todayBalanceAll = getView().getControl("ozwe_labelap5");
        todayBalanceAll.setText(money.length+"");
        Label todayBalanceLabel = getView().getControl("ozwe_labelap4");
        todayBalanceLabel.setText(String.format("%.2f", amountAll));
        //计算本月进账
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
        double amountMontyAll = 0;
        for (DynamicObject moneySingle:money) {
            if (sdfMonth.format(moneySingle.getDate("createtime")).equalsIgnoreCase(sdfMonth.format(today))) {
                if (moneySingle.getInt("ozwe_typevalue") >0) {
                    amountMontyAll += Double.parseDouble(moneySingle.getString("ozwe_operation"));
                }
            }
        }
        Label monthBalanceLabel = getView().getControl("ozwe_labelap41");
        monthBalanceLabel.setText(String.format("%.2f", amountMontyAll));
        //计算代缴任务
        DynamicObject[] task = BusinessDataServiceHelper.load("ozwe_paytask",
                "number",
				new QFilter[]{new QFilter("number",QCP.not_equals,null)});
                Label waitBalanceLabel = getView().getControl("ozwe_labelap54");
                waitBalanceLabel.setText(task.length+"");
        //计算申请任务
        DynamicObject[] task2 = BusinessDataServiceHelper.load("ozwe_fund",
                "billno",
				new QFilter[]{new QFilter("billno",QCP.not_equals,null)});
        DynamicObject[] task3 = BusinessDataServiceHelper.load("ozwe_scholarshipapply",
                "billno",
				new QFilter[]{new QFilter("billno",QCP.not_equals,null)});
        DynamicObject[] task4 = BusinessDataServiceHelper.load("ozwe_stipend",
                "billno",
				new QFilter[]{new QFilter("billno",QCP.not_equals,null)});
        Label applyLabel = getView().getControl("ozwe_labelap56");
        applyLabel.setText(task2.length+task3.length+task4.length+"");
        //计算发放任务
        DynamicObject[] task10 = BusinessDataServiceHelper.load("ozwe_fundput",
                "billno",
				new QFilter[]{new QFilter("billno",QCP.not_equals,null)});
        DynamicObject[] task11 = BusinessDataServiceHelper.load("ozwe_scholarshipput",
                "billno",
				new QFilter[]{new QFilter("billno",QCP.not_equals,null)});
        DynamicObject[] task12 = BusinessDataServiceHelper.load("ozwe_stipendput",
                "billno",
				new QFilter[]{new QFilter("billno",QCP.not_equals,null)});
        Label putLabel = getView().getControl("ozwe_labelap55");
        putLabel.setText(task10.length+task11.length+task12.length+"");
    }
}
