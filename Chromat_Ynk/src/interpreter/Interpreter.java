package interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cursors.Cursor;
import ihm.ChromatYnc;
import ihm.CursorController;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lexer.Command;
import lexer.InstructionNode;
import lexer.Parser;

public class Interpreter {
	private HashMap<String, UserObjectValue> vars;
	private CursorController cursorController;
	private ObservableMap<Integer, ObservableList<Cursor>> cursors;
	private ObservableList<Cursor> selectedCursor;
    
	private InstructionNode currentInstruction;
    
    public Interpreter(File f, ChromatYnc chromatYnc) {
    	this.vars= new HashMap<String, UserObjectValue>();
		this.cursorController = chromatYnc.getCursorController();
    	this.cursors= cursorController.getCursors();
    	Parser parser = new Parser(f);
		parser.parserRec();
		this.currentInstruction = parser.getStartInstruction();
    }
    public InstructionNode getCurrentInstruction() {
		return currentInstruction;
	}
	public void setNextCurrentInstruction() {
		currentInstruction = currentInstruction.getNextInstruction();
	}

    public Exception nextStep() throws InterpreterException {

    	UserObjectValue[] args = new UserObjectValue[currentInstruction.getArgs().length];
    	VariableType[][] argumentType=currentInstruction.getCommand().argumentType();
    	Boolean hasArgs=false;
    	for(VariableType[] j:argumentType) {
    		if(j.length==args.length) {
    			hasArgs=true;
	        	for(int i=0;i<j.length;i++) {
	        		//create a Evaluable and eval it to directly get its value;
					try {
	        			UserObjectValue t = new UserObjectValue(Evaluable.newEvaluable(currentInstruction.getArgs()[i],j[i]).eval(vars),j[i]);
						args[i] = t;
					} catch (Exception e) {
						return e;
					}
	        	}
    		}
    	}
    	if (hasArgs==false) {
			return new InterpreterException("Wrong number of arguments for command "+currentInstruction.getCommand()+" ,got "+Arrays.toString(currentInstruction.getArgs()));    		
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
    		} else if (currentInstruction.getCommand()==Command.MIMIC) {
    			if (currentInstruction.getHasBeenExecuted()) {
    				//Remove temporary Cursor

    				currentInstruction = currentInstruction.getNextInstruction();
    			} else {
    				//Create temporary Cursor
    				currentInstruction.setHasBeenExecuted();
					int idToMimic = args[0].getInt();
					List<Cursor> toAdd = new ArrayList<>();
					for(Cursor i: selectedCursor) {
						if (i != null) {
							toAdd.add(i);
						}
					}
					for(Cursor i: toAdd) {
						cursorController.addCursorMimic(idToMimic, i);
					}
					selectedCursor = cursorController.getCursors().get(selectedCursor.get(0).getId());
					//MIMIC the first cursor of the id given
					//cursors.get(args[0].getInt()).add(new Cursor(cursors.get(args[0].getInt()).get(0)));
					
    				currentInstruction = currentInstruction.getConditionInstruction();
    			}
    		} else if (currentInstruction.getCommand()==Command.MIRROR) {
    			if (currentInstruction.getHasBeenExecuted()) {
    				//Remove temporary Cursor

    				currentInstruction = currentInstruction.getNextInstruction();
    			} else {
    				//Create temporary Cursor
    				currentInstruction.setHasBeenExecuted();
    				if(args.length==2) {
						List<Cursor> toAdd = new ArrayList<>();
						for(Cursor i: selectedCursor) {
							if (i != null) {
								toAdd.add(i);
							}
						}
						for(Cursor i: toAdd) {
							cursorController.addCursorMirrorCenter(i ,args[0].getDouble(), args[1].getDouble());
						}
						selectedCursor = cursorController.getCursors().get(selectedCursor.get(0).getId());
					} else {
						List<Cursor> toAdd = new ArrayList<>();
						for(Cursor i: selectedCursor) {
							if (i != null) {
								toAdd.add(i);
							}
						}
						for(Cursor i: toAdd) {
							cursorController.addCursorMirrorAxial(i ,args[0].getDouble(), args[1].getDouble(), args[2].getDouble(), args[3].getDouble());
						}
						selectedCursor = cursorController.getCursors().get(selectedCursor.get(0).getId());
					}
    				currentInstruction = currentInstruction.getConditionInstruction();
    			}
    		} else {
    			throw new InterpreterException("Invalid value for "+currentInstruction);
    		}
    	} else if(currentInstruction.getCommand().isChangingVariable()) {

    		switch (currentInstruction.getCommand()) {
			case CURSOR: {
				int id = args[0].getInt();
				if(cursors.containsKey(id)) {
					return new InterpreterException("Cursor "+args[0].getInt()+" already exist");
				} else if (id<0) {
					throw new InterpreterException(args[0].getInt()+" is an invalid id");
				} else {
					cursorController.addCursorNormal(id);
					
					//select the created cursor
					selectedCursor=cursorController.getCursors().get(id);
				}
				break;
			}
			case SELECT: {
				int id = args[0].getInt();
				if(cursors.containsKey(id)==false) {
					return new InterpreterException("Cursor "+args[0].getInt()+" doesn't exist");
				} else {
					selectedCursor=cursors.get(id);
				}
				break;
			}
			case REMOVE: {
				int id = args[0].getInt();
				if(cursors.containsKey(id)==false) {
					return new InterpreterException("Cursor "+args[0].getInt()+" doesn't exist");
				} else {
					cursors.remove(id);
				}
				break;
			}
			case DEL: {
				if(vars.containsKey(args[0].getString())==false) {
	    			return new InterpreterException("Variable "+args[0].getString()+" doesn't exist");
	    		} else {
    				vars.remove(args[0].getString());
				}
    			break;
			}
			case NUM:
			case STR:
			case BOOL: {
	    		String varName =  args[0].getString();
	    		
				if(UserObjectValue.isAVariable(varName)==false) {
	    			return new InterpreterException("Variable "+varName+"is not a valide variable, variable can only contains letters.");
				}
				//convert from enum Command to enum VariableType
	    		VariableType currentCommand = VariableType.valueOf(currentInstruction.getCommand().toString());
	    		if(vars.containsKey(varName) && vars.get(varName).getReturnType(vars)!=currentCommand) {
	    			return new InterpreterException("cannot change type from "+vars.get(varName).getReturnType(vars)+" to "+currentCommand);
	    		}
    			vars.put(varName,args[1]);
    			break;
			}
			default:
				return new IllegalArgumentException("Unexpected value: " + currentInstruction.getCommand());
			}
			currentInstruction = currentInstruction.getNextInstruction();
    	} else if(currentInstruction.getCommand().isCursorCommand()) {
    		if (selectedCursor==null) {
    			return new InterpreterException("No cursor Selected");
    		}
			for(Cursor i:selectedCursor) {
				i.execCommand(currentInstruction.getCommand(), args);
			}			
			setNextCurrentInstruction();	
    	}
		return null;
    }
    
}