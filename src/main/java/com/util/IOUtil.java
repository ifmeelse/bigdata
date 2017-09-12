package com.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtil {
	public static void wirteString(String path, String context) {
		   try {
		    /* 创建写入对象 */
		    FileWriter fileWriter = new FileWriter(path,true);
		    /* 创建缓冲区 */
		    BufferedWriter writer = new BufferedWriter(fileWriter);
		    /* 写入字符串 */
		    writer.write(context);
		    /* 关掉对象 */
		    writer.close();
		   } catch (IOException e) {
		    e.printStackTrace();
		   }
		}
}
