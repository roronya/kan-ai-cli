package roronya.kanaicli;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

public class CustomLogAdvisor implements CallAdvisor {
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";


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
        System.out.println(YELLOW + "BEFORE ==========================" + RESET);
        System.out.println(YELLOW + chatClientRequest.prompt() + RESET);
        System.out.println();

        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);

        System.out.println(GREEN + "AFTER ==========================" + RESET);
        System.out.println(GREEN + response.chatResponse().getResult().getOutput().getText() + RESET);
        System.out.println();

        return response;
    }
}