package com.lwxf.industry4.webapp.domain.entity.base;

import java.io.Serializable;

/**
 * 功能：
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-05-24 9:41:18
 * @version：2018 1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public abstract class AbstractNoIdEntity implements Serializable {
	private static final long serialVersionUID = 5348988346723780682L;
	@Override
	public int hashCode() {
		int h = this.getClass().getName().hashCode();
		return h ^ (h >>> 16);
	}
}