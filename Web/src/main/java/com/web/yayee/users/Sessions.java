package com.web.yayee.users;

import com.web.yayee.config.AppConfigurations;
import com.web.yayee.notification.RoomsAssigner;
import com.web.yayee.notification.UserGroups;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class Sessions {

    private static RoomsAssigner roomsAssigner = null;

    private static final String U_DETAILS = "user_details";

    public static void setRoomsAssigner(RoomsAssigner roomsAssigner){

        Sessions.roomsAssigner = roomsAssigner;

    }



    public static RoomsAssigner getRoomsAssigner() {

        return roomsAssigner;

    }



    public static Map getUserDetails(String remoteUser){


        if(AppConfigurations.get().getUserDetailsProvider()!=null){


            return AppConfigurations.get().getUserDetailsProvider().getDetails(remoteUser);


        }

        return new HashMap<>();

    }

    public static void handleUserDetails(String remoteUser){


        if(remoteUser!=null){


                if(AppConfigurations.get().getUserDetailsProvider() !=null){

                    Map details = AppConfigurations.get().getUserDetailsProvider().getDetails(remoteUser);

                    if(details==null){

                        details = new HashMap();

                    }

                    UserGroups.clearMembership(remoteUser);

                    if(roomsAssigner !=null){

                        String[] groups = roomsAssigner.getNotificationGroupsFor(remoteUser,details);

                        if(groups!=null){

                            if(groups.length>0)
                                UserGroups.setMembership(remoteUser,groups);


                        }

                    }

                }

        }

    }
}
