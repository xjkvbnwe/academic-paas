package top.dream.function;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kd.bos.base.AbstractBasePlugIn;
import kd.bos.form.control.events.ItemClickEvent;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class HealthAnalyzePlugin extends AbstractBasePlugIn{
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addItemClickListeners("ozwe_analyze");
    }

    @Override
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        String itemKey = e.getItemKey();
        if (itemKey.equalsIgnoreCase("ozwe_analyze")) {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10*1000);
        requestFactory.setReadTimeout(10*1000);
        RestTemplate client = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("name", this.getModel().getValue("ozwe_name").toString());
        params.add("gender", this.getModel().getValue("ozwe_sex").toString());
        params.add("age", this.getModel().getValue("ozwe_age").toString());
        params.add("phone", this.getModel().getValue("ozwe_phone").toString());
        params.add("edu", this.getModel().getValue("ozwe_education").toString());
        params.add("pmh", this.getModel().getValue("ozwe_medical_history").toString());
        params.add("cva_r", "500");
        params.add("cva_l", "200");
        params.add("color_sense", this.getModel().getValue("ozwe_color").toString());
        params.add("other_eye", this.getModel().getValue("ozwe_other").toString());
        params.add("doctor_eye", this.getModel().getValue("ozwe_doctor").toString());
        params.add("hear_r", "6");
        params.add("hear_l", "6");
        params.add("ear", this.getModel().getValue("ozwe_earill").toString());
        params.add("smell", this.getModel().getValue("ozwe_smell").toString());
        params.add("stutter", this.getModel().getValue("ozwe_stutter").toString());
        params.add("face", this.getModel().getValue("ozwe_face").toString());
        params.add("fso", "无");
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(this.getModel().getValue("ozwe_heart_rate").toString());
        params.add("heart_rate", m.replaceAll("").trim());
        params.add("blood_pressure", "8/14");
        params.add("upgrowth", this.getModel().getValue("ozwe_nutrition").toString());
        params.add("angiocarpy", this.getModel().getValue("ozwe_blood_vessel").toString());
        params.add("nerve", this.getModel().getValue("ozwe_nerve").toString());
        params.add("liver", this.getModel().getValue("ozwe_liver").toString());
        params.add("lung", this.getModel().getValue("ozwe_lungs").toString());
        params.add("spleen", this.getModel().getValue("ozwe_spleen").toString());
        params.add("imd", "正常");
        params.add("height", this.getModel().getValue("ozwe_height").toString());
        params.add("weight", this.getModel().getValue("ozwe_weight").toString());
        params.add("skin", this.getModel().getValue("ozwe_textfield2").toString());
        params.add("al", this.getModel().getValue("ozwe_limbs").toString());
        params.add("lymph", this.getModel().getValue("ozwe_lympa").toString());
        params.add("arthrosis", "正常");
        params.add("spine", this.getModel().getValue("ozwe_spine").toString());
        params.add("thyroid", this.getModel().getValue("ozwe_thyroid").toString());
        params.add("sd", this.getModel().getValue("ozwe_liverfunction").toString());
        params.add("alt", "10.0");
        params.add("hbsag", this.getModel().getValue("ozwe_hepatitis").toString());
        params.add("lung_x", this.getModel().getValue("ozwe_chest").toString());
        params.add("other", this.getModel().getValue("ozwe_res").toString());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //  执行HTTP请求
        ResponseEntity<String> response = null;
        try{
            response = client.exchange("http://120.46.217.126:9999/get", HttpMethod.POST, requestEntity, String.class);
            String res = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(res);
            this.getModel().setValue("ozwe_result", jsonObject.getString("message"));
            this.getView().showMessage("分析结果已生成!");
        }
        catch (HttpClientErrorException ee){
            this.getView().showMessage(ee.getMessage());
            ee.printStackTrace();
            return;
        }
        catch (Exception eee) {
            this.getView().showMessage(eee.getMessage());
            return;
        }
        }
    }
}
