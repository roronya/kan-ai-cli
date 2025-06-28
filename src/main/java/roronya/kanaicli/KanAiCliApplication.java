package roronya.kanaicli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;


@SpringBootApplication
public class KanAiCliApplication implements CommandLineRunner {

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
        }
    }
}
