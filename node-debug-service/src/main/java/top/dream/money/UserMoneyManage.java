package top.dream.money;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class UserMoneyManage extends AbstractOperationServicePlugIn {
    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {

        for (DynamicObject single : e.getDataEntities()) {
            // 获取校园卡相关信息
            DynamicObject dy = BusinessDataServiceHelper.loadSingle("ozwe_schoolcard",
                    "number," +
                    "ozwe_amount",
                    (new QFilter("number", QCP.equals, single.getString("ozwe_card"))).toArray());
            dy.set("ozwe_amount", Double.parseDouble(single.getString("ozwe_amountend")));
            SaveServiceHelper.saveOperate("ozwe_schoolcard", new DynamicObject[] {dy}, null);
        }

    }
}
