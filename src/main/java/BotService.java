import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BotService {
    String token = "7535187126:AAFOcpnhwFplJi3tXNjWYcjQig78-cx5WPw";
    TelegramBot telegramBot = new TelegramBot(token);
    static Map<Long, UserState> userState = new HashMap();
    static Map<Long, String> userCurrency = new HashMap<>();

    public Convertor[] takeUrl() {
        URL url = null;
        try {
            url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            Reader reader = new InputStreamReader(stream);
            Gson gson = new Gson();
            Convertor[] albums = gson.fromJson(reader, Convertor[].class);
            return albums;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void handleUpdate(Update update) {
        if (update.message() != null) {
            Message message = update.message();
            Long chatId = message.chat().id();
            String text = message.text();

            if ("/start".equals(text)) {
                userState.put(chatId, UserState.NEW);
                String startMessage = """
                        Quyidagi tillardan birini tanlang
                        Выберите один из языков ниже
                        Choose one of the languages below
                        """;

                // Creating inline keyboard buttons
                InlineKeyboardButton buttonUz = new InlineKeyboardButton("Uzbek").callbackData("uzbek");
                InlineKeyboardButton buttonRu = new InlineKeyboardButton("Russian").callbackData("russian");
                InlineKeyboardButton buttonEn = new InlineKeyboardButton("English").callbackData("english");

                InlineKeyboardButton[] row1 = new InlineKeyboardButton[]{buttonUz};
                InlineKeyboardButton[] row2 = new InlineKeyboardButton[]{buttonRu};
                InlineKeyboardButton[] row3 = new InlineKeyboardButton[]{buttonEn};

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(row1, row2, row3);
                SendMessage sendMessage = new SendMessage(chatId, startMessage).replyMarkup(markup);
                telegramBot.execute(sendMessage);
            } else if ("Valyuta kurslari".equals(text)) {
                showRates(chatId, 0, update); // Start showing from the first page
//                BotService botService = new BotService();
//                Convertor[] convertors = botService.takeUrl();
//                StringBuilder stringBuilder = new StringBuilder();
//                for (Convertor convertor : convertors) {
//                    stringBuilder.append("Pul birligi: ").append(convertor.getCcyNm_UZ()).append("\n").append("Qiymati: ").append(convertor.getRate()).append("\n");
//                }
//                SendMessage sendMessage1 = new SendMessage(chatId, stringBuilder.toString() + "\n" + "Eslatib o'tamiz yuqoridagilarning bari\n so'mga nisbatan baholanadi!");
//                telegramBot.execute(sendMessage1);
            } else if ("Valyutani hisoblash".equals(text)) {
                BotService botService = new BotService();
                Convertor[] convertors = botService.takeUrl();
                String sendLine= """
                        Eslatib o'tamiz bu jarayon quyidagi pul birliklarini So'm ga hisoblaydi
                                                
                        Quyidagi bo'limlardan birini tanlang!
                        """;

                // Creating reply keyboard buttons
                InlineKeyboardButton buttonUSD = new InlineKeyboardButton("AQSH dollari").callbackData("AQSH dollari");
                InlineKeyboardButton buttonEv = new InlineKeyboardButton("EVRO").callbackData("EVRO");
                InlineKeyboardButton buttonRu = new InlineKeyboardButton("Rossiya rubli").callbackData("Rossiya rubli");
                InlineKeyboardButton buttonYa = new InlineKeyboardButton("Yaponiya iyenasi").callbackData("Yaponiya iyenasi");
                InlineKeyboardButton buttonXi = new InlineKeyboardButton("Xitoy yuani").callbackData("Xitoy yuani");
                InlineKeyboardButton buttonKo = new InlineKeyboardButton("Koreya Respublikasi voni").callbackData("Koreya Respublikasi voni");
                InlineKeyboardButton buttonTu = new InlineKeyboardButton("Turkiya lirasi").callbackData("Turkiya lirasi");
                InlineKeyboardButton buttonMi = new InlineKeyboardButton("Misr funti").callbackData("Misr funti");
                InlineKeyboardButton buttonSa = new InlineKeyboardButton("Saudiya Arabistoni riali").callbackData("Saudiya Arabistoni riali");
                InlineKeyboardButton buttonAf = new InlineKeyboardButton("Afg‘oniston afg‘onisi").callbackData("Afg‘oniston afg‘onisi");
                InlineKeyboardButton buttonQi = new InlineKeyboardButton("Qirg‘iz somi").callbackData("Qirg‘iz somi");
                InlineKeyboardButton buttonQo = new InlineKeyboardButton("Qozog‘iston tengesi").callbackData("Qozog‘iston tengesi");
                InlineKeyboardButton jarayon = new InlineKeyboardButton(" \uD83D\uDD01 Jarayonni almashtirish").callbackData("Jarayonni almashtirish");

                InlineKeyboardButton[] row1 = new InlineKeyboardButton[]{buttonUSD,buttonEv,buttonRu};
                InlineKeyboardButton[] row2 = new InlineKeyboardButton[]{buttonYa,buttonXi,buttonKo};
                InlineKeyboardButton[] row3 = new InlineKeyboardButton[]{buttonTu,buttonMi,buttonSa};
                InlineKeyboardButton[] row4 = new InlineKeyboardButton[]{buttonAf,buttonQi,buttonQo};
                InlineKeyboardButton[] row5 = new InlineKeyboardButton[]{jarayon};

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(row1, row2, row3,row4,row5);
                SendMessage sendMessage = new SendMessage(chatId,sendLine).replyMarkup(markup);
                userState.put(chatId, UserState.AWAITING_CURRENCY_CHOICE);
                telegramBot.execute(sendMessage);

                // for USD variable
            } else if ("Bot haqida".equals(text)) {
                SendMessage sendMessage=new SendMessage(chatId, """
                        Bu bot valyuta qiymatlarini Markaziy bankdan oladi va kunlik yangilanib turiladi.
                        Eslatib o'tamiz bu valyuta qiymatlari ishonchlidir.
                        Bot valyuta qiymatlini aniqlash va hisoblashga yordam beradi.
                        Agar bu botda biror bir xato va kamchiliklar aniqlansa adminga murojat qilishingizni so'raymiz
                        
                        Admin: 👨🏻‍💻
                        Username: @Tolxa_ibn_Ubaydulloh
                        """);
                telegramBot.execute(sendMessage);

            } else if ("Tabiiy boyliklar".equals(text)) {
                SendMessage sendMessage=new SendMessage(chatId, """
                       11.11.2024 sanadagi malumotga ko'ra: 
                       🥇 10 gram Oltin=6 650 000 so'm 🥇
                       
                       19.03.2024 sanadagi malumotga ko'ra: 
                       🥈 1 untsiya Kumush=399 104.422 so'm 🥈
                       
                       07.03.2022 sanadagi malumotga ko'ra: 
                      🛢 Jahonda bir barreli narxi $139 gacha ko'tarildi. 🛢
                        
                       Manba: Uzbekistan Markaziy Banki
                        """);
                telegramBot.execute(sendMessage);

            } else if (userState.get(chatId).equals(UserState.AWAITING_VALUE)) {
                SendMessage sendMessage = new SendMessage(chatId, "");
                String s = userCurrency.get(chatId);
                double sum = Double.parseDouble(update.message().text());
                BotService botService = new BotService();
                Convertor[] convertors = botService.takeUrl();
                //System.out.println(Arrays.toString(convertors));
                for (Convertor convertor : convertors) {
                    if (convertor.getCcyNm_UZ().equals(s)) {
                        BigDecimal bigDecimal = new BigDecimal("" + convertor.getRate(), MathContext.DECIMAL32);
                        BigDecimal convertedSum = bigDecimal.multiply(new BigDecimal(sum));
                        String message2 = String.format("Siz kiritgan "+sum+" "+s+" qiymatning So'mdagi miqdori\n%s", convertedSum.toPlainString());
                        sendMessage = new SendMessage(chatId, message2);
                        break;
                    }
                }
//                userState.put(chatId, UserState.RESULT);
                telegramBot.execute(sendMessage);

            } else {
                DeleteMessage deleteMessage = new DeleteMessage(chatId, message.messageId());
                telegramBot.execute(deleteMessage);
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            String data = callbackQuery.data();
            Long chatId = callbackQuery.message().chat().id();
            try {
                int i = Integer.parseInt(data);
                showRates(chatId, i, update);
            } catch (Exception e) {
                if (userState.get(chatId).equals(UserState.AWAITING_CURRENCY_CHOICE)) {
                    SendMessage sendMessage = new SendMessage(chatId, "Qiymatni kiriting !");
                    telegramBot.execute(sendMessage);
                    userCurrency.put(chatId, data);
                    userState.put(chatId, UserState.AWAITING_VALUE);
                } else {
                    userState.put(chatId, UserState.PATH_CHOICE);

                    SendMessage sendMessage = new SendMessage(chatId, "Bo'limlardan birini tanlang");

                    // Creating reply keyboard buttons
                    KeyboardButton buttonValyuta = new KeyboardButton("Valyuta kurslari");
                    KeyboardButton buttonHisoblash = new KeyboardButton("Valyutani hisoblash");
                    KeyboardButton buttonTabiiyBoyliklar = new KeyboardButton("Tabiiy boyliklar");
                    KeyboardButton buttonBotInfo = new KeyboardButton("Bot haqida");

                    ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup(
                            new KeyboardButton[]{buttonValyuta, buttonHisoblash},
                            new KeyboardButton[]{buttonTabiiyBoyliklar,buttonBotInfo}
                    ).resizeKeyboard(true);

                    sendMessage.replyMarkup(replyKeyboard);
                    telegramBot.execute(sendMessage);
                }
            }
        }

    }

    public void processCurrency(String currencyName, String currencyDisplayName, long chatId, Update update) {
        SendMessage sendMessage = new SendMessage(chatId, "Qiymatni kiriting");
        telegramBot.execute(sendMessage);
        userState.put(chatId, UserState.AWAITING_VALUE);
        userCurrency.put(chatId, currencyDisplayName);
    }

    // Function to show rates with pagination
    private void showRates(Long chatId, int page, Update update) {
        BotService botService = new BotService();
        Convertor[] convertors = botService.takeUrl();
        int itemsPerPage = 7; // Number of items you want to show per page
        int total = convertors.length;
        int fromIndex = page * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, total);
        int a = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = fromIndex; i < toIndex; i++) {
            a++;
            Convertor convertor = convertors[i];
            stringBuilder.append(i+1).append(") Pul birligi: ").append(convertor.getCcyNm_UZ())
                    .append("\n      Qiymati: ").append(convertor.getRate()).append("\n");
        }
        // Append navigation status
        stringBuilder.append("\nSahifa ").append((total + itemsPerPage - 1) / itemsPerPage).append(" dan ").append(page + 1);

        // Prepare navigation buttons
        InlineKeyboardButton[] buttons = {
                new InlineKeyboardButton("⬅️ Oldingisi").callbackData("" + Math.max(0, page - 1)),
                new InlineKeyboardButton("Keyingisi ➡️").callbackData("" + Math.min((total / itemsPerPage), page + 1))
        };
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttons);

        // Send message or edit existing message
        if (update.callbackQuery() != null) {
            // Edit the message if it's a callback query
            String callbackQueryId = update.callbackQuery().id();
            int messageId = update.callbackQuery().message().messageId();
            EditMessageText newMessage = new EditMessageText(chatId, messageId, stringBuilder.toString())
                    .replyMarkup(markup);
            telegramBot.execute(newMessage);
        } else {
            SendMessage message = new SendMessage(chatId, stringBuilder.toString()).replyMarkup(markup);
            telegramBot.execute(message);
        }
    }


}

enum UserState {
    NEW,
    PATH_CHOICE,
    AWAITING_CURRENCY_CHOICE,
    AWAITING_VALUE,
    RESULT
}

enum Currency {
    USD("AQSH dollari"), RUB("Rossiya rubli"), EURO("EVRO");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}