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

@ApiController(value = "bos", desc = "API寝室登记")
public class SchoolRoomRegisterAPI {
    @ApiPostMapping(value = "/getRoomReg", desc = "获取课程")
    public CustomApiResult<@ApiResponseBody("output") String> getRoomReg(@ApiParam(value = "input") Map<String,Object> inputMap) {

        DynamicObject[] roomList = BusinessDataServiceHelper.load("ozwe_stayinfo",
                        "number," +
                        "ozwe_type," +
                        "ozwe_sum," +
                        "status," +
                        "ozwe_user.id," +
                        "ozwe_situation," +
                        "ozwe_date",
                        (new QFilter("number", QCP.not_equals, null)).toArray());

        for (DynamicObject roomSingle : roomList) {
            DynamicObjectCollection dynamicEntry = roomSingle.getDynamicObjectCollection("ozwe_entryentity");
            for (DynamicObject entity : dynamicEntry) {
                if (entity.getLong("ozwe_user.id") == RequestContext.get().getCurrUserId()) {
                    entity.set("ozwe_situation", "1");
                    entity.set("ozwe_date", new Date());
                    SaveServiceHelper.saveOperate("ozwe_stayinfo", new DynamicObject[] {roomSingle}, null);
                    return CustomApiResult.success("1_登记成功，寝室号: "+roomSingle.getString("number"));
                }
            }
        }

        return CustomApiResult.success("0_未找到寝室");
    }
    
}
