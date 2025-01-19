package com.taklifnoma.taklifnomalar.bot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotConstants {
    public static final int PAYMENT_AMOUNT = 50000; // So'm
    public static final String PAYMENT_CARD = "8600 1234 5678 9012";

    public static final int MAX_REQUESTS_PER_MINUTE = 20;
    public static final int MAX_PHOTO_SIZE_MB = 5;

    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_ADDRESS_LENGTH = 200;
    public static final int MAX_MESSAGE_LENGTH = 1000;

    public static final String ERROR_INVALID_NAME = "Ism uzunligi 2 dan 50 gacha bo'lishi kerak";
    public static final String ERROR_INVALID_AGE = "Yosh 1 dan 150 gacha bo'lishi kerak";
    public static final String ERROR_INVALID_TIME = "Vaqt noto'g'ri formatda kiritildi";
    public static final String ERROR_INVALID_LOCATION = "Lokatsiya ma'lumotlari noto'g'ri";
    public static final String ERROR_SYSTEM = "Tizimda xatolik yuz berdi. Iltimos, keyinroq urinib ko'ring";

    public static final Map<String, List<TemplateInfo>> TYPE_TEMPLATES = new HashMap<>();

    static {
        TYPE_TEMPLATES.put("TO'Y", Arrays.asList(
                new TemplateInfo("To'y 1", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM"),
                new TemplateInfo("To'y 2", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM"),
                new TemplateInfo("To'y 3", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM")
        ));

        TYPE_TEMPLATES.put("TUG'ILGAN KUN TAKLIFNOMA", Arrays.asList(
                new TemplateInfo("Tug'ilgan kun 1", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM"),
                new TemplateInfo("Tug'ilgan kun 2", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM"),
                new TemplateInfo("Tug'ilgan kun 3", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM")
        ));

        TYPE_TEMPLATES.put("TUG'ILGAN KUN TABRIKNOMA", Arrays.asList(
                new TemplateInfo("Tabriknoma 1", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM"),
                new TemplateInfo("Tabriknoma 2", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM"),
                new TemplateInfo("Tabriknoma 3", "https://lh5.googleusercontent.com/proxy/z14mlq_5C4Lj2UUvQd2xTK6t-wRhImOBalhN8x8207FWsxSnFS_cfdrr2TEYBwji9PGcQR7VvcoMqCgGnEoZkc7FegCe5BVqiMbmdJKUHpeLhoYKbWWVCK-YOHr516YYkAX_LR0JsS0xXO9PIhNWW4bm5Md5AJMPW7NIqOv6RwH1JlM")
        ));
    }
}