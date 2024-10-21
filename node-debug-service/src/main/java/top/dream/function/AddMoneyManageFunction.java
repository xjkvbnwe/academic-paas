
package top.dream.function;

import java.util.Date;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class AddMoneyManageFunction {
    public static void addMoneyManage(String card, String amount, double operation, int typeValue, double amountEnd, String text) {
        // 增加金额管理记录
        DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("ozwe_moneymanage");
        StringBuffer sb1 = new StringBuffer();
        for (int i = 1; i <= 10; i++) {
            int ascii = 48 + (int) (Math.random() * 9);
            char c = (char) ascii;
            sb1.append(c);
        }
        dynamicObject.set("billno", sb1.toString());
        dynamicObject.set("ozwe_card", card);
        dynamicObject.set("ozwe_amount", Double.parseDouble(amount));
        dynamicObject.set("ozwe_operation", operation);
        dynamicObject.set("ozwe_typevalue", typeValue);
        dynamicObject.set("ozwe_amountend", amountEnd);
        dynamicObject.set("billstatus", "C");
        dynamicObject.set("ozwe_textarea", text);
        dynamicObject.set("createtime", new Date());
        SaveServiceHelper.saveOperate("ozwe_moneymanage", new DynamicObject[] {dynamicObject}, null);
    }
}
