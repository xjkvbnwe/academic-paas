package top.dream.work;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import kd.bos.ext.form.control.MapControl;
import kd.bos.form.ClientActions;
import kd.bos.form.IClientViewProxy;
import kd.bos.form.plugin.AbstractFormPlugin;

public class SchoolMap extends AbstractFormPlugin {

    @Override
    public void afterCreateNewData(EventObject e) {
        getPageCache().put("selectLocate", "ture");
        // 设置显示隐藏搜索框（通用u指令）*
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("showSearchBox", false); // 属性名称+值*
        dataMap.put("k", "ozwe_mapcontrolap");

        // 设置是否可拖动地图标记
        dataMap.put("canMarkerDraggble", true); // 属性名称,值*
        dataMap.put("k", "ozwe_mapcontrolap");
        IClientViewProxy proxy = this.getView().getService(IClientViewProxy.class);
        proxy.addAction(ClientActions.updateControlStates, dataMap);

        // 设置地图根据经纬度标记某个位置
        MapControl mapCtl = this.getView().getControl("ozwe_mapcontrolap");
        // 设置地图根据地址标记某个位置
        mapCtl.selectAddress("成都市金牛区西华大学-行政办公楼");

    }
}
