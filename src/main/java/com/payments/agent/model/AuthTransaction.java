package com.payments.agent.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity @Data
public class AuthTransaction {
  @Id String transactionId;
  String mti; // 1100 auth request, 1110 response
  String panLast4;
  String merchantId;
  String merchantName;
  double amount;
  String currency;
  String responseCode; // 00 approved, 05 declined
  String declineReason;
  String cardExpiry; // MMYY
  boolean isTokenized;
  String tokenStatus; // ACTIVE, SUSPENDED, EXPIRED
  Instant timestamp;
  String rawIso8583; // for RAG context

}
