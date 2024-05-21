package lexer;

public enum Command {
	FWD, BWD, TURN, MOV, POS, HIDE, SHOW, PRESS, COLOR, THICK, LOOKAT, CURSOR, SELECT, REMOVE, IF, FOR, WHILE, MIMIC, MIRROR, END;
	public static Command fromString(String s) {
		switch (s) {
		case "FWD": {return FWD;}
		case "BWD" : {return BWD;}
		case "TURN": {return TURN;}
		case "MOV" : {return MOV;}
		case "POS": {return POS;}
		case "HIDE": {return HIDE;}
		case "SHOW": {return SHOW;}
		case "PRESS": {return PRESS;}
		case "COLOR": {return COLOR;}
		case "THICK": {return THICK;}
		case "LOOKAT": {return LOOKAT;}
		case "CURSOR": {return CURSOR;}
		case "SELECT": {return SELECT;}
		case "REMOVE": {return REMOVE;}
		case "IF": {return IF;}
		case "FOR": {return FOR;}
		case "WHILE": {return WHILE;}
		case "MIMIC": {return MIMIC;}
		case "MIRROR": {return MIRROR;}
		case "END": {return END;}
		default:
			throw new IllegalArgumentException("Unexpected value: " + s);
		}
	}
}