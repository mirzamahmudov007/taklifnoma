package com.taklifnoma.taklifnomalar.bot;

import java.util.List;
import java.util.Map;

public class BotConstants {
    public static final String PAYMENT_CARD = "9860020115429191";
    public static final int PAYMENT_AMOUNT = 20000;



    public static final Map<String, List<TemplateInfo>> TYPE_TEMPLATES = Map.of(
            "TO'Y", List.of(
                    new TemplateInfo("To'y Shablon 1", "https://web2print.uz/content/pxp-template-set-cover/69B009770DF78F6930F6A0083A5EB3D4.jpg?size=M"),
                    new TemplateInfo("To'y Shablon 2", "https://web2print.uz/content/pxp-template-set-cover/69B009770DF78F6930F6A0083A5EB3D4.jpg?size=M"),
                    new TemplateInfo("To'y Shablon 3", "https://web2print.uz/content/pxp-template-set-cover/69B009770DF78F6930F6A0083A5EB3D4.jpg?size=M")
            ),
            "TUG'ILGAN KUN TAKLIFNOMA", List.of(
                    new TemplateInfo("Tug'ilgan kun Shablon 1", "https://web2print.uz/content/pxp-template-set-cover/69B009770DF78F6930F6A0083A5EB3D4.jpg?size=M"),
                    new TemplateInfo("Tug'ilgan kun Shablon 2", "https://web2print.uz/content/pxp-template-set-cover/69B009770DF78F6930F6A0083A5EB3D4.jpg?size=M")
            ),
            "TUG'ILGAN KUN TABRIKNOMA", List.of(
                    new TemplateInfo("Tabriknoma Shablon 1", "https://web2print.uz/content/pxp-template-set-cover/69B009770DF78F6930F6A0083A5EB3D4.jpg?size=M"),
                    new TemplateInfo("Tabriknoma Shablon 2", "https://web2print.uz/content/pxp-template-set-cover/69B009770DF78F6930F6A0083A5EB3D4.jpg?size=M")
            )
    );

    public static final Map<String, List<String>> TYPE_QUESTIONS = Map.of(
            "TO'Y", List.of(
                    "Kuyov ismini kiriting:",
                    "Kelin ismini kiriting:",
                    "To'yxona manzilini kiriting:",
                    "Lokatsiyani yuboring:",
                    "Ayollar uchun to'y oshi bormi? (Ha/Yo'q)",
                    "Erkaklar uchun to'y oshi bormi? (Ha/Yo'q)",
                    "Nikoh vaqtini kiriting (HH:mm formatida):"
            ),
            "TUG'ILGAN KUN TAKLIFNOMA", List.of(
                    "Tug'ilgan kun egasining ismini kiriting:",
                    "Necha yoshga to'layotganini kiriting:",
                    "Tadbir o'tkaziladigan joy manzilini kiriting:",
                    "Lokatsiyani yuboring:",
                    "Tadbir vaqtini kiriting (HH:mm formatida):"
            ),
            "TUG'ILGAN KUN TABRIKNOMA", List.of(
                    "Tabrik yo'llanayotgan kishining ismini kiriting:",
                    "Necha yoshga to'layotganini kiriting:",
                    "Tabrik matnini kiriting:"
            )
    );


}

