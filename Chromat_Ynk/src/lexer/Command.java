package lexer;

import interpreter.VariableType;

public enum Command {
	FWD, BWD, TURN, MOV, POS, HIDE, SHOW, PRESS, COLOR, THICK, LOOKAT, CURSOR, SELECT, REMOVE, IF, FOR, WHILE, END, MIMIC, MIRROR, NUM, STR, BOOL, DEL;
	//return a array of all the possible argument type the command can have
	public VariableType[][] argumentType() {
		switch (this) {
		case FWD:
		case BWD:
		case TURN:
		case PRESS:
		case THICK: {return new VariableType[][]{new VariableType[]{ VariableType.NUM }};}
		case MOV:
		case POS: {return new VariableType[][]{new VariableType[]{ VariableType.NUM,VariableType.NUM }};}
		case LOOKAT: {return new VariableType[][]{new VariableType[]{ VariableType.NUM },
													new VariableType[]{ VariableType.NUM,VariableType.NUM }};}
		case MIRROR: {return new VariableType[][]{new VariableType[]{ VariableType.NUM,VariableType.NUM,VariableType.NUM,VariableType.NUM },
													new VariableType[]{ VariableType.NUM,VariableType.NUM }};}
		case HIDE:
		case SHOW:
		case END: {return new VariableType[][]{new VariableType[]{}};}
		case COLOR: {return new VariableType[][]{new VariableType[]{ VariableType.NUM,VariableType.NUM,VariableType.NUM },
												   new VariableType[]{ VariableType.STR }};}
		case CURSOR:
		case SELECT:
		case REMOVE:
		case MIMIC: {return new VariableType[][]{new VariableType[]{ VariableType.NUM }};}
		case IF:
		case WHILE: {return new VariableType[][]{new VariableType[]{ VariableType.BOOL }};}
		case FOR: {return new VariableType[][]{new VariableType[]{ VariableType.VAR,VariableType.NUM,VariableType.NUM,VariableType.NUM },
												new VariableType[]{ VariableType.VAR,VariableType.NUM,VariableType.NUM },
												new VariableType[]{ VariableType.VAR,VariableType.NUM }};}
		case DEL: {return new VariableType[][]{new VariableType[]{ VariableType.VAR }};}
		case NUM: {return new VariableType[][]{new VariableType[]{ VariableType.VAR,VariableType.NUM }};}
		case BOOL: {return new VariableType[][]{new VariableType[]{ VariableType.VAR,VariableType.BOOL }};}
		case STR: {return new VariableType[][]{new VariableType[]{ VariableType.VAR,VariableType.STR }};}
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
	public boolean isInstructionBlock() {
		return this==IF || this==WHILE || this==FOR || this==END || this==MIRROR || this==MIMIC;
	}
	public boolean isChangingVariable() {
		return this==NUM||this==STR||this==BOOL||this==DEL||this==CURSOR||this==SELECT||this==REMOVE;
	}
	public boolean isCursorCommand() {
		return this==FWD||this==BWD||this==TURN||this==MOV||this==POS||this==HIDE||this==SHOW||this==PRESS||this==COLOR||this==THICK||this==LOOKAT;
	}
}