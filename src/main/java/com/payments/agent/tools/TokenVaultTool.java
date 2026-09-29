package com.payments.agent.tools;

import org.springframework.stereotype.Component;

@Component
public class TokenVaultTool {
  public String checkTokenStatus(String panLast4) {
    // Simulates Token Lifecycle Event platform check
    if (panLast4.endsWith("1")) {
      return "Token EXPIRED - issuer-originated lifecycle event received. Needs re-tokenization.";
    }
    if (panLast4.endsWith("2")) {
      return "Token SUSPENDED - suspicious activity. Check fraud signals.";
    }
    return "Token ACTIVE - valid for transaction.";
  }
}
