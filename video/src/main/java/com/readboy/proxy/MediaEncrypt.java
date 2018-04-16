/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.readboy.proxy;

public class MediaEncrypt {
	
	static{
		System.loadLibrary("rbsec");
	}
	
	public MediaEncrypt(Object holder){
		setup(holder);
	}
	
	private static native void setup(Object holder);
	/**
	 *
	 * @param header
	 * 文件头部分数据，用于解析格式，判断加密等
	 * @param filesize
	 * 文件大小
	 */
	public native boolean setHeader(byte[] header, long filesize);
	/**
	 * 
	 * @param data 加密的数据
	 * @param offset 相对于data字节偏移量
	 * @param size 要解密的数据字节数
	 * @param seekPos offset处相对于整个数据中的位置。
	 * @return 返回解密的数据大小，结果可能小于data大小。
	 */
	public native long decrypt(byte[] data, long offset, long size, long seekPos);
	
	/**
	 * 获取真实的偏移量
	 * 
	 * @return 真实的偏移量
	 * 需要在setHeader之后调用
	 */
	public native long getRealStartOffset();
	
	/**
	 * 获取真实的文件大小
	 * @return 真实的文件大小
	 * 需要在setHeader之后调用
	 */
	public native long getRealSize();
	
	
	
}
