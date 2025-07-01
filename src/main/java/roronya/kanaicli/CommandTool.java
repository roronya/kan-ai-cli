package roronya.kanaicli;

import org.springframework.ai.tool.annotation.Tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class CommandTool {
    @Tool(
            name = "command_executor",
            description = """
                    Execute given command.
                    Command shoud be split.
                    For exampmle "ls -al" should be String[]{"ls", "-al"}.
                    Response is command's result.
                    """)
    public String execute(String[] command) {
        System.out.println("Executing command: " + String.join(" ", command));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // Redirect error stream to output stream
            Process process = processBuilder.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            process.waitFor();
            return output;
        } catch (IOException | InterruptedException e) {
            String errorMessage = "Error executing command: " + e.getMessage();
            System.err.println(errorMessage);
            return errorMessage;
        }
    }
}
