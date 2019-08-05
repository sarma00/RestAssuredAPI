package com.sarma00.api.utlities;

import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ResponseWriter {
	static FileOutputStream fos;
	static PrintWriter writer;
	
	public static void writeResponseToFile(String fileName,String responseBody) throws Exception{
		fos = new FileOutputStream(fileName);
		writer =  new PrintWriter(fos);
		writer.write(responseBody);
		writer.flush();
		writer.close();
	}
	
}
