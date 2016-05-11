package mz.co.hi.web.frontier.model;

import java.util.HashMap;

/**
 * Created by Mario Junior.
 */
public class FrontierClass {

    private HashMap<String,FrontierMethod> methodsMap = new HashMap();
    private String className;
    private String simpleName;
    private Object object;

    public FrontierClass(String clazz,String simpleName, Object object){

        this.className = clazz;
        this.simpleName = simpleName;
        this.object = object;

    }

    public String getSimpleName(){

        return simpleName;

    }

    public String getClassName() {
        return className;
    }

    public void addMethod(FrontierMethod method){

        this.methodsMap.put(method.getName(),method);

    }

    public boolean hasMethod(String name){

        return methodsMap.containsKey(name);

    }

    public FrontierMethod getMethod(String name){

        return methodsMap.get(name);

    }

    public Object getObject() {
        return object;
    }

    public FrontierMethod[] getMethods(){

        FrontierMethod methods[] = new FrontierMethod[methodsMap.size()];
        methodsMap.values().toArray(methods);
        return methods;

    }
}
