package top.dream.work.course;

import kd.bos.context.RequestContext;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.report.plugin.AbstractReportFormPlugin;
import java.util.*;

import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;

public class ChooseFilterPlugin extends AbstractReportFormPlugin{
    @Override
    public boolean verifyQuery(ReportQueryParam queryParam) {
         // TODO Auto-generated method stub
        
        int type = Integer.parseInt(this.getModel().getValue("ozwe_type").toString());
        DynamicObject dy = BusinessDataServiceHelper.loadSingle("ozwe_batchinfo",
                        "ozwe_figure," +
                        "ozwe_begintime," +
                        "ozwe_endtime," + 
                        "ozwe_orgname," +
                        "ozwe_grade",
                        (new QFilter("ozwe_figure", QCP.equals, type)).toArray());

        if (dy == null) {
            this.getView().showMessage("未找到批次，请正确输入批次");
            return false;
        }
        
        Date beginDate = dy.getDate("ozwe_begintime");
        Date endDate = dy.getDate("ozwe_endtime");

        DynamicObject userOrgObject = BusinessDataServiceHelper.loadSingle("bos_adminorg",
                        "ozwe_academy," +
                        "ozwe_grade,"+ 
                        "id",
                        (new QFilter("id", QCP.equals, RequestContext.get().getOrgId())).toArray());          

        if (new Date().getTime() < beginDate.getTime()) {
            this.getView().showMessage("该批次还未到达选课时间，不可选课");
            return false;
        } else if (new Date().getTime() > endDate.getTime()) {
            this.getView().showMessage("该批次已经过了选课时间，不可选课");
            return false;
        } else if (!userOrgObject.getString("ozwe_academy").equalsIgnoreCase(dy.getString("ozwe_orgname")) && !dy.getString("ozwe_orgname").equalsIgnoreCase("all")) {
            this.getView().showMessage("您不属于该选课学院，请正确输入批次");
            return false;
        } else if (dy.getInt("ozwe_grade") != userOrgObject.getInt("ozwe_grade") && dy.getInt("ozwe_grade") != 0) {
            this.getView().showMessage("您不属于该选课年级，请正确输入批次");
            return false;
        }

        return true;
    }
}
