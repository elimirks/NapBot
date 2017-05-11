package com.tinytimrob.ppse.napbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class NapchartServlet extends HttpServlet
{
	private static final long serialVersionUID = 8296818065558260522L;
	Pattern NAPCHART_PATTERN = Pattern.compile("\\Q/\\E([a-zA-Z0-9]{5})");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String PATH = request.getPathInfo();
		System.out.println("jetty " + PATH);
		Matcher m = this.NAPCHART_PATTERN.matcher(PATH);
		if (m.matches())
		{
			String napchartString = m.group(1);
			File file = NapchartHandler.getNapchart(napchartString);
			if (file.exists())
			{
				FileInputStream fis = null;
				OutputStream out = null;
				try
				{
					response.setContentType("image/png");
					fis = new FileInputStream(file);
					out = response.getOutputStream();
					IOUtils.copy(fis, out);
				}
				finally
				{
					IOUtils.closeQuietly(out);
					IOUtils.closeQuietly(fis);
				}
				return;
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
}
