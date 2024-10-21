package top.dream.api;

import java.text.SimpleDateFormat;
import java.util.*;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.result.CustomApiResult;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import top.dream.function.AddMoneyManageFunction;
import kd.bos.openapi.common.custom.annotation.*;

@ApiController(value = "bos_2", desc = "API用户消费")
public class UserConsumptionAPI {

    @ApiPostMapping(value = "/userConsumption", desc = "用户签到")
    public CustomApiResult<@ApiResponseBody("comsumptionOutput") String> userConsumption(
            @ApiParam(value = "comsumptionInput") Map<String, Object> inputMap) {

                double money = Double.parseDouble(inputMap.get("money").toString());
                DynamicObject dy = BusinessDataServiceHelper.loadSingle("ozwe_schoolcard",
                        "number," +
                        "ozwe_amount," +
                        "ozwe_user.id",
                        (new QFilter("ozwe_user.id", QCP.equals, RequestContext.get().getCurrUserId())).toArray());
                dy.set("ozwe_amount", Double.parseDouble(dy.getString("ozwe_amount")) - money);
                SaveServiceHelper.saveOperate("ozwe_schoolcard", new DynamicObject[] {dy}, null);
                //添加金额管理记录
                AddMoneyManageFunction.addMoneyManage(
                    dy.getString("number"), 
                    (Double.parseDouble(dy.getString("ozwe_amount"))+money)+"",
                    money,
                    -1,
                    Double.parseDouble(dy.getString("ozwe_amount")), 
                    "任务名称: 普通消费,消费时间: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                return CustomApiResult.success("success");
            }
    
}
