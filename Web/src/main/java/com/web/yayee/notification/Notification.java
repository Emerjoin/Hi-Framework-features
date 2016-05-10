package com.web.yayee.notification;


public class Notification  {


    public Notification(){



    }


    public void informUser(String user){

        UsersInformer.getInstance().informUser(this,user);

    }

    public void informUsers(String users[]){

        UsersInformer.getInstance().informUsers(this,users);

    }


    public void informGroup(String group){

        UsersInformer.getInstance().informGroup(this,group);

    }

    public void informGroups(String groups[]){

        UsersInformer.getInstance().informGroups(this,groups);

    }

    public void informAll(){

        UsersInformer.getInstance().informUsers(this);

    }



}
