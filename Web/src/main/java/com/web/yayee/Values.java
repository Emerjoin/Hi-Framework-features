package com.web.yayee;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class Values {

    private HashMap values = new HashMap();

    public Values(){



    }


    public Values put(String key, Object v){

        this.values.put(key,v);
        return this;

    }

    public static Values map(String key,Object value){

        Values instance = new Values();
        instance.put(key,value);

        return instance;

    }

    public Map get(){

        return this.values;

    }

}
