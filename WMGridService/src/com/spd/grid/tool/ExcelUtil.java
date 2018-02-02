package com.spd.grid.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelUtil {
	
	/** 
     * 读取excel文件 
     * 根据文件名自动识别读取方式 
     * 支持97-2013格式的excel文档 
     *  
     * @param fileName 
     *            上传文件名 
     * @param file 
     *            上传的文件 
     * @return 返回列表内容格式： 
     *             每一行数据都是以对应列的表头为key 内容为value 比如 excel表格为： 
     * =============== 
     *  A | B | C | D 
     * ===|===|===|=== 
     *  1 | 2 | 3 | 4 
     * ---|---|---|---  
     *  a | b | c | d 
     * --------------- 
     * 返回值 map： 
     *   map1:   A:1 B:2 C:3 D:4 
     *   map2:   A:a B:b C:d D:d 
     * @throws java.io.IOException 
     */  
    @SuppressWarnings("rawtypes")  
    public static List<Map> readExcel(String filePath) throws Exception{  
        //准备返回值列表  
        List<Map> valueList=new ArrayList<Map>();  
        String ExtensionName=getExtensionName(filePath);  
        File tmpfile = new File(filePath);  
        if(ExtensionName.equalsIgnoreCase("xls")){  
            valueList=readExcel2003(filePath);  
        }else if(ExtensionName.equalsIgnoreCase("xlsx")) {  
            valueList=readExcel2007_2013(filePath);  
        }  
        //删除缓存文件  
        tmpfile.delete();  
        return valueList;  
                  
    }  
      
    /** 
     * 读取97-2003格式 
     * @param filePath 文件路径 
     * @throws java.io.IOException 
     */  
    @SuppressWarnings("rawtypes")  
    public static List<Map> readExcel2003(String filePath) throws IOException{  
        //返回结果集  
        List<Map> valueList=new ArrayList<Map>();  
        FileInputStream fis=null;  
        try {  
            fis=new FileInputStream(filePath);  
            HSSFWorkbook wookbook = new HSSFWorkbook(fis);  
            HSSFSheet sheet = wookbook.getSheetAt(0);   
            int rows = sheet.getPhysicalNumberOfRows(); 
            Map<Integer,String> keys=new HashMap<Integer, String>();  
            int cells=0;  
            HSSFRow firstRow = sheet.getRow(0);  
            if (firstRow != null) {  
                cells = firstRow.getPhysicalNumberOfCells();  
                for (int j = 0; j < cells; j++) {  
                    try {  
                        HSSFCell cell = firstRow.getCell(j);  
                        String cellValue = getCellValue(cell);  
                        keys.put(j,cellValue);                        
                    } catch (Exception e) {  
                        e.printStackTrace();      
                    }  
                }  
            }  
            for (int i = 1; i < rows; i++) {  
                HSSFRow row = sheet.getRow(i);  
                if (row != null) {  
                    Map<String, Object> val=new HashMap<String, Object>();  
                    boolean isValidRow = false;  
                    for (int j = 0; j < cells; j++) {  
                        try {  
                            HSSFCell cell = row.getCell(j);  
                            String cellValue = getCellValue(cell);  
                            val.put(keys.get(j),cellValue);   
                            if(!isValidRow && cellValue!=null && cellValue.trim().length()>0){  
                                isValidRow = true;  
                            }  
                        } catch (Exception e) {  
                            e.printStackTrace();          
                        }  
                    }  
                    if(isValidRow){  
                        valueList.add(val);  
                    }  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally {  
            fis.close();  
        }  
        return valueList;  
    }  
    /** 
     * 读取2007-2013格式 
     * @param filePath 文件路径 
     * @return 
     * @throws java.io.IOException 
     */  
    @SuppressWarnings("rawtypes")  
    public static List<Map> readExcel2007_2013(String filePath) throws IOException{  
        List<Map> valueList=new ArrayList<Map>();  
        FileInputStream fis =null;  
        try {  
            fis =new FileInputStream(filePath);  
            XSSFWorkbook xwb = new XSSFWorkbook(fis);   
            XSSFSheet sheet = xwb.getSheetAt(0);          
            // 定义 row、cell  
            XSSFRow row;  
            // 循环输出表格中的第一行内容   表头  
            Map<Integer, String> keys=new HashMap<Integer, String>();  
            row = sheet.getRow(0);  
            if(row !=null){  
                //System.out.println("j = row.getFirstCellNum()::"+row.getFirstCellNum());  
                //System.out.println("row.getPhysicalNumberOfCells()::"+row.getPhysicalNumberOfCells());  
                for (int j = row.getFirstCellNum(); j <=row.getPhysicalNumberOfCells(); j++) {  
                    // 通过 row.getCell(j).toString() 获取单元格内容，  
                    if(row.getCell(j)!=null){  
                        if(!row.getCell(j).toString().isEmpty()){  
                            keys.put(j, row.getCell(j).toString());  
                        }  
                    }else{  
                        keys.put(j, "K-R1C"+j+"E");  
                    }  
                }  
            }  
            // 循环输出表格中的从第二行开始内容  
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getPhysicalNumberOfRows(); i++) {  
                row = sheet.getRow(i);  
                if (row != null) {  
                    boolean isValidRow = false;  
                    Map<String, Object> val = new HashMap<String, Object>();  
                    for (int j = row.getFirstCellNum(); j <= row.getPhysicalNumberOfCells(); j++) {  
                        XSSFCell cell = row.getCell(j);  
                        if (cell != null) {  
                            String cellValue = null;  
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){  
                                if(DateUtil.isCellDateFormatted(cell)){  
                                    cellValue = new DataFormatter().formatRawCellContents(cell.getNumericCellValue(), 0, "yyyy-MM-dd HH:mm:ss");  
                                }  
                                else{  
                                    cellValue = String.valueOf(cell.getNumericCellValue());  
                                }  
                            }  
                            else{  
                                cellValue = cell.toString();  
                            }  
                            if(cellValue!=null&&cellValue.trim().length()<=0){  
                                cellValue=null;  
                            }  
                            val.put(keys.get(j), cellValue);  
                            if(!isValidRow && cellValue!= null && cellValue.trim().length()>0){  
                                isValidRow = true;  
                            }  
                        }  
                    }  
  
                    // 第I行所有的列数据读取完毕，放入valuelist  
                    if (isValidRow) {  
                        valueList.add(val);  
                    }  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally {  
            fis.close();  
        }  
  
        return valueList;  
    }  
      
    /** 
     * 文件操作 获取文件扩展名 
     *  
     * @param filename 
     *            文件名称包含扩展名 
     * @return 
     */  
    public static String getExtensionName(String filename) {  
        if ((filename != null) && (filename.length() > 0)) {  
            int dot = filename.lastIndexOf('.');  
            if ((dot > -1) && (dot < (filename.length() - 1))) {  
                return filename.substring(dot + 1);  
            }  
        }  
        return filename;  
    }  
  
  

      
    private static String getCellValue(HSSFCell cell) {  
        DecimalFormat df = new DecimalFormat("#");  
        String cellValue=null;  
        if (cell == null)  
            return null;  
        switch (cell.getCellType()) {  
            case Cell.CELL_TYPE_NUMERIC:  
                if(DateUtil.isCellDateFormatted(cell)){  
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
                    cellValue=sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue()));  
                    break;  
                }  
                cellValue=df.format(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING:           
                cellValue=String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA:  
                cellValue=String.valueOf(cell.getCellFormula());  
                break;  
            case Cell.CELL_TYPE_BLANK:  
                cellValue=null;  
                break;  
            case Cell.CELL_TYPE_BOOLEAN:  
                cellValue=String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_ERROR:  
                cellValue=String.valueOf(cell.getErrorCellValue());  
                break;  
        }  
        if(cellValue!=null&&cellValue.trim().length()<=0){  
            cellValue=null;  
        }  
        return cellValue;  
    }  
	 
	 
}
