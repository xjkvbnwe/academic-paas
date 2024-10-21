package top.dream.work;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import kd.bos.ext.form.control.Video;
import kd.bos.form.ClientActions;
import kd.bos.form.IClientViewProxy;
import kd.bos.form.plugin.AbstractFormPlugin;


public class SchoolVideo extends AbstractFormPlugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        Video videoCtl = this.getView().getControl("ozwe_videoap");
        videoCtl.setSrc("http://www.xhu.edu.cn/_upload/article/videos/ab/a7/bee6bdf24f479010110bf5def3b6/f3c3fd4c-30fc-4b53-9755-6ecd4bb5416c-B.mp4");

        // 设置自动播放
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("autoPlay", false); // 设置为自动播放
        dataMap.put("k", "ozwe_videoap");
        IClientViewProxy proxy = this.getView().getService(IClientViewProxy.class);
        proxy.addAction(ClientActions.updateControlStates, dataMap);
    }
}
