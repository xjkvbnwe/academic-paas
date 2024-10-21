package top.dream.work.exam;

import java.util.*;
import kd.bos.base.AbstractBasePlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.RichTextEditor;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class CorretExamPlugin extends AbstractBasePlugIn {

    public void beforeClosed(BeforeClosedEvent e) {
        // TODO Auto-generated method stub
        super.beforeClosed(e);
        e.setCheckDataChange(false);// 取消修改确认弹框，默认为true
    }

    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        String keys = e.getProperty().getName();
        if (keys.equals("ozwe_exam")) {
            this.getPageCache().put("type", "1");
            DynamicObject examObjectLog = (DynamicObject) this.getModel().getValue(keys);
            DynamicObject examObject = BusinessDataServiceHelper.loadSingle("ozwe_examrepository",
                    "number," +
                            "ozwe_cho_question," +
                            "ozwe_cho_answer1," +
                            "ozwe_cho_answer2," +
                            "ozwe_cho_answer3," +
                            "ozwe_cho_answer4," +
                            "ozwe_cho_answer," +
                            "ozwe_muti_question," +
                            "ozwe_muti_answer1," +
                            "ozwe_muti_answer2," +
                            "ozwe_muti_answer3," +
                            "ozwe_muti_answer4," +
                            "ozwe_muti_res1," +
                            "ozwe_muti_res2," +
                            "ozwe_muti_res3," +
                            "ozwe_muti_res4," +
                            "ozwe_entry_mutichoose," +
                            "ozwe_cho_score," +
                            "ozwe_muti_score," +
                            "ozwe_questionflex," +
                            "ozwe_que_question," +
                            "ozwe_coursename," +
                            "ozwe_teacher," +
                            "ozwe_ques_score," +
                            "ozwe_during",
                    (new QFilter("number", QCP.equals, examObjectLog.getString("ozwe_questionnumber"))).toArray());
            // 获取考试记录对象
            DynamicObjectCollection rows = examObjectLog.getDynamicObjectCollection("ozwe_entry_question");
            DynamicObjectCollection rows2 = examObject.getDynamicObjectCollection("ozwe_entry_question");
            this.getPageCache().put("length", rows.size() + "");
            if (rows.size() > 0) {
                this.getModel().setValue("ozwe_information", "第1/" + rows.size() + "题");
                this.getModel().setValue("ozwe_answer", rows.get(0).getString("ozwe_que_question"));
                RichTextEditor edit = this.getView().getControl("ozwe_richtexteditorap");
                edit.setText(rows.get(0).getString("ozwe_question_answerstu2"));
                this.getModel().setValue("ozwe_topic_score", rows2.get(0).getString("ozwe_ques_score"));
                this.getModel().setValue("ozwe_score", 0);
            } else {

            }

        }
        // else if (keys.equals("ozwe_score")) {
        // this.getPageCache().put("score" + this.getPageCache().get("type"),
        // this.getModel().getValue("ozwe_score").toString());
        // this.getView().showMessage(this.getModel().getValue("ozwe_score").toString());
        // }
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addItemClickListeners("ozwe_advcontoolbarap");
        this.addItemClickListeners("tbmain");
    }

    @Override
    public void itemClick(ItemClickEvent e) {
        try {
            DynamicObject examObjectLog = (DynamicObject) this.getModel().getValue("ozwe_exam");
            DynamicObject examObject = BusinessDataServiceHelper.loadSingle("ozwe_examrepository",
                    "number," +
                            "ozwe_cho_question," +
                            "ozwe_cho_answer1," +
                            "ozwe_cho_answer2," +
                            "ozwe_cho_answer3," +
                            "ozwe_cho_answer4," +
                            "ozwe_cho_answer," +
                            "ozwe_muti_question," +
                            "ozwe_muti_answer1," +
                            "ozwe_muti_answer2," +
                            "ozwe_muti_answer3," +
                            "ozwe_muti_answer4," +
                            "ozwe_muti_res1," +
                            "ozwe_muti_res2," +
                            "ozwe_muti_res3," +
                            "ozwe_muti_res4," +
                            "ozwe_entry_mutichoose," +
                            "ozwe_cho_score," +
                            "ozwe_muti_score," +
                            "ozwe_questionflex," +
                            "ozwe_que_question," +
                            "ozwe_coursename," +
                            "ozwe_teacher," +
                            "ozwe_ques_score," +
                            "ozwe_during",
                    (new QFilter("number", QCP.equals, examObjectLog.getString("ozwe_questionnumber"))).toArray());
            super.itemClick(e);
            String itemKey = e.getItemKey();
            if (itemKey.equalsIgnoreCase("ozwe_back")) {
                if (this.getPageCache().get("type").equalsIgnoreCase("1")) {
                    this.getView().showMessage("这已经是第一题了!");
                    return;
                }
                this.getPageCache().put("score" + this.getPageCache().get("type"),
                        this.getModel().getValue("ozwe_score").toString());
                this.getPageCache().put("type", (Integer.parseInt(this.getPageCache().get("type")) - 1) + "");
                try {
                    this.getModel().setValue("ozwe_score",
                            Double.parseDouble(this.getPageCache().get("score" + this.getPageCache().get("type"))));
                } catch (Exception ee) {
                    this.getModel().setValue("ozwe_score", 0);
                }

                DynamicObjectCollection rows = examObjectLog.getDynamicObjectCollection("ozwe_entry_question");
                DynamicObjectCollection rows2 = examObject.getDynamicObjectCollection("ozwe_entry_question");
                this.getPageCache().put("length", rows.size() + "");
                if (rows.size() > 0) {
                    this.getModel().setValue("ozwe_information",
                            "第" + this.getPageCache().get("type") + "/" + rows.size() + "题");
                    this.getModel().setValue("ozwe_answer", rows
                            .get(Integer.parseInt(this.getPageCache().get("type")) - 1).getString("ozwe_que_question"));
                    RichTextEditor edit = this.getView().getControl("ozwe_richtexteditorap");
                    edit.setText(rows.get(Integer.parseInt(this.getPageCache().get("type")) - 1)
                            .getString("ozwe_question_answerstu2"));
                    this.getModel().setValue("ozwe_topic_score", rows2
                            .get(Integer.parseInt(this.getPageCache().get("type")) - 1).getString("ozwe_ques_score"));
                }

            } else if (itemKey.equalsIgnoreCase("ozwe_next")) {
                this.getPageCache().put("score" + this.getPageCache().get("type"),
                        this.getModel().getValue("ozwe_score").toString());
                if (this.getPageCache().get("type").equalsIgnoreCase(this.getPageCache().get("length"))) {
                    this.getView().showMessage("这已经是最后一题了!");
                    return;
                }
                this.getPageCache().put("type", (Integer.parseInt(this.getPageCache().get("type")) + 1) + "");
                try {
                    this.getModel().setValue("ozwe_score",
                            Double.parseDouble(this.getPageCache().get("score" + this.getPageCache().get("type"))));
                } catch (Exception ee) {
                    this.getModel().setValue("ozwe_score", 0);
                }

                DynamicObjectCollection rows = examObjectLog.getDynamicObjectCollection("ozwe_entry_question");
                DynamicObjectCollection rows2 = examObject.getDynamicObjectCollection("ozwe_entry_question");
                this.getPageCache().put("length", rows.size() + "");
                if (rows.size() > 0) {
                    this.getModel().setValue("ozwe_information",
                            "第" + this.getPageCache().get("type") + "/" + rows.size() + "题");
                    this.getModel().setValue("ozwe_answer", rows
                            .get(Integer.parseInt(this.getPageCache().get("type")) - 1).getString("ozwe_que_question"));
                    RichTextEditor edit = this.getView().getControl("ozwe_richtexteditorap");
                    edit.setText(rows.get(Integer.parseInt(this.getPageCache().get("type")) - 1)
                            .getString("ozwe_question_answerstu2"));
                    this.getModel().setValue("ozwe_topic_score", rows2
                            .get(Integer.parseInt(this.getPageCache().get("type")) - 1).getString("ozwe_ques_score"));
                }

            } else if (itemKey.equalsIgnoreCase("ozwe_baritemap")) {
                this.getPageCache().put("score" + this.getPageCache().get("type"),
                        this.getModel().getValue("ozwe_score").toString());
                DynamicObjectCollection rows = examObjectLog.getDynamicObjectCollection("ozwe_entry_question");
                for (int i = 1; i <= rows.size(); i++) {
                    double score = Double.parseDouble(this.getPageCache().get("score" + i));
                    rows.get(i - 1).set("ozwe_question_score", score);
                }
                this.getView().showMessage("保存成功!");
                SaveServiceHelper.saveOperate("ozwe_selectlog", new DynamicObject[] { examObjectLog }, null);
            }
        } catch (NullPointerException ee) {
            this.getView().showMessage("请选择试卷");
        }
    }
}
