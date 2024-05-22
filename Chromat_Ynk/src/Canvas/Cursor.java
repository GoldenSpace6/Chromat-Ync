package Canvas;

import java.util.Arrays;

import Interpreter.UserObjectValue;
import lexer.Command;

public class Cursor {
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public Cursor() {
	}
	public Cursor(Cursor c) {
		//mimic
	}
	public Cursor(Cursor c,int v1,int v2) {
		//mirror
	}
	public Cursor(Cursor c,int v1,int v2,int v3,int v4) {
		//mirror
	}
	public void execCommand(Command c,UserObjectValue[] valueList) {
		System.out.println(c.toString()+" "+Arrays.toString(valueList));
		
	}
}
