package com.taklifnoma.taklifnomalar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.springframework.context.annotation.Bean;
import com.taklifnoma.taklifnomalar.bot.TelegramBot;

@SpringBootApplication
public class TaklifnomalarApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaklifnomalarApplication.class, args);
	}

	@Bean
	public TelegramBotsApi telegramBotsApi(TelegramBot bot) throws TelegramApiException {
		var api = new TelegramBotsApi(DefaultBotSession.class);
		api.registerBot(bot);
		return api;
	}
}

