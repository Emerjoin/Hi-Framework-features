package mz.co.hi.web.component;


import mz.co.hi.web.meta.WebComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebComponent
public class HiList {

    public static Map listEncode(Collection data, int matches, int page, int pages) throws IllegalArgumentException{

        if(data==null)
            throw new IllegalArgumentException();

        Object[] array = new Object[data.size()];
        data.toArray(array);

        return listEncode(array,matches,page,pages);

    }

    public static Map listEncode(Object[] data,int matches, int page, int pages) throws IllegalArgumentException{

        if(data==null||matches<0||pages<0||page<0)
            throw new IllegalArgumentException();

        Map result = new HashMap();
        result.put("data",data);
        result.put("totalRowsMatch",matches);
        result.put("pageNumber",page);
        result.put("totalPagesMatch",pages);
        return result;

    }

}
