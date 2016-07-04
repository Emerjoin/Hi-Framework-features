package mz.co.hi.web.frontier;

import mz.co.hi.web.RequestContext;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.FrontierMethod;
import mz.co.hi.web.frontier.model.MethodParam;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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


    public boolean invoke() throws Exception {

        MethodParam methodParams[] = method.getParams();
        Object[] invocationParams = new Object[params.size()];



        int i = 0;
        for(MethodParam methodParam: methodParams){


            invocationParams[i] = params.get(methodParam.getName());
            i++;

        }


        Object refreshedObj = frontier.getObject();

        try {

            returnedObject = method.getMethod().invoke(refreshedObj, invocationParams);


        }catch (Exception ex){

            if(ex instanceof InvocationTargetException){

                Throwable throwable = ex.getCause();

                if(throwable instanceof ConstraintViolationException){


                    throw (ConstraintViolationException) throwable;


                }

            }

        }

        return true;
    }

    public Object getReturnedObject() {

        return returnedObject;

    }
}
