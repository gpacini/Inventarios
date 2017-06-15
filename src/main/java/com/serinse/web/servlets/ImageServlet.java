package com.serinse.web.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serinse.common.Constants;

public class ImageServlet extends HttpServlet{
	private static final long serialVersionUID = -3249597492052107066L;
	
    private String basePath;
    
    public void init(){
        this.basePath = Constants.IMAGES_LOCATION;
    }
    

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String requestedFile = request.getPathInfo();
		
		File file = new File(basePath, URLDecoder.decode(requestedFile, "UTF-8"));
		if (!file.exists()) {
            // Throw 404, redirect to error page may is another selection
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
		
		// write via response's OutputStream
		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int bytesRead = 0;

			do {
				bytesRead = inputStream.read(buffer, 0, buffer.length);
				response.getOutputStream().write(buffer, 0, bytesRead);
			} while (bytesRead == buffer.length);

			response.getOutputStream().flush();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}
}
