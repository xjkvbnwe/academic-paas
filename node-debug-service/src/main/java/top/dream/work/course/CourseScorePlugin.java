package top.dream.work.course;

import java.util.*;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class CourseScorePlugin extends AbstractBillPlugIn {

    public void beforeClosed(BeforeClosedEvent e) {
        // TODO Auto-generated method stub
        super.beforeClosed(e);
        e.setCheckDataChange(false);// 取消修改确认弹框，默认为true
    }

    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        String keys = e.getProperty().getName();
        if (keys.equals("ozwe_course")) {
            DynamicObject courseObject = (DynamicObject) this.getModel().getValue(keys);
            String courseNumber = courseObject.getString("number");
            DynamicObject[] courseLogObjectList = BusinessDataServiceHelper.load("ozwe_chooseclass",
                    "creator.name," +
                            "creator.number," +
                            "creator," +
                            "ozwe_number",
                    (new QFilter("ozwe_number", QCP.equals, courseNumber).toArray()));
            for (int i = 0; i<courseLogObjectList.length; i++) {
                this.getModel().deleteEntryRow("entryentity", i);
                this.getModel().createNewEntryRow("entryentity");
                this.getModel().setValue("ozwe_number", courseLogObjectList[i].getString("creator.number"), i);
                this.getModel().setValue("ozwe_name", courseLogObjectList[i].getString("creator.name"), i);
            }
        }
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    @Override
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
            String itemKey = e.getItemKey();
            if (itemKey.equalsIgnoreCase("ozwe_baritemap")) {
                DynamicObject courseObject = (DynamicObject) this.getModel().getValue("ozwe_course");
                for (DynamicObject dy : this.getModel().getEntryEntity("entryentity")) {
                    DynamicObject courseLogObject = BusinessDataServiceHelper.loadSingle("ozwe_chooseclass",
                    "creator.name," +
                            "creator.number," +
                            "creator," +
                            "ozwe_number",
                    (new QFilter[] {new QFilter("ozwe_number", QCP.equals, courseObject.getString("number")),
                    new QFilter("creator.number", QCP.equals, dy.getString("ozwe_number"))}));
                    DynamicObject newScore = BusinessDataServiceHelper.newDynamicObject("ozwe_score");
                    newScore.set("enable", 1);
                    newScore.set("status", "C");
                    StringBuffer sb1 = new StringBuffer();
                    for (int i = 1; i <= 12; i++) {
                        int ascii = 48 + (int) (Math.random() * 9);
                        char c = (char) ascii;
                        sb1.append(c);
                    }
                    newScore.set("number", sb1.toString());
                    newScore.set("ozwe_student", courseLogObject.getDynamicObject("creator"));
                    newScore.set("ozwe_teacher", courseObject.getDynamicObject("ozwe_teacher"));
                    newScore.set("ozwe_year", courseObject.getString("ozwe_year"));
                    newScore.set("ozwe_term1", courseObject.getString("ozwe_term"));
                    newScore.set("ozwe_coursenumber", courseObject.getString("number"));
                    newScore.set("name", courseObject.getString("name"));
                    newScore.set("ozwe_point", Double.parseDouble(courseObject.getString("ozwe_point")));
                    newScore.set("creator", RequestContext.get().getCurrUserId());
                    newScore.set("ozwe_result", Double.parseDouble(dy.getString("ozwe_score")));
                    double gpa , score = Double.parseDouble(dy.getString("ozwe_score"));
                    if (score >=60) {
                        gpa = 1 + (score-60)/10;
                    } else {
                        gpa = 0;
                    }
                    newScore.set("ozwe_gpa", gpa);

                    newScore.set("ozwe_endgpa", gpa * Double.parseDouble(courseObject.getString("ozwe_point")));
                    newScore.set("ozwe_pass", dy.getString("ozwe_pass"));
                    SaveServiceHelper.saveOperate("ozwe_course", new DynamicObject[] { newScore }, null);
                    this.getView().showMessage("上传成功");
                    this.getView().close();
                }
            }
    }

}
