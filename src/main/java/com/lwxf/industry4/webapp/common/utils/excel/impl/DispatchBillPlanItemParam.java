package com.lwxf.industry4.webapp.common.utils.excel.impl;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.*;

import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockItemType;
import com.lwxf.industry4.webapp.common.utils.excel.ExcelParam;

/**
 * 功能：配送计划包裹导出excel的参数值
 *
 * @author：Administrator
 * @create：2019/5/22/022 10:18
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class DispatchBillPlanItemParam implements ExcelParam {

	private String fileName;

	{
		try {
			fileName = new String("配送计划".concat(".xls").getBytes(), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 定义excel标题
	 * @return
	 */
	@Override
	public Map<String, String> getHeaderMap() {
		Map<String,String> headerMap = new LinkedHashMap<String, String>(){
			{
				put("barcode","包装编号/条形码");
				put("orderNo","订单编号");
				put("type","类型");
				put("notes","描述");
				put("location","包裹位置");
				put("planNote","备注");
			}
		};
		return headerMap;
	}

	@Override
	public void createBody(HSSFWorkbook workbook, HSSFSheet sheet, HSSFCellStyle bodyStyle, List<Map<String, Object>> mapList) {
		//遍历集合数据,产生数据行
		HSSFRow row;
		int colIdx;

		for (int m = 0, len = mapList.size(); m < len; m++) {
			Map<String, Object> backlog = mapList.get(m);
			row = sheet.createRow(m + 1);
			Iterator<String> keys = getHeaderMap().keySet().iterator();
			colIdx = 0;
			while (keys.hasNext()) {
				String key = keys.next();
				Object value = backlog.get(key);
				HSSFCell cell = row.createCell(colIdx++);
				cell.setCellStyle(bodyStyle);
				if (null != value) {
					switch (key) {
						case "barcode":
							cell.setCellValue(value.toString());
							break;
						case "orderNo":
							cell.setCellValue(value.toString());
							break;
						case "type":
							cell.setCellValue(FinishedStockItemType.getByValue(Integer.parseInt(value.toString())).getName());
							break;
						case "notes":
							cell.setCellValue(value.toString());
							break;
						case "location":
							cell.setCellValue(value.toString());
							break;
						case "planNote":
							cell.setCellValue(value.toString());
							break;
					}
				}
			}
		}
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}
}
