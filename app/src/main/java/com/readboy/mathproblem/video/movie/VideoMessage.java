package com.readboy.mathproblem.video.movie;

public class VideoMessage {
	
	/**   可以开始播放     */
	public static final int READY	  = 0x01;
	
	/**   每秒钟心跳，用来更新进度条界面   */
	public static final int HEART	  = 0x02;
	
	/**   一个视频播放完成     */
	public static final int FINISHED  = 0x04;
	
	/**   隐藏控制界面     */
	public static final int NOOP	  = 0x08;
	
	/**   出错消息    */
	public static final int ERROR	  = 0x10;
	
	/**   文件夹过深的报错消息，以前平板文件夹太深文件会读不到，所以设置超过15层报错   */
	public static final int TOO_DEEP  = 0x20;
	
}
