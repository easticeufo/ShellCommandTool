package com.madongfang.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class TestCommands {

	@ShellMethod("print the parameter number")
	public String testPrintNumber(int number) {
		return "test number is " + number;
	}
}
