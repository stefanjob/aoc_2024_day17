import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
 
public class main {
   
    private static ArrayList<Integer> program = new ArrayList<>();
    private static int registerA = 0;
    private static int registerB = 0;
    private static int registerC = 0;
    private static int instructionPointer = 0;
    private static String output = "";
    
    public static void main(String[] args) {
        System.out.println("Advent of Code Day 16");
   
        boolean full = true;
        Scanner scanner = null;
 
        try {
            if (full) {
                scanner = new Scanner(new File("input_full.txt"));
            } else {
                scanner = new Scanner(new File("input_test.txt"));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        String line = null;
 
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            System.out.println(line);
 
            registerA = Integer.parseInt(line.split(": ")[1]);
            line = scanner.nextLine();
            System.out.println(line);
            registerB = Integer.parseInt(line.split(": ")[1]);
            line = scanner.nextLine();
            System.out.println(line);
            registerC = Integer.parseInt(line.split(": ")[1]);
            scanner.nextLine();
            line = scanner.nextLine();
            System.out.println(line);
            String nums[] = line.split(": ")[1].split(",");
            for (String s : nums) {
                program.add(Integer.parseInt(s));
            }
        }
       
        run();
        System.out.println("Output from program: " + output);
        
        int p[] = new int[program.size()];
        for (int i=0; i<program.size(); i++) {
            p[i] = program.get(i);
        }
        System.out.println("Lowest value for registerA= " + findInitialValueForA(p));
    }

    public static void run() {
        while (instructionPointer < program.size()-1) {
            int opcode = program.get(instructionPointer++);
            int operand = program.get(instructionPointer);
 
            switch (opcode) {
                case 0:
                    opcode0(operand);
                    instructionPointer++;
                    break;
                case 1:
                    opcode1(operand);
                    instructionPointer++;
                    break;
                case 2:
                    opcode2(operand);
                    instructionPointer++;
                    break;
                case 3:
                    opcode3(operand);
                    break;
                case 4:
                    opcode4(operand);
                    instructionPointer++;
                    break;
                case 5:
                    if (output.length() != 0) {
                        output = output.concat(",");
                    }
                    output = output.concat(opcode5(operand));
                    instructionPointer++;
                    break;
                case 6:
                    opcode5(operand);
                    instructionPointer++;
                    break;
                case 7:
                    opcode7(operand);
                    instructionPointer++;
                    break;
                default:
                    System.out.println("Should never get here !");
                    break;
            }
        }
    }
 
    public static int opcodeValue(int operand) {
        //Combo operands 0 through 3 represent literal values 0 through 3.
        if (operand < 4) {
            return operand;
        }
 
        // Combo operand 4 represents the value of register A.
        // Combo operand 5 represents the value of register B.
        // Combo operand 6 represents the value of register C.
        int ret = switch (operand) {
            case 4 -> registerA;
            case 5 -> registerB;
            case 6 -> registerC;
            default -> -1;
        };
 
        return ret;
    }
 
    // The adv instruction (opcode 0) performs division. The numerator is the value in the A register. The denominator is found by raising 2 to the power of the instruction's combo operand.
    // (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.) The result of the division operation is truncated to an integer and then written to the A register.
    public static void opcode0(int operand) {
        // 2 << opcodeValue(operand); would also work, using bitwise shift
        registerA = (int) (registerA / Math.pow(2, opcodeValue(operand)));
    }
 
    // The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's literal operand, then stores the result in register B.
    public static void opcode1(int operand) {
        registerB = registerB ^ operand;
    }
 
    // The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby keeping only its lowest 3 bits), then writes that value to the B register.
    public static void opcode2(int operand) {
        int v = opcodeValue(operand) % 8;
        if (v < 0) {
            v += 8;
        }
        registerB = v;
    }
 
    // The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is not zero, it jumps by setting the instruction pointer to the value of its literal operand;
    // if this instruction jumps, the instruction pointer is not increased by 2 after this instruction.
    public static void opcode3(int operand) {
        if ( registerA == 0) {
            // do nothing
        } else {
            instructionPointer = operand;
        }
    }
 
    // The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then stores the result in register B. (For legacy reasons, this instruction reads an operand but ignores it.)
    public static void opcode4(int operand) {
        registerB = registerB ^ registerC;
    }
 
    // The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value. (If a program outputs multiple values, they are separated by commas.)
    public static String opcode5(int operand) {
        int ret = opcodeValue(operand) % 8;
        if (ret < 0) {
            ret += 8;
        }
        return String.valueOf(ret);
    }
 
    // The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is stored in the B register. (The numerator is still read from the A register.)
    public static void opcode6(int operand) {
        // 2 << opcodeValue(operand); would also work, using bitwise shift
        registerB = (int) (registerA / Math.pow(2, opcodeValue(operand)));
    }
 
    // The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is stored in the C register. (The numerator is still read from the A register.)
    public static void opcode7(int operand) {
        // 2 << opcodeValue(operand); would also work, using bitwise shift
        registerC = (int) (registerA / Math.pow(2, opcodeValue(operand)));
    }

    private static int findInitialValueForA(int[] program) {
        int regA = 0;

        // Start with the expected output sequence
        List<Integer> expectedOutput = new ArrayList<>();
        for (int i = 0; i < program.length; i += 2) {
            if (program[i] == 5) { // out instruction
                expectedOutput.add(program[i + 1]);
            }
        }

        // Work backwards through the expected outputs to deduce the initial A
        for (int i = expectedOutput.size() - 1; i >= 0; i--) {
            int operand = expectedOutput.get(i);
            regA = reverseModulo(operand, regA);
        }

        return regA;
    }

    // Reverses the modulo operation to find the original A value
    private static int reverseModulo(int operand, int currentValue) {
        // Calculate the required value for A before the modulo
        int literalValue = reverseComboValue(operand);
        while (literalValue < currentValue) {
            literalValue += 8;
        }
        return literalValue;
    }

    // Reverses the division operation to deduce the original A
    private static int reverseDivision(int operand, int currentValue) {
        int divisor = (int) Math.pow(2, reverseComboValue(operand));
        return currentValue * divisor;
    }

    // Reverse the combo operand to its literal or register value
    private static int reverseComboValue(int operand) {
        switch (operand) {
            case 0:
            case 1:
            case 2:
            case 3:
                return operand;
            case 4:
                return 1; // Register A (tracing back to expected value 1)
            case 5:
            case 6:
                return 0; // Registers B and C start at 0
            case 7:
                throw new IllegalArgumentException("Operand 7 is invalid.");
            default:
                throw new IllegalArgumentException("Invalid combo operand: " + operand);
        }
    }
}
 