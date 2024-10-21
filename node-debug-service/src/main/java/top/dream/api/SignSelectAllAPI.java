package top.dream.api;

import java.text.SimpleDateFormat;
import java.util.*;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.result.CustomApiResult;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.openapi.common.custom.annotation.*;


@ApiController(value = "bos", desc = "API获取签到课程")
public class SignSelectAllAPI{
    @ApiPostMapping(value = "/getSignList", desc = "获取课程")
    public CustomApiResult<@ApiResponseBody("output") List<Map<String,String>>> getSignList(@ApiParam(value = "input") Map<String,Object> inputMap) {

        System.out.println(System.getProperty("attachment.fileserver"));
        
        DynamicObject[] signList = BusinessDataServiceHelper.load("ozwe_sign",
                        "billno," +
                        "ozwe_class," +
                        "ozwe_type," +
                        "creator.id," +
                        "ozwe_code," +
                        "ozwe_latitude," +
                        "ozwe_longitude," +
                        "ozwe_range," +
                        "ozwe_entryentity," +
                        "ozwe_number," +
                        "ozwe_name," +
                        "ozwe_datetime," +
                        "ozwe_end",
                        (new QFilter("billno", QCP.not_equals, null)).toArray());
        
        List<Map<String,String>> endResult = new ArrayList<>();

        mainLoop:
        for (DynamicObject signSingle : signList) {
            DynamicObjectCollection entryEntity = signSingle.getDynamicObjectCollection("ozwe_entryentity");
            for (DynamicObject entry : entryEntity) {
                if (entry.getString("ozwe_number").equalsIgnoreCase(RequestContext.get().getCurrUserId()+"")) {
                    continue mainLoop;
                }
            }
            DynamicObject course = signSingle.getDynamicObject("ozwe_class");
            DynamicObject[] dyList = BusinessDataServiceHelper.load("ozwe_chooseclass",
                        "ozwe_number," +
                        "creator.id," +
                        "creator.phone," +
                        "ozwe_teacher,"+
                        "ozwe_name",
                        (new QFilter[] {new QFilter("ozwe_number", QCP.equals, course.getString("number")),
                        new QFilter("creator.phone",QCP.equals, inputMap.get("phone"))} ));
            for (DynamicObject dySingle : dyList) {
                Map<String, String> result = new HashMap<>();
                result.put("signBillno", signSingle.getString("billno"));
                result.put("signType", signSingle.getString("ozwe_type"));
                result.put("signTextKey", signSingle.getString("ozwe_code"));
                result.put("latitude", signSingle.getString("ozwe_latitude"));
                result.put("longitude", signSingle.getString("ozwe_longitude"));
                result.put("range", signSingle.getString("ozwe_range"));
                result.put("courseName", dySingle.getString("ozwe_name"));
                result.put("teacher", dySingle.getString("ozwe_teacher"));
                result.put("endDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(signSingle.getDate("ozwe_end")));
                endResult.add(result);
            }
        }
        
        return CustomApiResult.success(endResult);
    }
}
