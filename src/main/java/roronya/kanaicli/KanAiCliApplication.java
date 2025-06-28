package roronya.kanaicli;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class KanAiCliApplication implements CommandLineRunner {

    private final ChatClient chatClient;
    private final List<Message> conversationHistory = new ArrayList<>();

    public KanAiCliApplication(ChatClient.Builder chatClientBuilder) {
        chatClient = chatClientBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(KanAiCliApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("oh-my-logo", "KAN AI CLI", "--filled", "--color");
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.equals("quit")) {
                break;
            }

            // Add user message to conversation history
            conversationHistory.add(new UserMessage(input));

            // Create a prompt with the conversation history
            String response = chatClient
                    .prompt()
                    .system("You are a helpful assistant.")
                    .messages(conversationHistory)
                    .call()
                    .content();

            // Add assistant response to conversation history
            conversationHistory.add(new AssistantMessage(response));

            System.out.println(response);
        }
        scanner.close();
    }
}
