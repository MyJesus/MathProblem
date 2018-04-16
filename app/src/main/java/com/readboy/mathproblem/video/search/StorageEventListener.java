package com.readboy.mathproblem.video.search;

public interface StorageEventListener {

	public void onFileChanged();
	public void onMounted(String path);
	public void onUnmounted(String path);
}
