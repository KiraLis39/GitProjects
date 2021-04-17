package ru.mail.angelicalis39.GeekBrainsReflection;

import annotations.AfterSuite;
import annotations.BeforeSuite;
import annotations.Priora;
import annotations.Test;

public class ExampleClass {
	
	@BeforeSuite
	public static void init() {System.out.println("Initialization...");}
	
	@Test
	@Priora(prioritet = 1)
	static void testingFirst() {
		System.out.println("First test accomplished.");
	}
	
	@Test
	@Priora(prioritet = 2)
	static void testingSecond() {
		System.out.println("Second test accomplished.");
	}
	
	@Test
	@Priora(prioritet = 3)
	static void testingOthers() {
		System.out.println("Other tests...");
	}
	
	@AfterSuite
	public static void finalizer() {System.out.println("End of test.");}
}
