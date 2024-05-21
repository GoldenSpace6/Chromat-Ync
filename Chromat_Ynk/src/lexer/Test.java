package lexer;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		/**Instruction[] ii= {new Instruction(Command.FWD,new String[]{"5"}),
				new Instruction(Command.MOV,new String[]{"10", "15"}),
				new Instruction(Command.WHILE,new String[]{"A", "==", "B"}),
				new Instruction(Command.COLOR,new String[]{"255", "255", "0"}),
				new Instruction(Command.END,new String[]{})};
		*/
		Parser main = new Parser(new File("src/lexer/fileTest"));
		
		main.parserRec();
		
		//print the code line by line by waiting for the user 
		//if the user enter i, it will enter the instruction block 
		//if the user enter e, the program will stop
		Scanner s = new Scanner(System.in);
		String str="";
		while(main.startInstruction!=null && (str.equals("e")==false) ) {
			System.out.print(main.startInstruction+" | ");
			str=s.next();
			if (str.equals("i") && main.startInstruction.getConditionInstruction()!=null) {
				main.startInstruction = main.startInstruction.getConditionInstruction();
			} else {
				main.startInstruction = main.startInstruction.getNextInstruction();
			}
		}
		s.close();
		/*//String[] boolExprSimple = {"TRUE","&&","TRUE","!","FALSE","||","FALSE","&&","TRUE"};
		String[] boolExprSimple = {"5","<","6","&&","a",">=","B","||","abc"};
		BoolExpression boolExpr = new BoolExpression(boolExprSimple);
		boolExpr.lexer();
		System.out.print(boolExpr);
		*///System.out.println(main.startInstruction.getNextInstruction().getNextInstruction().getConditionInstruction().getNextInstruction());
		
	}
}
