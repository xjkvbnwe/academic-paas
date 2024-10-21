package top.dream.work;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class RegistrationChange extends AbstractOperationServicePlugIn {

    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {
        for (ExtendedDataEntity entity : e.getSelectedRows()) {
            String billNo = entity.getBillNo();
            //获取报道申请信息
            DynamicObject single = BusinessDataServiceHelper.loadSingle("ozwe_registration",
                    "billno," +
                    "ozwe_number",
                    (new QFilter("billno", QCP.equals, billNo)).toArray());
            //获取人员信息
            DynamicObject userObject = BusinessDataServiceHelper.loadSingle("bos_user",
                    "number," +
                    "ozwe_status",
                    (new QFilter("number", QCP.equals, single.getString("ozwe_number"))).toArray());
            userObject.set("ozwe_status", "1");
            SaveServiceHelper.saveOperate("bos_user", new DynamicObject[] {userObject}, null);
        }
    }
    
}
