package com.web.yayee.mvc;

import com.web.yayee.YeeException;

/**
 * Created by Mario Junior.
 */
public class MalMarkedTemplateException extends TemplateException {

    public MalMarkedTemplateException(String templateFile){

        super("The template file is not correctly marked :");

    }

    public MalMarkedTemplateException(String templateFile,String problem){

        super("The template file is not correctly marked : "+problem);

    }
}
