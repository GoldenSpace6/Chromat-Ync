package interpreter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import ihm.Cursor;
import ihm.CursorController;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.canvas.Canvas;
import lexer.Command;
import lexer.InstructionNode;
import lexer.Parser;

public class Interpreter {
	private HashMap<String, UserObjectValue> vars;
	private CursorController cursorController;
	private ObservableMap<Integer, ObservableList<Cursor>> cursors;
	private ObservableList<Cursor> selectedCursor;
    
	private InstructionNode currentInstruction;
	private StringProperty output;
    
    public Interpreter(File f, Canvas canvas, StringProperty output, CursorController cursorController) {
    	this.vars= new HashMap<String, UserObjectValue>();
		this.cursorController = cursorController;
		this.output = output;
    	this.cursors= cursorController.getCursors();
    	Parser parser = new Parser(f);
		parser.parserRec();
		this.currentInstruction = parser.getStartInstruction();
    }
    public InstructionNode getCurrentInstruction() {
		return currentInstruction;
	}

    public void nextStep() throws InterpreterException {

    	UserObjectValue[] args = new UserObjectValue[currentInstruction.getArgs().length];
    	VariableType[][] argumentType=currentInstruction.getCommand().argumentType();
    	Boolean hasArgs=false;
    	for(VariableType[] j:argumentType) {
    		if(j.length==args.length) {
    			hasArgs=true;
	        	for(int i=0;i<j.length;i++) {
	        		//create a Evaluable and eval it to directly get its value;
	        		UserObjectValue t = new UserObjectValue(Evaluable.newEvaluable(currentInstruction.getArgs()[i],j[i]).eval(vars),j[i]);
	        		args[i] = t;
	        	}
    		}
    	}
    	if (hasArgs==false) {
			throw new InterpreterException("Wrong number of arguments for command "+currentInstruction.getCommand()+" ,got "+Arrays.toString(currentInstruction.getArgs()));    		
    	}
    	//----
        System.out.println(currentInstruction.getCommand().toString()+" "+Arrays.toString(args));
		//outputDisplay(currentInstruction.getCommand().toString()+" "+Arrays.toString(args));
    	//----
    	if(currentInstruction.getCommand().isInstructionBlock()) {
    		if( currentInstruction.getCommand()==Command.IF || currentInstruction.getCommand()==Command.WHILE ) {
    			if(args[0].getBoolean()) {
    				currentInstruction = currentInstruction.getConditionInstruction();
    			} else {
    				currentInstruction = currentInstruction.getNextInstruction();
    			}
    		} else if (currentInstruction.getCommand()==Command.MIMIC || currentInstruction.getCommand()==Command.MIRROR) {
    			if (currentInstruction.getHasBeenExecuted()) {
    				//Remove temporary Cursor
    				//jpp
    				currentInstruction = currentInstruction.getNextInstruction();
    			} else {
    				//Create temporary Cursor
    				currentInstruction.setHasBeenExecuted();
    				//jpp
    				currentInstruction = currentInstruction.getConditionInstruction();
    			}
    		} else {
    			outputDisplay("Invalid value for "+currentInstruction);
    			//throw "Invalid value for "+currentInstruction
    		}
    	} else if(currentInstruction.getCommand().isChangingVariable()) {

    		switch (currentInstruction.getCommand()) {
			case CURSOR: {
				if(cursors.containsKey(args[0].getInt()) ) {
	    			throw new InterpreterException("Cursor "+args[0].getInt()+" already exist");
	    		} else {
					cursorController.addCursor(args[0].getInt());
	    			
	    			//select the created cursor
	    			selectedCursor=cursorController.getCursors().get(args[0].getInt());
	    		}
				break;
			}
			case SELECT: {
				if(cursors.containsKey(args[0].getInt())==false) {
	    			throw new InterpreterException("Cursor "+args[0].getInt()+" doesn't exist");
	    		} else {
	    			selectedCursor=cursors.get(args[0].getInt());
	    		}
				break;
			}
			case REMOVE: {
				if(cursors.containsKey(args[0].getInt())==false) {
	    			throw new InterpreterException("Cursor "+args[0].getInt()+" doesn't exist");
	    		} else {
	    			cursors.remove(args[0].getInt());
	    		}
				break;
			}
			case DEL: {
				if(vars.containsKey(args[0].getString())==false) {
	    			throw new InterpreterException("Variable "+args[0].getString()+" doesn't exist");
	    		}
    			vars.remove(args[0].getString());
    			break;
			}
			case NUM:
			case STR:
			case BOOL: {
	    		String varName =  args[0].getString();
	    		
				if(UserObjectValue.isAVariable(varName)==false) {
	    			throw new InterpreterException("Variable "+varName+"is not a valide variable, variable can only contains letters.");
				}
	    		VariableType currentCommand = VariableType.valueOf(currentInstruction.getCommand().toString());
	    		if(vars.containsKey(varName) && vars.get(varName).getReturnType(vars)!=currentCommand) {
	    			throw new InterpreterException("cannot change type from "+vars.get(varName).getReturnType(vars)+" to "+currentCommand);
	    		}
    			vars.put(varName,args[1]);
    			break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + currentInstruction.getCommand());
			}
			currentInstruction = currentInstruction.getNextInstruction();
    	} else if(currentInstruction.getCommand().isCursorCommand()) {
    		if (selectedCursor==null) {
    			throw new InterpreterException("No cursor Selected");
    		}
    		for(Cursor i:selectedCursor) {
    			i.execCommand(currentInstruction.getCommand(), args);
    		}
			currentInstruction = currentInstruction.getNextInstruction();
    	}
    }

	public void outputDisplay(String text) {
        Date objDate = new Date();
        SimpleDateFormat objSDF = new SimpleDateFormat("HH:mm:ss");

        output.set("[" + objSDF.format(objDate) + "] : " + text);
        System.out.println("Output : " + text);
    }
    
}
