package com.tinytimrob.ppse.napbot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import com.tinytimrob.common.PlatformData;

public class NapchartHandler
{
	public static ConcurrentHashSet<String> charts = new ConcurrentHashSet<String>();

	public static File getNapchart(String chart) throws IOException
	{
		File f = new File(napchartDirectory, chart + ".png");
		if (charts.contains(chart) && f.exists())
		{
			return f;
		}
		return downloadNapchart(chart, f);
	}

	static synchronized File downloadNapchart(String chart, File f) throws IOException
	{
		if (f.exists())
		{
			charts.add(chart);
			return f;
		}
		else
		{
			try
			{
				/*
				driver.get("https://napchart.com/" + chart);
				File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				*/
				File screenshot = File.createTempFile("napchart", "png");
				FileUtils.copyURLToFile(new URL("https://napchart.com/api/getImage?chartid=" + chart + "&width=600&height=600&shape=circle"), screenshot);
				BufferedImage image = ImageIO.read(screenshot);
				screenshot.delete();
				/*
				WebElement canvas = driver.findElement(By.className("canvas"));
				Point canvasLocation = canvas.getLocation();
				int canvasWidth = canvas.getSize().getWidth();
				int canvasHeight = canvas.getSize().getHeight();
				BufferedImage eleScreenshot = image.getSubimage(canvasLocation.getX(), canvasLocation.getY(), canvasWidth, canvasHeight);
				*/
				BufferedImage eleScreenshot = image.getSubimage(20, 20, 560, 560); // slight edge crop
				ImageIO.write(eleScreenshot, "png", f);
				charts.add(chart);
			}
			catch (Throwable t)
			{
				throw new IOException("Failed to download image from napchart.com");
			}
		}
		return f;
	}

	/*
	static Process p = null;
	static WebDriver driver = null;
	*/
	static File napchartDirectory = null;

	public static synchronized void init() throws IOException
	{
		/*
		int DISPLAY_NUMBER = 99; // TODO make this configurable
		if (PlatformData.platformType == PlatformType.LINUX)
		{
			p = Runtime.getRuntime().exec("/usr/bin/Xvfb :" + DISPLAY_NUMBER);
		}
		FirefoxBinary firefox = new FirefoxBinary();
		if (PlatformData.platformType == PlatformType.LINUX)
		{
			firefox.setEnvironmentProperty("DISPLAY", ":" + DISPLAY_NUMBER);
		}
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setAcceptInsecureCerts(true);
		FirefoxOptions options = new FirefoxOptions();
		options.setBinary(firefox);
		options.addCapabilities(capabilities);
		driver = new FirefoxDriver(options);
		*/
		napchartDirectory = new File(PlatformData.installationDirectory, "napcharts");
		napchartDirectory.mkdirs();
	}

	public static synchronized void shutdown()
	{
		/*
		if (driver != null)
		{
			driver.close();
		}
		if (p != null)
		{
			p.destroy();
		}
		*/
	}
}
