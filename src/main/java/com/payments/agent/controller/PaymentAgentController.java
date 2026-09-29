package com.payments.agent.controller;

import com.payments.agent.model.AuthTransaction;
import com.payments.agent.service.PaymentExplainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/payments") @RequiredArgsConstructor
public class PaymentAgentController {

  private final PaymentExplainerService explainerService;

  @PostMapping("/ingest")
  public String ingest(@RequestBody AuthTransaction txn) {
    explainerService.ingestForRAG(txn);
    return "Ingested " + txn.getTransactionId();
  }

  @PostMapping("/explain")
  public String explain(@RequestBody AuthTransaction txn) {
    return explainerService.explainDecline(txn);
  }

  @GetMapping("/health")
  public String health() { return "Payment Intelligence Agent UP - Ready for Monzo/Stripe interviews"; }
}
