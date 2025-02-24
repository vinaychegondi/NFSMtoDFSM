import java.io.*;
import java.util.*;

public class NDFSMBuilder {
    public static void main(String[] args) {
        // File names for arguments
        String outputFileName = args[0]; //output file name for 1st argument
        String inputFileName = args[1];     // output filename for 2nd argument

        // Generate errors if inputs are less or more than required
        if (args.length != 2) {
            System.out.println("Error: Usage java NDFSMBuilder <NDFSM output File> <NDFSM input File>");
            return;
        }

        try {
            // Read the given input pattern
            BufferedReader inputFile = new BufferedReader(new FileReader(inputFileName)); //opens input file for reading
            String line = inputFile.readLine();      //reading 1st line

            // Check if file is empty
            if (line == null || line.trim().isEmpty()) {  //check if input file is empty or contains only spaces
                System.out.println("Error: NDFSM input file is empty or missing the alphabet");
                inputFile.close();  //close input file
                return;
            }

            // Check for invalid symbols
            if (!isValidInput(line)) {   //check for not valid characters
                System.out.println(
                        "Error: Input string contains invalid symbols.");
                inputFile.close();
                return;
            }

            // Creating a set for unique alphabets
            Set<Character> alphabetSet = new LinkedHashSet<>();   //set to store unique characters
            for (char c : line.toCharArray()) {
                alphabetSet.add(c);  //adds each charcters to set
            }

            int currentState = 1;  //initializing current state

            // Write the output file
            BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFileName)); //open output file for writting

            // Write unique alphabets in a row in output file
            for (char c : alphabetSet) {
                outputFile.write(c + " ");  //each unique alphabet from the set with spaces
            }
            outputFile.newLine(); 
            outputFile.newLine();

            // Print transition table
            for (int i = 1; i <= line.length(); i++) {  
                char currentChar = line.charAt(i - 1); //currentcharacter from input string
                int nextState = currentState + 1;

                System.out.println("State " + i + ": transitioning on '" + currentChar + "' -> State " + (i + 1));

                for (char c : alphabetSet) {
                    if (c == currentChar && i == 1) {  //check if alphabet sets 1st char is equall to currentcharacter(input strings) and if its a start state
                        outputFile.write("[" + currentState + "," + nextState + "] ");
                    } else if (currentState == 1) {  //check only if its 1st state
                        outputFile.write("[1] ");
                    } else if (c == currentChar) { //check if alphabet sets 1st char is equall to currentcharacter(input strings)
                        outputFile.write("[" + nextState + "] ");
                    } else {
                        outputFile.write("[] "); //empty transition
                    }
                }

                outputFile.newLine();
                currentState = nextState;
            }

            // Final state with no transitions
            int finalState = currentState;
            for (char c : alphabetSet) {
                outputFile.write("[" + finalState + "] ");
            }
            outputFile.newLine();

            // Accepting state
            outputFile.newLine();
            outputFile.write(String.valueOf(finalState));  // Write the accepting state without brackets

            outputFile.close();
            inputFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found");
        } catch (IOException e) {
            System.out.println("Error: I/O error");
        }
    }

    // Method to validate input (A-Z and a-z)
    private static boolean isValidInput(String input) {
        return input.matches("^[a-zA-Z]+$");  //returns true if input containds only letter a-z or A-Z
    }
}