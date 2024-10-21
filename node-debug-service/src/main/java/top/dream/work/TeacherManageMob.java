package top.dream.work;

import java.util.*;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.list.plugin.AbstractMobListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.form.control.Label;
import kd.bos.context.RequestContext;

public class TeacherManageMob extends AbstractMobListPlugin{
    @Override
    public void afterCreateNewData(EventObject e) {
        Label courseSum = getView().getControl("ozwe_labelap6");
        Label logSum = getView().getControl("ozwe_labelap7");
        Label signSum = getView().getControl("ozwe_labelap71");
        Label examSum = getView().getControl("ozwe_labelap72");

        DynamicObject[] courseList = BusinessDataServiceHelper.load("ozwe_course",
				"number," +
                "ozwe_teacher.id",
				new QFilter[]{new QFilter("ozwe_teacher.id",QCP.equals,RequestContext.get().getCurrUserId())});
        
        DynamicObject[] logList = BusinessDataServiceHelper.load("ozwe_score",
				"number," +
                "ozwe_teacher.id",
				new QFilter[]{new QFilter("ozwe_teacher.id",QCP.equals,RequestContext.get().getCurrUserId())});

        DynamicObject[] signList = BusinessDataServiceHelper.load("ozwe_sign",
				"billno," +
                "creator.id",
				new QFilter[]{new QFilter("creator.id",QCP.equals,RequestContext.get().getCurrUserId())});

        DynamicObject[] examList = BusinessDataServiceHelper.load("ozwe_examrepository",
				"number," +
                "creator.id",
				new QFilter[]{new QFilter("creator.id",QCP.equals,RequestContext.get().getCurrUserId())});

        courseSum.setText(courseList.length+"");
        logSum.setText(logList.length+"");
        signSum.setText(signList.length+"");
        examSum.setText(examList.length+"");
    }
}
