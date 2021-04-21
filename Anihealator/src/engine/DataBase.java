package engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import fox.adds.Out;
import registry.Registry;


public class DataBase {
	public static Connection conn;
	
	private static String[][] allData;
	private static int elementsCount;
	
	
	public void load() {
		try {
			Conn();
			CreateDB();
		} catch (ClassNotFoundException | SQLException e) {e.printStackTrace();}
	}
	
	// --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
	public void Conn() throws ClassNotFoundException, SQLException {
		if (conn != null) {
			conn.close();
			conn = null;
		}
		
	    Class.forName("org.sqlite.JDBC");
	    conn = DriverManager.getConnection("jdbc:sqlite:data\\db.db");	   
	    Out.Print(DataBase.class, 1, "База 'db.db' Подключена!");
	}
	
	// --------Создание таблицы--------
	public void CreateDB() throws ClassNotFoundException, SQLException {
		Statement statmt = conn.createStatement();
		
		try {
	//		PRAGMA foreign_keys = 0;
			
			statmt.execute("CREATE TABLE if not exists 'type' (" + 
					" 'id' INTEGER PRIMARY KEY AUTOINCREMENT," + 
					" 'typename' STRING (32) UNIQUE NOT NULL" + 
					");");
			
//			"'modificКРС', 'modificМРС', 'modificHrs', 'modificPig', 'modificPAn', 'modificBrd', 'modificDgz', 'modificCts'
			statmt.execute("CREATE TABLE if not exists 'aids' ("
					+ "'id' INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "'name' STRING (32) UNIQUE ON CONFLICT REPLACE NOT NULL, "
					+ "'type' INT REFERENCES type (id), "
					+ "'description' TEXT, "
					
					+ "'modificКРС' DOUBLE NOT NULL DEFAULT (0), "
					+ "'modificМРС' DOUBLE NOT NULL DEFAULT (0), "
					+ "'modificHrs' DOUBLE NOT NULL DEFAULT (0), "
					+ "'modificPig' DOUBLE NOT NULL DEFAULT (0), "
					+ "'modificPAn' DOUBLE NOT NULL DEFAULT (0), "
					+ "'modificBrd' DOUBLE NOT NULL DEFAULT (0), "
					+ "'modificDgz' DOUBLE NOT NULL DEFAULT (0), "
					+ "'modificCts' DOUBLE NOT NULL DEFAULT (0), "
					
					+ "'picpath' STRING (64)"
					+ ");");
			
	//		PRAGMA foreign_keys = 1;
			Out.Print(DataBase.class, 0, "Таблицы 'type' или 'aids' созданы или уже существуют.");
		} catch (SQLException e) {e.printStackTrace();}
		
		statmt.close();
		
		getElementsData();
	}
	
	public static void addNewData(String[] newData) {
		if (newData == null) {return;}
		
		if (newData[0].isEmpty() || newData[1].isEmpty()) {
			System.out.println("Не все поля были заполнены. Отбой.");
			 JOptionPane.showMessageDialog(null, 
					 "<html><b><h2 color='RED'>Операция отменена!</h2></b><br>Не достаточно данных для создания<br>новой карточки элемента.", "Внимание!",
					JOptionPane.ERROR_MESSAGE, Registry.messageIcon);
			return;
		}
		
		int tInt = -1;
		
		try {
			Statement statmt = conn.createStatement();
			tInt = statmt.executeQuery("SELECT * FROM type WHERE typename = '" + newData[0] + "';").getInt("id");
			
			if (tInt < 0) {
				System.err.println("DataBase: addNewData(): ERR: tInt is " + tInt);
				statmt.close();
				return;
			}

			try {
				statmt.execute("INSERT INTO 'aids' ("
				   + "'name', 'type', 'description', "
				   + "'modificКРС', 'modificМРС', 'modificHrs', 'modificPig', 'modificPAn', 'modificBrd', 'modificDgz', 'modificCts', 'picpath'"
				   + ") VALUES ("
				   + "'" + newData[1] + "', '" + tInt + "', '" + newData[2] + "', '" 
				   + newData[5] + "', '" + newData[6] + "', '" + newData[7] + "', '" + newData[8] + "', '" + newData[9] + "', '" 
				   + newData[10] + "', '" + newData[11] + "', '" + newData[12] + "', '" 
				   + newData[3] + "'); ");
			} catch (SQLException e) {
				Out.Print(DataBase.class, 1, "Не удалось создать элемент. Возможно, он уже есть.");
				Out.Print(DataBase.class, 1, "Попытка обновить запись id #" + newData[5]);
				
				Statement updStatmt = conn.createStatement();				
				updStatmt.execute("UPDATE aids SET "
						+ "name='" + newData[1] + "', "
						+ "type='" + tInt + "', "
						+ "description='" + newData[2] + "', "
						+ "modific='" + newData[3] + "', "
						+ "picpath='" + newData[4] + "' "
						+ "WHERE id=" + newData[5] + ";");
				
				updStatmt.close();
			} finally {statmt.close();}
			
			getElementsData();
		} catch (SQLException e) {e.printStackTrace();}
	}
		
	// -------- Удаление элемента --------
	public static void removeFromDB(String removableItemName) {
		try {
			Statement statmt = conn.createStatement();
			int correct = statmt.executeQuery("SELECT EXISTS (SELECT * FROM aids WHERE name='" + removableItemName + "' LIMIT 1);").getInt(1);
			
			if (correct != 0) {
				statmt.execute("DELETE FROM aids WHERE aids.name='" + removableItemName + "';");
				JOptionPane.showConfirmDialog(null, 
						"Удаление препарата '" + removableItemName + "' завершено.", 
						"Выполнено!", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showConfirmDialog(null, 
						"Не существует '" + removableItemName + "' в базе!", 
						"Ошибка:", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
			}
			
			statmt.close();
		} catch (SQLException e) {e.printStackTrace();}		
	}
	
	// --------Закрытие--------
	public void CloseDB() throws ClassNotFoundException, SQLException {
		conn.close();
		
		Out.Print(DataBase.class, 1, "Соединение c DB завершено.");
	}

	
	public static ArrayList<String> getTypeList() {
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			Statement tmp = conn.createStatement();
			ResultSet resSet = tmp.executeQuery("SELECT * FROM 'type';");
			while(resSet.next())	{result.add(resSet.getString("typename"));}
			resSet.close();
			tmp.close();
		} catch (SQLException e) {e.printStackTrace();}
		
		return result;
	}
	
	public static ArrayList<String> getIndexKeySet() {
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			Statement tmp = conn.createStatement();
			ResultSet resSet = tmp.executeQuery("SELECT name FROM aids;");
			while(resSet.next())	{result.add(resSet.getString(1));}
			resSet.close();
			tmp.close();
		} catch (SQLException e) {e.printStackTrace();}
		
		return result;
	}

	public static String[][] getElementsData() {
		try {
			Statement tmp = conn.createStatement();
			ResultSet resSet = tmp.executeQuery("SELECT 'id' FROM 'aids';");
			
			elementsCount = 0;
			while(resSet.next())	{elementsCount++;}
			allData = new String[elementsCount][14];
			
			int c = 0;
			resSet = tmp.executeQuery("SELECT * FROM 'aids';");
			while(resSet.next())	{
				int id = resSet.getInt("id");
				int type = resSet.getInt("type");
				
				if (type < 0) {
					System.err.println("DataBase: getElementsData(): ERR: type is " + type);
					break;
				}
				
				allData[c][0] = String.valueOf(id);
				Statement tmp2 = conn.createStatement();
				allData[c][1] = tmp2.executeQuery("SELECT * FROM type WHERE id=" + type + ";").getString("typename");
				tmp2.close();
				
				allData[c][2] = resSet.getString("name");
				allData[c][3] = resSet.getString("description");
				allData[c][4] = resSet.getString("picpath");
//				"'modificКРС', 'modificМРС', 'modificHrs', 'modificPig', 'modificPAn', 'modificBrd', 'modificDgz', 'modificCts'
				allData[c][5] = String.valueOf(resSet.getDouble("modificКРС"));
				allData[c][6] = String.valueOf(resSet.getDouble("modificМРС"));
				allData[c][7] = String.valueOf(resSet.getDouble("modificHrs"));
				allData[c][8] = String.valueOf(resSet.getDouble("modificPig"));
				allData[c][9] = String.valueOf(resSet.getDouble("modificPAn"));
				allData[c][10] = String.valueOf(resSet.getDouble("modificBrd"));
				allData[c][11] = String.valueOf(resSet.getDouble("modificDgz"));
				allData[c][12] = String.valueOf(resSet.getDouble("modificCts"));

				c++;
			}
			
			tmp.close();
			resSet.close();
		} catch (SQLException e) {e.printStackTrace();}
		
		return allData;
	}

	public static String[] getElement(int i) {return allData[i];}

	public static String[] getElement(String chosenItemName) {
		int i = -1;
		
		for (int j = 0; j < allData.length; j++) {
			if (allData[j][2].equalsIgnoreCase(chosenItemName)) {
				i = j;
				break;
			}
		}
		
		if (i == -1) {return null;}
		return allData[i];
	}

	public static int getElementsCount() {return elementsCount;}
}