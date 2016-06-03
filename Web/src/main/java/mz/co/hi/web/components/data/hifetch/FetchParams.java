package mz.co.hi.web.components.data.hifetch;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class FetchParams implements Serializable {

    private String text;
    private int maxItems;
    private AttributeConstraints attrConstraints[];
    private Map filters;
    private int page;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public AttributeConstraints[] getAttrConstraints() {
        return attrConstraints;
    }

    public void setAttrConstraints(AttributeConstraints[] attrConstraints) {
        this.attrConstraints = attrConstraints;
    }

    public Map getFilters() {
        return filters;
    }

    public void setFilters(Map filters) {
        this.filters = filters;
    }
}
