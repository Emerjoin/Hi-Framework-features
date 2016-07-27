package mz.co.hi.web.component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mário Júnior
 */
public class DatasourceExample {


    public Map datasource1(int pageNumber, int itemsPerPage, Map filter, Map ordering){

        Map result = new HashMap();
        result.put("data","array");
        result.put("totalRowsMatch",1000);
        result.put("pageNumber",pageNumber);
        result.put("totalPagesMatch",50);
        result.put("itemsPerPage",itemsPerPage);

        return result;

    }


}
