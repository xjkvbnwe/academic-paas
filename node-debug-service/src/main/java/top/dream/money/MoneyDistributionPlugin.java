package top.dream.money;


import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import top.dream.function.AddMoneyManageFunction;

public class MoneyDistributionPlugin extends AbstractOperationServicePlugIn {

    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {

        try {
            for (DynamicObject single : e.getDataEntities()) {
                // 获取校园卡相关信息
                DynamicObject dy = BusinessDataServiceHelper.loadSingle("ozwe_schoolcard",
                        "number," +
                        "ozwe_amount",
                        (new QFilter("number", QCP.equals, single.getString("ozwe_putcard"))).toArray());
                dy.set("ozwe_amount", Double.parseDouble(dy.getString("ozwe_amount")) + Double.parseDouble(single.getString("ozwe_amountfield")));
                SaveServiceHelper.saveOperate("ozwe_schoolcard", new DynamicObject[] {dy}, null);
                //添加金额管理记录
                AddMoneyManageFunction.addMoneyManage(
                    single.getString("ozwe_putcard"), 
                    (Double.parseDouble(dy.getString("ozwe_amount"))-Double.parseDouble(single.getString("ozwe_amountfield")))+"",
                    Double.parseDouble(single.getString("ozwe_amountfield")),
                    2,
                    Double.parseDouble(dy.getString("ozwe_amount")), 
                    String.format("任务编号:%s,任务名称:奖学金发放",single.getString("billNo")));
            }
        } catch(Exception ee) {
            for (ExtendedDataEntity entity : e.getSelectedRows()) {
                String billNo = entity.getBillNo();
                //获取发放单信息
                DynamicObject single = BusinessDataServiceHelper.loadSingle("ozwe_scholarshipput",
                        "billno," +
                        "ozwe_putcard," + 
                        "ozwe_amountfield",
                        (new QFilter("billno", QCP.equals, billNo)).toArray());
                //获取校园卡信息
                DynamicObject dy = BusinessDataServiceHelper.loadSingle("ozwe_schoolcard",
                        "number," +
                        "ozwe_amount",
                        (new QFilter("number", QCP.equals, single.getString("ozwe_putcard"))).toArray());
                dy.set("ozwe_amount", Double.parseDouble(dy.getString("ozwe_amount")) + Double.parseDouble(single.getString("ozwe_amountfield")));
                SaveServiceHelper.saveOperate("ozwe_schoolcard", new DynamicObject[] {dy}, null);
                //添加金额管理记录
                AddMoneyManageFunction.addMoneyManage(
                    single.getString("ozwe_putcard"), 
                    (Double.parseDouble(dy.getString("ozwe_amount"))-Double.parseDouble(single.getString("ozwe_amountfield")))+"",
                    Double.parseDouble(single.getString("ozwe_amountfield")),
                    2,
                    Double.parseDouble(dy.getString("ozwe_amount")), 
                    String.format("任务编号:%s,任务名称:奖学金发放",single.getString("billNo")));
            }
        }

    }
    
}
