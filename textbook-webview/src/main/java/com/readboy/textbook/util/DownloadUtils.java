package com.readboy.textbook.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.os.Handler;


public class DownloadUtils{
	
	public static final int DOWNLOAD_ERROR_MSG = 0x2001;
	public static final int DOWNLOAD_FINISH_MSG = 0x2002;
	public static final int DOWNLOAD_TIMEOUT_MSG = 0x2003;
	
	/** 任务执行队列 */
	private ConcurrentLinkedQueue<MyRunnable> taskQueue = null;
	/**
	 * 正在等待执行或已经完成的任务队列
	 * 
	 * 备注：Future类，一个用于存储异步任务执行的结果，比如：判断是否取消、是否可以取消、是否正在执行、是否已经完成等
	 * 
	 * */
	private ConcurrentMap<Future, MyRunnable> taskMap = null;
	/**
	 * 创建一个不限制大小的线程池 此类主要有以下好处 1，以共享的无界队列方式来运行这些线程. 2，执行效率高。 3，在任意点，在大多数
	 * nThreads 线程会处于处理任务的活动状态
	 * 4，如果在关闭前的执行期间由于失败而导致任何线程终止，那么一个新线程将代替它执行后续的任务（如果需要）。
	 * 
	 * */
	private ExecutorService mES = null;
	/**
	 * 在此类中使用同步锁时使用如下lock对象即可，官方推荐的，不推荐直接使用MyRunnableActivity.this类型的,可以详细读一下/
	 * framework/app下面的随便一个项目
	 */
	private Object lock = new Object();
	/** 唤醒标志，是否唤醒线程池工作 */
	private boolean isNotify = true;
	/** 线程池是否处于运行状态(即:是否被释放!) */
	private boolean isRuning = true;
	/** 用此Handler来更新我们的UI */
	private Handler mHandler = null;

	public DownloadUtils(Handler handler)
	{
		init(handler);
	}
	
	public void init(Handler handler) {
		taskQueue = new ConcurrentLinkedQueue<MyRunnable>();
		taskMap = new ConcurrentHashMap<Future, MyRunnable>();
		if (mES == null) {
			mES = Executors.newCachedThreadPool();
		}
		mHandler = handler;
	}
	
	public void download(String url, String file)
	{
		addTask(new MyRunnable(mHandler, url, file)); 
	}

	/**
	 * 增加一个任务到队列当中
	 */
	public void addTask(final MyRunnable mr) {
		if (mES == null) {
			mES = Executors.newCachedThreadPool();
			notifyWork();
		}
		if (taskQueue == null) {
			taskQueue = new ConcurrentLinkedQueue<MyRunnable>();
		}
		if (taskMap == null) {
			taskMap = new ConcurrentHashMap<Future, MyRunnable>();
		}
		mES.execute(new Runnable() {
			@Override
			public void run() {
				/**
				 * 插入一个Runnable到任务队列中
				 * 这个地方解释一下,offer跟add方法,试了下,效果都一样,没区别,官方的解释如下: 1 offer : Inserts
				 * the specified element at the tail of this queue. As the queue
				 * is unbounded, this method will never return {@code false}. 2
				 * add: Inserts the specified element at the tail of this queue.
				 * As the queue is unbounded, this method will never throw
				 * {@link IllegalStateException} or return {@code false}.
				 * 
				 * 
				 * */
				if(!taskQueue.contains(mr))
				{
					taskQueue.offer(mr);
					// taskQueue.add(mr);
					notifyWork();
					start();
				}
			}
		});
	}

	/**
	 * 停止任务释放资源
	 */
	private void release() {
		isRuning = false;
		Iterator iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Future, MyRunnable> entry = (Map.Entry<Future, MyRunnable>) iter
					.next();
			Future result = entry.getKey();
			if (result == null) {
				continue;
			}
			result.cancel(true);
			taskMap.remove(result);
		}
		if (null != mES) {
			mES.shutdown();
		}
		mES = null;
		taskMap = null;
		taskQueue = null;
	}

	/**
	 * 重新执行 任务
	 * @param mr
	 */
	private void reload(final MyRunnable mr) {
		mHandler.sendEmptyMessage(0);
		if (mES == null) {
			mES = Executors.newCachedThreadPool();
			notifyWork();
		}
		if (taskQueue == null) {
			taskQueue = new ConcurrentLinkedQueue<MyRunnable>();
		}
		if (taskMap == null) {
			taskMap = new ConcurrentHashMap<Future, MyRunnable>();
		}
		mES.execute(new Runnable() {
			@Override
			public void run() {
				/** 插入一个Runnable到任务队列中 */
				taskQueue.offer(mr);
				// taskQueue.add(mr);
				notifyWork();
			}
		});
		mES.execute(new Runnable() {
			@Override
			public void run() {
				if (isRuning) {
					MyRunnable myRunnable = null;
					synchronized (lock) {
						myRunnable = taskQueue.poll(); // 从线程队列中取出一个Runnable对象来执行，如果此队列为空，则调用poll()方法会返回null
						if (myRunnable == null) {
							isNotify = true;
						}
					}
					if (myRunnable != null) {
						taskMap.put(mES.submit(myRunnable), myRunnable);
					}
				}
			}
		});
	}

	/**
	 * <Summary Description>
	 */
	private void stop() {
		for (MyRunnable runnable : taskMap.values()) {
			runnable.setCancleTaskUnit(true);
		}
	}

	/**
	 * <Summary Description>
	 */
	private void start() {
		if (mES == null || taskQueue == null || taskMap == null) {
			return;
		}
		mES.execute(new Runnable() {
			@Override
			public void run() {
				if (isRuning) {
					MyRunnable myRunnable = null;
					synchronized (lock) {
						myRunnable = taskQueue.poll(); // 从线程队列中取出一个Runnable对象来执行，如果此队列为空，则调用poll()方法会返回null
						if (myRunnable == null) {
							isNotify = true;
						}
					}
					if (myRunnable != null) {
						taskMap.put(mES.submit(myRunnable), myRunnable);
					}
				}
			}
		});
	}

	private void notifyWork() {
		synchronized (lock) {
			if (isNotify) {
				lock.notifyAll();
				isNotify = !isNotify;
			}
		}
	}
}
