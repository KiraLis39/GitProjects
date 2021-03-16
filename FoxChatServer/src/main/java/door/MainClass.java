package door;

import gui.MonitorFrame;
import server.Server;


public class MainClass {
	
	public static void main(String[] args) {
		new Server(); // Инициализация и подготовка к работе класса "Сервер".
		new MonitorFrame(); // Запуск фрейма "Монитор" и старт сервера.
	}
}