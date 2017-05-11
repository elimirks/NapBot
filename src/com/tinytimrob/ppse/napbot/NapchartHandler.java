package com.tinytimrob.ppse.napbot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.tinytimrob.common.PlatformData;
import com.tinytimrob.common.PlatformType;

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
			driver.get("https://napchart.com/" + chart);
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage image = ImageIO.read(screenshot);
			WebElement canvas = driver.findElement(By.id("canvas"));
			Point canvasLocation = canvas.getLocation();
			int canvasWidth = canvas.getSize().getWidth();
			int canvasHeight = canvas.getSize().getHeight();
			BufferedImage eleScreenshot = image.getSubimage(canvasLocation.getX(), canvasLocation.getY(), canvasWidth, canvasHeight);
			ImageIO.write(eleScreenshot, "png", f);
			charts.add(chart);
		}
		return f;
	}

	static Process p = null;
	static WebDriver driver = null;
	static File napchartDirectory = null;

	public static synchronized void init() throws IOException
	{
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
		driver = new FirefoxDriver(firefox, null);
		napchartDirectory = new File(PlatformData.installationDirectory, "napcharts");
		napchartDirectory.mkdirs();
	}

	public static synchronized void shutdown()
	{
		if (driver != null)
		{
			driver.close();
		}
		if (p != null)
		{
			p.destroy();
		}
	}
}
