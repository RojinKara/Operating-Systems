package taskA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Task A: Implementing cat, cut, sort, uniq and wc commands.
 * The program reads the user's input and executes the specified command.
 * The program supports the use of pipes to pass the output of
 * one command as input to another command.
 */

public class TaskA {

    /**
     * Main method to run the program.
     * @param args Command line arguments
     * @throws IOException If an I/O error occurs
     */

    public static void main(String[] args) throws IOException {

        System.out.println("Operating Systems Coursework");
        System.out.println("Name: Rojin Kara");
        System.out.println("Please enter your commands - cat, cut, sort, uniq, wc or |");

        String filePath = "taskA.txt";
        String commandLine;

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        // Loop to keep the program running

        while (true) {
            System.out.print(">> ");
            commandLine = console.readLine().trim();
            if (commandLine.isEmpty())
                continue;


            String[] individualCommands = commandLine.split("\\|");
            List<String> pipe = new ArrayList<>();

            // Loop through each command in the pipeline

            for (int i = 0; i < individualCommands.length; i++) {
                List<String> tokens = new ArrayList<>(Arrays.asList(individualCommands[i]
                        .trim().split("\\s+")));
                String command = tokens.remove(0);

                // Switch statement to determine which command to run

                /**
                 * cat command: Reads the file and outputs the contents.
                 * cut command: Extracts the specified fields from the input.
                 * sort command: Sorts the input.
                 * uniq command: Removes duplicate lines from the input.
                 * wc command: Counts the number of lines, words and bytes in the input.
                 */

                switch (command) {
                    case "cat":
                        if (tokens.size() != 1) {
                            System.out.println("Usage: cat filename");
                            continue;
                        }
                        try {
                            pipe = cat(tokens);
                        } catch (Exception e) {
                            System.err.println("Error reading file");
                        }
                        break;

                    case "cut":
                        if (tokens.size() < 2 || !tokens.contains("-f")) {
                            System.out.println("Usage: cut -f field [-d delimiter] [filename]");
                            continue;
                        }
                        try {
                            pipe = cut(tokens, pipe);
                        } catch (Exception e) {
                            System.err.println("Error executing cut command");
                        }
                        break;

                    case "sort":
                        try {
                            pipe = sort(tokens, pipe);
                        } catch (Exception e) {
                            System.err.println("Error executing sort command");
                            e.printStackTrace();
                        }
                        break;

                    case "uniq":
                        try {
                            pipe = uniq(tokens, pipe);
                        } catch (Exception e) {
                            System.err.println("Error executing uniq command");
                            e.printStackTrace();
                        }
                        break;

                    case "wc":
                        try {
                            pipe = wc(tokens, pipe);
                        } catch (IOException e) {
                            System.err.println("Error executing wc command");
                            e.printStackTrace();
                        }
                        break;

                    default:
                        System.out.println("Command not recognized: " + command);
                        break;
                }
            }

            for (String s : pipe) {
                System.out.println(s);
            }
        }
    }

    // Methods for each command

    /**
     * cat command: Reads the file and outputs the contents.
     * @param args Command line arguments
     * @return List of strings containing the file contents
     * @throws Exception If an I/O error occurs
     */

    private static List<String> cat(List<String> args) throws Exception {
        List<String> output = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(args.get(0)));
        String line;
        while ((line = reader.readLine()) != null) {
            output.add(line);
        }
        reader.close();

        return output;
    }

    /**
     * cut command: Extracts the specified fields from the input.
     * @param args Command line arguments
     * @param input List of strings containing the input
     * @return List of strings containing the extracted fields
     * @throws Exception If an I/O error occurs
     */

    private static List<String> cut(List<String> args, List<String> input) throws Exception {
        int fieldIndex = args.indexOf("-f");
        int delimiterIndex = args.indexOf("-d");
        String field = "";
        String delimiter = "\",\"";
        List<Integer> fields;
        List<String> output = new ArrayList<>();

        if (fieldIndex != -1) field = args.get(args.indexOf("-f") + 1);
        if (delimiterIndex != -1) delimiter = args.get(args.indexOf("-d") + 1);

        fields = parseField(field);
        delimiter = delimiter.substring(1, delimiter.length() - 1);

        if (input.isEmpty()) {
            BufferedReader reader = new BufferedReader(new FileReader(args.get(args.size() - 1)));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(delimiter);
                List<String> temp = new ArrayList<>();

                for (int i : fields) {
                    if (i >= 1 && i < parts.length) {
                        temp.add(parts[i - 1]);
                    }
                }
                output.add(String.join(delimiter, temp));
            }
            reader.close();
        } else {
            for (String s : input) {
                String[] parts = s.split(delimiter);
                List<String> temp = new ArrayList<>();

                for (int i : fields) {
                    if (i >= 1 && i < parts.length) {
                        temp.add(parts[i - 1]);
                    }
                }
                output.add(String.join(delimiter, temp));
            }
        }

        return output;
    }

    /**
     * sort command: Sorts the input based on alphabetical order.
     * @param args Command line arguments
     * @param input List of strings containing the input
     * @return List of strings containing the sorted input
     * @throws Exception If an I/O error occurs
     */

    private static List<String> sort(List<String> args, List<String> input) throws Exception {
        List<String> lines = new ArrayList<>();

        if (input.isEmpty() && !args.isEmpty()) {
            BufferedReader reader = new BufferedReader(new FileReader(args.get(0)));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            reader.close();
        } else {
            lines = input;
        }

        Collections.sort(lines);

        return lines;
    }

    /**
     * uniq command: Removes duplicate lines from the input.
     * @param args Command line arguments
     * @param input List of strings containing the input
     * @return List of strings containing the unique lines
     * @throws Exception If an I/O error occurs
     */

    private static List<String> uniq(List<String> args, List<String> input) throws Exception {
        List<String> output = new ArrayList<>();
        List<String> temp = new ArrayList<>();

        if (input.isEmpty() && !args.isEmpty()) {
            BufferedReader reader = new BufferedReader(new FileReader(args.get(0)));
            String line;

            while ((line = reader.readLine()) != null) {
                temp.add(line);
            }
            reader.close();
        } else {
            temp = input;
        }

        String current = "";
        for (String line : temp) {
            if (!current.equals(line)) {
                current = line;
                output.add(line);
            }
        }

        return output;
    }

    /**
     * wc command: Counts the number of lines, words and bytes in the input.
     * @param args Command line arguments
     * @param input List of strings containing the input
     * @return List of strings containing the line, word and byte count
     * @throws IOException If an I/O error occurs
     */

    private static List<String> wc(List<String> args, List<String> input) throws IOException {
        boolean lFlag = args.remove("-l");
        List<String> output = new ArrayList<>();
        int lines = 0;
        int words = 0;
        int bytes = 0;

        if (input.isEmpty() && !args.isEmpty()) {
            BufferedReader reader = new BufferedReader(new FileReader(args.get(0)));
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                bytes += line.getBytes().length;
                words += line.split("\\s+").length;
            }
            reader.close();
        } else {
            for (String s : input) {
                lines++;
                bytes += s.getBytes().length;
                words += s.split("\\s+").length;
            }
        }

        if (lFlag) {
            output.add(String.valueOf(lines));
        } else {
            output.add(lines + " " + words + " " + bytes);
        }

        return output;
    }

    /**
     * Parses the field argument for the cut command.
     * @param field The field argument
     * @return List of integers containing the fields
     */

    private static List<Integer> parseField(String field) {
        List<Integer> intFields = new ArrayList<>();
        String[] rawFields = field.split(",");
        for (String part : rawFields) {
            if (part.contains("-")) {
                String[] range = part.split("-");

                for (int i = Integer.parseInt(range[0]); i <= Integer.parseInt(range[1]); i++) {
                    intFields.add(i);
                }
            } else {
                intFields.add(Integer.parseInt(part));
            }
        }
        return intFields;
    }
}
