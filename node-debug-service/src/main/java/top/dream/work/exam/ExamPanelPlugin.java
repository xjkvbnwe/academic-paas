package top.dream.work.exam;

import java.util.*;
import kd.bos.base.AbstractBasePlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.ext.form.control.CountDown;
import kd.bos.ext.form.control.events.CountDownEvent;
import kd.bos.ext.form.control.events.CountDownListener;
import kd.bos.form.control.RichTextEditor;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class ExamPanelPlugin extends AbstractBasePlugIn implements CountDownListener {

    public void beforeClosed(BeforeClosedEvent e) {
        // TODO Auto-generated method stub
        super.beforeClosed(e);
        e.setCheckDataChange(false);// 取消修改确认弹框，默认为true

    }

    @Override
    public void afterCreateNewData(EventObject e) {

        this.getView().setVisible(false, "ozwe_multichooseflex");
        this.getView().setVisible(false, "ozwe_questionflex");
        // 获取题库选择题列表
        DynamicObject examObject = BusinessDataServiceHelper.loadSingle("ozwe_examrepository",
                "number," +
                        "ozwe_cho_question," +
                        "ozwe_cho_answer1," +
                        "ozwe_cho_answer2," +
                        "ozwe_cho_answer3," +
                        "ozwe_cho_answer4," +
                        "ozwe_cho_answer" +
                        "ozwe_muti_question," +
                        "ozwe_muti_answer1," +
                        "ozwe_muti_answer2," +
                        "ozwe_muti_answer3," +
                        "ozwe_muti_answer4," +
                        "ozwe_entry_mutichoose," +
                        "ozwe_questionflex," +
                        "ozwe_que_question," +
                        "ozwe_coursename," +
                        "ozwe_teacher," +
                        "ozwe_during," +
                        "ozwe_entry_question",
                (new QFilter("number", QCP.equals, getView().getFormShowParameter().getCustomParam("number"))).toArray());
        CountDown countdown = this.getView().getControl("ozwe_countdownap");
        // 设置倒计时时间为70秒
        countdown.setDuration(examObject.getInt("ozwe_during") * 60);
        countdown.start();

        // 获取目前题目缓存
        this.getPageCache().put("type", "singleChoose");
        this.getPageCache().put("topic", "1");
        DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_choose");
        if (rows.size() > 0) {
            DynamicObject rowData = rows.get(0);
            this.getModel().setValue("ozwe_question", rowData.getString("ozwe_cho_question"));
            for (int i = 1; i <= 4; i++) {
                Map<String, Object> arg1 = new HashMap<>();
                arg1.put("zh_CN", rowData.getString("ozwe_cho_answer" + i));
                Map<String, Object> map = new HashMap<>();
                map.put("caption", arg1);
                this.getView().updateControlMetadata("ozwe_choose" + i, map);
            }

            this.getModel().setValue("ozwe_information", "第1/" + rows.size() + "题");
        }
    }

    @Override
    public void onCountDownEnd(CountDownEvent evt) {
        CountDownListener.super.onCountDownEnd(evt);
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
                        "ozwe_during",
                (new QFilter("number", QCP.equals, getView().getFormShowParameter().getCustomParam("number"))).toArray());
        Map<String, String> mapStuAnswer = new HashMap<>();
        mapStuAnswer.put("1", "A");
        mapStuAnswer.put("2", "B");
        mapStuAnswer.put("3", "C");
        mapStuAnswer.put("4", "D");
        // 保存缓存
        String type = this.getPageCache().get("type");
        switch (type) {
            case "singleChoose": {
                String singleAnswer;
                try {
                    singleAnswer = this.getModel().getValue("ozwe_radiogroup").toString();
                } catch (NullPointerException ee) {
                    singleAnswer = null;
                }
                this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                        singleAnswer);
                break;
            }
            case "question": {
                RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                        edit.getText());
                break;
            }
            case "multiChoose": {
                StringBuilder sb = new StringBuilder();
                sb.append((this.getModel().getValue("ozwe_checkbox1").toString().equalsIgnoreCase("true") ? "1"
                        : "0")
                        + (this.getModel().getValue("ozwe_checkbox2").toString().equalsIgnoreCase("true") ? "1"
                                : "0")
                        + (this.getModel().getValue("ozwe_checkbox3").toString().equalsIgnoreCase("true") ? "1"
                                : "0")
                        + (this.getModel().getValue("ozwe_checkbox4").toString().equalsIgnoreCase("true") ? "1"
                                : "0"));
                this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                        sb.toString());
                break;
            }
        }
        // 获取答案键值对
        DynamicObject newDynamicObject = BusinessDataServiceHelper.newDynamicObject("ozwe_answerlog");
        newDynamicObject.set("status", "C");
        newDynamicObject.set("enable", 1);
        // 创建编码
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= 10; i++) {
            int ascii = 48 + (int) (Math.random() * 9);
            char c = (char) ascii;
            sb.append(c);
        }
        newDynamicObject.set("number", sb.toString());
        newDynamicObject.set("ozwe_questionnumber", examObject.getString("number"));
        newDynamicObject.set("name", examObject.getString("ozwe_coursename"));
        newDynamicObject.set("ozwe_teacher", examObject.getString("ozwe_teacher"));
        newDynamicObject.set("creator", RequestContext.get().getCurrUserId());
        DynamicObjectCollection rowsChoose = examObject.getDynamicObjectCollection("ozwe_entry_choose");
        DynamicObjectCollection rowsMuti = examObject.getDynamicObjectCollection("ozwe_entry_mutichoose");
        DynamicObjectCollection rowsQuestion = examObject.getDynamicObjectCollection("ozwe_entry_question");
        Map<String, String> mapResult = new HashMap<>();

        for (int i = 1; i <= rowsChoose.size(); i++) {
            mapResult.put("singleChoose" + i, this.getPageCache().get("singleChoose" + i));
            DynamicObjectCollection rows = newDynamicObject.getDynamicObjectCollection("ozwe_entry_choose");
            DynamicObject dObject = rows.addNew();
            dObject.set("ozwe_cho_question", rowsChoose.get(i - 1).getString("ozwe_cho_question"));
            dObject.set("ozwe_answerstudent", this.getPageCache().get("singleChoose" + i));
            dObject.set("ozwe_cho_answer", rowsChoose.get(i - 1).getString("ozwe_cho_answer"));
            try {
                dObject.set("ozwe_situation",
                        mapStuAnswer.get(this.getPageCache().get("singleChoose" + i))
                                .equalsIgnoreCase(rowsChoose.get(i - 1).getString("ozwe_cho_answer"))
                                        ? Double.parseDouble(rowsChoose.get(i - 1).getString("ozwe_cho_score"))
                                        : 0);
                                        
            } catch (Exception ee) {
                dObject.set("ozwe_situation", 0);
            }
            rows.set(i - 1, dObject);
        }
        for (int i = 1; i <= rowsMuti.size(); i++) {
            mapResult.put("multiChoose" + i, this.getPageCache().get("multiChoose" + i));
            DynamicObjectCollection rows = newDynamicObject.getDynamicObjectCollection("ozwe_entry_mutichoose");
            DynamicObject dObject = rows.addNew();
            dObject.set("ozwe_muti_question", rowsMuti.get(i -
                    1).getString("ozwe_muti_question"));
            dObject.set("ozwe_muti_answerstu", this.getPageCache().get("multiChoose" +
                    i));
            dObject.set("ozwe_muti_res1", rowsMuti.get(i -
                    1).getBoolean("ozwe_muti_res1"));
            dObject.set("ozwe_muti_res2", rowsMuti.get(i -
                    1).getBoolean("ozwe_muti_res2"));
            dObject.set("ozwe_muti_res3", rowsMuti.get(i -
                    1).getBoolean("ozwe_muti_res3"));
            dObject.set("ozwe_muti_res4", rowsMuti.get(i -
                    1).getBoolean("ozwe_muti_res4"));
            StringBuilder sb1 = new StringBuilder();
            sb1.append((rowsMuti.get(i -
                    1).getString("ozwe_muti_res1").equalsIgnoreCase("true") ? "1"
                            : "0")
                    + (rowsMuti.get(i - 1).getString("ozwe_muti_res2").equalsIgnoreCase("true")
                            ? "1"
                            : "0")
                    + (rowsMuti.get(i - 1).getString("ozwe_muti_res3").equalsIgnoreCase("true")
                            ? "1"
                            : "0")
                    + (rowsMuti.get(i - 1).getString("ozwe_muti_res4").equalsIgnoreCase("true")
                            ? "1"
                            : "0"));
            try {
                dObject.set("ozwe_muti_score",
                        this.getPageCache().get("multiChoose" + i).trim()
                                .contains(sb1.toString().trim())
                                        ? Double.parseDouble(rowsMuti.get(i - 1).getString("ozwe_muti_score"))
                                        : 0);
            } catch (Exception ee) {
                dObject.set("ozwe_muti_score", 0);
            }
            rows.set(i - 1, dObject);
        }
        for (int i = 1; i <= rowsQuestion.size(); i++) {
            mapResult.put("question" + i, this.getPageCache().get("question" + i));
            DynamicObjectCollection rows = newDynamicObject.getDynamicObjectCollection("ozwe_entry_question");
            DynamicObject dObject = rows.addNew();
            dObject.set("ozwe_que_question", rowsQuestion.get(i -
                    1).getString("ozwe_que_question"));
            dObject.set("ozwe_question_answerstu2", this.getPageCache().get("question" +
                    i));
            dObject.set("ozwe_question_score", 0);
            // 设置
            rows.set(i - 1, dObject);
        }
        this.getView().showMessage("时间到了，试卷已提交成功");
        this.getView().close();
        SaveServiceHelper.saveOperate("ozwe_answerlog", new DynamicObject[] {
                newDynamicObject }, null);

    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addItemClickListeners("ozwe_advcontoolbarap");
        this.addItemClickListeners("ozwe_tbmain");
        this.addItemClickListeners("tbmain");
        CountDown countdown = this.getView().getControl("ozwe_countdownap");
        countdown.addCountDownListener(this);
    }

    @Override
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        String itemKey = e.getItemKey();
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
                        "ozwe_during",
                (new QFilter("number", QCP.equals, getView().getFormShowParameter().getCustomParam("number"))).toArray());
        Map<String, String> mapStuAnswer = new HashMap<>();
        mapStuAnswer.put("1", "A");
        mapStuAnswer.put("2", "B");
        mapStuAnswer.put("3", "C");
        mapStuAnswer.put("4", "D");
        if (itemKey.equalsIgnoreCase("ozwe_finish")) {
            // 保存缓存
            String type = this.getPageCache().get("type");
            switch (type) {
                case "singleChoose": {
                    String singleAnswer;
                    try {
                        singleAnswer = this.getModel().getValue("ozwe_radiogroup").toString();
                    } catch (NullPointerException ee) {
                        singleAnswer = null;
                    }
                    this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                            singleAnswer);
                    break;
                }
                case "question": {
                    RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                    this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                            edit.getText());
                    break;
                }
                case "multiChoose": {
                    StringBuilder sb = new StringBuilder();
                    sb.append((this.getModel().getValue("ozwe_checkbox1").toString().equalsIgnoreCase("true") ? "1"
                            : "0")
                            + (this.getModel().getValue("ozwe_checkbox2").toString().equalsIgnoreCase("true") ? "1"
                                    : "0")
                            + (this.getModel().getValue("ozwe_checkbox3").toString().equalsIgnoreCase("true") ? "1"
                                    : "0")
                            + (this.getModel().getValue("ozwe_checkbox4").toString().equalsIgnoreCase("true") ? "1"
                                    : "0"));
                    this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                            sb.toString());
                    break;
                }
            }

            // 获取答案键值对
            DynamicObject newDynamicObject = BusinessDataServiceHelper.newDynamicObject("ozwe_answerlog");
            newDynamicObject.set("status", "C");
            newDynamicObject.set("enable", 1);
            // 创建编码
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i <= 10; i++) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb.append(c);
            }
            newDynamicObject.set("number", sb.toString());
            newDynamicObject.set("ozwe_questionnumber", examObject.getString("number"));
            newDynamicObject.set("name", examObject.getString("ozwe_coursename"));
            newDynamicObject.set("ozwe_teacher", examObject.getString("ozwe_teacher"));
            newDynamicObject.set("creator", RequestContext.get().getCurrUserId());
            DynamicObjectCollection rowsChoose = examObject.getDynamicObjectCollection("ozwe_entry_choose");
            DynamicObjectCollection rowsMuti = examObject.getDynamicObjectCollection("ozwe_entry_mutichoose");
            DynamicObjectCollection rowsQuestion = examObject.getDynamicObjectCollection("ozwe_entry_question");
            Map<String, String> mapResult = new HashMap<>();

            for (int i = 1; i <= rowsChoose.size(); i++) {
                mapResult.put("singleChoose" + i, this.getPageCache().get("singleChoose" + i));
                DynamicObjectCollection rows = newDynamicObject.getDynamicObjectCollection("ozwe_entry_choose");
                DynamicObject dObject = rows.addNew();
                dObject.set("ozwe_cho_question", rowsChoose.get(i - 1).getString("ozwe_cho_question"));
                dObject.set("ozwe_answerstudent", this.getPageCache().get("singleChoose" + i));
                dObject.set("ozwe_cho_answer", rowsChoose.get(i - 1).getString("ozwe_cho_answer"));
                try {
                    dObject.set("ozwe_situation",
                            mapStuAnswer.get(this.getPageCache().get("singleChoose" + i))
                                    .equalsIgnoreCase(rowsChoose.get(i - 1).getString("ozwe_cho_answer"))
                                            ? Double.parseDouble(rowsChoose.get(i - 1).getString("ozwe_cho_score"))
                                            : 0);
                } catch (Exception ee) {
                    dObject.set("ozwe_situation", 0);
                }
                rows.set(i - 1, dObject);
            }
            for (int i = 1; i <= rowsMuti.size(); i++) {
                mapResult.put("multiChoose" + i, this.getPageCache().get("multiChoose" + i));
                DynamicObjectCollection rows = newDynamicObject.getDynamicObjectCollection("ozwe_entry_mutichoose");
                DynamicObject dObject = rows.addNew();
                dObject.set("ozwe_muti_question", rowsMuti.get(i -
                        1).getString("ozwe_muti_question"));
                dObject.set("ozwe_muti_answerstu", this.getPageCache().get("multiChoose" +
                        i));
                dObject.set("ozwe_muti_res1", rowsMuti.get(i -
                        1).getBoolean("ozwe_muti_res1"));
                dObject.set("ozwe_muti_res2", rowsMuti.get(i -
                        1).getBoolean("ozwe_muti_res2"));
                dObject.set("ozwe_muti_res3", rowsMuti.get(i -
                        1).getBoolean("ozwe_muti_res3"));
                dObject.set("ozwe_muti_res4", rowsMuti.get(i -
                        1).getBoolean("ozwe_muti_res4"));
                StringBuilder sb1 = new StringBuilder();
                sb1.append((rowsMuti.get(i -
                        1).getString("ozwe_muti_res1").equalsIgnoreCase("true") ? "1"
                                : "0")
                        + (rowsMuti.get(i - 1).getString("ozwe_muti_res2").equalsIgnoreCase("true")
                                ? "1"
                                : "0")
                        + (rowsMuti.get(i - 1).getString("ozwe_muti_res3").equalsIgnoreCase("true")
                                ? "1"
                                : "0")
                        + (rowsMuti.get(i - 1).getString("ozwe_muti_res4").equalsIgnoreCase("true")
                                ? "1"
                                : "0"));
                try {
                    dObject.set("ozwe_muti_score",
                            this.getPageCache().get("multiChoose" + i).trim()
                                    .contains(sb1.toString().trim())
                                            ? Double.parseDouble(rowsMuti.get(i - 1).getString("ozwe_muti_score"))
                                            : 0);
                } catch (Exception ee) {
                    dObject.set("ozwe_muti_score", 0);
                }

                rows.set(i - 1, dObject);
            }
            for (int i = 1; i <= rowsQuestion.size(); i++) {
                mapResult.put("question" + i, this.getPageCache().get("question" + i));
                DynamicObjectCollection rows = newDynamicObject.getDynamicObjectCollection("ozwe_entry_question");
                DynamicObject dObject = rows.addNew();
                dObject.set("ozwe_que_question", rowsQuestion.get(i -
                        1).getString("ozwe_que_question"));
                dObject.set("ozwe_question_answerstu2", this.getPageCache().get("question" +
                        i));
                dObject.set("ozwe_question_score", 0);
                // 设置
                rows.set(i - 1, dObject);
            }
            this.getView().showMessage("提交成功!");
            this.getView().close();
            SaveServiceHelper.saveOperate("ozwe_answerlog", new DynamicObject[] {
                    newDynamicObject }, null);
        }
        try {
            if (itemKey.equalsIgnoreCase("ozwe_next")) {
                String type = this.getPageCache().get("type");
                switch (type) {
                    case "singleChoose": {
                        String singleAnswer;
                        try {
                            singleAnswer = this.getModel().getValue("ozwe_radiogroup").toString();
                        } catch (NullPointerException ee) {
                            singleAnswer = null;
                        }
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                singleAnswer);
                        this.getPageCache().put("topic", Integer.parseInt(this.getPageCache().get("topic")) + 1 + "");
                        int topic = Integer.parseInt(this.getPageCache().get("topic"));
                        DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_choose");
                        if (rows.size() > topic - 1) {
                            DynamicObject rowData = rows.get(topic - 1);
                            this.getModel().setValue("ozwe_question", rowData.getString("ozwe_cho_question"));
                            for (int i = 1; i <= 4; i++) {
                                Map<String, Object> arg1 = new HashMap<>();
                                arg1.put("zh_CN", rowData.getString("ozwe_cho_answer" + i));
                                Map<String, Object> map = new HashMap<>();
                                map.put("caption", arg1);
                                this.getView().updateControlMetadata("ozwe_choose" + i, map);
                            }
                            if (this.getPageCache()
                                    .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                                this.getModel().setValue("ozwe_radiogroup", this.getPageCache()
                                        .get(this.getPageCache().get("type") + this.getPageCache().get("topic")));
                            } else {
                                this.getModel().setValue("ozwe_radiogroup", null);
                            }

                            this.getModel().setValue("ozwe_information", "第" + topic + "/" + rows.size() + "题");
                        } else {
                            this.getView().showMessage("这已经是该题型的最后一题了!");
                            this.getPageCache().put("topic",
                                    Integer.parseInt(this.getPageCache().get("topic")) - 1 + "");
                        }
                        break;
                    }
                    case "multiChoose": {
                        StringBuilder sb = new StringBuilder();
                        sb.append((this.getModel().getValue("ozwe_checkbox1").toString().equalsIgnoreCase("true") ? "1"
                                : "0")
                                + (this.getModel().getValue("ozwe_checkbox2").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox3").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox4").toString().equalsIgnoreCase("true") ? "1"
                                        : "0"));
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                sb.toString());
                        this.getPageCache().put("topic", Integer.parseInt(this.getPageCache().get("topic")) + 1 + "");
                        int topic = Integer.parseInt(this.getPageCache().get("topic"));
                        DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_mutichoose");
                        if (rows.size() > topic - 1) {
                            DynamicObject rowData = rows.get(topic - 1);
                            this.getModel().setValue("ozwe_question", rowData.getString("ozwe_muti_question"));
                            for (int i = 1; i <= 4; i++) {
                                Map<String, Object> arg1 = new HashMap<>();
                                arg1.put("zh_CN", rowData.getString("ozwe_muti_answer" + i));
                                Map<String, Object> map = new HashMap<>();
                                map.put("caption", arg1);
                                this.getView().updateControlMetadata("ozwe_checkbox" + i, map);
                            }
                            if (this.getPageCache()
                                    .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                                String cache = this.getPageCache()
                                        .get(this.getPageCache().get("type") + this.getPageCache().get("topic"));
                                this.getModel().setValue("ozwe_checkbox1", Integer.parseInt(cache.charAt(0) + ""));
                                this.getModel().setValue("ozwe_checkbox2", Integer.parseInt(cache.charAt(1) + ""));
                                this.getModel().setValue("ozwe_checkbox3", Integer.parseInt(cache.charAt(2) + ""));
                                this.getModel().setValue("ozwe_checkbox4", Integer.parseInt(cache.charAt(3) + ""));
                            } else {
                                this.getModel().setValue("ozwe_checkbox1", 0);
                                this.getModel().setValue("ozwe_checkbox2", 0);
                                this.getModel().setValue("ozwe_checkbox3", 0);
                                this.getModel().setValue("ozwe_checkbox4", 0);
                                this.getPageCache().put(
                                        this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                        "0000");
                            }

                            this.getModel().setValue("ozwe_information", "第" + topic + "/" + rows.size() + "题");
                        } else {
                            this.getView().showMessage("这已经是该题型的最后一题了!");
                            this.getPageCache().put("topic",
                                    Integer.parseInt(this.getPageCache().get("topic")) - 1 + "");
                        }
                        break;
                    }
                    case "question": {
                        RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                edit.getText());
                        this.getPageCache().put("topic", Integer.parseInt(this.getPageCache().get("topic")) + 1 + "");
                        int topic = Integer.parseInt(this.getPageCache().get("topic"));
                        DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_question");
                        if (rows.size() > topic - 1) {
                            DynamicObject rowData = rows.get(topic - 1);
                            this.getModel().setValue("ozwe_question", rowData.getString("ozwe_que_question"));
                            if (this.getPageCache()
                                    .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                                String cache = this.getPageCache()
                                        .get(this.getPageCache().get("type") + this.getPageCache().get("topic"));
                                edit.setText(cache);
                                ;
                            } else {
                                edit.setText("");
                                this.getPageCache().put(
                                        this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                        "");
                            }

                            this.getModel().setValue("ozwe_information", "第" + topic + "/" + rows.size() + "题");
                        } else {
                            this.getView().showMessage("这已经是该题型的最后一题了!");
                            this.getPageCache().put("topic",
                                    Integer.parseInt(this.getPageCache().get("topic")) - 1 + "");
                        }
                        break;
                    }
                }
            } else if (itemKey.equalsIgnoreCase("ozwe_back")) {
                String type = this.getPageCache().get("type");
                switch (type) {
                    case "singleChoose": {
                        String singleAnswer;
                        try {
                            singleAnswer = this.getModel().getValue("ozwe_radiogroup").toString();
                        } catch (NullPointerException ee) {
                            singleAnswer = null;
                        }
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                singleAnswer);
                        this.getPageCache().put("topic", Integer.parseInt(this.getPageCache().get("topic")) - 1 + "");
                        int topic = Integer.parseInt(this.getPageCache().get("topic"));
                        DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_choose");
                        if (topic - 1 >= 0) {
                            DynamicObject rowData = rows.get(topic - 1);
                            this.getModel().setValue("ozwe_question", rowData.getString("ozwe_cho_question"));
                            for (int i = 1; i <= 4; i++) {
                                Map<String, Object> arg1 = new HashMap<>();
                                arg1.put("zh_CN", rowData.getString("ozwe_cho_answer" + i));
                                Map<String, Object> map = new HashMap<>();
                                map.put("caption", arg1);
                                this.getView().updateControlMetadata("ozwe_choose" + i, map);
                            }
                            if (this.getPageCache()
                                    .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                                this.getModel().setValue("ozwe_radiogroup", this.getPageCache()
                                        .get(this.getPageCache().get("type") + this.getPageCache().get("topic")));
                            } else {
                                this.getModel().setValue("ozwe_radiogroup", null);
                            }
                            this.getModel().setValue("ozwe_information", "第" + topic + "/" + rows.size() + "题");
                        } else {
                            this.getView().showMessage("这已经是该题型的第一题了!");
                            this.getPageCache().put("topic",
                                    Integer.parseInt(this.getPageCache().get("topic")) + 1 + "");
                        }
                        break;
                    }
                    case "multiChoose": {
                        StringBuilder sb = new StringBuilder();
                        sb.append((this.getModel().getValue("ozwe_checkbox1").toString().equalsIgnoreCase("true") ? "1"
                                : "0")
                                + (this.getModel().getValue("ozwe_checkbox2").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox3").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox4").toString().equalsIgnoreCase("true") ? "1"
                                        : "0"));
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                sb.toString());
                        this.getPageCache().put("topic", Integer.parseInt(this.getPageCache().get("topic")) - 1 + "");
                        int topic = Integer.parseInt(this.getPageCache().get("topic"));
                        DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_mutichoose");
                        if (topic - 1 >= 0) {
                            DynamicObject rowData = rows.get(topic - 1);
                            this.getModel().setValue("ozwe_question", rowData.getString("ozwe_muti_question"));
                            for (int i = 1; i <= 4; i++) {
                                Map<String, Object> arg1 = new HashMap<>();
                                arg1.put("zh_CN", rowData.getString("ozwe_muti_answer" + i));
                                Map<String, Object> map = new HashMap<>();
                                map.put("caption", arg1);
                                this.getView().updateControlMetadata("ozwe_checkbox" + i, map);
                            }
                            if (this.getPageCache()
                                    .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                                String cache = this.getPageCache()
                                        .get(this.getPageCache().get("type") + this.getPageCache().get("topic"));
                                this.getModel().setValue("ozwe_checkbox1", Integer.parseInt(cache.charAt(0) + ""));
                                this.getModel().setValue("ozwe_checkbox2", Integer.parseInt(cache.charAt(1) + ""));
                                this.getModel().setValue("ozwe_checkbox3", Integer.parseInt(cache.charAt(2) + ""));
                                this.getModel().setValue("ozwe_checkbox4", Integer.parseInt(cache.charAt(3) + ""));
                            } else {
                                this.getModel().setValue("ozwe_checkbox1", 0);
                                this.getModel().setValue("ozwe_checkbox2", 0);
                                this.getModel().setValue("ozwe_checkbox3", 0);
                                this.getModel().setValue("ozwe_checkbox4", 0);
                                this.getPageCache().put(
                                        this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                        "0000");
                            }
                            this.getModel().setValue("ozwe_information", "第" + topic + "/" + rows.size() + "题");
                        } else {
                            this.getView().showMessage("这已经是该题型的第一题了!");
                            this.getPageCache().put("topic",
                                    Integer.parseInt(this.getPageCache().get("topic")) + 1 + "");
                        }
                        break;
                    }
                    case "question": {
                        RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                edit.getText());
                        this.getPageCache().put("topic", Integer.parseInt(this.getPageCache().get("topic")) - 1 + "");
                        int topic = Integer.parseInt(this.getPageCache().get("topic"));
                        DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_question");
                        if (topic - 1 >= 0) {
                            DynamicObject rowData = rows.get(topic - 1);
                            this.getModel().setValue("ozwe_question", rowData.getString("ozwe_que_question"));
                            if (this.getPageCache()
                                    .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                                String cache = this.getPageCache()
                                        .get(this.getPageCache().get("type") + this.getPageCache().get("topic"));
                                edit.setText(cache);
                            } else {
                                edit.setText("");
                                this.getPageCache().put(
                                        this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                        "");
                            }

                            this.getModel().setValue("ozwe_information", "第" + topic + "/" + rows.size() + "题");
                        } else {
                            this.getView().showMessage("这已经是该题型的最后一题了!");
                            this.getPageCache().put("topic",
                                    Integer.parseInt(this.getPageCache().get("topic")) + 1 + "");
                        }
                        break;
                    }
                }
            } else if (itemKey.equalsIgnoreCase("ozwe_button_single")) {

                // 保存缓存
                String type = this.getPageCache().get("type");
                switch (type) {
                    case "singleChoose": {
                        String singleAnswer;
                        try {
                            singleAnswer = this.getModel().getValue("ozwe_radiogroup").toString();
                        } catch (NullPointerException ee) {
                            singleAnswer = null;
                        }
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                singleAnswer);
                        break;
                    }
                    case "question": {
                        RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                edit.getText());
                        break;
                    }
                    case "multiChoose": {
                        StringBuilder sb = new StringBuilder();
                        sb.append((this.getModel().getValue("ozwe_checkbox1").toString().equalsIgnoreCase("true") ? "1"
                                : "0")
                                + (this.getModel().getValue("ozwe_checkbox2").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox3").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox4").toString().equalsIgnoreCase("true") ? "1"
                                        : "0"));
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                sb.toString());
                        break;
                    }
                }

                this.getView().setVisible(false, "ozwe_multichooseflex");
                this.getView().setVisible(false, "ozwe_questionflex");
                this.getView().setVisible(true, "ozwe_singlechooseflex");
                // 获取题库选择题列表

                // 获取目前题目缓存
                this.getPageCache().put("type", "singleChoose");
                this.getPageCache().put("topic", "1");
                DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_choose");
                if (rows.size() > 0) {
                    DynamicObject rowData = rows.get(0);
                    this.getModel().setValue("ozwe_question", rowData.getString("ozwe_cho_question"));
                    for (int i = 1; i <= 4; i++) {
                        Map<String, Object> arg1 = new HashMap<>();
                        arg1.put("zh_CN", rowData.getString("ozwe_cho_answer" + i));
                        Map<String, Object> map = new HashMap<>();
                        map.put("caption", arg1);
                        this.getView().updateControlMetadata("ozwe_choose" + i, map);
                    }
                    if (this.getPageCache()
                            .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                        this.getModel().setValue("ozwe_radiogroup", this.getPageCache()
                                .get(this.getPageCache().get("type") + this.getPageCache().get("topic")));
                    } else {
                        this.getModel().setValue("ozwe_radiogroup", null);
                    }

                    this.getModel().setValue("ozwe_information", "第1/" + rows.size() + "题");
                }
            } else if (itemKey.equalsIgnoreCase("ozwe_button_multi")) {

                // 保存缓存
                String type = this.getPageCache().get("type");
                switch (type) {
                    case "question": {
                        RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                edit.getText());
                        break;
                    }
                    case "multiChoose": {
                        StringBuilder sb = new StringBuilder();
                        sb.append((this.getModel().getValue("ozwe_checkbox1").toString().equalsIgnoreCase("true") ? "1"
                                : "0")
                                + (this.getModel().getValue("ozwe_checkbox2").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox3").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox4").toString().equalsIgnoreCase("true") ? "1"
                                        : "0"));
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                sb.toString());
                        break;
                    }
                    case "singleChoose": {
                        String singleAnswer;
                        try {
                            singleAnswer = this.getModel().getValue("ozwe_radiogroup").toString();
                        } catch (NullPointerException ee) {
                            singleAnswer = null;
                        }
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                singleAnswer);
                        break;
                    }
                }

                this.getView().setVisible(false, "ozwe_singlechooseflex");
                this.getView().setVisible(false, "ozwe_questionflex");
                this.getView().setVisible(true, "ozwe_multichooseflex");
                // 获取题库选择题列表

                // 获取目前题目缓存
                this.getPageCache().put("type", "multiChoose");
                this.getPageCache().put("topic", "1");
                DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_mutichoose");
                if (rows.size() > 0) {
                    DynamicObject rowData = rows.get(0);
                    this.getModel().setValue("ozwe_question", rowData.getString("ozwe_muti_question"));
                    for (int i = 1; i <= 4; i++) {
                        Map<String, Object> arg1 = new HashMap<>();
                        arg1.put("zh_CN", rowData.getString("ozwe_muti_answer" + i));
                        Map<String, Object> map = new HashMap<>();
                        map.put("caption", arg1);
                        this.getView().updateControlMetadata("ozwe_checkbox" + i, map);
                    }
                    if (this.getPageCache()
                            .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                        String cache = this.getPageCache()
                                .get(this.getPageCache().get("type") + this.getPageCache().get("topic"));
                        this.getModel().setValue("ozwe_checkbox1", Integer.parseInt(cache.charAt(0) + ""));
                        this.getModel().setValue("ozwe_checkbox2", Integer.parseInt(cache.charAt(1) + ""));
                        this.getModel().setValue("ozwe_checkbox3", Integer.parseInt(cache.charAt(2) + ""));
                        this.getModel().setValue("ozwe_checkbox4", Integer.parseInt(cache.charAt(3) + ""));
                    } else {
                        this.getModel().setValue("ozwe_checkbox1", 0);
                        this.getModel().setValue("ozwe_checkbox2", 0);
                        this.getModel().setValue("ozwe_checkbox3", 0);
                        this.getModel().setValue("ozwe_checkbox4", 0);
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                "0000");
                    }
                    this.getModel().setValue("ozwe_information", "第1/" + rows.size() + "题");
                }
            } else if (itemKey.equalsIgnoreCase("ozwe_button_question")) {

                // 保存缓存
                String type = this.getPageCache().get("type");
                switch (type) {
                    case "multiChoose": {
                        StringBuilder sb = new StringBuilder();
                        sb.append((this.getModel().getValue("ozwe_checkbox1").toString().equalsIgnoreCase("true") ? "1"
                                : "0")
                                + (this.getModel().getValue("ozwe_checkbox2").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox3").toString().equalsIgnoreCase("true") ? "1"
                                        : "0")
                                + (this.getModel().getValue("ozwe_checkbox4").toString().equalsIgnoreCase("true") ? "1"
                                        : "0"));
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                sb.toString());
                        break;
                    }
                    case "singleChoose": {
                        String singleAnswer;
                        try {
                            singleAnswer = this.getModel().getValue("ozwe_radiogroup").toString();
                        } catch (NullPointerException ee) {
                            singleAnswer = null;
                        }
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                singleAnswer);
                        break;
                    }
                    case "question": {
                        RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                edit.getText());
                        break;
                    }
                }

                this.getView().setVisible(false, "ozwe_singlechooseflex");
                this.getView().setVisible(true, "ozwe_questionflex");
                this.getView().setVisible(false, "ozwe_multichooseflex");
                this.getPageCache().put("type", "question");
                this.getPageCache().put("topic", "1");

                // 获取目前题目缓存
                DynamicObjectCollection rows = examObject.getDynamicObjectCollection("ozwe_entry_question");
                if (rows.size() > 0) {
                    DynamicObject rowData = rows.get(0);
                    this.getModel().setValue("ozwe_question", rowData.getString("ozwe_que_question"));
                    RichTextEditor edit = this.getView().getControl("ozwe_questionflex");
                    if (this.getPageCache()
                            .get(this.getPageCache().get("type") + this.getPageCache().get("topic")) != null) {
                        String cache = this.getPageCache()
                                .get(this.getPageCache().get("type") + this.getPageCache().get("topic"));
                        edit.setText(cache);
                    } else {
                        edit.setText("");
                        this.getPageCache().put(this.getPageCache().get("type") + this.getPageCache().get("topic"),
                                "");
                    }
                    this.getModel().setValue("ozwe_information", "第1/" + rows.size() + "题");
                }
            }
        } catch (Exception ee) {

        }
    }

}
