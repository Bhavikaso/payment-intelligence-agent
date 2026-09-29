# Payment Intelligence Agent - AI-Powered Auth Explainer & Card Refresher

**Portfolio project for Staff Engineer roles at Monzo, Stripe, Revolut, Google Pay, Apple Pay, Meta**

This is your unfair advantage: 12 years payments domain (Amex Card Refresher, Token Lifecycle, ISO 8583) + AI Agent built in Java.

## What it does
- **RAG over transaction logs**: Explains why a payment was declined using similar past declines
- **Card Refresher Tool**: AI checks if updated card exists (your Amex project) - drives 6% auth uplift
- **Token Vault Tool**: AI checks token lifecycle events (your Token platform project)
- **Observability**: OpenTelemetry + Prometheus metrics - your org standard at Amex

## Architecture (Staff-level diagram for interviews)
```
Merchant -> Auth Request (MTI 1100) -> Payment Agent API /explain
                                      -> VectorStore (pgvector) similaritySearch for similar declines
                                      -> Tools: CardRefresherTool, TokenVaultTool (Couchbase/Cassandra in prod)
                                      -> LLM (GPT-4o-mini / Ollama) -> Merchant-friendly explanation + action
                                      -> Metrics: latency, tool calls, RAG hits
```

## Phase 1 - Run Locally (This Starter)
1. `docker run -d -p 5432:5432 -e POSTGRES_DB=payments_ai -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres ankane/pgvector`
2. `export OPENAI_API_KEY=sk-...` or set `use-local-ollama=true` and run `ollama run llama3`
3. `mvn spring-boot:run`
4. Test:
```bash
curl -X POST http://localhost:8080/api/v1/payments/ingest -H "Content-Type: application/json" -d '{
  "transactionId":"txn_123","mti":"1100","panLast4":"1001","merchantId":"merch_monzo","merchantName":"Monzo Merchant","amount":49.99,"currency":"GBP","responseCode":"05","declineReason":"EXPIRED_CARD","cardExpiry":"1124","tokenized":true,"tokenStatus":"EXPIRED","timestamp":"2024-01-15T10:00:00Z","rawIso8583":"MTI=1100 PAN=... RC=05"
}'

curl -X POST http://localhost:8080/api/v1/payments/explain -H "Content-Type: application/json" -d '{...same...}'
```
Expected: "Declined due to expired card. Card Refresher has updated expiry 1228. Merchant should use updated credentials - expected 6% uplift."

## Phase 2 - Add Agentic (Next)
- Spring AI ToolCalling: agent decides which tool to call
- Evaluation harness: does agent correctly recommend refresher for expired cards?
- Dashboard in Grafana

## Phase 3 - Staff Interview Story
Tell Monzo: "I built this because at Amex I saw merchants lose 6% auth due to stale cards. I took my Card Refresher domain and added RAG so merchants get not just code 05 but actionable fix. Built in Java with Spring AI, pgvector, OpenTelemetry - same observability standard I set at Amex."

## Resume Bullet
`Built AI Payment Intelligence Agent (Java 21, Spring AI, pgvector, OpenTelemetry) - RAG over ISO 8583 auth logs with Card Refresher & Token Vault tools, explaining declines with actionable merchant guidance, driving 6% auth uplift pattern. Showcased for Staff roles at Monzo/Stripe.`

## Learning Path
- Week 1: You just did - RAG + Vector DB
- Week 2: Tools + Function Calling
- Week 3: Observability + Evals
- Week 4: Deploy to fly.io / Render

## For Monzo L50 Interview
Be ready: How do you handle PCI-DSS with LLM? (Don't send PAN to OpenAI - use last4 + tokenized). How to keep latency <200ms? (Cache embeddings, async tool calls).
