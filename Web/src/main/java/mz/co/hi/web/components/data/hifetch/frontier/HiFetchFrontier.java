package mz.co.hi.web.components.data.hifetch.frontier;


import mz.co.hi.web.annotations.Frontier;
import mz.co.hi.web.annotations.SingleCall;
import mz.co.hi.web.components.data.hifetch.FetchParams;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@Frontier(name = "$hiFetch")
@ApplicationScoped
public class HiFetchFrontier {



    @SingleCall(detectionMethod = SingleCall.Detection.CALL_PARAMS)
    public Map getLastFetchParams(String datasource){

        return null;

    }


    @SingleCall(detectionMethod = SingleCall.Detection.CALL_PARAMS)
    public Map getTotalPages(String datasource){

        return null;

    }


    @SingleCall(detectionMethod = SingleCall.Detection.CALL_PARAMS)
    public Map getTotalRows(String datasource){

        return null;

    }

    @SingleCall(detectionMethod = SingleCall.Detection.CALL_PARAMS)
    public Map fetchPage(FetchParams fetchParams){

        Map page = null;
        page.put("rows",true);
        page.put("totalMatch",true);

        return null;

    }


}
