package lexicalAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


    

public class HackAssemblerL {
	
	static int[] RAM = new int[32768]; // size 32768 for Hack architecture
    static HashMap<String, Integer> symbolTable = new HashMap<>();
    static List<String> assemblyCode = new ArrayList<>();
    static List<String> machineCode = new ArrayList<>(); // To store the generated machine cod
    static int currRam = 16; // Start at RAM address 16 for variables
    
    static {
        // Initialize the symbol table with predefined symbols
        for (int i = 0; i < 16; i++) {
            symbolTable.put("R" + i, i);
        }
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
    }
    
    public static void firstPass(List <String> assemblyCode) {
    	int currAddress = 0;
    	
    	for (String line : assemblyCode) {
    		String cleanLine = removeWhitespaceAndComments(line);
    		
    		if (isLabel(cleanLine)) {
    			String label = extractLabel(cleanLine);
    			
    			symbolTable.put(label, currAddress);
    		} else if (isInstruction(cleanLine)) {
    			currAddress++;
    		}
    		
    		
    	}
    }
    
public static void secondPass (List <String> assemblyCode) {
	int currAddress = 0;
	for(String line: assemblyCode) {
		String cleanLine = removeWhitespaceAndComments(line);
		if (isAInstruction(cleanLine)) {
			machineCode.add(handleAInstruction(cleanLine));
			currAddress++;
		}else if (isCInstruction(cleanLine)) {
			machineCode.add(handleCInstruction(cleanLine));
			currAddress++;
		}else if (isLabel(cleanLine)) {
			//skip labels, they're handled in pass one
			currAddress++;
		} else {
			currAddress++;
		}
	}
}
    public static boolean isAInstruction (String line) {
    	return line.startsWith("@");
    }
   
    public static String removeWhitespaceAndComments(String line) {
        // Trim leading and trailing whitespace
        line = line.trim();
        
        // Check if the line contains a comment
        if (line.contains("//")) {
            // Split the line at the semicolon and take the part before it
            line = line.substring(0, line.indexOf("//")).trim();
        }
        
        return line;
    }

    
    public static boolean isLabel(String line) {
    	return line.startsWith("(") && line.endsWith(")");
    }
    public static void replaceWithAddress(String line, int address) {
    	
    }
    public static boolean isInstruction(String line) {
    	if (isAInstruction(line)) {
    		return true;
    	}else if (isCInstruction(line)) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isCInstruction(String line) {
    	return line.startsWith("0") || line.startsWith("1") || line.startsWith("-1") || line.startsWith("D") || line.startsWith("A") || line.startsWith("!D") || line.startsWith("!A") || line.startsWith("-D") || line.startsWith("-A") || line.startsWith("D+!") || line.startsWith("A+1") || line.startsWith("D-1") ||line.startsWith("A-1") || line.startsWith("D+A") || line.startsWith("D-A") || line.startsWith("A-D") || line.startsWith("D&A") || line.startsWith("D|A") || line.startsWith("M") || line.startsWith("!M") || line.startsWith("-M") || line.startsWith("M+1") || line.startsWith("D+M") || line.startsWith("D-M") || line.startsWith("M-D") || line.startsWith("D&M") || line.startsWith("D|M");
    }
    
    public static String extractLabel(String line) {
    	return line.substring(1, line.length() - 1);
    }
    public static String handleCInstruction(String line) {
    	// Split the instruction into components
        String dest = null, comp = null, jump = null;

        if (line.contains("=")) {
            String[] parts = line.split("=");
            dest = parts[0].trim();
            comp = parts[1].trim();
        } else {
            comp = line;
        }

        if (comp.contains(";")) {
            String[] parts = comp.split(";");
            comp = parts[0].trim();
            jump = parts[1].trim();
        }

        // Convert to binary
        String binaryInstruction = "111"; // Start with the '111' prefix for C instructions
        String a = (comp.startsWith("M") ? "1" : "0"); // Set 'a' bit based on comp
        String cccccc = getCompBinary(comp); // Get the binary for comp
        String ddd = getDestBinary(dest); // Get the binary for dest
        String jjj = getJumpBinary(jump);
        
        binaryInstruction += a;
        binaryInstruction += cccccc;
        binaryInstruction += ddd;
        binaryInstruction += jjj;
        
    	
    	return binaryInstruction;
    	
    }
    private static String getCompBinary(String comp) {
        // Map of comp mnemonics to binary codes
        switch (comp) {
            case "0": return "101010";
            case "1": return "011111";
            case "-1": return "111010";
            case "D": return "001100";
            case "A": return "110000";
            case "!D": return "001101";
            case "!A": return "110001";
            case "-D": return "001111";
            case "-A": return "110011";
            case "D+1": return "011111";
            case "A+1": return "110111";
            case "D-1": return "001110";
            case "A-1": return "110010";
            case "D+A": return "000010";
            case "D-A": return "001100";
            case "A-D": return "000111";
            case "D&A": return "000000";
            case "D|A": return "010101";
            case "M": return "110000"; // M is A register
            case "!M": return "110001";
            case "-M": return "110011";
            case "M+1": return "110111";
            case "D+M": return "000010";
            case "D-M": return "001100";
            case "M-D": return "000111";
            case "D&M": return "000000";
            case "D|M": return "010101";
            default: return "101010"; // Default case for unknown comp return 0
        }
    }
    private static String getDestBinary(String dest) {
        // Map of dest mnemonics to binary codes
        if (dest == null) return "000"; // No dest
        String binary = "";
        binary += dest.contains("A") ? "1" : "0";
        binary += dest.contains("D") ? "1" : "0";
        binary += dest.contains("M") ? "1" : "0";
        return binary;
    }
    
    private static String getJumpBinary(String jump) {
        // Map of jump mnemonics to binary codes
        if (jump == null) return "000"; // No jump
        switch (jump) {
            case "JGT": return "001";
            case "JEQ": return "010";
            case "JGE": return "011";
            case "JLT": return "100";
            case "JNE": return "101";
            case "JLE": return "110";
            case "JMP": return "111";
            default: return "000"; // Default case for unknown jump
        }
    }
    public static String handleAInstruction(String line) {
        // Remove the first character (which is assumed to be '@')
        String symbol = line.substring(1);
        
        // Try to parse the symbol as an integer
        try {
            // If it's a number, parse it directly
            int address = Integer.parseInt(symbol);
            // Convert the number to a binary string and format it to 16 bits
            return String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
        } catch (NumberFormatException e) {
            // If it's not a number, it must be a label
            if (symbolTable.containsKey(symbol)) {
                int address = symbolTable.get(symbol);
                // Convert the address to a binary string and format it to 16 bits
                return String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
            } else {
                // Handle undefined label error
                System.err.println("Error: Undefined label " + symbol);
                return "0000000000000000"; // Return a default value or handle error appropriately
            }
        }
    }



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 1) {
	        System.out.println("Usage: java HackAssembler <path/to/your/file.asm>");
	        return;
	    }

	    String inputFilePath = args[0]; // Get the input file path from the command-line argument
	    String outputFilePath = "Prog.hack"; // Output file name

	    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));) 
	    {

	        String line;
	        while ((line = br.readLine()) != null) {
	        	assemblyCode.add(line); // Parse and translate to binary
	             // Only write if the output is not null
	                
	            }
	        firstPass(assemblyCode);
	        secondPass(assemblyCode);
	        
	        
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
                for (String code : machineCode) {
                    bw.write(code);
                    bw.newLine(); // Write each machine code line to the file
                }
            }
	        
	        System.out.println("Processing complete. Output written to " + outputFilePath);
	    } catch (IOException e) {
	        System.err.println("Error while processing the file: " + e.getMessage());
	        e.printStackTrace();
	    }
	}


}
