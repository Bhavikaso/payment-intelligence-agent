package com.payments.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
public class PaymentIntelligenceAgentApplication {
  public static void main(String[] args) {
    SpringApplication.run(PaymentIntelligenceAgentApplication.class, args);
  }

}