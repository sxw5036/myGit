package com.lwxf.industry4.webapp.common.enums.customorder;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/4/16/016 16:07
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public enum ProduceOrderState {
	NOT_YET_BEGUN(0,"未开始"),
	IN_PRODUCTION(1,"生产中"),
	COMPLETE(2,"已完成");


	private int value;
	private String name;

	ProduceOrderState(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
	public static Boolean contains(int value){
		return null==getByValue(value)?Boolean.FALSE.booleanValue():Boolean.TRUE.booleanValue();
	}
	public static ProduceOrderState getByValue(int value){
		ProduceOrderState allVaues[] = ProduceOrderState.values();
		ProduceOrderState status;
		for (int i = 0,len = allVaues.length; i < len; i++) {
			status = allVaues[i];
			if (status.getValue()==value){
				return status;
			}
		}
		return null;
	}

}
