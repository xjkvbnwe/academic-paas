package top.dream.function;

import java.util.EventObject;

import kd.bos.base.AbstractMobBasePlugIn;

public class BillNoLockFunctionMobile extends AbstractMobBasePlugIn{
	
	@Override
	public void afterCreateNewData(EventObject e) {
		//获取数据库中的信息
		StringBuffer sb = new StringBuffer();
		for (int i = 1 ; i<=10; i++) {
			int ascii = 48+(int)(Math.random()*9);
			char c = (char) ascii;
			sb.append(c);
		}
		//设置编码为信息长度+1
		this.getModel().setValue("billno", sb.toString());
    }
}