package com.lwxf.industry4.webapp.domain.dto.customorder;

import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderLog;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/20 13:39
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class CustomOrderLogDto extends CustomOrderLog {

    private  Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

