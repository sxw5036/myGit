package com.lwxf.industry4.webapp.common.enums.order;

public enum OrderStage {

    CREATE(0,"订单创建"),
    PAYMENT_VERIFY(1,"财务审核"),
    PRODUCE(2,"生产中"),
    PACKAGING(3,"包装中"),
    DELIVERY(4,"已发货");

    private Integer value;
    private String name;


    OrderStage(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 数据范围校验
     *
     * @param value
     * @return
     */
    public static boolean contains(Integer value) {
        return null == value?Boolean.FALSE.booleanValue():Boolean.TRUE.booleanValue();
    }
    public Integer getValue() {
        return this.value;
    }

    public String getName(){
        return this.name;
    }

}
