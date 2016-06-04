package mz.co.hi.web.frontier;

import mz.co.hi.web.DispatcherServlet;
import mz.co.hi.web.Helper;
import mz.co.hi.web.annotations.MultipleCalls;
import mz.co.hi.web.annotations.SingleCall;
import mz.co.hi.web.config.AppConfigurations;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.FrontierMethod;
import mz.co.hi.web.frontier.model.MethodParam;

import java.lang.annotation.Annotation;
import java.security.SecureRandom;

/**
 * Created by Mario Junior.
 */
public class Scripter {

    public Scripter(){



    }


    /*
    private String generateHeaders(String frontierName, String method){


        return "{\"Invoked-Class\":\""+frontierName+"\",\"Invoked-Method\":\""+method+"\"}";

    }*/

    private String generateUrl(String beanName, String method){

        return "f.m.call/"+beanName+"/"+method;

    }

    private Object[] generateSignatureAndObject(FrontierMethod method){

        StringBuilder signature = new StringBuilder();
        signature.append("(");


        StringBuilder methodBody = new StringBuilder();
        java.security.SecureRandom secureRandom = new SecureRandom();

        String token = String.valueOf(secureRandom.nextLong());


        methodBody.append("var params = {};");

        MethodParam[] parameters = method.getParams();

        int index = 0;
        for(MethodParam parameter : parameters){

            String paramName = parameter.getName();
            signature.append(paramName);
            methodBody.append("params."+paramName+"="+paramName+";");

            //Not last item
            if(index!=parameters.length-1)
                signature.append(",");

            index++;

        }

        signature.append(")");


        Annotation mInvocation = method.getMethod().getAnnotation(MultipleCalls.class);
        if(mInvocation!=null){


            methodBody.append("var _$fmut = \""+token+"\";");
            methodBody.append("var _$mi=true;");
            methodBody.append("var _$si=false;");

        }else{

            Annotation sInvocation = method.getMethod().getAnnotation(SingleCall.class);
            methodBody.append("var _$si=true;");
            methodBody.append("var _$mi=false;");


            if(sInvocation!=null){

                SingleCall singleCall = (SingleCall) sInvocation;

                if(singleCall.detectionMethod()== SingleCall.Detection.METHOD_CALL){


                    methodBody.append("var _$fmut = \""+token+"\";");

                    methodBody.append("var _$si_method = true;");
                    methodBody.append("var _$si_params = false;");

                }else{

                    methodBody.append("var _$fmut = \""+token+"\"+JSON.stringify(params).trim();");

                    methodBody.append("var _$si_params = true;");
                    methodBody.append("var _$si_method = false;");

                }

                if(singleCall.abortionPolicy()== SingleCall.AbortPolicy.ABORT_NEW_INVOCATION){

                    methodBody.append("var _$abpnew = true;");
                    methodBody.append("var _$abpon = false;");

                }else{

                    methodBody.append("var _$abpnew = false;");
                    methodBody.append("var _$abpon = true;");

                }


            }else{

                methodBody.append("var _$fmut = \""+token+"\";");

                methodBody.append("var _$abpon = true;");
                methodBody.append("var _$abpnew = false;");

                methodBody.append("var _$si_method = true;");
                methodBody.append("var _$si_params = false;");

            }

        }




        return new Object[]{signature.toString(),methodBody.toString()};

    }

    private String generateMethodMirror(FrontierMethod method,String beanName){

        Object[] signatureAndData = generateSignatureAndObject(method);
        String signature = signatureAndData[0].toString();
        String data = signatureAndData[1].toString();

        StringBuilder mirror = new StringBuilder();
        mirror.append("function "+signature+"{");
        mirror.append("console.log(\" My caller is\");");
        mirror.append("console.log("+beanName+"."+method.getName()+".caller.prototype);");
        mirror.append(data);
        mirror.append("var $functionUrl=App.base_url+\""+generateUrl(beanName,method.getName())+"\";");
        //mirror.append("var $headers="+generateHeaders(beanName,method.getName())+";");
        mirror.append(DispatcherServlet.genericFrontierScript);
        mirror.append("};");

        return mirror.toString();

    }

    public String generate(FrontierClass frontierClass){

        try {


            FrontierMethod[] methods = frontierClass.getMethods();

            StringBuilder script = new StringBuilder();
            script.append("var "+frontierClass.getSimpleName()+"={};");

            for(FrontierMethod method : methods){

                String methodName = method.getName();
                String generatedMirrorScript = generateMethodMirror(method,frontierClass.getSimpleName());
                script.append(frontierClass.getSimpleName()+"."+methodName+"="+generatedMirrorScript);

            }

            return script.toString();

        }catch (Exception ex){

            return null;

        }

    }

}
