package interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cursors.Cursor;
import ihm.ChromatYnc;
import ihm.CursorController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.canvas.Canvas;
import lexer.Command;
import lexer.InstructionNode;
import lexer.Parser;

public class Interpreter {
	private static volatile boolean running = true;

	private ChromatYnc chromatYnc;
	private HashMap<String, UserObjectValue> vars;
	private CursorController cursorController;
	private ObservableMap<Integer, ObservableList<Cursor>> cursors;
    
	private InstructionNode currentInstruction;
	private Canvas canvas;
	private LinkedList<ArrayList<Cursor>> cursorsToDeleteList;

    
    public Interpreter(File f, ChromatYnc chromatYnc) {	
		Interpreter.running = true;
		this.chromatYnc = chromatYnc;
    	this.vars= new HashMap<String, UserObjectValue>();
		this.cursorController = chromatYnc.getCursorController();
		this.cursorsToDeleteList = new LinkedList<>();
    	this.cursors= cursorController.getCursors();
		this.canvas = chromatYnc.getCanvas();
    	Parser parser = new Parser(f, chromatYnc);
		parser.parserRec();
		this.currentInstruction = parser.getStartInstruction();
    }
	public Interpreter(String str, ChromatYnc chromatYnc) {
    	this.vars= new HashMap<String, UserObjectValue>();
		this.chromatYnc = chromatYnc;
		this.cursorController = chromatYnc.getCursorController();
		this.cursorsToDeleteList = new LinkedList<>();
    	this.cursors= cursorController.getCursors();
		this.canvas = chromatYnc.getCanvas();
    	Parser parser = new Parser(str, chromatYnc);
		parser.parserRec();
		this.currentInstruction = parser.getStartInstruction();
    }
    public InstructionNode getCurrentInstruction() {
		return currentInstruction;
	}
	public void setNextCurrentInstruction() {
		currentInstruction = currentInstruction.getNextInstruction();
	}
	public static boolean isRunning() {
		return running;
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
	        			UserObjectValue t = new UserObjectValue(Evaluable.newEvaluable(currentInstruction.getArgs()[i],j[i]).eval(vars),j[i], currentInstruction.getArgs()[i]);
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
        System.out.print(currentInstruction.getCommand().toString()+" "+Arrays.toString(args));
		if (args.length>0 && args[0] != null && args[0].getIsPercentage()) {
			System.out.print("%");
		}	
		System.out.println("");
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
					ArrayList<Cursor> toDelete = cursorsToDeleteList.getLast();
					Platform.runLater(() -> {
						for(Cursor i: toDelete) {
							cursorController.removeCursor(i);
						}
						cursorsToDeleteList.removeLast();
					});
    				currentInstruction = currentInstruction.getNextInstruction();
    			} else {
    				//Create temporary Cursor
    				currentInstruction.setHasBeenExecuted();
					int idToMimic = args[0].getInt();
					List<Cursor> toAdd = new ArrayList<>();
					for(Cursor i: chromatYnc.getSelectedCursor()) {
						if (i != null) {
							toAdd.add(i);
						}
					}
					ArrayList<Cursor> cursorMimicArray = new ArrayList<>();
					for(Cursor i: toAdd) {
						Cursor cursorMimic = cursorController.createCursorMimic(idToMimic, i);
						cursorController.addCursor(cursorMimic);
						cursorMimicArray.add(cursorMimic);
					}
					cursorsToDeleteList.addLast(cursorMimicArray);
					chromatYnc.setSelectedCursor(cursorController.getCursors().get(chromatYnc.getSelectedCursor().get(0).getId()));
					//MIMIC the first cursor of the id given
					//cursors.get(args[0].getInt()).add(new Cursor(cursors.get(args[0].getInt()).get(0)));
					
    				currentInstruction = currentInstruction.getConditionInstruction();
    			}
    		} else if (currentInstruction.getCommand()==Command.MIRROR) {
    			if (currentInstruction.getHasBeenExecuted()) {
    				//Remove temporary Cursor
					ArrayList<Cursor> toDelete = cursorsToDeleteList.getLast();
					Platform.runLater(() -> {
						for(Cursor i: toDelete) {
							cursorController.removeCursor(i);
						}
						cursorsToDeleteList.removeLast();
					});
    				currentInstruction = currentInstruction.getNextInstruction();
    			} else {
    				//Create temporary Cursor
    				currentInstruction.setHasBeenExecuted();
    				if(args.length==2) {
						List<Cursor> toAdd = new ArrayList<>();
						for(Cursor i: chromatYnc.getSelectedCursor()) {
							if (i != null) {
								toAdd.add(i);
							}
						}
						ArrayList<Cursor> cursorMirrorArray = new ArrayList<>();
						for(Cursor i: toAdd) {
							Double xCenter = args[0].getDouble();
							Double yCenter = args[1].getDouble();
							if (args[0].getIsPercentage()) {
								xCenter = xCenter/100 * canvas.getWidth(); 
							}
							if (args[1].getIsPercentage()) {
								yCenter = yCenter/100 * canvas.getHeight();
							}
							Cursor cursorMirror = cursorController.createCursorMirrorCenter(i, xCenter, yCenter);
							cursorController.addCursor(cursorMirror);
							cursorMirrorArray.add(cursorMirror);
						}
						cursorsToDeleteList.addLast(cursorMirrorArray);
						chromatYnc.setSelectedCursor(cursorController.getCursors().get(chromatYnc.getSelectedCursor().get(0).getId()));
					} else {
						List<Cursor> toAdd = new ArrayList<>();
						for(Cursor i: chromatYnc.getSelectedCursor()) {
							if (i != null) {
								toAdd.add(i);
							}
						}
						ArrayList<Cursor> cursorMirrorArray = new ArrayList<>();
						for(Cursor i: toAdd) {
							Double x1 = args[0].getDouble();
							Double y1 = args[1].getDouble();
							Double x2 = args[2].getDouble();
							Double y2 = args[3].getDouble();
							if (args[0].getIsPercentage()) {
								x1 = x1/100 * canvas.getWidth(); 
							}
							if (args[1].getIsPercentage()) {
								y1 = y1/100 * canvas.getHeight();
							}		
							if (args[2].getIsPercentage()) {
								x2 = x2/100 * canvas.getWidth(); 
							}
							if (args[3].getIsPercentage()) {
								y2 = y2/100 * canvas.getHeight();
							}
							Cursor cursorMirror = cursorController.createCursorMirrorAxial(i, x1, y1, x2, y2);
							cursorController.addCursor(cursorMirror);
							cursorMirrorArray.add(cursorMirror);
						}
						cursorsToDeleteList.addLast(cursorMirrorArray);
						chromatYnc.setSelectedCursor(cursorController.getCursors().get(chromatYnc.getSelectedCursor().get(0).getId()));
					}
    				currentInstruction = currentInstruction.getConditionInstruction();
    			}
    		} else {
    			return new InterpreterException("Invalid value for "+currentInstruction);
    			//throw "Invalid value for "+currentInstruction
    		}
    	} else if(currentInstruction.getCommand().isChangingVariable()) {

    		switch (currentInstruction.getCommand()) {
			case CURSOR: {
				int id = args[0].getInt();
				if(cursors.containsKey(id)) {
					return new InterpreterException("Cursor "+args[0].getInt()+" already exist");
				} else if (id<0) {
					return new InterpreterException(args[0].getInt()+" is an invalid id");
				} else {
					Cursor cursorNormal = cursorController.createCursorNormal(id);
					cursorController.addCursor(cursorNormal);
					
					//select the created cursor
					chromatYnc.setSelectedCursor(cursorController.getCursors().get(id));
				}
				break;
			}
			case SELECT: {
				int id = args[0].getInt();
				if(cursors.containsKey(id)==false) {
					return new InterpreterException("Cursor "+args[0].getInt()+" doesn't exist");
				} else {
					chromatYnc.setSelectedCursor(cursors.get(id));
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
    		if (chromatYnc.getSelectedCursor()==null) {
    			return new InterpreterException("No cursor Selected");
    		}
			for(Cursor i: chromatYnc.getSelectedCursor()) {
				Exception exception = i.execCommand(currentInstruction.getCommand(), args);
				if (exception != null) {return exception;} // exit if exception found in execCommand of cursor
			}			
			setNextCurrentInstruction();	
    	}
		return null;
    }

	public static void stopProcessFileThread() {
		running = false;
	}
}