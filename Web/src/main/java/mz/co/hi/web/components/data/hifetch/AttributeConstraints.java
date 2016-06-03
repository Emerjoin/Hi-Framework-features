package mz.co.hi.web.components.data.hifetch;

/**
 * Created by Mario Junior.
 */
public class AttributeConstraints {

    private boolean restrictedOrder=false;
    private String  orderDirection=null;
    private String name;

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }

    public boolean isRestrictedOrder() {
        return restrictedOrder;
    }

    public void setRestrictedOrder(boolean restrictedOrder) {
        this.restrictedOrder = restrictedOrder;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }
}
