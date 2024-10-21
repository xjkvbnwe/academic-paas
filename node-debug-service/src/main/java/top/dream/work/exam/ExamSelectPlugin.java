package top.dream.work.exam;

import java.util.*;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.CloseCallBack;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.report.IReportView;
import kd.bos.report.ReportList;
import kd.bos.report.plugin.AbstractReportFormPlugin;

public class ExamSelectPlugin extends AbstractReportFormPlugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        ReportList reportList = getControl("reportlistap");
        IReportView view = (IReportView) this.getView();
        reportList.addHyperClickListener(new HyperLinkClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void hyperLinkClick(HyperLinkClickEvent e) {
                String field = e.getFieldName();
                if (field.equalsIgnoreCase("ozwe_number")) {
                    DynamicObject rowData = e.getRowData();
                    FormShowParameter formShowParameter = new FormShowParameter();
                    // 设置子页面id
                    formShowParameter.setFormId("ozwe_selectexam");
                    // 传递参数
                    formShowParameter.setCustomParam("number", rowData.getString("ozwe_number"));
                    formShowParameter.setCustomParam("course", rowData.getString("ozwe_course"));
                    formShowParameter.setCustomParam("teacher", rowData.getString("ozwe_teacher"));
                    formShowParameter.setCustomParam("time", rowData.getString("ozwe_time"));
                    // 回调参数
                    formShowParameter.setCloseCallBack(new CloseCallBack());
                    formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                    view.showForm(formShowParameter);
                }

            }

        });
    }
}
