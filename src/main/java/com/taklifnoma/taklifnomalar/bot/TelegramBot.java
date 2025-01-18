package com.taklifnoma.taklifnomalar.bot;

import com.taklifnoma.taklifnomalar.entity.FileStorage;
import com.taklifnoma.taklifnomalar.entity.Taklifnoma;
import com.taklifnoma.taklifnomalar.repository.TaklifnomaRepository;
import com.taklifnoma.taklifnomalar.service.FileStorageService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private TaklifnomaRepository taklifnomaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) {
        long chatId = message.getChatId();
        UserSession session = userSessions.computeIfAbsent(chatId, k -> new UserSession());

        if (message.hasText()) {
            String text = message.getText();
            if ("/start".equals(text)) {
                startCommand(chatId);
            } else {
                handleTextMessage(chatId, text, session);
            }
        } else if (message.hasLocation()) {
            handleLocation(chatId, message.getLocation(), session);
        } else if (message.hasPhoto()) {
            handlePhoto(chatId, message.getPhoto(), session);
        }
    }

    private void handleTextMessage(long chatId, String text, UserSession session) {
        switch (session.getState()) {
            case START:
            case AWAITING_TYPE:
                handleTypeSelection(chatId, text, session);
                break;
            case AWAITING_TEMPLATE:
                handleTemplateSelection(chatId, text, session);
                break;
            case AWAITING_KUYOV_ISMI:
                session.getTaklifnoma().setKuyovIsmi(text);
                session.setState(BotState.AWAITING_KELIN_ISMI);
                askQuestion(chatId, "Kelin ismini kiriting:");
                break;
            case AWAITING_KELIN_ISMI:
                session.getTaklifnoma().setKelinIsmi(text);
                session.setState(BotState.AWAITING_MANZIL);
                askQuestion(chatId, "To'yxona manzilini kiriting:");
                break;
            case AWAITING_MANZIL:
                session.getTaklifnoma().setManzil(text);
                session.setState(BotState.AWAITING_LOCATION);
                askQuestion(chatId, "Lokatsiyani yuboring:");
                break;
            case AWAITING_LOCATION:
                // Skip - handled by location handler
                break;
            case AWAITING_AYOLLAR_TOSH:
                handleYesNoAnswer(chatId, text, session, true);
                break;
            case AWAITING_ERKAKLAR_TOSH:
                handleYesNoAnswer(chatId, text, session, false);
                break;
            case AWAITING_NIKOH_VAQTI:
                handleTimeInput(chatId, text, session, true);
                break;
            case AWAITING_TUGILGAN_KUN_EGASI:
                session.getTaklifnoma().setTugulganKunEgasi(text);
                session.setState(BotState.AWAITING_YOSH);
                askQuestion(chatId, "Necha yoshga to'layotganini kiriting:");
                break;
            case AWAITING_YOSH:
                handleAgeInput(chatId, text, session);
                break;
            case AWAITING_TADBIR_VAQTI:
                handleTimeInput(chatId, text, session, false);
                break;
            case AWAITING_TABRIK_MATNI:
                session.getTaklifnoma().setTabrikMatni(text);
                requestPayment(chatId, session);
                break;
            case AWAITING_CONFIRMATION:
                handleConfirmation(chatId, text, session);
                break;
            default:
                sendMessage(chatId, "Noma'lum buyruq. Iltimos, /start buyrug'ini yuboring.");
        }
    }

    private void handleTypeSelection(long chatId, String text, UserSession session) {
        if (BotConstants.TYPE_TEMPLATES.containsKey(text)) {
            session.getTaklifnoma().setType(text);
            session.setState(BotState.AWAITING_TEMPLATE);
            sendTemplatesWithPreviews(chatId, text);
        } else {
            sendMessage(chatId, "Iltimos, quyidagi turlardan birini tanlang:", createTypeKeyboard());
        }
    }

    private void handleTemplateSelection(long chatId, String templateName, UserSession session) {
        session.getTaklifnoma().setTemplate(templateName);
        switch (session.getTaklifnoma().getType()) {
            case "TO'Y":
                session.setState(BotState.AWAITING_KUYOV_ISMI);
                askQuestion(chatId, "Kuyov ismini kiriting:");
                break;
            case "TUG'ILGAN KUN TAKLIFNOMA":
            case "TUG'ILGAN KUN TABRIKNOMA":
                session.setState(BotState.AWAITING_TUGILGAN_KUN_EGASI);
                askQuestion(chatId, "Tug'ilgan kun egasining ismini kiriting:");
                break;
        }
    }

    private void handleYesNoAnswer(long chatId, String text, UserSession session, boolean isAyollar) {
        boolean answer = text.equalsIgnoreCase("Ha");
        if (isAyollar) {
            session.getTaklifnoma().setAyollarToyOshi(answer);
            session.setState(BotState.AWAITING_ERKAKLAR_TOSH);
            askQuestion(chatId, "Erkaklar uchun to'y oshi bormi? (Ha/Yo'q)");
        } else {
            session.getTaklifnoma().setErkaklarToyOshi(answer);
            session.setState(BotState.AWAITING_NIKOH_VAQTI);
            askQuestion(chatId, "Nikoh vaqtini kiriting (HH:mm formatida):");
        }
    }

    private void handleTimeInput(long chatId, String text, UserSession session, boolean isNikohVaqti) {
        try {
            LocalTime time = LocalTime.parse(text, TIME_FORMATTER);
            if (isNikohVaqti) {
                session.getTaklifnoma().setNikohVaqti(time);
                requestPayment(chatId, session);
            } else {
                session.getTaklifnoma().setTadbirVaqti(time);
                requestPayment(chatId, session);
            }
        } catch (DateTimeParseException e) {
            sendMessage(chatId, "Noto'g'ri vaqt formati. Iltimos, vaqtni HH:mm formatida kiriting (masalan, 14:30):");
        }
    }

    private void handleAgeInput(long chatId, String text, UserSession session) {
        try {
            int age = Integer.parseInt(text);
            if (age <= 0 || age > 150) {
                sendMessage(chatId, "Noto'g'ri yosh. Iltimos, to'g'ri yoshni kiriting:");
                return;
            }
            session.getTaklifnoma().setYosh(age);
            if ("TUG'ILGAN KUN TAKLIFNOMA".equals(session.getTaklifnoma().getType())) {
                session.setState(BotState.AWAITING_MANZIL);
                askQuestion(chatId, "Tadbir o'tkaziladigan joy manzilini kiriting:");
            } else {
                session.setState(BotState.AWAITING_TABRIK_MATNI);
                askQuestion(chatId, "Tabrik matnini kiriting:");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Noto'g'ri format. Iltimos, raqam kiriting:");
        }
    }

    private void handleLocation(long chatId, Location location, UserSession session) {
        if (session.getState() == BotState.AWAITING_LOCATION) {
            session.getTaklifnoma().setLatitude(location.getLatitude());
            session.getTaklifnoma().setLongitude(location.getLongitude());

            if ("TO'Y".equals(session.getTaklifnoma().getType())) {
                session.setState(BotState.AWAITING_AYOLLAR_TOSH);
                askQuestion(chatId, "Ayollar uchun to'y oshi bormi? (Ha/Yo'q)");
            } else {
                session.setState(BotState.AWAITING_TADBIR_VAQTI);
                askQuestion(chatId, "Tadbir vaqtini kiriting (HH:mm formatida):");
            }
        }
    }

    private void handlePhoto(long chatId, List<PhotoSize> photos, UserSession session) {
        if (session.getState() != BotState.AWAITING_PAYMENT) {
            return;
        }

        try {
            PhotoSize largestPhoto = photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElseThrow(() -> new IllegalStateException("Rasm topilmadi"));

            String fileId = largestPhoto.getFileId();
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);

            String filePath = execute(getFile).getFilePath();
            String fullFilePath = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

            URL url = new URL(fullFilePath);
            byte[] imageBytes;
            try (InputStream is = url.openStream()) {
                imageBytes = is.readAllBytes();
            }

            String fileName = "payment_" + chatId + "_" + System.currentTimeMillis() + ".jpg";
            FileStorage savedFile = fileStorageService.saves(fileName, imageBytes, "image/jpeg");

            session.getTaklifnoma().setPaymentReceiptPath(savedFile.getHashId());
            session.setState(BotState.AWAITING_CONFIRMATION);
            sendConfirmation(chatId, session);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "To'lov chekini yuklashda xatolik yuz berdi. Iltimos, qaytadan urinib ko'ring.");
        }
    }

    private void handleConfirmation(long chatId, String text, UserSession session) {
        if ("Tasdiqlash".equalsIgnoreCase(text)) {
            Taklifnoma taklifnoma = session.getTaklifnoma();
            taklifnoma.setBuyurtmachiId(String.valueOf(chatId));
            taklifnomaRepository.save(taklifnoma);
            sendMessage(chatId, "Sizning buyurtmangiz qabul qilindi. Tez orada siz bilan bog'lanamiz.");
            userSessions.remove(chatId);
        } else if ("Bekor qilish".equalsIgnoreCase(text)) {
            sendMessage(chatId, "Buyurtma bekor qilindi. Qaytadan boshlash uchun /start buyrug'ini yuboring.");
            userSessions.remove(chatId);
        } else {
            sendMessage(chatId, "Iltimos, 'Tasdiqlash' yoki 'Bekor qilish' tugmalaridan birini tanlang.");
        }
    }

    private void startCommand(long chatId) {
        UserSession session = new UserSession();
        session.setState(BotState.AWAITING_TYPE);
        userSessions.put(chatId, session);
        sendMessage(chatId, "Taklifnoma turini tanlang:", createTypeKeyboard());
    }

    private void requestPayment(long chatId, UserSession session) {
        session.setState(BotState.AWAITING_PAYMENT);
        sendMessage(chatId, String.format(
                "Buyurtmani tasdiqlash uchun, iltimos, quyidagi kartaga %d so'm o'tkazing:\n\n" +
                        "Karta raqami: %s\n\n" +
                        "To'lovni amalga oshirgach, to'lov chekining rasmini yuboring.",
                BotConstants.PAYMENT_AMOUNT,
                BotConstants.PAYMENT_CARD
        ));
    }

    private void sendConfirmation(long chatId, UserSession session) {
        Taklifnoma taklifnoma = session.getTaklifnoma();
        StringBuilder message = new StringBuilder();
        message.append("To'lov cheki: Qabul qilindi\n\n");
        message.append("Tasdiqlaysizmi?\n\n");
        message.append(String.format("Turi: %s\n", taklifnoma.getType()));
        message.append(String.format("Template: %s\n", taklifnoma.getTemplate()));

        switch (taklifnoma.getType()) {
            case "TO'Y":
                appendWeddingDetails(message, taklifnoma);
                break;
            case "TUG'ILGAN KUN TAKLIFNOMA":
                appendBirthdayInvitationDetails(message, taklifnoma);
                break;
            case "TUG'ILGAN KUN TABRIKNOMA":
                appendBirthdayCardDetails(message, taklifnoma);
                break;
        }

        sendMessage(chatId, message.toString(), createConfirmKeyboard());
    }

    private void appendWeddingDetails(StringBuilder message, Taklifnoma taklifnoma) {
        message.append(String.format("Kuyov ismi: %s\n", taklifnoma.getKuyovIsmi()));
        message.append(String.format("Kelin ismi: %s\n", taklifnoma.getKelinIsmi()));
        message.append(String.format("Manzil: %s\n", taklifnoma.getManzil()));
        message.append(String.format("Lokatsiya: %.6f, %.6f\n",
                taklifnoma.getLongitude(), taklifnoma.getLatitude()));
        message.append(String.format("Ayollar to'y oshi: %s\n",
                taklifnoma.isAyollarToyOshi() ? "Bor" : "Yo'q"));
        message.append(String.format("Erkaklar to'y oshi: %s\n",
                taklifnoma.isErkaklarToyOshi() ? "Bor" : "Yo'q"));
        message.append(String.format("Nikoh vaqti: %s\n",
                taklifnoma.getNikohVaqti().format(TIME_FORMATTER)));
    }

    private void appendBirthdayInvitationDetails(StringBuilder message, Taklifnoma taklifnoma) {
        message.append(String.format("Tug'ilgan kun egasi: %s\n", taklifnoma.getTugulganKunEgasi()));
        message.append(String.format("Yoshi: %d\n", taklifnoma.getYosh()));
        message.append(String.format("Manzil: %s\n", taklifnoma.getManzil()));
        message.append(String.format("Lokatsiya: %.6f, %.6f\n",
                taklifnoma.getLongitude(), taklifnoma.getLatitude()));
        message.append(String.format("Tadbir vaqti: %s\n",
                taklifnoma.getTadbirVaqti().format(TIME_FORMATTER)));
    }

    private void appendBirthdayCardDetails(StringBuilder message, Taklifnoma taklifnoma) {
        message.append(String.format("Tug'ilgan kun egasi: %s\n", taklifnoma.getTugulganKunEgasi()));
        message.append(String.format("Yoshi: %d\n", taklifnoma.getYosh()));
        message.append(String.format("Tabrik matni: %s\n", taklifnoma.getTabrikMatni()));
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void askQuestion(long chatId, String question) {
        sendMessage(chatId, question);
    }

    private ReplyKeyboardMarkup createTypeKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("TO'Y");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("TUG'ILGAN KUN TAKLIFNOMA");
        row2.add("TUG'ILGAN KUN TABRIKNOMA");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup createConfirmKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Tasdiqlash");
        row.add("Bekor qilish");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    private void sendTemplatesWithPreviews(long chatId, String type) {
        sendMessage(chatId, "Taklifnoma shablonlari yuklanmoqda. Iltimos, kuting...");

        List<TemplateInfo> templates = BotConstants.TYPE_TEMPLATES.get(type);
        for (TemplateInfo template : templates) {
            try {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(String.valueOf(chatId));
                sendPhoto.setPhoto(new InputFile(new URL(template.getPreviewUrl()).openStream(),
                        template.getName() + ".jpg"));

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();

                InlineKeyboardButton selectButton = new InlineKeyboardButton();
                selectButton.setText("Tanlash");
                selectButton.setCallbackData("select_template:" + template.getName());
                rowInline.add(selectButton);

                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);

                sendPhoto.setReplyMarkup(markupInline);
                sendPhoto.setCaption(template.getName());

                execute(sendPhoto);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(chatId, "Shablon rasmini yuklashda xatolik: " + template.getName());
            }
        }

        sendMessage(chatId, "Barcha shablonlar yuklandi. Iltimos, o'zingizga yoqqanini tanlang.");
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        UserSession session = userSessions.get(chatId);

        if (callbackData.startsWith("select_template:")) {
            String templateName = callbackData.split(":")[1];
            handleTemplateSelection(chatId, templateName, session);
        }
    }
}

