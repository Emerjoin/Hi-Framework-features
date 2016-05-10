package com.web.yayee.mvc;

/**
 * Created by Mario Junior.
 */
public class NoSuchViewException extends MvcException {

    private String controller;
    private String view;

    public NoSuchViewException(String controller,String view){
        super("Could not find the view file <"+view+"> for controller <"+controller+">");
        this.controller = controller;
        this.view = view;

    }

}
