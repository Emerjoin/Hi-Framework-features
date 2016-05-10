package com.web.yayee.notification;

import java.util.Map;

/**
 * Created by Mario Junior.
 */
public abstract class RoomsAssigner {

    public abstract String[] getNotificationGroupsFor(String principal, Map data);

}
