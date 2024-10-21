package top.dream.function;

import java.util.EventObject;

import kd.bos.base.AbstractBasePlugIn;
import kd.bos.servicehelper.user.UserServiceHelper;

public class AutoWriteOrg extends AbstractBasePlugIn {

    @Override
    public void afterCreateNewData(EventObject e) {
            Long currentUserId = UserServiceHelper.getCurrentUserId();
		    long mainOrgid = UserServiceHelper.getUserMainOrgId(currentUserId);
		    this.getModel().setValue("org", mainOrgid);
	}

}