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
                "O6IUy8JB2CUmA4Lym7lpbWHMYMmdwCzbkSW58fDESjWLaSqzz5mtYo6AYvcf6w6rA3EviFGdz/JXumg3rXg122+9PzXwYy5Fnos2WKYncIY9cIOLfLR8SQc5qMUA1wrRNQHbd1KI7JzixTJhRY8C9wdB04t89/1O/w1cDnyilFU=")
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
