package top.dream.task;

import java.util.*;

import com.alibaba.fastjson.*;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.exception.KDException;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import top.dream.function.GetAPIFunction;

public class GetWeatherTask extends AbstractTask {

    @Override
    public void execute(RequestContext arg0, Map<String, Object> arg1) throws KDException {
        // String url =
        // "http://route.showapi.com/104-29?showapi_appid=1015196&city=成都&showapi_sign=3a4fc08e4f7d42238eb3d702e67a80b0";
        String url = "http://route.showapi.com/9-9?showapi_appid=1015196&area=成都&showapi_sign=3a4fc08e4f7d42238eb3d702e67a80b0";
        String result = GetAPIFunction.getResult(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject resultObject = jsonObject.getJSONObject("showapi_res_body");
        JSONArray resultArray = resultObject.getJSONArray("dayList");
        DeleteServiceHelper.delete("ozwe_weatherinfo", new QFilter[] { new QFilter("billno", QCP.not_equals, null)});
        for (Object o : resultArray) {
            JSONObject resultSingle = (JSONObject) o;
            StringBuffer sb1 = new StringBuffer();
            for (int i = 1; i <= 10; i++) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb1.append(c);
            }
            DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("ozwe_weatherinfo");
            dynamicObject.set("billno", sb1.toString());
            dynamicObject.set("billstatus", "C");
            dynamicObject.set("ozwe_city", resultSingle.getString("area"));
            dynamicObject.set("ozwe_date", resultSingle.getString("daytime"));
            dynamicObject.set("ozwe_textfield", resultSingle.getString("day_weather"));
            dynamicObject.set("ozwe_weather_night", resultSingle.getString("night_weather"));
            dynamicObject.set("ozwe_textfield1", resultSingle.getString("day_air_temperature"));
            dynamicObject.set("ozwe_temporature_night", resultSingle.getString("night_air_temperature"));
            dynamicObject.set("creator", RequestContext.get().getCurrUserId());
            dynamicObject.set("auditor", RequestContext.get().getCurrUserId());
            SaveServiceHelper.saveOperate("ozwe_weatherinfo", new DynamicObject[] {dynamicObject}, null);
        }
    }

}
