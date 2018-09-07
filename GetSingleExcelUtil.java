package com.walmart.utils;

import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 工具类
 * @author xxxs
 *
 */
public class GetSingleExcelUtil {
    public static final Logger log = LoggerFactory.getLogger(GetSingleExcelUtil.class);

    public static void getSingleExcel(Class cls, String name, List list, HttpServletResponse response){
        try {
            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("sheet");
            ExportUtil exportUtil = new ExportUtil(workBook, sheet);
            XSSFCellStyle headStyle = exportUtil.getHeadStyle();
            XSSFCellStyle bodyStyle = exportUtil.getBodyStyle();
            // 构建表头
            XSSFRow headRow = sheet.createRow(0);
            XSSFCell cell = null;
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                cell = headRow.createCell(i);
                cell.setCellStyle(headStyle);
                cell.setCellValue(fields[i].getName());
            }
            // 构建表体数据
            if (list != null && list.size() > 0) {
                for (int j = 0; j < list.size(); j++) {
                    XSSFRow bodyRow = sheet.createRow(j + 1);
                    for (int i = 0; i < fields.length; i++) {
                        cell = bodyRow.createCell(i);
                        cell.setCellStyle(bodyStyle);
                        if (ReflectionUtil.getValue(list.get(j), fields[i].getName()) != null) {
                            cell.setCellValue(ReflectionUtil.getValue(list.get(j), fields[i].getName()).toString());
                        }
                    }
                }
            }
            //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
            String file = name + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(file.getBytes("UTF-8"), "ISO-8859-1"));
            ServletOutputStream out = response.getOutputStream();
            workBook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
