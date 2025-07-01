package roronya.kanaicli.tool;

import org.junit.jupiter.api.Test;
import roronya.kanaicli.CommandTool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandToolTest {

    @Test
    void testExecuteEchoCommand() {
        CommandTool commandTool = new CommandTool();

        // Test with echo command
        String[] echoCommand = {"echo", "Hello, World!"};
        String result = commandTool.execute(echoCommand);

        // The result should contain "Hello, World!"
        assertTrue(result.contains("Hello, World!"), "Echo command should return the input text");
    }

    @Test
    void testExecuteListCommand() {
        CommandTool commandTool = new CommandTool();

        // Test with ls command
        String[] lsCommand = {"ls", "-la"};
        String result = commandTool.execute(lsCommand);

        // The result should not be empty
        assertFalse(result.isEmpty(), "List command should return non-empty result");
    }

    @Test
    void testExecuteInvalidCommand() {
        CommandTool commandTool = new CommandTool();

        // Test with an invalid command
        String[] invalidCommand = {"thisCommandDoesNotExist"};
        String result = commandTool.execute(invalidCommand);

        // The result should contain an error message
        assertTrue(result.contains("Error executing command"), "Invalid command should return an error message");
    }
}