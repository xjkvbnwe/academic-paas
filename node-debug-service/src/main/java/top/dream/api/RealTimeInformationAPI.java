package top.dream.api;

import java.util.*;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.result.CustomApiResult;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.openapi.common.custom.annotation.*;

@ApiController(value = "bos_2", desc = "API实时信息")
public class RealTimeInformationAPI {

    @ApiPostMapping(value = "/realTimeInformation", desc = "实时信息显示")
    public CustomApiResult<@ApiResponseBody("comsumptionOutput") String> realTimeInformation(
            @ApiParam(value = "comsumptionInput") Map<String, Object> inputMap) {

        DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("ozwe_realtimeinfo");
        StringBuffer sb1 = new StringBuffer();
        for (int i = 1; i <= 10; i++) {
            int ascii = 48 + (int) (Math.random() * 9);
            char c = (char) ascii;
            sb1.append(c);
        }
        dynamicObject.set("creator", RequestContext.get().getCurrUserId());
        dynamicObject.set("auditor", RequestContext.get().getCurrUserId());
        dynamicObject.set("billstatus", "C");
        dynamicObject.set("createtime", new Date());
        dynamicObject.set("billno", sb1.toString());
        dynamicObject.set("ozwe_temporature", inputMap.get("temporature"));
        dynamicObject.set("ozwe_humidity", inputMap.get("humidity"));
        dynamicObject.set("ozwe_lightintensity", inputMap.get("lightintensity"));
        SaveServiceHelper.saveOperate("ozwe_realtimeinfo", new DynamicObject[] {dynamicObject}, null);

        return CustomApiResult.success("success");
    }

}
