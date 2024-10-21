package top.dream.function;

import java.util.List;

import kd.bos.dataentity.entity.ILocaleString;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.message.api.MessageChannels;
import kd.bos.servicehelper.workflow.MessageCenterServiceHelper;
import kd.bos.workflow.engine.msg.info.MessageInfo;

public class SendMessage {
	/**
	 * 向用户发送信息
	 * @param Title
	 * @param Content
	 * @param Tag
	 * @param MobContentUrl
	 * @param ContentUrl
	 * @param receiversId
	 * @return
	 */
	public static Long sendMessage(String Title,String Content,String Tag,String MobContentUrl , String ContentUrl, List<Long> receiversId) {
		MessageInfo message = new MessageInfo();
		ILocaleString title = new LocaleString();
		title.setLocaleValue_zh_CN(Title);
		ILocaleString content = new LocaleString();
		content.setLocaleValue_zh_CN(Content);
		message.setMessageTitle(title);
		message.setMessageContent(content);
		message.setTag(Tag);
		message.setUserIds(receiversId);
		message.setType(MessageInfo.TYPE_MESSAGE); 
		message.setEntityNumber("ozwe_workmanage");
		message.setOperation("submit"); 
		message.setMobContentUrl(MobContentUrl); 
		message.setContentUrl(ContentUrl);
		message.setTplScene("success");
		StringBuilder notifyType = new StringBuilder();
		notifyType.append(MessageChannels.YUNZHIJIA).append(","); 
		notifyType.append(MessageChannels.SMS);  
		message.setNotifyType(notifyType.toString());
		Long msgId = MessageCenterServiceHelper.sendMessage(message);
		return msgId;
	}

}
