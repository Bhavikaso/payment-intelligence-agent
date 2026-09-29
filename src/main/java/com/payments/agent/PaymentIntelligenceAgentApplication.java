package com.payments.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentIntelligenceAgentApplication {
  public static void main(String[] args) { SpringApplication.run(PaymentIntelligenceAgentApplication.class, args); }

  @Bean
  ChatClient.Builder chatClientBuilder(ChatClient.Builder builder) { return builder; }
}
