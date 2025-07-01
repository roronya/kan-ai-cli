package roronya.kanaicli;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

public class CustomLogAdvisor implements CallAdvisor {
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    // Debug flag to control logging
    private static boolean debugEnabled = false;

    // Method to enable/disable debug logging
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    // Method to check if debug is enabled
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }


    @Override
    public String getName() {
        return "CustomLogAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        // Only log if debug is enabled
        if (debugEnabled) {
            System.out.println(YELLOW + "BEFORE ==========================" + RESET);
            System.out.println(YELLOW + chatClientRequest.prompt() + RESET);
            System.out.println();
        }

        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);

        // Only log if debug is enabled
        if (debugEnabled) {
            System.out.println(GREEN + "AFTER ==========================" + RESET);
            System.out.println(GREEN + response.chatResponse().getResult().getOutput().getText() + RESET);
            System.out.println();
        }

        return response;
    }
}
