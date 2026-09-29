package com.payments.agent.tools;

import org.springframework.stereotype.Component;

/**
 * Simulates your real Card Refresher domain - provides updated card info to merchants.
 * This is your Amex project, now as a tool for AI agent.
 */
@Component
public class CardRefresherTool {

  public String checkUpdatedCard(String panLast4, String expiry) {
    // In real prod, query Couchbase/Cassandra for refreshed card
    // Demo logic: if expiry < 1225, return updated expiry
    if (expiry != null && expiry.compareTo("1225") < 0) {
      return "FOUND: Updated card expiry 1228 available for PAN ending " + panLast4 + 
             ". Merchant should use updated credentials via Card Refresher API. Expected auth uplift 6%.";
    }
    return "NOT_FOUND: No updated card in refresher vault. Card is current.";
  }
}
