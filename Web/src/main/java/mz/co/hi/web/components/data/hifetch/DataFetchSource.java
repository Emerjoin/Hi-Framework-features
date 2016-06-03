package mz.co.hi.web.components.data.hifetch;

import java.io.Serializable;


public abstract class DataFetchSource<QueryType,RowType> implements Serializable {

    public abstract void setMaxItems(int total);
    public abstract int getMaxItems();

    public long countRows(FetchParams fetchParams){

        return runLongQuery(generateCountRowsQuery(fetchParams));

    }

    public int countPages(FetchParams fetchParams){

        return runIntQuery(generateCountPagesQuery(fetchParams));

    }


    public RowType[] fetchRows(FetchParams fetchParams){

        return runRowsQuery(generateFindRowsQuery(fetchParams));

    }

    public abstract boolean supportResuming();
    public abstract int getLastFetchedPage();
    public abstract FetchParams getLastFetchParams();

    public abstract QueryType generateFindRowsQuery(FetchParams params);
    public abstract QueryType generateCountRowsQuery(FetchParams params);
    public abstract QueryType generateCountPagesQuery(FetchParams params);

    public abstract RowType[] runRowsQuery(QueryType queryType);
    public abstract int runIntQuery(QueryType queryType);
    public abstract long runLongQuery(QueryType queryType);

}
