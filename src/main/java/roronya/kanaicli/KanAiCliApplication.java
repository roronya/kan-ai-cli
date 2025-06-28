package roronya.kanaicli;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@SpringBootApplication
public class KanAiCliApplication implements CommandLineRunner {

    private ChatClient chatClient;
    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;

    private final List<Message> conversationHistory = new ArrayList<>();

    @Autowired
    public KanAiCliApplication(OpenAiChatModel openAiChatModel, OllamaChatModel ollamaChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
        chatClient = ChatClient.builder(openAiChatModel).build();
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

            if (input.equals("\\history")) {
                System.out.println("Conversation History:");
                for (int i = 0; i < conversationHistory.size(); i++) {
                    Message message = conversationHistory.get(i);
                    if (message instanceof UserMessage) {
                        System.out.println("User: " + message.toString());
                    } else if (message instanceof AssistantMessage) {
                        System.out.println("Assistant: " + message.toString());
                    }
                }
                continue;
            }

            if (input.equals("\\models")) {
                System.out.println(openAiChatModel.toString());
                System.out.println(ollamaChatModel.toString());
                continue;
            }

            if (input.startsWith("\\model ")) {
                String modelName = input.substring(7).trim().toLowerCase();
                switch (modelName) {
                    case "openai":
                        chatClient = ChatClient.builder(openAiChatModel).build();
                        System.out.println("Switched to OpenAI model.");
                        break;
                    case "ollama":
                        chatClient = ChatClient.builder(ollamaChatModel).build();
                        System.out.println("Switched to Ollama model.");
                        break;
                    default:
                        System.out.println("Unknown model: " + modelName);
                        System.out.println("Available models: openai, ollama");
                }
                continue;
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
