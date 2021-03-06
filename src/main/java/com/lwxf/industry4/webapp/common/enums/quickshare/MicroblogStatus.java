package com.lwxf.industry4.webapp.common.enums.quickshare;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/7/20 14:49
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public enum MicroblogStatus {
	DISABLED((byte)0,"禁用"),
	ENABLED((byte)1,"启用");


	private byte value;
	private String name;

	MicroblogStatus(Byte value,String name){
		this.value = value;
		this.name = name;
	}

	/**
	 * 数据范围校验
	 *
	 * @param value
	 * @return
	 */
	public static boolean contains(Byte value) {
		return null == getByValue(value)?Boolean.FALSE.booleanValue():Boolean.TRUE.booleanValue();
	}

	public Byte getValue() {
		return this.value;
	}

	public String getName(){
		return this.name;
	}

	public static MicroblogStatus getByValue(Byte value){
		MicroblogStatus allVaues[] = MicroblogStatus.values();
		MicroblogStatus status;
		for (int i=0,len = allVaues.length;i<len;i++){
			status = allVaues[i];
			if(status.getValue()==value.byteValue()){
				return status;
			}
		}
		return null;
	}





}
