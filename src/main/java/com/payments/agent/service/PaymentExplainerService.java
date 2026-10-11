package com.payments.agent.service;

import com.payments.agent.model.AuthTransaction;
import com.payments.agent.tools.CardRefresherTool;
import com.payments.agent.tools.TokenVaultTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service @Slf4j
public class PaymentExplainerService {

  private final VectorStore vectorStore;
  private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
  private final CardRefresherTool cardRefresherTool;
  private final TokenVaultTool tokenVaultTool;

    public PaymentExplainerService(VectorStore vectorStore, ObjectProvider<ChatClient.Builder> chatClientBuilderProvider, CardRefresherTool cardRefresherTool, TokenVaultTool tokenVaultTool) {
        this.vectorStore = vectorStore;
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.cardRefresherTool = cardRefresherTool;
        this.tokenVaultTool = tokenVaultTool;
    }

    public String explainDecline(AuthTransaction txn) {
    // 1. RAG - find similar past declines
    String searchQuery = String.format(
      "Decline reason: %s merchant: %s amount: %.2f",
      txn.getDeclineReason(), txn.getMerchantName(), txn.getAmount()
    );
    List<Document> similar = vectorStore.similaritySearch(searchQuery);
    String context = similar.stream().map(Document::getFormattedContent).reduce("", (a,b) -> ((String) a).isEmpty()?b:a + "\n" + b);

    // 2. Check tools - real payments logic you know
    String refreshedCard = cardRefresherTool.checkUpdatedCard(txn.getPanLast4(), txn.getCardExpiry());
    String tokenInfo = tokenVaultTool.checkTokenStatus(txn.getPanLast4());

    // 3. LLM reasoning with payments domain knowledge
    var prompt = """
      You are a Senior Payments Engineer at Monzo/Stripe. Explain why this transaction was declined.

      Transaction: {txn}
      Similar past declines (RAG): {context}
      Card Refresher Tool result: {refresher}
      Token Vault Tool result: {token}

      Rules:
      - If card expiry is past and refresher has updated card, recommend using updated card (6% auth uplift pattern).
      - If tokenStatus is EXPIRED/SUSPENDED, explain token lifecycle issue.
      - If responseCode 05 and amount high, mention risk checks.
      - Provide merchant-friendly action, not just bank code.
      - Be concise, production-grade explanation.
      """;
      ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
      if (builder == null) {
        throw new IllegalStateException("ChatClient.Builder not available");
      }
    return builder.build().prompt()
      .system("You are a payments expert. You explain declines for merchants.")
      .user(u -> u.text(prompt).params(Map.of(
        "txn", txn.toString(),
        "context", context,
        "refresher", refreshedCard,
        "token", tokenInfo
      )))
      .call().content();
  }

  public void ingestForRAG(AuthTransaction txn) {
    String formattedTxn = String.format(
      "TXN %s: MTI %s Merchant %s Amount %.2f Response %s Reason %s Token %s Expiry %s Raw: %s",
      txn.getTransactionId(), txn.getMti(), txn.getMerchantName(), txn.getAmount(),
      txn.getResponseCode(), txn.getDeclineReason(), txn.getTokenStatus(), txn.getCardExpiry(), txn.getRawIso8583()
    );
    log.info("Ingesting transaction: {}", formattedTxn);

    Document doc = new Document(
      formattedTxn,
      Map.of("transactionId", txn.getTransactionId(), "responseCode", txn.getResponseCode())
    );
    vectorStore.add(List.of(doc));
    log.info("Ingested txn {} for RAG", txn.getTransactionId());
  }
}
