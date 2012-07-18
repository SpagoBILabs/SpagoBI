package com.tensegrity.wpalo.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 4693591104733092288L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		doGet(request, response);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String fileName = request.getParameter("fileName");
		String viewName = request.getParameter("viewName");
		if (fileName != null) {
			// The fileName should be decoded already, but just in case:
			fileName = URLDecoder.decode(fileName, "UTF-8");

			File f = new File(fileName);
			int contentLength = (int) f.length();

			response.reset();
			response.setBufferSize(10240);
			response.setContentLength(contentLength);
			response.setHeader("Content-disposition", "attachment; filename=" + viewName);
			response.setContentType("application/pdf");
//			response.setHeader("Accept-Ranges", "bytes");
//						
//			response.setHeader("Pragma", "private");

			BufferedInputStream input = new BufferedInputStream(new FileInputStream(f), 10240);
			BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream(), 10240);
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			output.flush();
			output.close();
			input.close();

//		    BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
//			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
//			
//			System.err.println("Length: " + f.length());
//			response.setContentLength((int) f.length());
//			byte [] buffer = new byte[4096];
//			int len;
//			while ((len = bis.read(buffer)) > 0) {
//				bos.write(buffer, 0, len);
//				System.err.println(new String(buffer));
//			}
//			bos.flush();
//			bos.close();
//			bis.close();			
//			
//			new File(fileName).delete();
		}		
	}
}
