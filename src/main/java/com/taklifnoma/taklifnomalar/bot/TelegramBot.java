package com.taklifnoma.taklifnomalar.bot;

import com.taklifnoma.taklifnomalar.entity.Taklifnoma;
import com.taklifnoma.taklifnomalar.repository.TaklifnomaRepository;
import com.taklifnoma.taklifnomalar.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private TaklifnomaRepository taklifnomaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private Map<Long, String> userStates = new HashMap<>();
    private Map<Long, Taklifnoma> userSessions = new HashMap<>();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final List<TemplateInfo> TEMPLATES = Arrays.asList(
            new TemplateInfo("Shablon 1", "http://137.184.135.3:9090/api/v1/file/file-preview/Vj84jo")
    );

    private static class TemplateInfo {
        private final String name;
        private final String previewUrl;

        public TemplateInfo(String name, String previewUrl) {
            this.name = name;
            if (!isValidUrl(previewUrl)) {
                throw new IllegalArgumentException("Invalid URL: " + previewUrl);
            }
            this.previewUrl = previewUrl;
        }

        public String getName() {
            return name;
        }

        public String getPreviewUrl() {
            return previewUrl;
        }

        private boolean isValidUrl(String url) {
            try {
                new URL(url).toURI();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if ("/start".equals(messageText)) {
                startCommand(chatId);
            } else {
                processUserInput(chatId, messageText);
            }
        } else if (update.hasMessage() && update.getMessage().hasLocation()) {
            long chatId = update.getMessage().getChatId();
            processLocation(chatId, update.getMessage().getLocation());
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            long chatId = update.getMessage().getChatId();
            processPhoto(chatId, update.getMessage().getPhoto());
        }
    }

    private void startCommand(long chatId) {
        userStates.put(chatId, "ENTER_TYPE");
        userSessions.put(chatId, new Taklifnoma());
        sendMessage(chatId, "Taklifnoma turini tanlang:", createTypeKeyboard());
    }

    private void processUserInput(long chatId, String messageText) {
        String currentState = userStates.getOrDefault(chatId, "");
        Taklifnoma taklifnoma = userSessions.getOrDefault(chatId, new Taklifnoma());

        switch (currentState) {
            case "ENTER_TYPE":
                taklifnoma.setType(messageText);
                userStates.put(chatId, "ENTER_TEMPLATE");
                sendTemplatesWithPreviews(chatId);
                break;
            case "ENTER_TEMPLATE":
                taklifnoma.setTemplate(messageText);
                userStates.put(chatId, "ENTER_KUYOV_ISMI");
                sendMessage(chatId, "Kuyov ismini kiriting:");
                break;
            case "ENTER_KUYOV_ISMI":
                taklifnoma.setKuyovIsmi(messageText);
                userStates.put(chatId, "ENTER_KELIN_ISMI");
                sendMessage(chatId, "Kelin ismini kiriting:");
                break;
            case "ENTER_KELIN_ISMI":
                taklifnoma.setKelinIsmi(messageText);
                userStates.put(chatId, "ENTER_TAKLIF_QILUVCHI_ISMI");
                sendMessage(chatId, "Taklif qiluvchi ismini kiriting:");
                break;
            case "ENTER_TAKLIF_QILUVCHI_ISMI":
                taklifnoma.setTaklifQiluvchiIsmi(messageText);
                userStates.put(chatId, "ENTER_MANZIL");
                sendMessage(chatId, "To'y manzilini kiriting:");
                break;
            case "ENTER_MANZIL":
                taklifnoma.setManzil(messageText);
                userStates.put(chatId, "ENTER_LOCATION");
                sendMessage(chatId, "Lokatsiyani yuboring (Telegram location):");
                break;
            case "ENTER_LOCATION":
                // This case will be handled in processLocation method
                break;
            case "AYOLLAR_TOY_OSHI":
                if (messageText.equalsIgnoreCase("Ha")) {
                    taklifnoma.setAyollarToyOshi(true);
                    userStates.put(chatId, "AYOLLAR_TOY_OSHI_VAQTI");
                    sendMessage(chatId, "Ayollar to'y oshi vaqtini kiriting (14:00 formatida):");
                } else {
                    taklifnoma.setAyollarToyOshi(false);
                    userStates.put(chatId, "ERKAKLAR_TOY_OSHI");
                    sendMessage(chatId, "Erkaklar uchun to'y oshi bormi?", createYesNoKeyboard());
                }
                break;
            case "AYOLLAR_TOY_OSHI_VAQTI":
                try {
                    LocalTime ayollarToyOshiVaqti = LocalTime.parse(messageText, TIME_FORMATTER);
                    taklifnoma.setAyollarToyOshiVaqti(ayollarToyOshiVaqti);
                    userStates.put(chatId, "ERKAKLAR_TOY_OSHI");
                    sendMessage(chatId, "Erkaklar uchun to'y oshi bormi?", createYesNoKeyboard());
                } catch (DateTimeParseException e) {
                    sendMessage(chatId, "Noto'g'ri vaqt formati. Iltimos, vaqtni HH:mm formatida kiriting (masalan, 14:30):");
                }
                break;
            case "ERKAKLAR_TOY_OSHI":
                if (messageText.equalsIgnoreCase("Ha")) {
                    taklifnoma.setErkaklarToyOshi(true);
                    userStates.put(chatId, "ERKAKLAR_TOY_OSHI_VAQTI");
                    sendMessage(chatId, "Erkaklar to'y oshi vaqtini kiriting (HH:mm formatida):");
                } else {
                    taklifnoma.setErkaklarToyOshi(false);
                    userStates.put(chatId, "NIKOH_VAQTI");
                    sendMessage(chatId, "Nikoh vaqtini kiriting (14:00 formatida):");
                }
                break;
            case "ERKAKLAR_TOY_OSHI_VAQTI":
                try {
                    LocalTime erkaklarToyOshiVaqti = LocalTime.parse(messageText, TIME_FORMATTER);
                    taklifnoma.setErkaklarToyOshiVaqti(erkaklarToyOshiVaqti);
                    userStates.put(chatId, "NIKOH_VAQTI");
                    sendMessage(chatId, "Nikoh vaqtini kiriting (HH:mm formatida):");
                } catch (DateTimeParseException e) {
                    sendMessage(chatId, "Noto'g'ri vaqt formati. Iltimos, vaqtni HH:mm formatida kiriting (masalan, 14:30):");
                }
                break;
            case "NIKOH_VAQTI":
                try {
                    LocalTime nikohVaqti = LocalTime.parse(messageText, TIME_FORMATTER);
                    taklifnoma.setNikohVaqti(nikohVaqti);
                    userStates.put(chatId, "REQUEST_PAYMENT");
                    requestPayment(chatId);
                } catch (DateTimeParseException e) {
                    sendMessage(chatId, "Noto'g'ri vaqt formati. Iltimos, vaqtni HH:mm formatida kiriting (masalan, 14:30):");
                }
                break;
            case "REQUEST_PAYMENT":
                // This case will be handled in processPhoto method
                break;
            case "CONFIRM":
                if (messageText.equalsIgnoreCase("Tasdiqlash")) {
                    saveTaklifnoma(chatId);
                } else {
                    sendMessage(chatId, "Buyurtma bekor qilindi. Qaytadan boshlash uchun /start buyrug'ini yuboring.");
                    userStates.remove(chatId);
                    userSessions.remove(chatId);
                }
                break;
            default:
                sendMessage(chatId, "Noma'lum buyruq. Iltimos, /start buyrug'ini yuboring.");
        }

        userSessions.put(chatId, taklifnoma);
    }

    private void processLocation(long chatId, org.telegram.telegrambots.meta.api.objects.Location location) {
        String currentState = userStates.getOrDefault(chatId, "");
        if ("ENTER_LOCATION".equals(currentState)) {
            Taklifnoma taklifnoma = userSessions.getOrDefault(chatId, new Taklifnoma());
            taklifnoma.setLongitude(location.getLongitude());
            taklifnoma.setLatitude(location.getLatitude());
            userStates.put(chatId, "AYOLLAR_TOY_OSHI");
            sendMessage(chatId, "Ayollar uchun to'y oshi bormi?", createYesNoKeyboard());
            userSessions.put(chatId, taklifnoma);
        }
    }

    private void processPhoto(long chatId, List<PhotoSize> photos) {
        String currentState = userStates.getOrDefault(chatId, "");
        if ("REQUEST_PAYMENT".equals(currentState)) {
            Taklifnoma taklifnoma = userSessions.getOrDefault(chatId, new Taklifnoma());

            try {
                // Get the largest photo
                PhotoSize largestPhoto = photos.stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElseThrow(() -> new IllegalStateException("No photo found"));

                // Get file from Telegram
                GetFile getFile = new GetFile();
                getFile.setFileId(largestPhoto.getFileId());
                org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFile);

                // Download file
                String filePath = telegramFile.getFilePath();
                String fullFilePath = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

                URL url = new URL(fullFilePath);
                byte[] imageBytes;
                try (InputStream is = url.openStream()) {
                    imageBytes = is.readAllBytes();
                }

                // Generate a unique filename
                String fileName = "payment_" + chatId + "_" + System.currentTimeMillis() + ".jpg";

                // Save file using FileStorageService
                String savedFilePath = fileStorageService.save(fileName, imageBytes, "image/jpeg");

                // Update taklifnoma with the saved file path
                taklifnoma.setPaymentReceiptPath(savedFilePath);
                userSessions.put(chatId, taklifnoma);

                // Move to confirmation state
                userStates.put(chatId, "CONFIRM");
                sendConfirmation(chatId, taklifnoma);

            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(chatId, "To'lov chekini yuklashda xatolik yuz berdi. Iltimos, qaytadan urinib ko'ring.");
            }
        }
    }

    private void requestPayment(long chatId) {
        sendMessage(chatId, "Buyurtmani tasdiqlash uchun, iltimos, quyidagi kartaga 20000 so'm o'tkazing:\n" +
                "\n" +
                "Karta raqami: 9860020115429191\n" +
                "\n" +
                "To'lovni amalga oshirgach, to'lov chekining rasmini yuboring.");
    }

    private void saveTaklifnoma(long chatId) {
        Taklifnoma taklifnoma = userSessions.get(chatId);
        taklifnoma.setBuyurtmachiId(String.valueOf(chatId));
        taklifnomaRepository.save(taklifnoma);
        sendMessage(chatId, "Sizning buyurtmangiz qabul qilindi. Tez orada siz bilan bog'lanamiz.");
        userStates.remove(chatId);
        userSessions.remove(chatId);
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

    private void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createTypeKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("To'y");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    private void sendTemplatesWithPreviews(long chatId) {
        sendMessage(chatId, "Taklifnoma shablonini tanlang:", createTemplateKeyboard());

        for (TemplateInfo template : TEMPLATES) {
            try {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(String.valueOf(chatId));
                sendPhoto.setPhoto(new InputFile(new URL(template.getPreviewUrl()).openStream(), template.getName() + ".jpg"));
                sendPhoto.setCaption(template.getName());
                execute(sendPhoto);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(chatId, "Shablon rasmini yuklashda xatolik: " + template.getName() + ". Xato: " + e.getMessage());
            }
        }
    }

    private ReplyKeyboardMarkup createTemplateKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Create rows with two templates each
        for (int i = 0; i < TEMPLATES.size(); i += 2) {
            KeyboardRow row = new KeyboardRow();
            row.add(TEMPLATES.get(i).getName());
            if (i + 1 < TEMPLATES.size()) {
                row.add(TEMPLATES.get(i + 1).getName());
            }
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup createYesNoKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Ha");
        row.add("Yo'q");
        keyboard.add(row);
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

    private void sendConfirmation(long chatId, Taklifnoma taklifnoma) {
        String confirmationMessage = String.format(
                "To'lov cheki: Qabul qilindi\n\n" +
                        "Tasdiqlaysizmi?\n\n" +
                        "Turi: %s\n" +
                        "Template: %s\n" +
                        "Kuyov ismi: %s\n" +
                        "Kelin ismi: %s\n" +
                        "Taklif qiluvchi: %s\n" +
                        "Manzil: %s\n" +
                        "Lokatsiya: %.6f, %.6f\n" +
                        "Ayollar to'y oshi: %s\n" +
                        "Ayollar to'y oshi vaqti: %s\n" +
                        "Erkaklar to'y oshi: %s\n" +
                        "Erkaklar to'y oshi vaqti: %s\n" +
                        "Nikoh vaqti: %s\n",
                taklifnoma.getType(),
                taklifnoma.getTemplate(),
                taklifnoma.getKuyovIsmi(),
                taklifnoma.getKelinIsmi(),
                taklifnoma.getTaklifQiluvchiIsmi(),
                taklifnoma.getManzil(),
                taklifnoma.getLongitude(),
                taklifnoma.getLatitude(),
                taklifnoma.isAyollarToyOshi() ? "Bor" : "Yo'q",
                taklifnoma.getAyollarToyOshiVaqti() != null ? taklifnoma.getAyollarToyOshiVaqti().format(TIME_FORMATTER) : "Yo'q",
                taklifnoma.isErkaklarToyOshi() ? "Bor" : "Yo'q",
                taklifnoma.getErkaklarToyOshiVaqti() != null ? taklifnoma.getErkaklarToyOshiVaqti().format(TIME_FORMATTER) : "Yo'q",
                taklifnoma.getNikohVaqti() != null ? taklifnoma.getNikohVaqti().format(TIME_FORMATTER) : "Kiritilmagan"
        );
        sendMessage(chatId, confirmationMessage, createConfirmKeyboard());
    }
}

