import java.util.*;
import java.io.*;

public class EXDFSM {
	public static void main(String[] args) {
		// it checks the correct number of arguments/inputs
		if (args.length != 2) {
			System.out.println("Usage: java EXDFSM <dfsm specification file> <input string file>");
			return;
		}
		// file names for the arguments
		String specFileName = args[0];
		String inputFileName = args[1];
		// print the given arguments
		// System.out.println("DFSM file:" + specFileName);
		// System.out.println("Input string:" + inputFileName);

		// variables to hold dfsm configuration
		String[] alphabet = null;
		int[][] transitionTable = null;
		Set<Integer> acceptingStates = new HashSet<>();
		Set<Integer> validStates = new HashSet<>(); // To track valid states in the transition table
		// Read the dfsm.txt
		try {
			// System.out.println("try block executed succesfully");

			// open dfsm.txt file for reading
			BufferedReader specFile = new BufferedReader(new FileReader(specFileName));

			// read and print the alphabet line
			// System.out.println("Alphabet:");
			String line = specFile.readLine();
			// System.out.println(line);
			// check if file is empty
			if (line == null || line.trim().isEmpty()) {
				System.out.println("Error: DFSM file is empty or missing the alphabet");
			}
			alphabet = line.trim().split("\\s+");

			// check if all alphabets are valid single characters
			for (String symbol : alphabet) {
				if (symbol.length() != 1 || !Character.isLetter(symbol.charAt(0))) {
					System.out.println("Error: invalid alphabet symbol:'" + symbol + "'");
					return;
				}
			}
			// System.out.println(Arrays.toString(alphabet));
			// Read and skip the empty line after the alphabet
			line = specFile.readLine();
			if (line != null && !line.trim().isEmpty()) {
				System.out.println("Error: Expected an empty line after the alphabet");
				return;
			}
			// read and print the transition table
			// System.out.println("Transition table: \n");
			List<int[]> transitions = new ArrayList<>();
			// boolean foundTransitionTable=false;
			// System.out.println("before entering while loop");

			while ((line = specFile.readLine()) != null && !line.trim().isEmpty()) {

				// System.out.println("entered while loop");
				String[] parts = line.trim().split("\\s+");
				int[] transitionRow = new int[parts.length];
				// Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
				for (int i = 0; i < parts.length; i++) {
					try {
						int value = Integer.parseInt(parts[i]);
						if (value < 0) {
							System.out.println("Error: transition value '" + value + "'cannot be negative ");
							return;
						}
						transitionRow[i] = value;
						validStates.add(value); // adding validstate to set
					} catch (NumberFormatException e) {
						System.out.println("Error: transition value '" + parts[i] + "' is not a valid integer");
						return;
					}
				}
				transitions.add(transitionRow);
				// System.out.println("Reading line:[" + line + "]");
				// if(!line.trim().isEmpty()){
				// System.out.println(Arrays.toString(transitionRow));
				// System.out.println(line.trim());
			}
			/*
			 * if(!foundTransitionTable){
			 * System.out.println("Error: no transition table");
			 * return;
			 * }
			 */
			// Check if the transition table is empty
			if (transitions.isEmpty()) {
				System.out.println("Error: Transition table is empty");
				return;
			}
			transitionTable = transitions.toArray(new int[transitions.size()][]);

			for (int[] row : transitionTable) {
				if (row.length != alphabet.length) {
					System.out.println("Error: Transition table row length does not match alphabet length");
					return;
				}
			}
			// read and print accepting state.
			// System.out.println("Accepting states:");
			if ((line = specFile.readLine()) != null && !line.trim().isEmpty()) {
				// System.out.println(line);

				// Check for leading spaces
				if (line.startsWith(" ")) {
					System.out.println("Error: Accepting states line contains leading spaces");
					return;
				}
				String[] states = line.trim().split("\\s+");
				for (String state : states) {
					try {
						int stateNumber = Integer.parseInt(state);

						// check if accepting state is a valid state, throw an error
						if (!validStates.contains(stateNumber)) {
							System.out.println(
									"Error: Accepting state '" + stateNumber + "' is not in transition table   ");
							return;
						}

						acceptingStates.add(stateNumber);
					} catch (NumberFormatException e) {
						System.out.println("Error: Accepting state  '" + state + "' is not an integer");
						return;
					}
				}
				// System.out.println(acceptingStates);
			} else {
				System.out.println("Error: Accepting states line is empty");
			}
			specFile.close();
			// read the input string.
			BufferedReader inputFile = new BufferedReader(new FileReader(inputFileName));
			String inputString = inputFile.readLine();
			System.out.println("input string:" + inputString);

	
			// check if input string is empty
			if (inputString.isEmpty()) {
				System.out.println("Error: input string is empty");
				return;
			}
			// inputFile.close();
		

			// processing the input string
			int currentState = 1; // assume intial state is 1
			for (char input : inputString.toCharArray()) {
				int inputIndex = -1;
				// Check if the input character is an alphabet
				if (!Character.isLetter(input)) {
					System.out.println("Error: Invalid input symbol: '" + input + "'");
					return;
				}
			
			//	System.out.println("inputString char:" + input);
				for (int i = 0; i < alphabet.length; i++) {
					// System.out.println(input);
					// System.out.println(i);
				//	System.out.println("transition table alphabet:" + alphabet[i]);
					if (alphabet[i].charAt(0) == input) {
						inputIndex = i;
						break;
					}
				}
				if (inputIndex == -1) {
					System.out.println("error invalid input symbol:" + input);
					return;
				}
				// System.out.println("input string:" +input);
				currentState = transitionTable[currentState - 1][inputIndex];
				//System.out.println("currentState:" + currentState);
				inputFile.close();
			}
			// check if current state is accepting or not
			if (acceptingStates.contains(currentState)) {
				System.out.println("EXDFSM machine accepts the input string  ( YES )");
			} else {
				System.out.println("EXDFSM machine does not accept the input string ( NO )");
			}

		} catch (FileNotFoundException e) {
			System.out.println("error file not found");
		} catch (IOException e) {
			System.out.println("error i/o error");
		}
	}
}
