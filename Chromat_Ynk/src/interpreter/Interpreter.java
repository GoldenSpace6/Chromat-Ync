package interpreter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import ihm.Cursor;
import ihm.CursorController;
import javafx.beans.property.DoubleProperty;
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
    private DoubleProperty timeBetweenFrames;
	private StringProperty output;
    
    public Interpreter(File f, Canvas canvas, DoubleProperty timeBetweenFrames, StringProperty output, CursorController cursorController) {
    	this.vars= new HashMap<String, UserObjectValue>();
		this.cursorController = cursorController;
		this.timeBetweenFrames = timeBetweenFrames;
		this.output = output;
    	this.cursors= cursorController.getCursors();
    	Parser parser = new Parser(f);
		parser.parserRec();
		this.currentInstruction = parser.getStartInstruction();
    }
    public InstructionNode getCurrentInstruction() {
		return currentInstruction;
	}

    public void nextStep() {

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
			outputDisplay("Wrong number of arguments, got "+currentInstruction.getArgs());
			//throw "Wrong number of arguments, got "+currentInstruction.getArgs()
    		
    	}
    	//----

		outputDisplay(currentInstruction.getCommand().toString()+" "+Arrays.toString(args));
    	//----
    	
    	if(currentInstruction.getCommand().isCondition()) {
    		if( currentInstruction.getCommand()==Command.IF || currentInstruction.getCommand()==Command.WHILE ) {
    			if((boolean) args[0].getValue()) {
    				currentInstruction = currentInstruction.getConditionInstruction();
    			} else {
    				currentInstruction = currentInstruction.getNextInstruction();
    			}
    		} else if (currentInstruction.getCommand()==Command.FOR) {
    			// ¯\_(ツ)_/¯
    		} else {
    			outputDisplay("Invalid value for "+currentInstruction);
    			//throw "Invalid value for "+currentInstruction
    		}
    	} else if(currentInstruction.getCommand().isChangingVariable()) {

    		switch (currentInstruction.getCommand()) {
			case CURSOR: {
				if(cursors.containsKey(args[0].getValue())) {
	    			outputDisplay("Cursor "+args[0].getValue()+" already exist");
	    			//throw "Cursor "+args[0].getValue()+" already exist"
	    		} else {
					cursorController.addCursor((int) Math.round((double)(args[0].getValue())));
	    			
	    			//select the created cursor
	    			selectedCursor=cursorController.getCursors().get((int) Math.round((double)args[0].getValue()));
	    		}
				break;
			}
			case SELECT: {
				if(cursors.containsKey(args[0].getValue())==false) {
	    			outputDisplay("Cursor "+args[0].getValue()+" doesn't exist");
	    			//throw "Cursor "+args[0].getValue()+" doesn't exist"
	    		} else {
	    			selectedCursor=cursors.get((int) args[0].getValue());
	    		}
				break;
			}
			case REMOVE: {
				if(cursors.containsKey(args[0].getValue())==false) {
	    			outputDisplay("Cursor "+args[0].getValue()+" doesn't exist");
	    			//throw "Cursor "+args[0].getValue()+" doesn't exist"
	    		} else {
	    			cursors.remove((int) args[0].getValue());
	    		}
				break;
			}
			case MIRROR: {
				if(args.length==2) {
					for(Cursor i: selectedCursor) {
						//selectedCursor.add(new Cursor(i, (int) args[0].getValue(), (int) args[1].getValue()));
					}
				} else {
					for(Cursor i: selectedCursor) {
						//selectedCursor.add(new Cursor(i, (int) args[0].getValue(), (int) args[1].getValue(), (int) args[2].getValue(), (int) args[3].getValue()));
					}
				}
				break;
			}
			case MIMIC: {
				//MIMIC the first cursor of the id given
				//cursors.get((int) args[0].getValue()).add(new Cursor(cursors.get((int) args[0].getValue()).get(0)));
				break;
			}
			case DEL: {
	    		String varName = (String) args[0].getValue();
				if(UserObjectValue.isAVariable(varName)==false) {
	    			outputDisplay("Variable "+varName+"is not a valide variable, variable can only contain letters.");
	    			//throw varName+"is not a valide variable, variable can only contain letters."
				}
				if(vars.containsKey(varName)==false) {
	    			outputDisplay("Variable "+varName+" doesn't exist");
	    			//throw varName+" doesn't exist"
	    		}
    			vars.remove(varName);
    			break;
			}
			case NUM:
			case STR:
			case BOOL: {
	    		String varName = (String) args[0].getValue();
	    		
				if(UserObjectValue.isAVariable(varName)==false) {
	    			outputDisplay("Variable "+varName+"is not a valide variable, variable can only contain letters.");
	    			//throw varName+"is not a valide variable, variable can only contain letters."
				}
	    		VariableType currentCommand = VariableType.valueOf(currentInstruction.getCommand().toString());
	    		if(vars.containsKey(varName) && vars.get(varName).getReturnType()!=currentCommand) {
	    			outputDisplay("cannot change type from "+vars.get(varName).getReturnType()+" to "+currentCommand);
	    			//throw "cannot change type from "+vars.get(arg[0]).getReturnType()+" to "+currentCommand
	    		}
    			vars.put(varName,args[1]);
    			break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + currentInstruction.getCommand());
			}
			currentInstruction = currentInstruction.getNextInstruction();
    	} else if(currentInstruction.getCommand().isCursorCommand()) {
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
    
    public void executeAll() {
    	while(currentInstruction != null) {
    		nextStep();
			
			try {
				Thread.sleep((long)(timeBetweenFrames.get()*1000));
			  } catch (InterruptedException e) {
				outputDisplay("ERROR");
				Thread.currentThread().interrupt();
			  }
    		
    	}
    }
}
