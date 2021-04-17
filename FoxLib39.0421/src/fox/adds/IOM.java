package adds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import adds.Out.LEVEL;


public class IOM {
	public static enum HEADERS {REGISTRY, CONFIG, USER_SAVE, LAST_USER, USER_LIST, SECURE, TEMP}
	private static ArrayList<Properties> PropsArray = new ArrayList<Properties>(HEADERS.values().length);
	private static Charset codec = StandardCharsets.UTF_8;
	private static Boolean consoleOut = false;
	private static String DEFAULT_EMPTY_STRING = "none";
	

	public synchronized static void add(Object propertiName, File PropertiFile) {
		String name = propertiName.toString();

		// проверяем не идет ли повторная попытка создания такого же проперчеса...
		for (int i = 0; i < PropsArray.size(); i++) {
			if (PropsArray.get(i).containsKey("propName") && PropsArray.get(i).getProperty("propName").equals(name)) {
				debugOut(IOM.class, LEVEL.ACCENT, "Такой экземпляр уже есть! Перезапись...");
				PropsArray.remove(i);
			}
		}

		debugOut(IOM.class, LEVEL.INFO, "Создание проперчеса " + name + " от файла " + PropertiFile);
		Properties tmp = new Properties(4);

		// проверяем есть ли файл для чтения\записи...
		if (testFileExist(PropertiFile)) {
			try (InputStreamReader ISR = new InputStreamReader(new FileInputStream(PropertiFile), codec)) {
				tmp.load(ISR);
	
				tmp.setProperty("propName", name);
				tmp.setProperty("propFile", PropertiFile.getPath());
	
				PropsArray.add(tmp);
	
				save(name);
				debugOut(IOM.class, LEVEL.INFO, "Cоздание нового потока: " + name + " завершено.");
			} catch (Exception ex) {debugOut(IOM.class, LEVEL.ERROR, "Проблема при чтении файла последнего пользователя lastUserFile!");}
		}
	}
	
	public synchronized static void set(Object propertiName, Object key, Object value) {
		String name = propertiName.toString(), parameter = key.toString();
		
		if (name.isEmpty()) {showWithoutNameErr(name);
		} else if (parameter.equals("")) {showWithoutKeyErr(parameter);
		} else {
			for (int i = 0; i < PropsArray.size(); i++) {
				if (PropsArray.get(i).containsKey("propName")) {
					if (PropsArray.get(i).getProperty("propName").equals(name)) {
						debugOut(IOM.class, LEVEL.INFO, "Запись в проперчес " + name + " параметра " + String.valueOf(value) + "' (" + value.getClass().getTypeName() + ").");
						if (PropsArray.get(i).containsKey(parameter)) {PropsArray.get(i).setProperty(parameter, String.valueOf(value));
						} else {PropsArray.get(i).putIfAbsent(parameter, String.valueOf(value));}
						return;
					}
				}
			}

			showNotExistsErr(name);
		}
	}

	public synchronized static void setIfNotExist(Object propertiName, Object existKey, Object defaultValue) {
		String name = propertiName.toString(), parameter = existKey.toString();
		
		if (name.isEmpty() || name.isBlank()) {showWithoutNameErr(name);
		} else if (parameter.isEmpty() || parameter.isBlank()) {showWithoutKeyErr(parameter);
		} else {
			for (int i = 0; i < PropsArray.size(); i++) {
				if (!PropsArray.get(i).containsKey("propName")) {
					debugOut(IOM.class, LEVEL.ERROR, "Каким-то образом проперчес " + PropsArray.get(i).toString() + " не имеет ключа с именем.");
					continue;
				}
				
				if (PropsArray.get(i).getProperty("propName").equals(name)) {
					if (PropsArray.get(i).containsKey(parameter)) {return;
					} else {set(propertiName, existKey, defaultValue);}
					return;
				}
			}

			showNotExistsErr(name);
		}
	}
	
	
	public synchronized static Boolean getBoolean(Object propertiName, Object key) {
		if (!existProp(propertiName.toString())) {
			showNotExistsErr(propertiName.toString());
			return false;
		}
		
		try {
			int ind = getPropIndex(propertiName.toString());
			debugOut(IOM.class, LEVEL.INFO, "Конфигурация найдена. Чтение флага " + key.toString() + "...");				
			PropsArray.get(ind).putIfAbsent(key.toString(), "false");
			
			debugOut(IOM.class, LEVEL.INFO, "Возврат флага " + PropsArray.get(ind).getProperty(key.toString()) + ".");				
			return Boolean.valueOf(PropsArray.get(ind).getProperty(key.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			debugOut(IOM.class, LEVEL.ERROR, "Параметр '" + key + "' не является типом Boolean. (" + key.getClass() + ")");
			return false;
		}
	}
	
	public synchronized static Double getDouble(Object propertiName, Object key) {
		if (!existProp(propertiName.toString())) {
			showNotExistsErr(propertiName.toString());
			return -1D;
		}
		
		int ind = -1;		
		try {
			ind = getPropIndex(propertiName.toString());
			if (!existKey(propertiName.toString(), key.toString())) {
				PropsArray.get(ind).putIfAbsent(key.toString(), "-1D");
				save(propertiName.toString());
			}
			return Double.parseDouble(PropsArray.get(ind).getProperty(key.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			debugOut(IOM.class, LEVEL.ERROR, "Параметр '" + key + "' не является типом Double. (" + key.getClass() + ")" + (ind == -1 ? "" : " (value: " + PropsArray.get(ind).getProperty(key.toString()) + ")."));
		}
		
		return null;
	}
	
	public synchronized static String getString(Object propertiName, Object key) {
		if (!existProp(propertiName.toString())) {
			showNotExistsErr(propertiName.toString());
			return null;
		}
		
		int ind = getPropIndex(propertiName.toString());
		if (!existKey(propertiName.toString(), key.toString())) {
			PropsArray.get(ind).putIfAbsent(key.toString(), DEFAULT_EMPTY_STRING);
			save(propertiName.toString());
		}
		return PropsArray.get(ind).getProperty(key.toString());
	}
	
	public synchronized static Integer getInt(Object propertiName, Object key) {
		if (!existProp(propertiName.toString())) {
			showNotExistsErr(propertiName.toString());
			return -1;
		}
		
		try {
			int ind = getPropIndex(propertiName.toString());
			Properties tmp = PropsArray.get(ind);
			if (!existKey(propertiName.toString(), key.toString())) {
				tmp.putIfAbsent(key.toString(), "-1");
				save(propertiName.toString());
			}
			
			return Integer.parseInt(tmp.getProperty(key.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			debugOut(IOM.class, LEVEL.ERROR, "Параметр '" + key + "' не является типом Integer. (" + key.getClass() + ")");
		}
		
		return null;
	}

	public synchronized static void remove(Object propertiName, Object _key) {
		String key = _key.toString();
		
		if (propertiName.toString().isEmpty() || propertiName.toString().isBlank()) {showWithoutNameErr(propertiName.toString());
		} else {
			int propCount = -1;
			for (int i = 0; i < PropsArray.size(); i++) {
				if (PropsArray.get(i).containsKey("propName")) {
					if (PropsArray.get(i).getProperty("propName").equals(propertiName.toString())) {
						debugOut(IOM.class, LEVEL.ACCENT, "Удаление из проперчес " + propertiName.toString() + " параметра " + key + "'.");
						propCount = i;
						break;
					}
				}
			}

			if (propCount != -1) {
				if (PropsArray.get(propCount).containsKey(key)) {PropsArray.get(propCount).remove(key);}
			} else {showNotExistsErr(propertiName.toString());}
		}
	}
	
	
	public synchronized static Boolean save(Object propertiName) {
		for (Properties properties : PropsArray) {
			if (properties.get("propName").equals(propertiName.toString())) {
				try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(properties.getProperty("propFile")), false), codec)) {
					properties.store(osw, "IOM_SAVE");
					debugOut(IOM.class, LEVEL.INFO, "Сохранение " + properties.getProperty("propFile") + " завершено!");
					return true;
				} catch (IOException e) {
					showLoadStreamErr(properties);
					e.printStackTrace();
					return false;
				}
			}
		}
		
		debugOut(IOM.class, LEVEL.WARN, "Не найден поток " + propertiName.toString() + ".");
		return false;
	}

	public synchronized static void saveAll() {
		debugOut(IOM.class, LEVEL.INFO, "Каскадное сохранение всех файлов...");
		for (Properties properties : PropsArray) {save(properties);}
	}

	
	public synchronized static Boolean load(Object propertiName) {
		for (Properties properties : PropsArray) {
			if (properties.get("propName").equals(propertiName.toString())) {
				try (InputStreamReader ISR = new InputStreamReader(new FileInputStream(new File(properties.getProperty("propFile"))), codec)) {
					debugOut(IOM.class, LEVEL.INFO, "Загрузка файла " + propertiName.toString() + " в поток " + properties.getProperty("propName"));
					properties.load(ISR);
					return true;
				} catch (IOException e) {
					debugOut(IOM.class, LEVEL.ERROR, "Проблема с загрузкой потока " + properties.getProperty("propName") + "!");
					e.printStackTrace();
					break;
				}				
			}
		}

		return false;
	}

	public synchronized static void loadAll() {
		debugOut(IOM.class, LEVEL.INFO, "Каскадная загрузка всех файлов (перезагрузка проперчесов)...");
		for (Properties properties : PropsArray) {load(properties);}
	}

	
	private static void showNotExistsErr(Object data) {debugOut(IOM.class, LEVEL.ERROR, "Не найден поток '" + data + "'.");}
	private static void showWithoutNameErr(Object data) {Out.Print(IOM.class, LEVEL.ERROR, "Запись в проперчес имени невозможна: " + data);}
	private static void showWithoutKeyErr(Object data) {Out.Print(IOM.class, LEVEL.ERROR, "Запись в проперчес ключа невозможна: " + data);}
	private static void showLoadStreamErr(Object data) {debugOut(IOM.class, LEVEL.ERROR, "Проблема с выгрузкой потока " + data + " в файл!");}
	
	
	public synchronized static Boolean existProp(String propertiName) {
		for (Properties properti : PropsArray) {
			if (properti.get("propName").equals(propertiName.toString())) {return true;}
		}
		return false;
	}
	
	public synchronized static Boolean existKey(String propertiName, String key) {
		for (Properties properties : PropsArray) {
			if (properties.get("propName").equals(propertiName.toString())) {
				if (properties.containsKey(key)) {return true;}
			}
		}

		return false;
	}
	
	public synchronized static int getPropIndex(String propertiName) {
		for (Properties properti : PropsArray) {
			if (properti.get("propName").equals(propertiName)) {return PropsArray.indexOf(properti);}
		}
		return -1;
	}
	
	
	public synchronized static String headersList() {
		ArrayList<String> propsNames = new ArrayList<String>(PropsArray.size());
		for (Properties properties : PropsArray) {propsNames.add(properties.getProperty("propName"));}
		return Arrays.toString(propsNames.toArray());
	}

	private static Boolean testFileExist(File file) {
		File parentDir = file.getParentFile();
		while (!parentDir.exists()) {
			debugOut(IOM.class, LEVEL.ACCENT, "Попытка создания директории '" + parentDir + "'...");
			
			try {parentDir.mkdirs();
			} catch (Exception e0) {
				e0.printStackTrace();
				return false;
			}
		}
		
		while (!file.exists()) {
			debugOut(IOM.class, LEVEL.ACCENT, "Попытка создания файла '" + file + "'...");
			
			try {file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public static void setDefaultEmptyString(String des) {DEFAULT_EMPTY_STRING = des;}
	
	public static Boolean isConsoleOutOn() {return consoleOut;}
	public static void setConsoleOutOn(Boolean onOff) {consoleOut = onOff;}
	
	private static void debugOut(Class<?> address, LEVEL lvl, String message) {
		if (consoleOut) {Out.Print(address, lvl, message);}
	}
}