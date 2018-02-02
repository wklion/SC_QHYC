package com.spd.grid.climatic;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @作者:wangkun
 * @日期:2017年7月24日
 * @公司:spd
 * @说明:气候方法
*/
public class ClimaticFun {
	static{
		//System.loadLibrary ("opencv_java310");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月24日
	 * @修改日期:2017年7月24日
	 * @参数:matX-X矩阵;matY-Y矩阵
	 * @返回:2个时间系数
	 * @说明:求解EOF
	 */
	public Mat[] EOF(Mat matX,Mat matY){
		Mat[] matXTime = getTime(matX);
		Mat[] matYTime = getTime(matY);
		Mat[] result = new Mat[4];
		result[0] = matXTime[1];
		result[1] = matYTime[1];
		result[2] = matXTime[0];
		result[3] = matYTime[0];
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年7月20日
	 * @修改日期:2017年7月20日
	 * @参数:
	 * @返回:
	 * @说明:求时间系数
	 */
	private static Mat[] getTime(Mat mat){
			Mat[] result = new Mat[2];
			Mat matT = mat.t();//转置矩阵
			Mat matS = new Mat();//实对阵
			Core.gemm(mat, matT, 1.0, Mat.zeros(mat.size(), mat.type()), 0.0, matS);
			Mat matEigen = new Mat(matS.rows(),matS.cols(),CvType.CV_64F);//特征值
			Mat matVetor = new Mat(matS.rows(),matS.cols(),CvType.CV_64F);//特征向量
			Core.eigen(matS, matEigen,matVetor);
			Mat matTime = new Mat();
			Core.gemm(matVetor, mat, 1.0, Mat.zeros(matVetor.size(), matVetor.type()), 0.0, matTime);
			int C = matTime.cols();
			for(int r=0;r<10;r++){
					for(int c=0;c<C;c++){
							double val = matTime.get(r, c)[0];
							val = ((int)(val*1000))/1000.0;
							System.out.print(val+";");
					}
					System.out.println("");
			}
			//取前5行
			int cols = matTime.cols();
			Mat MatMax = new Mat(5,matTime.cols(),matTime.type());
			for(int r=0;r<5;r++){
					for(int c=0;c<cols;c++){
							double val = matTime.get(r, c)[0];
							MatMax.put(r, c, val);
					}
			}
			result[0] = matVetor;
			result[1] = MatMax;
			return result;
	}
}
