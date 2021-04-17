package ru.mail.angelicalis39.GeekBrainsReflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

import annotations.AfterSuite;
import annotations.BeforeSuite;
import annotations.Priora;


public class Tester {	
	
	public static void start(String className) {
		try {start(Class.forName(className));
		} catch (ClassNotFoundException e) {e.printStackTrace();}
	}

	@SuppressWarnings("rawtypes")
	public static void start(Class c) {
		System.out.println("Starts with '" + c.getSimpleName() + "':");
		System.out.println();
		
		try {
			Method[] methods = c.getDeclaredMethods();
			ArrayList<Method> list = new ArrayList<Method>();
			
			for (Method m : methods) {
				if (m.isAnnotationPresent(Priora.class)) {
					int prio = m.getAnnotation(Priora.class).prioritet();
					if (prio < 0 || prio > 10) {throw new RuntimeException("Priority can`t be less than 0 or more 10!");}
					list.add(m);
				}
			}
			
			list.sort(new Comparator<Method>() {
				public int compare(Method o1, Method o2) {
					return o1.getAnnotation(Priora.class).prioritet() - o2.getAnnotation(Priora.class).prioritet();
				}				
			});
			
			for (Method m : methods) {
				if (m.isAnnotationPresent(BeforeSuite.class)) {
					if (list.get(0).isAnnotationPresent(BeforeSuite.class)) {throw new RuntimeException("BeforeSuite must not exists more than 1!");}
					list.add(0, m);
				}
				
				if (m.isAnnotationPresent(AfterSuite.class)) {
					if (list.get(list.size() - 1).isAnnotationPresent(AfterSuite.class)) {throw new RuntimeException("AfterSuite must not exists more than 1!");}
					list.add(m);
				}
			}
			
			for (Method m : list) {
				try {m.invoke(null);
				} catch (IllegalAccessException e) {e.printStackTrace();
				} catch (IllegalArgumentException e) {e.printStackTrace();
				} catch (InvocationTargetException e) {e.printStackTrace();}
			}
		} catch (SecurityException e) {e.printStackTrace();}
		
		System.out.println();
		System.out.println("Well done!");
	}
}