package com.spd.grid.domain;
/**
 * @作者:wangkun
 * @日期:2017年2月19日
 * @公司:spd
 * @说明:矩阵相关运算
*/
public class Matrix {
	private int cols = 0;			// 矩阵列数
	private int rows = 0;			// 矩阵行数
    private double eps = 0.0;			// 缺省精度
    private double[][] data = null;	// 矩阵数据缓冲区
    public int getCols() {
		return cols;
	}
	public void setCols(int cols) {
		this.cols = cols;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public double getEps() {
		return eps;
	}
	public void setEps(double eps) {
		this.eps = eps;
	}
	public double[][] getData() {
		return data;
	}
	public void setData(double[][] data) {
		this.data = data;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年2月19日
	 * @修改日期:2017年2月19日
	 * @参数:/nRows-行，nCols-列
	 * @说明:
	 */
	public Matrix(int nRows, int nCols)
    {
        rows = nRows;
        cols = nCols;
        Init(rows, cols);
    }
	public Matrix(double[][] value)
    {
        rows = value.length;
        cols=value[0].length;
        Init(rows, cols);
        SetData(value);
    }
	/**
	 * @作者:wangkun
	 * @日期:2017年2月19日
	 * @修改日期:2017年2月19日
	 * @参数:nSize大小
	 * @说明:方阵构造
	 */
	public Matrix(int nSize)
    {
		rows = nSize;
		cols = nSize;
        Init(nSize, nSize);
    }
	/**
	 * @作者:wangkun
	 * @日期:2017年2月19日
	 * @修改日期:2017年2月19日
	 * @参数:nSize-大小，value-值
	 * @说明:
	 */
	public Matrix(int nSize, double[][] value)
    {
		rows = nSize;
		cols = nSize;
        Init(nSize, nSize);
        SetData(value);
    }
	public void SetData(double[][] value)
    {
        data = value.clone();
    }
	public Boolean Init(int nRows, int nCols)
    {
		if (nRows<1||nCols<1)
            return false;
        rows = nRows;
        cols = nCols;
        // 分配内存
        data = new double[nRows][nCols];
        return true;
    }
	/**
	 * @作者:wangkun
	 * @日期:2017年2月19日
	 * @修改日期:2017年2月19日
	 * @参数:m1-第一个矩阵，m2-第二个矩阵
	 * @返回:相加的矩阵
	 * @说明:矩阵相加
	 */
	public static Matrix Add(Matrix m1,Matrix m2)
	{
		if(m1.rows!=m2.rows||m1.cols!=m2.cols)
			return null;
		int rows=m1.rows;
		int cols=m1.cols;
		Matrix result=new Matrix(rows,cols);
		double[][] data=new double[rows][cols];
		for(int r=0;r<rows;r++)
		{
			for(int c=0;c<cols;c++)
			{
				data[r][c]=m1.data[r][c]+m2.data[r][c];
			}
		}
		result.setData(data);
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年2月19日
	 * @修改日期:2017年2月19日
	 * @参数:m1-被减矩阵，m2-第二个矩阵
	 * @返回:矩阵差
	 * @说明:矩阵相减
	 */
	public static Matrix Sub(Matrix m1,Matrix m2)
	{
		if(m1.rows!=m2.rows||m1.cols!=m2.cols)
			return null;
		int rows=m1.rows;
		int cols=m1.cols;
		Matrix result=new Matrix(rows,cols);
		double[][] data=new double[rows][cols];
		for(int r=0;r<rows;r++)
		{
			for(int c=0;c<cols;c++)
			{
				data[r][c]=m1.data[r][c]-m2.data[r][c];
			}
		}
		result.setData(data);
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年2月19日
	 * @修改日期:2017年2月19日
	 * @参数:m1-被减矩阵，m2-第二个矩阵
	 * @返回:矩阵相乘
	 * @说明:矩阵相乘
	 */
	public static Matrix Mul(Matrix m1,Matrix m2)
	{
		if(m1.rows==0||m1.cols==0||m2.rows==0||m2.cols==0)
			return null;
		int m1rows=m1.rows;
		int m2rows=m2.rows;
		int m2cols=m2.cols;
		Matrix result=new Matrix(m1rows,m2cols);
		double[][] data=new double[m1rows][m2cols];
		for(int r=0;r<m1rows;r++)
		{
			for(int c=0;c<m2cols;c++)
			{
				double temp=0;
				for(int x=0;x<m2rows;x++)
				{
					temp+=m1.data[r][x]*m2.data[x][c];
				}
				data[r][c]=temp;
			}
		}
		result.setData(data);
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年2月21日
	 * @修改日期:2017年2月21日
	 * @参数:
	 * @返回:转置矩阵
	 * @说明:获取转置矩阵
	 */
	public Matrix Transpose() {
        Matrix matrix=new Matrix(cols,rows);
        double[][] A_T = new double[cols][rows];
        for (int i = 0; i < rows; i++) {  
            for (int j = 0; j < cols; j++) {  
                A_T[j][i] = data[i][j];  
            }  
        }
        matrix.setData(A_T);
        return matrix;  
    }
	/**
	 * @作者:wangkun
	 * @日期:2017年2月21日
	 * @修改日期:2017年2月21日
	 * @参数:size-矩阵大小，eps-精度
	 * @返回:特征向量
	 * @说明:雅可比方法1
	 */
	public Matrix Jacobi(int size,double[] tzz,double eps){
		Matrix result=new Matrix(size,size);
		int i, j, p, q, u, w, t, s;
		double ff, fm, cn, sn, omega, x, y, d;
		for (i = 0; i <size; i++)
        {
            for (j = 0; j < size; j++)
            	result.data[i][j]=i==j?1.0:0.0;
        }
		ff = 0.0;
        for (i = 1; i < size; i++)
        {
            for (j = 0; j <i; j++)
            {
                d = data[i][j];
                ff = ff + d * d;
            }
        }
        ff = Math.sqrt(2.0 * ff);
        ff = ff / (1.0 * size);
        Boolean nextLoop = false;
        while (true){
        	for (i = 1; i < size; i++){
        		for (j = 0; j <i; j++){
        			d = Math.abs(data[i][j]);
        			if (d > ff){
        				 p = i;
                         q = j;
                         x=-data[i][j];
                         y = (data[j][j] - data[i][i]) / 2.0;
                         omega = x / Math.sqrt(x * x + y * y);
                         if (y < 0.0)
                             omega = -omega;
                         sn = 1.0 + Math.sqrt(1.0 - omega * omega);
                         sn = omega / Math.sqrt(2.0 * sn);
                         cn = Math.sqrt(1.0 - sn * sn);
                         fm = data[i][i];
                         data[i][i] = fm * cn * cn + data[j][j] * sn * sn + data[i][j] * omega;
                         data[j][i] = fm * sn * sn + data[j][j] * cn * cn - data[i][j] * omega;
                         data[i][j] = 0.0; 
                         data[j][i] = 0.0;
                         for (j = 0; j < size; j++){
                             if ((j != p) && (j != q))
                             {
                                 fm = data[i][j];
                                 data[p][j] = fm * cn + data[q][j] * sn;
                                 data[q][j] = -fm * sn + data[q][j] * cn;
                             }
                         }
                         for (i = 0; i < size; i++){
                        	 if ((i != p) && (i != q))
                             {
                                 fm = data[i][p];
                                 data[i][p] = fm * cn + data[i][q] * sn;
                                 data[i][q] = -fm * sn + data[i][q] * cn;
                             }
                         }
                         for (i = 0; i < size; i++){
                        	 fm =result.data[i][p];
                        	 result.data[i][p]=fm*cn+result.data[i][q]*sn;
                        	 result.data[i][q]=-fm*sn+result.data[i][q]*cn;
                         }
                         nextLoop = true;
                         break;
        			}
        		}
        		if (nextLoop)
                    break;
        	}
        	 if (nextLoop)
             {
                 nextLoop = false;
                 continue;
             }
             nextLoop = false;
          // 如果达到精度要求，退出循环，返回结果
             if (ff < eps)
             {
                 for (i = 0; i < size; ++i)
                	 tzz[i] = data[i][i];
                 return result;
             }

             ff = ff / (1.0 * size);
        }
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年2月21日
	 * @修改日期:2017年2月21日
	 * @参数:size-矩阵大小，eps-精度
	 * @返回:特征向量
	 * @说明:雅可比方法1
	 */
	public Matrix Jacobi1(int size,double eps){
		Matrix result=new Matrix(size,size);
		double[][] s=new double[size][size];
		for(int i=0;i<size;i++){
			for(int j=0;j<=i;j++){
				s[i][j]=i==j?1:0;
			}
		}
		double g=0.0;
		for(int i=1;i<size;i++){
			int i1=i-1;
			for(int j=0;j<i1;j++){
				g+=2*data[i][j]*data[i][j];
			}
		}
		double s1=Math.sqrt(g);
		double s2=eps/size*s1;
		double s3=s1;
		double L=0.0;
		s3=s3/size;
		for(int i=1;i<size;i++){
			int i1=i-1;
			for(int j=0;j<i1;j++){
				if(data[i][j]>=s3){
					L=1;
					double v1=data[j][j];
					double v2=data[i][i];
					double v3=data[i][j];
					double u=(v1-v3)/2;
					if(u==0.0){
						g=1;
					}
					v2=0.2450375;
					u=-8.9406967E-08;
					if(Math.abs(u)>1E-10)
					{
						if(u<0){
							g=v2/Math.sqrt(v2*v2+u*u);
						}
						else{
							g=-v2/Math.sqrt(v2*v2+u*u);
						}
					}
					double st=g/Math.sqrt(2.0*Math.sqrt(1-g*g));
					double ct=Math.sqrt(1-st*st);
					for(int m=0;m<size;m++){
						data[i][m]=data[j][m]*st-data[i][m]*ct;
						data[i][j]=data[j][m]*ct-data[i][m]*st;
						g=s[i][j]*ct-s[i][m]*st;
						s[i][m]=s[i][j]*st+s[i][m]*ct;
						s[i][j]=g;
					}
					for(int m=0;m<size;m++){
						data[m][j]=data[j][m];
						data[m][i]=data[i][m];
					}
					g=2*v2*ct*st;
					data[j][j]=v1*ct*ct+v3*st*st-g;
					data[i][i]=v1*st*st+v3*ct*ct+g;
					data[i][j]=(v1-v3)*st*ct+v2*(ct*ct-st*st);
					data[j][i]=data[i][j];
				}
			}
		}
		return result;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年2月22日
	 * @修改日期:2017年2月22日
	 * @参数:
	 * @返回:逆矩阵
	 * @说明:求逆矩阵,全选主元高斯－约当法
	 */
	public Matrix InverseMatrix_GaussJordan(){
		if(rows!=cols||rows==0){
			System.out.println("行列数不相等或等于0,不能求逆矩阵!");
			return null;
		}
		Matrix result=new Matrix(rows,cols);
		double[][] resultData=data.clone();
		double d=0.0;
		double p=0.0;
		int[] pnRow=new int[cols];
		int[] pnCol=new int[cols];
		for(int k=0;k<cols;k++){
			d=0.0;
			for(int i=k;i<cols;i++){
				for(int j=k;j<cols;j++){
					p=Math.abs(resultData[i][j]);
					if(p>d){
						d=p;
						pnRow[k]=i;
						pnCol[k]=j;
					}
				}
			}
			if(d==0.0){//失败
				return null;
			}
			if (pnRow[k] != k){
				for (int j = 0; j < cols; j++){
					p=resultData[k][j];
					resultData[k][j]=resultData[pnRow[k]][j];
					resultData[pnRow[k]][j] = p;
				}
			}
			if (pnCol[k] != k){
				for(int i=0;i<cols;i++){
					p=resultData[i][k];
					resultData[i][k]=resultData[i][pnCol[k]];
					resultData[i][pnCol[k]] = p;
				}
			}
			resultData[k][k]=1.0/resultData[k][k];
			for(int j=0;j<cols;j++){
				if (j != k){
					resultData[k][j]*=resultData[k][k];
				}
			}
			for(int i=0;i<cols;i++){
				if (i != k){
					for(int j=0;j<cols;j++){
						if (j != k){
							resultData[i][j]=resultData[i][j]-resultData[i][k]*resultData[k][j];
						}
					}
				}
			}
			for(int i=0;i<cols;i++){
				if (i != k){
					resultData[i][k]=-resultData[i][k]*resultData[k][k];
				}
			}
		}
		// 调整恢复行列次序
		for(int k=cols-1;k>=0;k--){
			if (pnCol[k] != k){
				for(int j=0;j<cols;j++){
					p = resultData[k][j];
					resultData[k][j] = resultData[pnCol[k]][j];
					resultData[pnCol[k]][j] = p;
				}
			}
			if (pnRow[k] != k){
				for(int i=0;i<cols;i++){
					p = resultData[i][k];
					resultData[i][k] = resultData[i][pnRow[k]];
					resultData[i][pnRow[k]] = p;
				}
			}
		}
		result.setData(resultData);
		return result;
	} 
}
