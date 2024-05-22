package Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Canvas.Cursor;
import lexer.Command;
import lexer.InstructionNode;
import lexer.Parser;

public class Interpreter {
	private HashMap<String, UserObjectValue> vars;
	private HashMap<Integer, ArrayList<Cursor>> cursors;
	private ArrayList<Cursor> selectedCursor;
    
	private InstructionNode currentInstruction;
    private Float timeBetweenFrame;
    
    public Interpreter(File f) {
    	this.vars= new HashMap<String, UserObjectValue>();
    	this.cursors= new HashMap<Integer, ArrayList<Cursor>>();
    	Parser parser = new Parser(f);
		parser.parserRec();
		this.currentInstruction = parser.getStartInstruction();
    }
    public InstructionNode getCurrentInstruction() {
		return currentInstruction;
	}
    public Float getTimeBetweenFrame() {
		return timeBetweenFrame;
	}
    public void setTimeBetweenFrame(Float timeBetweenFrame) {
		this.timeBetweenFrame = timeBetweenFrame;
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
			System.out.println("Wrong number of arguments, got "+currentInstruction.getArgs());
			//throw "Wrong number of arguments, got "+currentInstruction.getArgs()
    		
    	}
    	//----

		System.out.println(currentInstruction.getCommand().toString()+" "+Arrays.toString(args));
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
    			System.out.print("Invalid value for "+currentInstruction);
    			//throw "Invalid value for "+currentInstruction
    		}
    	} else if(currentInstruction.getCommand().isChangingVariable()) {

    		switch (currentInstruction.getCommand()) {
			case CURSOR: {
				if(cursors.containsKey(args[0].getValue())) {
	    			System.out.println("Cursor "+args[0].getValue()+" already exist");
	    			//throw "Cursor "+args[0].getValue()+" already exist"
	    		} else {
	    			ArrayList<Cursor> cursorList = new ArrayList<Cursor>();
	    			cursorList.add(new Cursor());
	    			cursors.put((int) args[0].getValue(), cursorList);
	    			
	    			//select the created cursor
	    			selectedCursor=cursors.get((int) args[0].getValue());
	    		}
				break;
			}
			case SELECT: {
				if(cursors.containsKey(args[0].getValue())==false) {
	    			System.out.println("Cursor "+args[0].getValue()+" doesn't exist");
	    			//throw "Cursor "+args[0].getValue()+" doesn't exist"
	    		} else {
	    			selectedCursor=cursors.get((int) args[0].getValue());
	    		}
				break;
			}
			case REMOVE: {
				if(cursors.containsKey(args[0].getValue())==false) {
	    			System.out.println("Cursor "+args[0].getValue()+" doesn't exist");
	    			//throw "Cursor "+args[0].getValue()+" doesn't exist"
	    		} else {
	    			cursors.remove((int) args[0].getValue());
	    		}
				break;
			}
			case MIRROR: {
				if(args.length==2) {
					for(Cursor i: selectedCursor) {
						selectedCursor.add(new Cursor(i, (int) args[0].getValue(), (int) args[1].getValue()));
					}
				} else {
					for(Cursor i: selectedCursor) {
						selectedCursor.add(new Cursor(i, (int) args[0].getValue(), (int) args[1].getValue(), (int) args[2].getValue(), (int) args[3].getValue()));
					}
				}
				break;
			}
			case MIMIC: {
				//MIMIC the first cursor of the id given
				cursors.get((int) args[0].getValue()).add(new Cursor(cursors.get((int) args[0].getValue()).get(0)));
				break;
			}
			case DEL: {
	    		String varName = (String) args[0].getValue();
				if(UserObjectValue.isAVariable(varName)==false) {
	    			System.out.println("Variable "+varName+"is not a valide variable, variable can only contain letters.");
	    			//throw varName+"is not a valide variable, variable can only contain letters."
				}
				if(vars.containsKey(varName)==false) {
	    			System.out.println("Variable "+varName+" doesn't exist");
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
	    			System.out.println("Variable "+varName+"is not a valide variable, variable can only contain letters.");
	    			//throw varName+"is not a valide variable, variable can only contain letters."
				}
	    		VariableType currentCommand = VariableType.valueOf(currentInstruction.getCommand().toString());
	    		if(vars.containsKey(varName) && vars.get(varName).getReturnType()!=currentCommand) {
	    			System.out.println("cannot change type from "+vars.get(varName).getReturnType()+" to "+currentCommand);
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
    
    public void executeAll() {
    	while(true) {
    		nextStep();
    		//wait(timeBetweenFrame);
    	}
    }
}
