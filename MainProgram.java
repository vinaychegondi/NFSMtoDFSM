
import java.io.*;

public class MainProgram {

    public static void main(String[] args) {
       //System.out.println("entered main program");
       if (args.length != 2) {
        System.out.println("Usage: java MainProgram <pattern> <input string file>");
        return;
    }
    String patternFile = args[0];        // The pattern file to build the NDFSM
        String inputStringFile = args[1];    // Input file containing the string to process
        String dfsmSpecFile = "dfsmoutput.txt"; // Automatically generated DFSM output file

        String ndfsmSpecFile = "ndfsmoutput.txt";  // file to store the NDFSM specification

        System.out.println();
        System.out.println();


        // Step 1: Call NDFSMBuilder to create an NDFSM specification from the pattern
        System.out.println("Step 1: Generating NDFSM specification");
        System.out.println();

        String[] ndfsmBuilderArgs = {ndfsmSpecFile, patternFile};
        try {
            NDFSMBuilder.main(ndfsmBuilderArgs);
        } catch (Exception e) {
            System.out.println("Error during NDFSM generation: " + e.getMessage());
            return;
        }

        System.out.println();
        System.out.println();

         // Step 2.1: Check if NDFSM exceeds number of alphabet transitions
         try (BufferedReader reader = new BufferedReader(new FileReader(ndfsmSpecFile))) {
            String alphabetLine = reader.readLine();
            if (alphabetLine == null) {
                throw new Exception("Error: Alphabet missing in NDFSM specification.");
            }

            String[] alphabet = alphabetLine.split(" ");
            int numSymbols = alphabet.length;

            // Now check the transitions
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[") && line.endsWith("]")) {
                    String[] transitions = line.split("\\]\\s*\\[");
                    if (transitions.length > numSymbols + 1) {
                        throw new Exception("Error: Transition count exceeds number of alphabet symbols.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }


          // Step 2.2: Call NDFSMtoDFSM to convert the NDFSM specification to a DFSM specification
          System.out.println("Step 2: Converting NDFSM to DFSM");
          System.out.println();

          String[] ndfsmToDfsmArgs = {ndfsmSpecFile, dfsmSpecFile};
          try {
              NDFSMtoDFSM.main(ndfsmToDfsmArgs);
          } catch (Exception e) {
              System.out.println("Error during NDFSM to DFSM conversion: " + e.getMessage());
              return;
          }

          System.out.println();
          System.out.println();

           // Step 3: Call EXDFSM to test the DFSM specification with the input string
        System.out.println("Step 3: Testing the input string on the DFSM");
        System.out.println();

        String[] exDfsmArgs = {dfsmSpecFile, inputStringFile};
        try {
            EXDFSM.main(exDfsmArgs);
        } catch (Exception e) {
            System.out.println("Error during DFSM testing: " + e.getMessage());
        }


    }
}
