package com.web.yayee.frontier;

import com.web.yayee.RequestContext;
import com.web.yayee.frontier.model.FrontierClass;
import com.web.yayee.frontier.model.FrontierMethod;
import com.web.yayee.frontier.model.MethodParam;

import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class FrontierInvoker {


    private RequestContext requestContext;
    private FrontierClass frontier;
    private FrontierMethod method;
    private Map params;
    private Object returnedObject;

    public FrontierInvoker(RequestContext requestContext, FrontierClass frontierClass, FrontierMethod method, Map params){

        this.requestContext = requestContext;
        this.frontier = frontierClass;
        this.method = method;
        this.params = params;

    }

    public boolean invoke() throws FrontierInvocationFailedException {

        MethodParam methodParams[] = method.getParams();
        Object[] invocationParams = new Object[params.size()];

        int i = 0;
        for(MethodParam methodParam: methodParams){


            invocationParams[i] = params.get(methodParam.getName());
            i++;

        }

        Object result = null;

        try {

            returnedObject = method.getMethod().invoke(frontier.getObject(), invocationParams);

        }catch (Exception ex){

            throw new FrontierInvocationFailedException(frontier.getClassName(),method.getName(),ex);

        }

        return true;
    }

    public Object getReturnedObject() {

        return returnedObject;

    }
}
