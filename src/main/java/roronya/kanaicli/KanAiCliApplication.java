package roronya.kanaicli;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@SpringBootApplication
public class KanAiCliApplication implements CommandLineRunner {

    private ChatClient chatClient;
    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;

    private final List<Message> conversationHistory = new ArrayList<>();

    private String systemPrompt;

    @Autowired
    public KanAiCliApplication(
            OpenAiChatModel openAiChatModel,
            OllamaChatModel ollamaChatModel,
            @Value("classpath:prompt.st") Resource promptResource
    ) {
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
        chatClient = ChatClient.builder(openAiChatModel).build();
        systemPrompt = new PromptTemplate(promptResource).render();
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
        label:
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            switch (input) {
                case "":
                    continue;
                case "quit":
                    break label;
                case "\\history":
                    System.out.println("Conversation History:");
                    for (Message message : conversationHistory) {
                        if (message instanceof UserMessage) {
                            System.out.println("User: " + message);
                        } else if (message instanceof AssistantMessage) {
                            System.out.println("Assistant: " + message);
                        }
                    }
                    continue;
                case "\\models":
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

            String response = chatClient
                    .prompt()
                    .system(systemPrompt)
                    .user(input)
                    .messages(conversationHistory)
                    .call()
                    .content();

            // Create a prompt with the conversation history
            conversationHistory.add(new UserMessage(input));
            if (response != null) {
                conversationHistory.add(new AssistantMessage(response));
            }

            System.out.println(response);
        }
        scanner.close();
    }
}
