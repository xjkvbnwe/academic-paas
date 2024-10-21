package top.dream.work.exam;

import java.util.EventObject;

import kd.bos.bill.BillShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;

public class ConfirmExamPlugin4 extends AbstractFormPlugin{

    @Override
    public void afterCreateNewData(EventObject e) {
        String number = getView().getFormShowParameter().getCustomParam("number");
    	String course = getView().getFormShowParameter().getCustomParam("course");
    	String teacher = getView().getFormShowParameter().getCustomParam("teacher");
    	String time = getView().getFormShowParameter().getCustomParam("time");
        this.getModel().setValue("ozwe_number", number);
        this.getModel().setValue("ozwe_course", course);
        this.getModel().setValue("ozwe_teacher", teacher);
        this.getModel().setValue("ozwe_time", time);
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button confirm = this.getView().getControl("btnok");
        confirm.addClickListener(this);
    }

    @Override
    public void click(EventObject e) {
        Control source = (Control)e.getSource();
        if (source.getKey().equalsIgnoreCase("btnok")) {
            BillShowParameter billShowParameter = new BillShowParameter();
			billShowParameter.setFormId("ozwe_exam");
			billShowParameter.setCustomParam("number", this.getModel().getValue("ozwe_number").toString());
			billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
			this.getView().showForm(billShowParameter);
        }
    }
    
}
