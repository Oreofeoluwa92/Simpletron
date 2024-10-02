import java.util.Scanner;

public class Simpletron {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            SimpletronV2 simpletron = new SimpletronV2();
            
            System.out.println("*** Welcome to Simpletron V2! ***");

            System.out.print("*** Do you have a file that contains your SML program (Y/N)? *** ");
            String response = scanner.nextLine();  // Use the scanner directly here

            if (response.equalsIgnoreCase("Y")) {
                System.out.print("Filename: ");
                String filename = scanner.nextLine();  // Use the scanner for file input
                simpletron.loadProgram(filename);
            } else {
               // System.out.println("*** Please enter your program one instruction (or data word) at a time ***");
                simpletron.manualProgramInput();  // The input for the program is handled inside the SimpletronV2 class
            }

            simpletron.execute();
        }
    }
    
}
