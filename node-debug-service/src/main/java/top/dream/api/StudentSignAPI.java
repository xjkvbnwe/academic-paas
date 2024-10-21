package top.dream.api;

import java.util.*;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.result.CustomApiResult;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.openapi.common.custom.annotation.*;

@ApiController(value = "bos_2", desc = "API用户签到")
public class StudentSignAPI {

    @ApiPostMapping(value = "/userSign", desc = "用户签到")
    public CustomApiResult<@ApiResponseBody("signOutput") String> userSign(
            @ApiParam(value = "signInput") Map<String, Object> inputMap) {

        String signBillno = inputMap.get("billno").toString();

        DynamicObject signTarget = BusinessDataServiceHelper.loadSingle("ozwe_sign",
                "billno," +
                        "ozwe_class," +
                        "ozwe_type," +
                        "creator.id," +
                        "ozwe_code," +
                        "ozwe_latitude," +
                        "ozwe_longitude," +
                        "ozwe_range," +
                        "ozwe_end," +
                        "ozwe_entryentity," +
                        "ozwe_number," +
                        "ozwe_name," +
                        "ozwe_datetime",
                (new QFilter("billno", QCP.equals, signBillno)).toArray());
        DynamicObjectCollection doc = signTarget.getDynamicObjectCollection("ozwe_entryentity");
        DynamicObject student = doc.addNew();
        student.set("ozwe_number", RequestContext.get().getCurrUserId()+"");
        student.set("ozwe_name", RequestContext.get().getUserName());
        student.set("ozwe_datetime", new Date());

        SaveServiceHelper.saveOperate("ozwe_sign", new DynamicObject[] { signTarget }, null);
        return CustomApiResult.success("success");
    }

}
