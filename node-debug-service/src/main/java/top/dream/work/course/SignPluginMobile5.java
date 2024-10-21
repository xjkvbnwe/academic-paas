package top.dream.work.course;

import java.util.*;
import java.math.*;
import kd.bos.base.AbstractMobBasePlugIn;
import kd.bos.form.control.Button;
import kd.bos.ext.form.control.MapControl;
import kd.bos.ext.form.control.events.MapSelectEvent;
import kd.bos.ext.form.control.events.MapSelectListener;
import kd.bos.ext.form.dto.MapSelectPointOption;

public class SignPluginMobile5 extends AbstractMobBasePlugIn implements MapSelectListener {

    SignPluginMobile5 that = this;

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);

        Button bt = getControl("ozwe_mapinfo");
        bt.addClickListener(this);
        MapControl mapControl = getControl("ozwe_mapcontrolap");
        mapControl.addSelectListener((MapSelectListener) this);
        mapControl.setDraggable(true);
        mapControl.setDroppable(true);
    }

    @Override
    public void click(EventObject evt) {

        Object object = evt.getSource();
        if (object instanceof Button) {
            Button bt = (Button) object;
            if (bt.getKey().equals("ozwe_mapinfo")) {
                MapControl mapControl = getControl("ozwe_mapcontrolap");
                // 缓存添加实时打卡标记，防止死循环
                getPageCache().put("selectLocate", "ture");

                // 获取地图控件点位当前标记位置。
                mapControl.getAddress();
                this.getView().updateView("ozwe_mapcontrolap");
            }
        }
    }

    /**
     * 获取地图当前位置标记点信息。
     * mapControl.getAddress();回调自动触发
     * 
     * @param evt 地图信息
     */
    @Override
    public void select(MapSelectEvent evt) {
        Map<String, Object> map = evt.getPoint();
        Map point = (Map) map.get("point");

        // 地图控件的 selectAddress,getAddress方法都会调用这个回调，为防止死循环，需要做一个标志位判断
        if ("ture".equals(getPageCache().get("selectLocate"))) {
            // 获取地位后。移除标记点
            getPageCache().remove("selectLocate");
            MapControl mapControl = getControl("ozwe_mapcontrolap");
            MapSelectPointOption mapSelectPointOption = new MapSelectPointOption();
            // 经度
            double longitude = ((BigDecimal) point.get("lng")).doubleValue();
            mapSelectPointOption.setLng(longitude);
            // 维度
            double latitude = ((BigDecimal) point.get("lat")).doubleValue();
            mapSelectPointOption.setLat(latitude);
            mapControl.selectPoint(mapSelectPointOption);
            getModel().setValue("ozwe_longitude", longitude);
            getModel().setValue("ozwe_latitude", latitude);
        }
    }
}
