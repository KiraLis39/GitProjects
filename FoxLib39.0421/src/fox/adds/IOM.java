package fox.adds;

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


public class IOM {
	public static enum HEADERS {REGISTRY, CONFIG, USER_SAVE, LAST_USER, USER_LIST, SECURE, TEMP}
	private static ArrayList<Properties> PropsArray = new ArrayList<Properties>(HEADERS.values().length);
	private static Charset codec = StandardCharsets.UTF_8;
	private static Boolean debug = false;
	private static String DEFAULT_EMPTY_STRING = "none";
	

	public synchronized static void add(Object propertiName, File PropertiFile) {
		String name = propertiName.toString();

		// проверяем не идет ли повторная попытка создания такого же проперчеса...
		for (int i = 0; i < PropsArray.size(); i++) {
			if (PropsArray.get(i).containsKey("propName") && PropsArray.get(i).getProperty("propName").equals(name)) {
				debugOut(IOM.class, 2, "Такой экземпляр уже есть! Перезапись...");
				PropsArray.remove(i);
			}
		}

		debugOut(IOM.class, 1, "Создание проперчеса " + name + " от файла " + PropertiFile);
		Properties tmp = new Properties(4);

		// проверяем есть ли файл для чтения\записи...
		if (testFileExist(PropertiFile)) {
			try (InputStreamReader ISR = new InputStreamReader(new FileInputStream(PropertiFile), codec)) {
				tmp.load(ISR);
	
				tmp.setProperty("propName", name);
				tmp.setProperty("propFile", PropertiFile.getPath());
	
				PropsArray.add(tmp);
	
				save(name);
				debugOut(IOM.class, 0, "Cоздание нового потока: " + name + " завершено.");
			} catch (Exception ex) {debugOut(IOM.class, 3, "Проблема при чтении файла последнего пользователя lastUserFile!");}
		}
	}
	
	public synchronized static void set(Object propertiName, Object key, Object value) {
		String name = propertiName.toString(), parameter = key.toString();
		
		if (name.isEmpty()) {Out.Print(IOM.class, 3, "Запись в проперчес без имени не возможна!");
		} else if (parameter.equals("")) {Out.Print(IOM.class, 3, "Запись в проперчес пустого ключа не возможна!");
		} else {
			for (int i = 0; i < PropsArray.size(); i++) {
				if (PropsArray.get(i).containsKey("propName")) {
					if (PropsArray.get(i).getProperty("propName").equals(name)) {
						debugOut(IOM.class, 0, "Запись в проперчес " + name + " параметра " + String.valueOf(value) + "' (" + value.getClass().getTypeName() + ").");
						if (PropsArray.get(i).containsKey(parameter)) {PropsArray.get(i).setProperty(parameter, String.valueOf(value));
						} else {PropsArray.get(i).putIfAbsent(parameter, String.valueOf(value));}
						return;
					}
				}
			}

			debugOut(IOM.class, 2, "Не найден поток " + name);
		}
	}

	public synchronized static void setIfNotExist(Object propertiName, Object existKey, Object defaultValue) {
		String name = propertiName.toString(), parameter = existKey.toString();
		
		if (name.isEmpty()) {Out.Print(IOM.class, 3, "Запись в проперчес без имени не возможна!");
		} else if (parameter.equals("")) {Out.Print(IOM.class, 3, "Запись в проперчес пустого ключа не возможна!");
		} else {
			for (int i = 0; i < PropsArray.size(); i++) {
				if (!PropsArray.get(i).containsKey("propName")) {
					debugOut(IOM.class, 3, "Каким-то образом проперчес " + PropsArray.get(i).toString() + " не имеет ключа с именем.");
					continue;
				}
				
				if (PropsArray.get(i).getProperty("propName").equals(name)) {
					if (PropsArray.get(i).containsKey(parameter)) {return;
					} else {
						debugOut(IOM.class, 0, "Запись в проперчес " + name + " параметра " + String.valueOf(defaultValue) + "' (" + defaultValue.getClass().getTypeName() + ").");
						PropsArray.get(i).put(parameter, String.valueOf(defaultValue));}
					return;
				}
			}

			debugOut(IOM.class, 2, "Не найден поток " + name);
		}
	}
	
	
	public synchronized static Boolean getBoolean(Object propertiName, Object key) {
		try {
			debugOut(IOM.class, 0, "Поиск конфигурации " + propertiName.toString() + "...");
			
			int ind;
			if ((ind = exist(propertiName.toString())) != -1) {
				debugOut(IOM.class, 0, "Конфигурация найдена. Чтение флага " + key.toString() + "...");				
				PropsArray.get(ind).putIfAbsent(key.toString(), "false");
				
				debugOut(IOM.class, 0, "Возврат флага " + PropsArray.get(ind).getProperty(key.toString()) + ".");				
				return Boolean.valueOf(PropsArray.get(ind).getProperty(key.toString()));
			} else {
				debugOut(IOM.class, 3, "Не найден поток " + propertiName.toString() + ".");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			debugOut(IOM.class, 3, "Параметр '" + key + "' не является типом Boolean. (" + key.getClass() + ")");
		}
		
		return false;
	}
	
	public synchronized static Double getDouble(Object propertiName, Object key) {
		int ind = -1;
		
		try {
			if ((ind = exist(propertiName.toString())) != -1) {
				if (!existKey(propertiName.toString(), key.toString())) {
					PropsArray.get(ind).putIfAbsent(key.toString(), "-1D");
					save(propertiName.toString());
				}
				return Double.parseDouble(PropsArray.get(ind).getProperty(key.toString()));
			} else {debugOut(IOM.class, 3, "Не найден поток " + propertiName.toString() + ".");}
		} catch (Exception e) {
			e.printStackTrace();
			debugOut(IOM.class, 3, "Параметр '" + key + "' не является типом Double. (" + key.getClass() + ")" + (ind == -1 ? "" : " (value: " + PropsArray.get(ind).getProperty(key.toString()) + ")."));
		}
		
		return null;
	}
	
	public synchronized static String getString(Object propertiName, Object key) {
		int ind;
		if ((ind = exist(propertiName.toString())) != -1) {
			if (!existKey(propertiName.toString(), key.toString())) {
				PropsArray.get(ind).putIfAbsent(key.toString(), DEFAULT_EMPTY_STRING);
				save(propertiName.toString());
			}
			return PropsArray.get(ind).getProperty(key.toString());
		} else {debugOut(IOM.class, 3, "Не найден поток '" + propertiName.toString() + "'.");}
		
		return null;
	}
	
	public synchronized static Integer getInt(Object propertiName, Object key) {
		try {
			int ind;
			if ((ind = exist(propertiName.toString())) != -1) {
				Properties tmp = PropsArray.get(ind);
				if (!existKey(propertiName.toString(), key.toString())) {
					tmp.putIfAbsent(key.toString(), "-1");
					save(propertiName.toString());
				}
				return Integer.parseInt(tmp.getProperty(key.toString()));
			} else {debugOut(IOM.class, 3, "Не найден поток " + propertiName.toString() + ".");}
		} catch (Exception e) {
			e.printStackTrace();
			debugOut(IOM.class, 3, "Параметр '" + key + "' не является типом Integer. (" + key.getClass() + ")");
		}
		
		return null;
	}

	public synchronized static void remove(Object propertiName, Object _key) {
		String name = propertiName.toString(), key = _key.toString();
		
		if (name.isEmpty()) {Out.Print(IOM.class, 3, "Запись в проперчес без имени не возможна!");
		} else {
			int propCount = -1;
			for (int i = 0; i < PropsArray.size(); i++) {
				if (PropsArray.get(i).containsKey("propName")) {
					if (PropsArray.get(i).getProperty("propName").equals(name)) {
						debugOut(IOM.class, 0, "Удаление из проперчес " + name + " параметра " + key + "'.");
						propCount = i;
						break;
					}
				}
			}

			if (propCount != -1) {
				if (PropsArray.get(propCount).containsKey(key)) {PropsArray.get(propCount).remove(key);}
			} else {debugOut(IOM.class, 2, "Не найден поток " + name);}
		}
	}
	
	
	public synchronized static Boolean save(Object propertiName) {
		for (Properties properties : PropsArray) {
			if (properties.get("propName").equals(propertiName.toString())) {
				try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(properties.getProperty("propFile")), false), codec)) {
					properties.store(osw, "IOM: save()");
					debugOut(IOM.class, 0, "Сохранение " + properties.getProperty("propFile") + " завершено!");
					return true;
				} catch (IOException e) {
					debugOut(IOM.class, 3, "Проблема с выгрузкой потока " + properties.getProperty("propName") + " в файл!");
					e.printStackTrace();
					return false;
				}
			}
		}
		
		debugOut(IOM.class, 3, "Не найден поток " + propertiName.toString() + ".");
		return false;
	}

	public synchronized static void saveAll() {
		debugOut(IOM.class, 0, "Каскадное сохранение всех файлов...");

		for (Properties properties : PropsArray) {
			try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(properties.getProperty("propFile")), false), codec)) {
				properties.store(osw, "IOM: saveAll():");
				debugOut(IOM.class, 0, "Сохранение потока " + properties.getProperty("propName") + " в файл завершено!");
			} catch (Exception ex) {
				debugOut(IOM.class, 3, "Проблема с выгрузкой потока " + properties.getProperty("propName") + " в файл!");
				ex.printStackTrace();
			}
		}
	}

	
	public synchronized static Boolean load(String propertiName) {
		String name = propertiName.toString();
		
		for (Properties properties : PropsArray) {
			if (properties.get("propName").equals(name)) {
				try (InputStreamReader ISR = new InputStreamReader(new FileInputStream(new File(properties.getProperty("propFile"))), codec)) {
					debugOut(IOM.class, 0,	"Загрузка файла " + name + " в поток " + properties.getProperty("propName"));
					properties.load(ISR);
					return true;
				} catch (IOException e) {
					debugOut(IOM.class, 3,	"Проблема с загрузкой потока " + properties.getProperty("propName") + "!");
					e.printStackTrace();
					break;
				}				
			}
		}

		return false;
	}

	public synchronized static Boolean loadAll() {
		debugOut(IOM.class, 0, "Каскадная загрузка всех файлов (перезагрузка проперчесов)...");

		for (Properties properties : PropsArray) {
			try (InputStreamReader ISR = new InputStreamReader(new FileInputStream(new File(properties.getProperty("propFile"))), codec)) {
				properties.load(ISR);
				debugOut(IOM.class, 0, "Загрузка потока " + properties.getProperty("propName") + " из файла успешно завершена!");
			} catch (Exception ex) {
				debugOut(IOM.class, 3, "Проблема с загрузкой потока из файла!");
				ex.printStackTrace();
				return false;
			}
		}

		return true;
	}

	
	public synchronized static int exist(String propertiName) {
		for (Properties properti : PropsArray) {
			if (properti.get("propName").equals(propertiName.toString())) {return PropsArray.indexOf(properti);}
		}

		return -1;
	}
	
	public synchronized static Boolean existKey(String propertiName, String key) {
		for (Properties properties : PropsArray) {
			if (properties.get("propName").equals(propertiName.toString())) {
				if (properties.containsKey(key)) {return true;}
			}
		}

		return false;
	}
	
	public synchronized static String headersList() {
		ArrayList<String> propsNames = new ArrayList<String>(PropsArray.size());
		for (Properties properties : PropsArray) {propsNames.add(properties.getProperty("propName"));}

	return Arrays.toString(propsNames.toArray());
	}

	private static Boolean testFileExist(File file) {
		File parentDir = file.getParentFile();
		while (!parentDir.exists()) {
			debugOut(IOM.class, 1, "Попытка создания директории '" + parentDir + "'...");
			
			try {parentDir.mkdirs();
			} catch (Exception e0) {
				e0.printStackTrace();
				return false;
			}
		}
		
		while (!file.exists()) {
			debugOut(IOM.class, 1, "Попытка создания файла '" + file + "'...");
			
			try {file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public static void setDefaultEmptyString(String des) {DEFAULT_EMPTY_STRING = des;}
	
	public static Boolean isDebugOn() {return debug;}
	public static void setDebugOn(Boolean onOff) {debug = onOff;}
	
	private static void debugOut(Class<?> address, int code, String message) {
		if (debug) {
			Out.Print(address, code, message);
			if (code == 3) {throw new RuntimeException("debugOut: Error by message-code '3'. Check it please.");}
		}
	}

}