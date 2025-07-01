package roronya.kanaicli;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


@SpringBootApplication
public class KanAiCliApplication implements CommandLineRunner {

    private ChatClient chatClient;
    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;

    private static final String BOLD = "\u001B[1m";
    private static final String RESET = "\u001B[0m";


    private final List<Message> conversationHistory = new ArrayList<>();

    @Value("classpath:prompt.st")
    Resource promptResource;

    @Autowired
    public KanAiCliApplication(
            VertexAiGeminiChatModel vertexAiGeminiChatModel,
            OpenAiChatModel openAiChatModel,
            OllamaChatModel ollamaChatModel
    ) {
        this.vertexAiGeminiChatModel = vertexAiGeminiChatModel;
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
        chatClient = ChatClient.builder(openAiChatModel)
                // .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(new CustomLogAdvisor())
                .build();

    }

    public static void main(String[] args) {
        SpringApplication.run(KanAiCliApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("oh-my-logo", "KAN-AI-CLI", "--filled", "--color");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        label:
        while (true) {
            System.out.print("> ");
            String input;
            try {
                input = scanner.nextLine();
            } catch (java.util.NoSuchElementException e) {
                System.err.println("No input available. Exiting...");
                break;
            }

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
                    System.out.println(vertexAiGeminiChatModel.toString());
                    System.out.println(openAiChatModel.toString());
                    System.out.println(ollamaChatModel.toString());
                    continue;
                case "\\debug on":
                    CustomLogAdvisor.setDebugEnabled(true);
                    System.out.println("Debug logging enabled");
                    continue;
                case "\\debug off":
                    CustomLogAdvisor.setDebugEnabled(false);
                    System.out.println("Debug logging disabled");
                    continue;
            }

            if (input.startsWith("\\model ")) {
                String modelName = input.substring(7).trim().toLowerCase();
                switch (modelName) {
                    case "gemini":
                        chatClient = ChatClient.builder(vertexAiGeminiChatModel)
                                .defaultAdvisors(new CustomLogAdvisor())
                                .build();
                        System.out.println("Switched to Vertex AI model.");
                        break;
                    case "openai":
                        chatClient = ChatClient.builder(openAiChatModel)
                                .defaultAdvisors(new CustomLogAdvisor())
                                .build();
                        System.out.println("Switched to OpenAI model.");
                        break;
                    case "ollama":
                        chatClient = ChatClient.builder(ollamaChatModel)
                                .defaultAdvisors(new CustomLogAdvisor())
                                .build();
                        System.out.println("Switched to Ollama model.");
                        break;
                    default:
                        System.out.println("Unknown model: " + modelName);
                        System.out.println("Available models: openai, ollama");
                }
                conversationHistory.clear(); // モデルを変えたらヒストリーをリセットする。(モデルの自認が壊れる)
                continue;
            }

            BeanOutputConverter<Response> converter = new BeanOutputConverter<>(Response.class);
            String format = converter.getFormat();
            String systemPrompt = new PromptTemplate(promptResource)
                    .create(Map.of("format", format))
                    .getContents();

            String rawResponse = chatClient
                    .prompt()
                    .system(systemPrompt)
                    .user(input)
                    .tools(new CommandTool())
                    .messages(conversationHistory)
                    .call()
                    .content();

            Response response = converter.convert(rawResponse);

            // Create a prompt with the conversation history
            conversationHistory.add(new UserMessage(input));
            conversationHistory.add(new AssistantMessage(rawResponse));

            System.out.println(BOLD + response.content() + RESET);
            System.out.println("（´-`）.｡oO(" + response.reasonWhyYouRespondThatContent() + ")");
        }
        scanner.close();
    }
}
