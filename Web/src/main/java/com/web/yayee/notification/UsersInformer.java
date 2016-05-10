package com.web.yayee.notification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class UsersInformer {

    private static UsersInformer instance = null;

    private UsersInformer(){



    }

    public static UsersInformer getInstance(){

        if(instance==null)
            instance = new UsersInformer();

        return instance;

    }

    private String toJSON(Notification notification){

        if(notification==null)
            return null;

        Map notificationMessage = new HashMap<>();
        notificationMessage.put("envelope",notification);
        notificationMessage.put("type",notification.getClass().getSimpleName());

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(notificationMessage);
        return json;

    }

    public void informGroup(Notification notification, String group){

        if(notification==null)
            return;

        WebSocket.tellGroup(toJSON(notification),group);

    }

    public void informGroups(Notification notification, String[] groups){

        if(notification==null)
            return;

        WebSocket.tellGroups(toJSON(notification),groups);


    }



    public void informUser(Notification notification, String username){

        if(notification==null)
            return;


        WebSocket.tellUser(toJSON(notification),username);


    }


    public void informUsers(Notification notification, String[] usernames){

        if(notification==null)
            return;

        if(usernames==null)
            return;

        if(usernames.length==0)
            return;


        for(String user: usernames){


            WebSocket.tellUser(toJSON(notification),user);

        }

    }


    public void informUsers(Notification notification){

        if(notification==null)
            return;


        WebSocket.tellAllUsers(toJSON(notification));

    }




}
