package com.mengxiao.sqlconvert.demos.service.impl;

import com.mengxiao.sqlconvert.demos.service.SqlConvertService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

@Service
public class SqlConvertServiceImpl implements SqlConvertService {
    private static final Logger logger = LoggerFactory.getLogger(SqlConvertServiceImpl.class);
    @Override
    public String convert(MultipartFile file) {
        logger.info("检查文件是否为空....");
        String res = "";
        try {
            String name = file.getOriginalFilename();
            if (file.isEmpty() || name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))){
                logger.error("该文件为空或者不是excel类型");
                return "该文件为空或者不是excel类型";
            }
            logger.info("开始转换....");
            res = processExcelFile(file.getInputStream());
        } catch (IOException e) {
            logger.error("读取文件异常:"+e.getMessage());
            res = "读取文件异常:"+e.getMessage();
        }
        return res;
    }
    public String processExcelFile (InputStream inputStream){
        String res = "";
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            String tableName = sheet.getRow(0).getCell(0).getStringCellValue();
            int colNum = sheet.getRow(1).getLastCellNum();
            StringBuilder sqlTemp = new StringBuilder("INSERT INTO "+tableName +"(");
            StringBuilder sql = new StringBuilder();
            StringBuilder insert = new StringBuilder();
            if ("".equals(tableName) || tableName == null)
                return "没有设置表名";
            for (Cell cell : sheet.getRow(1)){
                sqlTemp.append(cell.getStringCellValue()).append(" , ");
            }
            sqlTemp.setLength(sqlTemp.length()-3);
            sqlTemp.append(") VALUES (");
            insert.append(sqlTemp);
            for(int i =2; i<sheet.getLastRowNum();i++){
                Row row = sheet.getRow(i);
                for (int j = 0; j < colNum; j++){
                    Cell cell = row.getCell(j);
                    if (cell != null){
                        if (cell.getCellType() == CellType.NUMERIC){
                            Double num = cell.getNumericCellValue();
                            BigDecimal bd = new BigDecimal(num);
                            if (bd.scale()==0){
                                DecimalFormat df = new DecimalFormat("#");
                                cell.setCellValue(df.format(num));
                            }else{
                                cell.setCellValue(String.valueOf(num));
                            }
                        }
                        sqlTemp.append("'").append(cell.getStringCellValue()).append("' , ");
                    }else
                        sqlTemp.append("NULL").append(", ");
                }
                sqlTemp.setLength(sqlTemp.length()-3);
                sql.append(sqlTemp).append(")").append(";\n");
                sqlTemp.setLength(0);
                sqlTemp.append(insert);
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\sql.txt"));
            writer.write(sql.toString());
            writer.close();
            res = "转换成功";
            logger.info("转换完成....");
        } catch (IOException e) {
            logger.error("转换异常："+e.getMessage());
            res = "转换异常："+e.getMessage();
        }
        return res;
    }
}
