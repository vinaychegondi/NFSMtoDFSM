import java.io.*;
import java.util.*;

public class NDFSMtoDFSM {

    public static void main(String[] args) {
        // Check if the correct number of command-line arguments is given
        if (args.length != 2) {
            System.out.println("Usage: java NDFSMtoDFSMConverter <input_file> <output_file>");
            return;
        }

        String inputFile = args[0]; // ndfsm input
        String outputFile = args[1]; //dfsm output

        try (BufferedReader fileReader = new BufferedReader(new FileReader(inputFile)); //opens to read input
             BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFile))) { //opens to write output

            // Step 1: Read the alphabet from the file
            String alphabetStr = fileReader.readLine();
            if (alphabetStr == null || alphabetStr.trim().isEmpty()) {
                throw new IOException("Error: Alphabet line is missing or empty.");
            }
            String[] alphabet = splitString(alphabetStr.trim(), ' '); //split alphabet string by soaces to get individual synmbols

            // Number of input symbols (alphabet size)
            int symbolCount = alphabet.length;

            // Step 2: Parse the state transitions from the file
            List<String[]> stateTransitions = new ArrayList<>();
            String currentLine;
            while ((currentLine = fileReader.readLine()) != null) {
                currentLine = currentLine.trim();
                if (currentLine.isEmpty()) continue;

                if (currentLine.startsWith("[") && currentLine.endsWith("]")) {  //check if states are in []
                    String[] parsedTransitionRow = new String[symbolCount];
                    String[] transitionParts = currentLine.split("\\]\\s*\\[");  //split line by []

                    if (transitionParts.length > symbolCount) {
                        throw new IOException("Error: Too many transitions for the number of input symbols.");
                    }

                    for (int i = 0; i < transitionParts.length && i < symbolCount; i++) {
                        String part = transitionParts[i].replace("[", "").replace("]", "").trim(); 
                        parsedTransitionRow[i] = part.isEmpty() ? "[]" : "[" + part + "]";
                    }

                    stateTransitions.add(parsedTransitionRow);   //add parsed transitions to stateTransitions
                } else {
                    break;
                }
            }

            // Step 3: Extract the accepting states from the next line
            String acceptingStatesStr = currentLine;
            if (acceptingStatesStr == null || acceptingStatesStr.trim().isEmpty()) {
                throw new IOException("Error: Accepting states line is missing or empty.");
            }
            String[] acceptingStates = splitString(acceptingStatesStr.trim(), ' ');

            // Step 4: Start the conversion from NDFSM to DFSM
            List<String[]> dfsmStateTransitions = new ArrayList<>();
            Map<String, Integer> stateMap = new HashMap<>();
            List<Set<Integer>> discoveredStates = new ArrayList<>();

            // Initial state for the NDFSM, which is state 1
            Set<Integer> initialNDFSMState = new HashSet<>();
            initialNDFSMState.add(1);
            discoveredStates.add(initialNDFSMState); //add it to list
            stateMap.put(setToCSV(initialNDFSMState), 1);  // map new DFSM states

            // Process newly discovered states
            int nextDFSMStateId = 2; // DFSM state numbering starts at 2
            for (int i = 0; i < discoveredStates.size(); i++) {
                Set<Integer> currentNDFSMState = discoveredStates.get(i);
                String[] currentDFSMTransitions = new String[symbolCount];

                // Handle transitions for each symbol
                for (int j = 0; j < alphabet.length; j++) {
                    Set<Integer> nextState = new HashSet<>();
                    
                    // Calculate reachable states on the current symbol
                    for (Integer ndState : currentNDFSMState) {
                        String transition = stateTransitions.get(ndState - 1)[j];
                        if (!transition.equals("[]")) {
                            String[] reachableStates = splitString(transition.replace("[", "").replace("]", "").trim(), ',');
                            for (String reachable : reachableStates) {
                                nextState.add(Integer.parseInt(reachable.trim()));
                            }
                        }
                    }

                    // Check if this new state is already mapped
                    String nextStateKey = setToCSV(nextState);
                    if (!stateMap.containsKey(nextStateKey)) {
                        stateMap.put(nextStateKey, nextDFSMStateId++);
                        discoveredStates.add(nextState);
                    }

                    // Save the DFSM state transitions
                    currentDFSMTransitions[j] = String.valueOf(stateMap.get(nextStateKey));
                }

                // Store the transitions for the current DFSM state
                dfsmStateTransitions.add(currentDFSMTransitions);
            }

            // Step 5: Find accepting states for the DFSM
            Set<Integer> dfsmAcceptStates = new HashSet<>();
            for (Set<Integer> stateSet : discoveredStates) {
                for (String accState : acceptingStates) {
                    if (stateSet.contains(Integer.parseInt(accState))) {
                        dfsmAcceptStates.add(stateMap.get(setToCSV(stateSet)));
                        break;
                    }
                }
            }

            // Step 6: Write DFSM details to the output file
            fileWriter.write(String.join(" ", alphabet) + "\n\n"); // write the alphabet

            // Write state transitions of DFSM
            for (String[] dfsmTransition : dfsmStateTransitions) {
                for (int j = 0; j < dfsmTransition.length; j++) {
                    fileWriter.write(dfsmTransition[j] + (j < dfsmTransition.length - 1 ? " " : ""));
                }
                fileWriter.write("\n");
            }
            fileWriter.write("\n");

            for (Integer acceptState : dfsmAcceptStates) {
                fileWriter.write(acceptState + " "); //write accepting states of dfsm
            }
            fileWriter.write("\n");

            System.out.println("DFSM successfully written to " + outputFile);

        } catch (IOException e) {
            System.err.println("File processing error: " + e.getMessage());
        }
    }

    // Helper function: Splits a string by a specific delimiter
    private static String[] splitString(String input, char delimiter) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (char ch : input.toCharArray()) {
            if (ch == delimiter) {
                if (buffer.length() > 0) {
                    parts.add(buffer.toString());
                    buffer.setLength(0);
                }
            } else {
                buffer.append(ch);
            }
        }

        if (buffer.length() > 0) {
            parts.add(buffer.toString()); //add last part if it exists
        }

        return parts.toArray(new String[0]);
    }

    // Helper function: Converts a set to a CSV-like string
    private static String setToCSV(Set<Integer> stateSet) {
        StringBuilder result = new StringBuilder();
        for (Integer state : stateSet) {
            result.append(state).append(",");
        }
        return result.toString();
    }
}
