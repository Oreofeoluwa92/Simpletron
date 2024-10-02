import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class SimpletronV2 {
    public static final int MEMORYSIZE = 10000;
    public static final int PAGESIZE = 100;
    public static final int NUMBERPAGES = 100;

    public int[][] memoryword;
    public int accumulator;
    public int instructionCounter;
    public int instructionRegister;
    public int indexRegister;

    public Scanner scanner;

    public SimpletronV2() {
        memoryword = new int[NUMBERPAGES][PAGESIZE];  // Proper 2D array initialization
        accumulator = 0;
        instructionCounter = 0;
        instructionRegister = 0;
        indexRegister = 0;
        scanner = new Scanner(System.in);
    }

    public void loadProgram(String filename) {
        try {
            File file = new File(filename);
            Scanner fileScanner = new Scanner(file);
            int memoryIndex = 0;

            while (fileScanner.hasNextLine() && memoryIndex < MEMORYSIZE) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                String instructionStr = parts[parts.length - 1];

                instructionStr = instructionStr.startsWith("+") ? instructionStr.substring(1) : instructionStr;

                try {
                    int instruction = Integer.parseInt(instructionStr);
                    if (instruction < -999999 || instruction > 999999) {
                        throw new NumberFormatException("Instruction out of valid range");
                    }

                    int page = memoryIndex / PAGESIZE;
                    int offset = memoryIndex % PAGESIZE;
                    memoryword[page][offset] = instruction;
                    memoryIndex++;
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing instruction at line " + (memoryIndex + 1) + ": " + line);
                    System.out.println("Skipping this instruction.");
                }
            }

            fileScanner.close();
            System.out.println("Program loaded successfully. " + memoryIndex + " instructions loaded.");

            while (memoryIndex < MEMORYSIZE) {
                int page = memoryIndex / PAGESIZE;
                int offset = memoryIndex % PAGESIZE;
                memoryword[page][offset] = 0;
                memoryIndex++;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filename);
        }
    }

    public void coreDump(int startPage, int endPage) {
        System.out.println("Core Dump:");
        System.out.printf("Accumulator:          %+06d\n", accumulator);
        System.out.printf("InstructionCounter:   %06d\n", instructionCounter);
        System.out.printf("IndexRegister:        %06d\n", indexRegister);
        System.out.printf("InstructionRegister:  %+06d\n", instructionRegister);

        System.out.println("Memory:");
        for (int page = startPage; page <= endPage; page++) {
            System.out.printf("PAGE #%02d\n", page);
            for (int i = 0; i < PAGESIZE; i++) {
                if (i % 10 == 0) {
                    System.out.println();
                }
                System.out.printf("%+06d ", memoryword[page][i]);
            }
            System.out.println();
        }
    }

    public void manualProgramInput() {
        System.out.println("*** Please enter your program one instruction( or data word ) at a time ");
         System.out.println("*** I will type the location number and a question mark (?).");
        System.out.println("*** Type the word GO to execute your program ***");
        int location = 0;
        while (location < MEMORYSIZE) {
            System.out.printf("%06d ? ", location);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("GO")) {
                System.out.println("*** Program loading complete ***");
                     break;
            }
//to store instruction
            try {
                int instruction = Integer.parseInt(input);
                if (instruction >= -999999 && instruction <= 999999) {
                    int page = location / PAGESIZE;
                    int offset = location % PAGESIZE;
                    memoryword[page][offset] = instruction;
                    location++;
                } else {
                    System.out.println("Instruction out of range. Enter a value between -999999 and 999999.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    // Now execute method is part of SimpletronV2
    public void execute() {
        System.out.println("*** Program execution begins ***");
        //int instructionCount = 0;
        while (true) {
            // Split instruction into opcode and operand
            int page = instructionCounter / PAGESIZE;
            int offset = instructionCounter % PAGESIZE;
            int instruction = memoryword[page][offset];
            int opcode = instruction / 10000;
            int operand = instruction % 10000;

               // Debug output
       System.out.println("Instruction: " + instruction + ", Opcode: " + opcode + ", Operand: " + operand);
        //System.out.println("Accumulator: " + accumulator + ", InstructionCounter: " + instructionCounter + ", IndexRegister: " + indexRegister);

            // Process instructions
            switch (opcode) {
                case OperationCodes.READ:
                    System.out.print("Enter an integer: ");
                    int input = scanner.nextInt();
                    memoryword[operand / PAGESIZE][operand % PAGESIZE] = input;
                    break;
                case OperationCodes.WRITE:
                    System.out.println(memoryword[operand / PAGESIZE][operand % PAGESIZE]);
                    break;
                case OperationCodes.LOAD:
                    accumulator = memoryword[operand / PAGESIZE][operand % PAGESIZE];
                    break;
                case OperationCodes.LOADIM:
                    accumulator = operand;
                    break;
                case OperationCodes.LOADX:
                    indexRegister = memoryword[operand / PAGESIZE][operand % PAGESIZE];
                    break;
                case OperationCodes.LOADIDX:
                    accumulator = memoryword[indexRegister / PAGESIZE][indexRegister % PAGESIZE];
                    break;
                case OperationCodes.STORE:
                    memoryword[operand / PAGESIZE][operand % PAGESIZE] = accumulator;
                    break;
                case OperationCodes.STOREIDX:
                    memoryword[indexRegister / PAGESIZE][indexRegister % PAGESIZE] = accumulator;
                    break;
                case OperationCodes.ADD:
                    accumulator += memoryword[operand / PAGESIZE][operand % PAGESIZE];
                    break;
                case OperationCodes.ADDX:
                    accumulator += memoryword[indexRegister / PAGESIZE][indexRegister % PAGESIZE];
                    break;
                case OperationCodes.SUBTRACT:
                    accumulator -= memoryword[operand / PAGESIZE][operand % PAGESIZE];
                    break;
                case OperationCodes.SUBTRACTX:
                    accumulator -= memoryword[indexRegister / PAGESIZE][indexRegister % PAGESIZE];
                    break;
                case OperationCodes.DIVIDE:
                    if (memoryword[operand / PAGESIZE][operand % PAGESIZE] == 0) {
                        System.out.println("Error: Division by zero");
                        return;
                    }
                    accumulator /= memoryword[operand / PAGESIZE][operand % PAGESIZE];
                    break;
                case OperationCodes.DIVIDEX:
                    if (memoryword[indexRegister / PAGESIZE][indexRegister % PAGESIZE] == 0) {
                        System.out.println("Error: Division by zero");
                        return;
                    }
                    accumulator /= memoryword[indexRegister / PAGESIZE][indexRegister % PAGESIZE];
                    break;
                case OperationCodes.MULTIPLY:
                    accumulator *= memoryword[operand / PAGESIZE][operand % PAGESIZE];
                    break;
                case OperationCodes.MULTIPLYX:
                    accumulator *= memoryword[indexRegister / PAGESIZE][indexRegister % PAGESIZE];
                    break;
                case OperationCodes.INC:
                    indexRegister++;
                    break;

                case OperationCodes.DEC:
                    indexRegister--;
                    break;

                    case OperationCodes.BRANCH:
                   // System.out.println("Branching to " + operand);
                    instructionCounter = operand -1;
                    break;

                case OperationCodes.BRANCHNEG:
                    //System.out.println("Checking BRANCHNEG condition: " + (accumulator < 0));
                    if (accumulator < 0) {
                        //System.out.println("Branching to " + operand);
                        instructionCounter = operand -1;
                        //continue;
                    }
                    break;
                case OperationCodes.BRANCHZERO:
                    //System.out.println("Checking BRANCHZERO condition: " + (accumulator == 0));
                    if (accumulator == 0) {
                        //System.out.println("Branching to " + operand);
                        instructionCounter = operand -1;
                
                    }
                    break;
                case OperationCodes.SWAP:
                    int temp = accumulator;
                    accumulator = indexRegister;
                    indexRegister = temp;
                    break;

                case OperationCodes.HALT:
                System.out.println("*** Simpletron execution terminated ***");
                    int startPage = operand / 100;
                    int endPage = operand % 100;
                    coreDump(startPage, endPage);
                    return;
                default:
                    System.out.println("Error: Invalid operation code");
                    return;
            }

            instructionCounter++;
           // instructionCount++;
        
            // Safety check to prevent infinite loops
           // if (instructionCount > 1000) {
           //    System.out.println("Error: Maximum instruction count reached. Possible infinite loop.");
              //  return;
           // }
        }
    }

    // Inner class for operation codes
    class OperationCodes {
        public static final int READ = 10;
        public static final int WRITE = 11;
        public static final int LOAD = 20;
        public static final int LOADIM = 21;
        public static final int LOADX = 22;
        public static final int LOADIDX = 23;
        public static final int STORE = 25;
        public static final int STOREIDX = 26;
        public static final int ADD = 30;
        public static final int ADDX = 31;
        public static final int SUBTRACT = 32;
        public static final int SUBTRACTX = 33;
        public static final int DIVIDE = 34;
        public static final int DIVIDEX = 35;
        public static final int MULTIPLY = 36;
        public static final int MULTIPLYX = 37;
        public static final int INC = 38;
        public static final int DEC = 39;
        public static final int BRANCH = 40;
        public static final int BRANCHNEG = 41;
        public static final int BRANCHZERO = 42;
        public static final int SWAP = 43;
        public static final int HALT = 45;
    }
}


        
    
 
