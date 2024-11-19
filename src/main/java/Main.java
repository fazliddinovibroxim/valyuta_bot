import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyDescription;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static ThreadLocal<BotService> threadLocal = ThreadLocal.withInitial(BotService::new);

    public static void main(String[] args) {

        String token = "7535187126:AAFOcpnhwFplJi3tXNjWYcjQig78-cx5WPw";

        TelegramBot telegramBot = new TelegramBot(token);


        telegramBot.setUpdatesListener(
                updates -> {
                    for (Update update : updates) {
                        CompletableFuture.runAsync(() -> {
                            threadLocal.get().handleUpdate(update);
                        });
                    }
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                },
                e -> {
                    e.printStackTrace();
                }
        );
    }
}
