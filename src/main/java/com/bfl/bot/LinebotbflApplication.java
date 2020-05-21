package com.bfl.bot;

import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@SpringBootApplication
@LineMessageHandler
public class LinebotbflApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinebotbflApplication.class, args);
	}

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	String senderName = "Sender";
    	final String followedUserId = event.getSource().getUserId();
    	
        LineMessagingClient client = LineMessagingClient.builder(
                "h9CYzPXg/rTBqqqqzzzkkSHn0IwelbzkGPp16JytO06iROwfrvW+rgEwsoEq0ZTDKwsNMnEiJ/3Dc3YYo9RioYNl2eBXNWtqu27jGzzUFeSNQnI59PhcbeYjpe83L9NunkszEg/TXe2Q5RLTGrwSIQdB04t89/1O/w1cDnyilFU=")
                .build();
        UserProfileResponse userProfileResponse = null;
        try {
            userProfileResponse = client.getProfile(followedUserId).get();
            senderName = userProfileResponse.getDisplayName();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("event: " + event);
        return new TextMessage(event.getMessage().getText() + senderName + userProfileResponse);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
