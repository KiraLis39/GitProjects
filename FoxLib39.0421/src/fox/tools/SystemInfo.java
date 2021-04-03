package fox.tools;

import java.awt.Toolkit;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Properties;

import com.sun.management.OperatingSystemMXBean;


public class SystemInfo {
	
	private static StringBuilder sb;
	private OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	
	public static String getAllSystemParameters() {
		sb = new StringBuilder();
		Properties mass = System.getProperties();
		ArrayList<?> lstK = new ArrayList<Object>(mass.keySet());
		ArrayList<?> lstV = new ArrayList<Object>(mass.values());

		int i = 0;
		while (lstK.size() > i) {
			sb.append(lstK.get(i) + ": " + lstV.get(i));
			i++;
		}
		
	return sb.toString();
	}

	public void printAll() {
		System.out.println("vendor: " + System.getProperty("java.vm.vendor"));
		System.out.println("vendor.url: " + System.getProperty("java.vendor.url"));
		System.out.println();

		System.out.println("\t\tСВОЙСТВА ПК:");
		System.out.println();

		System.out.println("Имя пользователя:\t" + System.getProperty("user.name"));
		System.out.println("Страна и язык:\t" + System.getProperty("user.country") + "_" + System.getProperty("user.language"));
		System.out.println("Кодировка системы:\t" + System.getProperty("file.encoding") + " ("	+ System.getProperty("sun.jnu.encoding") + ")");
		System.out.println("Временная зона:\t"	+ (System.getProperty("user.timezone") == null ? System.getProperty("user.timezone") : "none"));
		System.out.println("Домашний каталог:\t" + System.getProperty("user.home"));
		System.out.println("Текущий каталог:\t" + System.getProperty("user.dir"));
		System.out.println();

		System.out.println("Имя данной ОС:\t" + System.getProperty("os.name"));
		System.out.println("Версия данной ОС:\t" + System.getProperty("os.version"));
		System.out.println("Архитектура ОС:\t" + System.getProperty("os.arch"));
		System.out.println("Семейство ОС:\t" + System.getenv().get("OS"));
		System.out.println();

		System.out.println("Кодировка системы: " + System.getProperty("file.encoding") + " (" + System.getProperty("sun.jnu.encoding") + ")");
		System.out.println("Имя компьютера:\t" + System.getenv().get("COMPUTERNAME"));
		System.out.println("Имя процессора:\t" + System.getProperty("sun.cpu.isalist").toUpperCase());
		System.out.println("Архитектура процессора:\t" + System.getProperty("sun.arch.data.model") + "bit");
		System.out.println("Ядер процессора:\t" + Runtime.getRuntime().availableProcessors());
		System.out.println("О процессоре:\t" + System.getenv().get("PROCESSOR_IDENTIFIER"));
		System.out.println();

		System.out.println("Текущая Java-машина:\t" + System.getProperty("java.vm.name") + "\n\t\t" + System.getProperty("java.specification.name") + "\n\t\t" + System.getProperty("java.runtime.name"));
		System.out.println("Версия Java-машины:\t" + System.getProperty("java.vm.version") + " (" + System.getProperty("java.runtime.version") + " )");
		System.out.println("Каталог Java-машины:\t" + System.getProperty("java.home"));
		System.out.println("Архетиктура Java:\t" + System.getProperty("sun.arch.data.model") + "bit");
		System.out.println();

		System.out.println("http.proxyHost    =\t" + System.getProperty("http.proxyHost"));
		System.out.println("https.proxyHost  =\t" + System.getProperty("https.proxyHost"));
		System.out.println("ftp.proxyHost      =\t" + System.getProperty("ftp.proxyHost"));
		System.out.println("socksProxyHost  =\t" + System.getProperty("socksProxyHost"));
		System.out.println();

		System.out.println("Разрешение экрана в dpi: " + Toolkit.getDefaultToolkit().getScreenResolution());
		System.out.println("Разрешение экрана стндрт: " + (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() + " х " + (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		System.out.println();

		File[] roots = File.listRoots();
		for (File root : roots) {
			System.out.println("Диск: " + root.getAbsolutePath());
			System.out.println("Всего места: " + (root.getTotalSpace() / 1048576 / 1024) + " Гб. или " + (root.getTotalSpace() / 1048576) + " мб.");
			System.out.println("Свободно: " + (root.getFreeSpace() / 1048576 / 1024) + " Гб. или " + (root.getFreeSpace() / 1048576) + " мб.");
			System.out.println("Занято: " + ((root.getTotalSpace() / 1048576 / 1024) - (root.getFreeSpace() / 1048576 / 1024)) + " Гб. " + "или " + ((root.getTotalSpace() / 1048576) - (root.getFreeSpace() / 1048576)) + " мб.");
			System.out.println();
		}

		System.out.println("Данная программа использует "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "мб из "
				+ (Runtime.getRuntime().totalMemory() / 1048576) + "мб выделенных под неё."
				+ "\nСпасибо за использование утилиты компании MultyVerse39 Group!");
		
		long usingLong = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		System.out.println("\nMax memory:\t\t" + Runtime.getRuntime().maxMemory() + " byte (" + (Runtime.getRuntime().maxMemory() / 1048576) + "mb)");
		System.out.println("Using memory:\t" +	usingLong + " byte (" + (usingLong / 1048576) + "mb)");
		
		
		long physicalMemorySize, freePhysicalMemory, freeSwapSize, commitedVirtualMemorySize;
		
		physicalMemorySize = os.getTotalPhysicalMemorySize() / 1048576;
	    freePhysicalMemory = os.getFreePhysicalMemorySize() / 1048576;
	    freeSwapSize = os.getFreeSwapSpaceSize() / 1048576;
	    commitedVirtualMemorySize = os.getCommittedVirtualMemorySize() / 1048576;
	    
	    System.out.println("\nВторая линия данных: "
	    		+ "Physical Memory Size > " + physicalMemorySize + "мб, "
	    		+ "Free Physical Memory > " + freePhysicalMemory + "мб, "
	    		+ "Free Swap Size > " + freeSwapSize + "мб, "
	    		+ "Commited Virtual Memory Size > " + commitedVirtualMemorySize + "мб.");
	}

	
	public static class USER {
		public static String getUSER_LANG() {
			return System.getProperty("user.country") + "_" + System.getProperty("user.language");
		}

		public static String getUSER_HOME() {
			return System.getProperty("user.home");
		}

		public static String getUSER_NAME() {
			return System.getProperty("user.name");
		}

		public static String getUSER_DIR() {
			return System.getProperty("user.dir");
		}
	}

	public static class OS {
		public static String getOS_NAME() {
			return System.getProperty("os.name");
		}

		public static String getOS_ARCH() {
			return System.getProperty("os.arch");
		}

		public static String getOS_VERSION() {
			return System.getProperty("os.version");
		}

		public static String getOS_ENCODE() {
			return System.getProperty("file.encoding") + "_" + System.getProperty("sun.jnu.encoding");
		}

		public static String getOS_TIMEZONE() {
			return System.getProperty("user.timezone");
		}

		public static String getOS_TYPE() {
			return System.getenv().get("OS");
		}
	}

	public static class JAVA {
		public static String VM_NAME() {
			return System.getProperty("java.vm.name") + "\n" + System.getProperty("java.specification.name") + "\n"
					+ System.getProperty("java.runtime.name");
		}

		public static String JAVA_VERSION() {
			return System.getProperty("java.version") + "\n" + System.getProperty("java.runtime.version");
		}

		public static String JAVA_HOME() {
			return System.getProperty("java.home");
		}

		public static String JAVA_ARCH() {
			return System.getProperty("sun.arch.data.model") + "bit";
		}
	}

	public static class CPU {
		public static String getCPU_ARCH() {
			return System.getProperty("sun.arch.data.model") + "bit";
		}

		public static String getCPU_MODEL() {
			return System.getProperty("sun.cpu.isalist").toUpperCase();
		}

		public static String getCPU_NAME() {
			return System.getenv().get("COMPUTERNAME");
		}

		public static int getCPU_CORES() {
			return Runtime.getRuntime().availableProcessors();
		}

		public static String getCPU_IDENTIFIER() {
			return System.getenv().get("PROCESSOR_IDENTIFIER");
		}
	}

	public static class NET {
		public static String NET_HTTP_HOST() {
			return System.getProperty("http.proxyHost");
		}

		public static String NET_HTTPS_HOST() {
			return System.getProperty("https.proxyHost");
		}

		public static String NET_FTP_HOST() {
			return System.getProperty("ftp.proxyHost");
		}

		public static String NET_SOCKS_HOST() {
			return System.getProperty("socksProxyHost");
		}
	}

	public static class SCREEN {
		public static int SCREEN_SIZE_DPI() {
			return Toolkit.getDefaultToolkit().getScreenResolution();
		}

		public static String SCREEN_SIZE() {
			return Toolkit.getDefaultToolkit().getScreenSize().getWidth() + " х "
					+ Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		}
	}
}
