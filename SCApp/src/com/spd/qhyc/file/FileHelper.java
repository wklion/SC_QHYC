package com.spd.qhyc.file;
/**     
 * @公司:	spd
 * @作者: wangkun       
 * @创建: 2017-07-27
 * @最后修改: 2017-07-27
 * @功能: 文件帮助
 **/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.spd.qhyc.other.EndFilter;
import com.spd.qhyc.util.DateUtil;

public class FileHelper {
	/**
	 * @作者:wangkun
	 * @日期:2017年8月8日
	 * @修改日期:2017年8月8日
	 * @参数:
	 * @返回:
	 * @说明:读文件
	 */
	public String readFile(String strFile) throws Exception{
		File file = new File(strFile);
		if(!file.exists()){
			System.out.println("文件不存在!");
			return "";
		}
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader read = new InputStreamReader(fis,"utf-8");
		BufferedReader bufferedReader = new BufferedReader(read);
		StringBuilder sb = new StringBuilder();
		String lineTxt = null;
		while((lineTxt = bufferedReader.readLine()) != null){
			sb.append(lineTxt);
        }
		read.close();
		fis.close();
		return sb.toString();
	}
	public File findGrid2(String dic,String strFormater){
		File file = new File(dic);
		File[] files = file.listFiles(new EndFilter(strFormater));
		if(files==null||files.length<1){
			return null;
		}
		return files[0];
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年10月31日
	 * @修改日期:2017年10月31日
	 * @参数:cal-日期
	 * @返回:文件
	 * @说明:根据日期获取文件
	 */
	public File getFileByDateTime(Calendar cal,String path){
		int year = cal.get(Calendar.YEAR);
		String strYYYYMMDD = DateUtil.format("yyyyMMdd", cal);
		String strYYYYMMddHH = DateUtil.format("yyyyMMddHH", cal);
		String strFormater = strYYYYMMddHH+".GRB2";
		strFormater = "_HOR-PRE-"+strFormater;
		FileHelper fileHelper = new FileHelper();
		String newPath = path+year+"/"+strYYYYMMDD+"/";
		File findFile = fileHelper.findGrid2(newPath, strFormater);
		return findFile;
	}
	/**
	 * @作者:杠上花
	 * @日期:2018年1月23日
	 * @修改日期:2018年1月23日
	 * @参数:
	 * @返回:
	 * @说明:数组写入文件
	 */
	public void writeToFile(double[][] data,String strfile){
	    File file = new File(strfile);
	    int rows = data.length;
	    int cols = data[0].length;
	    try {
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            StringBuilder sb = null;
            for(int r=0;r<rows;r++){
                sb = new StringBuilder();
                for(int c=0;c<cols;c++){
                	double val = data[r][c];
                	val = (int)(val*100)/100.0;
                    sb.append(val);
                    sb.append(" ");
                }
                out.write(sb.toString().trim());
                out.write("\r\n");
                out.flush();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
