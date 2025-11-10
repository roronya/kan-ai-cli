package roronya.kanaicli.tool;

import org.junit.jupiter.api.Test;
import roronya.kanaicli.CommandTool;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandToolTest {

    // Helper method to create a Scanner that always returns "y"
    private Scanner createYesScanner() {
        return new Scanner(new ByteArrayInputStream("y\n".getBytes()));
    }

    @Test
    void testExecuteEchoCommand() {
        CommandTool commandTool = new CommandTool(createYesScanner());

        // Test with echo command
        String result = commandTool.execute("echo Hello, World!");

        // The result should contain "Hello, World!"
        assertTrue(result.contains("Hello, World!"), "Echo command should return the input text");
    }

    @Test
    void testExecuteListCommand() {
        CommandTool commandTool = new CommandTool(createYesScanner());

        // Test with ls command
        String result = commandTool.execute("ls -al");

        // The result should not be empty
        assertFalse(result.isEmpty(), "List command should return non-empty result");
    }

    @Test
    void testExecuteInvalidCommand() {
        CommandTool commandTool = new CommandTool(createYesScanner());

        // Test with an invalid command
        String invalidCommand = "thisCommandDoesNotExist";
        String result = commandTool.execute(invalidCommand);

        // The result should contain an error message
        assertTrue(result.contains("command not found"), "Invalid command should return an error message");
    }
}
