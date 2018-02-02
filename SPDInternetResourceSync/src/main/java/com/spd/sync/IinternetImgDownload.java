package com.spd.sync;

/**
 * 下载网络图片接口
 * @author xianchao
 *
 */
public interface IinternetImgDownload {

	public boolean downloadImg(String urlAddress, String dir, String fileName);
}
