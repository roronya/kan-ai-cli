package roronya.kanaicli;

import org.springframework.ai.tool.annotation.Tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CommandTool {
    private Scanner scanner;

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";

    public CommandTool() {
        this.scanner = new Scanner(System.in);
    }

    // Constructor for testing purposes
    public CommandTool(Scanner scanner) {
        this.scanner = scanner;
    }

    @Tool(name = "command_executor", description = """
            Executes the given shell command as bash -c argument.
            Returns the command's output as a string, or an error message if execution fails.
            """)
    public String execute(String command) {
        System.out.println(RED + "About to execute command: " + command + RESET);
        System.out.print("Do you want to proceed? (y/n): ");

        String response = scanner.nextLine().trim().toLowerCase();
        boolean proceed = response.equals("y") || response.equals("yes");

        if (proceed) {
            System.out.println("Executing command: bash -c " + command);
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
                processBuilder.redirectErrorStream(true); // Redirect error stream to output stream
                Process process = processBuilder.start();

                // Capture the output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = reader.lines().collect(Collectors.joining(System.lineSeparator()));

                process.waitFor();
                System.out.println(output);
                return output;
            } catch (IOException | InterruptedException e) {
                String errorMessage = "Error executing command: " + e.getMessage();
                System.err.println(errorMessage);
                return errorMessage;
            }
        } else {
            return "Command execution cancelled by user.";
        }
    }
}
