package lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ihm.ChromatYnc;
import interpreter.Interpreter;


public class Parser {
	private ChromatYnc chromatYnc;

	private Instruction[] instructions;
	public void lexerFile (File file) throws IOException {
		ArrayList<Instruction> instructionList = new ArrayList<Instruction>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			while (line!=null) {
				String[] words = line.split(" ");
				if (toInstruction(words) != null) {
					instructionList.add(toInstruction(words));
				}
				line = reader.readLine();
			}
			instructions = new Instruction[instructionList.size()];
			instructions = instructionList.toArray(instructions);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void lexerStr (String str) throws IOException {
		ArrayList<Instruction> instructionList = new ArrayList<Instruction>();
		String line = str;
		if (line!=null) {
			String[] words = line.split(" ");
			if (toInstruction(words) != null) {
			instructionList.add(toInstruction(words));
			}
			line = str;
		}
		instructions = new Instruction[instructionList.size()];
		instructions = instructionList.toArray(instructions);

	}
	public Instruction toInstruction (String[] words) {
		//System.out.print(words[0] + " ");
		try {
			Instruction instruction = new Instruction(Command.valueOf(words[0]), Arrays.copyOfRange(words,1,words.length));
			return instruction;
		} catch (IllegalArgumentException e){
			chromatYnc.errorOutputDisplay("no command \"" + words[0] + "\"");
			if (chromatYnc.getStopWhenException()) {
				Interpreter.stopProcessFileThread();
			}				
			return null;
		}	
	}
	
	

	//this.instructions = [["F",0,1]];
	private int ci;//currentInsruction
	InstructionNode startInstruction;
	InstructionNode prevConditionInstruction;
	ArrayList<InstructionNode> prevList = new ArrayList<>(); //listdestrucquichercheapointersurlaprochaineinstruction en next 
	
	public Parser(File file, ChromatYnc chromatYnc) { //temporary arguments
		this.chromatYnc = chromatYnc;
		try {
			lexerFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ci = 0;
		startInstruction= null;
		prevConditionInstruction = null;
	}
	public Parser(String str, ChromatYnc chromatYnc) { //temporary arguments
		this.chromatYnc = chromatYnc;
		try {
			lexerStr(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ci = 0;
		startInstruction= null;
		prevConditionInstruction = null;
	}
	
	
	public void parserRec() {
		//return graph starting at instructions[ci]
		if (ci>=instructions.length ||  instructions[ci].getCommand() == Command.END) {
			ci++;
			return;
		}
	
		//ret is the first instruction
		InstructionNode ret = new InstructionNode(instructions[ci].getCommand(), instructions[ci].getArgs());
		if (startInstruction == null) {
			startInstruction = ret;
		}
		//connect every unconnected instruction to this instruction ------
		if(this.prevConditionInstruction != null) {
			this.prevConditionInstruction.setConditionInstruction(ret);
			this.prevConditionInstruction = null;
		}
		for(InstructionNode j : this.prevList) {
			j.setNextInstruction(ret);
		}
		this.prevList.clear();
		//------
		
		if (instructions[ci].getCommand().isInstructionBlock()==false ) {// != {FOR,WHILE}
			ret.setConditionInstruction(null);
			ci++;
			//save the last instruction in prevList to know where to go afterward
			this.prevList.add(ret);
			
			//generate tree starting on the line after
			parserRec();
			return;
		} else {
			try {
				if (instructions[ci+1].getCommand() == Command.END) {
					// create empty instruction blocks;
					return;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// create instruction block on its own		
				return;
			}
			if(instructions[ci].getCommand() != Command.FOR)  {
				Command tempcommand = instructions[ci].getCommand();

				ci++;
				this.prevConditionInstruction = ret;
				parserRec();
				//all inside instruction must link back to WHILE MIMIR or MIRROR
				if(tempcommand != Command.IF) {
					for(InstructionNode j : this.prevList) {
						j.setNextInstruction(ret);
					}
					this.prevList.clear();
				}
	
				//ret and the last instruction(s) must link to the same next instruction
				this.prevList.add(ret);
				parserRec();
				return;
			} else {
				//turn for loop into while loop
				//create variable
				String[] tempArgs=instructions[ci].getArgs();
				ret.setCommand(Command.NUM);
				if(tempArgs.length>=3) {
					//FROM value was given
					ret.setArgs(new String[]{tempArgs[0],tempArgs[1]});
					//create while
					ret.setNextInstruction(new InstructionNode(Command.WHILE, new String[]{tempArgs[0]+"<"+tempArgs[2]}));
				} else {
					//FROM value was not given
					ret.setArgs(new String[]{tempArgs[0],"0"});
					//create while
					ret.setNextInstruction(new InstructionNode(Command.WHILE, new String[]{tempArgs[0]+"<"+tempArgs[1]}));
				}
				ret.setConditionInstruction(null);
				ret = ret.getNextInstruction();
						
				ci++;
				this.prevConditionInstruction = ret;
				parserRec();
				
				
				//create incrementation;
				InstructionNode adder;
				if(tempArgs.length==4) {
					adder = new InstructionNode(Command.NUM, new String[]{tempArgs[0],tempArgs[0]+"+"+tempArgs[3]});
				} else {
					adder = new InstructionNode(Command.NUM, new String[]{tempArgs[0],tempArgs[0]+"+1"});
					
				}

				//all inside instruction must link back to adder
				for(InstructionNode j : this.prevList) {
					j.setNextInstruction(adder);
				}
				this.prevList.clear();
				
				adder.setNextInstruction(ret);
				
				//create DEL instruction
				ret.setNextInstruction(new InstructionNode(Command.DEL, new String[]{tempArgs[0]}));
				
				this.prevList.add(ret);
				parserRec();
				return;
			}
		}
	}
	public InstructionNode getStartInstruction() {
		return startInstruction;
	}
}
