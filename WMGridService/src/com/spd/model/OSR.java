package com.spd.model;

import java.util.HashMap;
import com.spd.grid.tool.LogTool;

/**
 * @作者:wangkun
 * @日期:2017年3月27日
 * @公司:spd
 * @说明:
*/
public class OSR {
	private int pCount=0;
	private int tCount=0;
	private double[][] a=null;
	private double[] ks=new double[100];
	private double[][] ex=new double[30][30];
	private double[][] coe=new double[30][30];//回归系数
	private double[][] cc=new double[100][30];
	private double[][] xx=new double[100][30];
	private double[][] xf=new double[100][100];
	private double[] x=null;//真实数据
	private double[] x1=null;//回归数据
	private double[] x2=new double[100];
	private double[] csca=new double[30];
	private double[] avgData=null;
	private double[][] data=null;
	private HashMap<Integer, double[]> xishu=new HashMap<>();
	private double[] result=null;//结果
	/**
	 * @作者:wangkun
	 * @日期:2017年3月27日
	 * @修改日期:2017年3月27日
	 * @参数:xData-模式数据，yData-观测数据
	 * @返回:
	 * @说明:建方程,xData[0]点;yData[1]时序
	 */
	public double[] CreateEquation(double[][] xData,double[] yData){
		tCount=xData.length;//观测点数
		if(yData.length!=tCount){
			LogTool.logger.warn("观测数据和模式数据长度不相同!");
			return null;
		}
		pCount=xData[0].length;
		//初始化
		a=new double[pCount+1][pCount+1];
		x=new double[tCount];
		x1=new double[tCount];
		avgData=new double[pCount+1];
		data=new double[tCount][pCount+1];
		for(int i=0;i<pCount;i++){
			for(int j=0;j<tCount;j++){
				data[j][i]=xData[j][i];
			}
		}
		for(int i=0;i<tCount;i++){
			data[i][pCount]=yData[i];
		}
		sreg();
		return result;
	}
	private void sreg() {
		double[] ma=new double[100];
		double[] rss=new double[pCount];
		//求平均
		for(int i=0;i<pCount+1;i++){
			double sum=0;
			for(int j=0;j<tCount;j++){
				sum+=data[j][i];
			}
			double avg=sum/tCount;
			avgData[i]=avg;
		}
		for(int i=0;i<pCount+1;i++){//30
			for(int j=0;j<pCount+1;j++){//30
				double s=0;
				for(int k=0;k<tCount;k++){//40
					s+=data[k][i]*data[k][j];
				}
				s=s-tCount*avgData[i]*avgData[j];
				a[i][j]=s;
				a[i][j]=s;
			}
		}
		//double sto=a[pCount][pCount];
		int ih=(int) (Math.pow(2, pCount-1)-1);
		ks[0]=1;
		for(int k=2;k<=pCount-1;k++){//50
			int j=(int) Math.pow(2, k-1);
			ks[j-1]=k;
			for(int i=1;i<=j-1;i++){//55
				double m=ks[j-i-1];
				ks[j+i-1]=-m;
			}
		}
		for(int i=0;i<pCount;i++){//60
			rss[i]=Math.pow(10, 20);
			ma[i]=0;
		}
		int it=0;
		int nb=0;
		for(int m=0;m<ih;m++){//70
			it=(int) Math.abs(ks[m]);
			if(ks[m]>0){
				nb=nb+1;
				reg1(ma,nb,rss,pCount+1,it,pCount);
			}
			else{
				nb=nb-1;
				reg2(ma,nb,rss,pCount+1,it,pCount);
			}
		}
		it=pCount;
		nb=nb+1;
		reg1(ma,nb,rss,pCount+1,it,pCount);
		for(int m=0;m<ih;m++){//80
			int m1=(int) ((-1)*ks[ih-m-1]);
			it=Math.abs(m1);
			if(m1>0){
				nb=nb+1;
				reg1(ma,nb,rss,pCount+1,it,pCount);
			}
			else{
				nb=nb-1;
				reg2(ma,nb,rss,pCount+1,it,pCount);
			}
		}
		for(int k=0;k<pCount;k++){//90  ip次回归
			System.out.println("第"+(k+1)+"次回归!");
			double c=0;
			for(int j=0;j<pCount;j++){//100
				if(ex[k][j]!=0){
					c+=coe[k][j]*avgData[j];
				}
			}
			c=avgData[pCount]-c;
			System.out.println("常数项:"+c);
			int mm=0;
			//输出方程end
			StringBuilder sbFC=new StringBuilder();
			sbFC.append("方程:Y="+c);
			double[] tempXiShu=new double[pCount+1];//加上系数
			tempXiShu[0]=c;
			for(int j=0;j<pCount;j++){
				if(ex[k][j]!=0){
					sbFC.append(String.format("%fX%d+", coe[k][j],j));
					tempXiShu[j+1]=coe[k][j];
				}
				else{
					tempXiShu[j+1]=0;
				}
			}
			xishu.put(k, tempXiShu);
			sbFC.delete(sbFC.length()-1, sbFC.length());
			System.out.println(sbFC.toString());
			//输出方程end
			for(int j=0;j<pCount;j++){//301
				if(ex[k][j]!=0){
					mm=mm+1;
					cc[k][mm-1]=coe[k][j];
					for(int i=0;i<tCount;i++){//300
						xx[i][mm-1]=data[i][j];
					}
				}
			}
			for(int i=0;i<tCount;i++){//302
				xf[k][i]=c;
				for(int j=0;j<mm;j++){//303
					xf[k][i]=xf[k][i]+cc[k][j]*xx[i][j];
					x[i]=data[i][pCount];
					x1[i]=xf[k][i];
				}
			}
			for(int j=0;j<tCount;j++){//401
				x2[j]=xf[k][j]-x[j];
			}
			double w=0;
			for(int j=0;j<tCount;j++){//402
				w+=x2[j]*x2[j];
			}
			double v=Math.sqrt(w/tCount);
			double csc3=csc(tCount,x,x1,k);
			csca[k]=csc3;
		}
		double aa=0;
		for(int i=0;i<pCount;i++){//201,先出最大的CSC
			if(csca[i]>aa){
				aa=csca[i];
			}
		}
		int maxCSCIndex=0;//最大CSC位置
		for(int i=0;i<pCount;i++){//202
			if(csca[i]==aa){
				maxCSCIndex=i;
				break;
			}
		}
		//最优子集
		StringBuilder sbSet=new StringBuilder();
		for(int i=0;i<pCount;i++){
			if(ex[maxCSCIndex][i]!=0){
				sbSet.append(String.format("X%d", i)+"  ");
			}
		}
		System.out.println(sbSet.toString());
		for(int j=0;j<pCount;j++){//188
			if(j==maxCSCIndex){
				for(int i=0;i<tCount;i++){//185
					x1[i]=xf[j][i];
				}
			}
		}
		//输出方程系数
		result=xishu.get(maxCSCIndex);
	}
	private void reg1(double[] ma,int nb,double[] rss,int ipp,int it,int ip) {
		int k=it;
		reg3(k-1,ipp);
		ma[it-1]=1;
		if(a[ipp-1][ipp-1]>rss[nb-1])
			return;
		rss[nb-1]=a[ipp-1][ipp-1];
		for(int j=0;j<ip;j++){
			ex[nb-1][j]=ma[j];
			coe[nb-1][j]=a[j][ipp-1];
		}
	}
	private void reg2(double[] ma,int nb,double[] rss,int ipp,int it,int ip) {
		int k=it;
		reg3(k-1,ipp);
		ma[it-1]=0;
		if(a[ipp-1][ipp-1]>rss[nb-1])
			return;
		rss[nb-1]=a[ipp-1][ipp-1];
		for(int j=0;j<ip;j++){
			ex[nb-1][j]=ma[j];
			coe[nb-1][j]=a[j][ipp-1];
		}
	}
	private void reg3(int k,int ipp) {
		if(Math.abs(a[k][k])<=Math.pow(10, -12)){
			System.out.println("erro!");
			return;
		}
		for(int i=0;i<ipp;i++){//10
			for(int j=0;j<ipp;j++){
				if(i!=k&&j!=k){
					a[i][j]=a[i][j]-a[i][k]*a[k][j]/a[k][k];
				}
			}
		}
		for(int j=0;j<ipp;j++){//20
			if(j!=k){
				a[k][j]=a[k][j]/a[k][k];
				a[j][k]=-a[j][k]/a[k][k];
			}
		}
		a[k][k]=1/a[k][k];
	}
	private double csc(int N,double[] X,double[] X1,int K) {
		double csc3=0;
		double u=0,v=0;
		double[][] le=new double[3][3];
		double[] p=new double[3];
		double[] q=new double[3];
		for(int i=0;i<N-1;i++){//10
			u +=Math.abs(X1[i+1]-X1[i])/(N-1);
			v +=Math.abs(X[i+1]-X[i])/(N-1);
		}
		for(int i=0;i<N-1;i++){//30
			double xp=x[i+1]-x[i];
			double xp1=x1[i+1]-x1[i];
			if(xp1>=u&&xp>=v){
				le[0][0]+=1;
			}
			if(xp1>=u&&xp<=0){
				le[0][2]+=1;
			}
			if(xp1>=u&&xp<v&&xp>0){
				le[0][1]+=1;
			}
			if(xp1<u&&xp1>0&&xp>=v){
				le[1][0]+=1;
			}
			if(xp1<u&&xp1>0&&xp<v&&xp>0){
				le[1][1]+=1;
			}
			if(xp1<u&&xp1>0&&xp<=0){
				le[1][2]+=1;
			}
			if(xp1<=0&&xp>=v){
				le[2][0]+=1;
			}
			if(xp1<=0&&xp>0&&xp<v){
				le[2][1]+=1;
			}
			if(xp1<=0&&xp<=0){
				le[2][2]+=1;
			}
		}
		double r1=0,r2=0,r3=0;
		for(int i=0;i<3;i++){//55
			for(int j=0;j<3;j++){//55
				if(le[i][j]!=0){
					r1+=le[i][j]*Math.log(le[i][j]);
				}
			}
		}
		for(int i=0;i<3;i++){//60
			for(int j=0;j<3;j++){//60
				p[i]+=le[i][j];
				q[i]+=le[j][i];
			}
		}
		for(int i=0;i<3;i++){//70
			if(q[i]!=0){
				r2+=q[i]*Math.log(q[i]);
			}
			if(p[i]!=0){
				r3+=p[i]*Math.log(p[i]);
			}
		}
		double s1=2*(r1+(N-1)*Math.log(N-1)-(r2+r3));
		double xm1=0;
		for(int i=0;i<N;i++){//80
			xm1+=x[i]/N;
		}
		double qk=0;
		double qx=0;
		for(int i=0;i<N;i++){//90
			qk+=(x[i]-x1[i])*(x[i]-x1[i])/N;
			qx+=(x[i]-xm1)*(x[i]-xm1)/N;
		}
		double s2=(N-K-1)*(1-qk/qx);
		csc3=s1+s2;
		return csc3;
	}
}
