# CGovern

Spring Boot control plane for **Continuous Integration**, **Continuous Deployment**, **Continuous Testing**, **Continuous Monitoring**, and **Agentic Monitoring**.

Jobs always run **sequentially**. Tasks in the same parallel group can run **in parallel**.

## Stack

- Java 21
- Maven
- Spring Boot 3.3
- Thymeleaf + Spring Security
- Spring Data JPA / H2 (swap for PostgreSQL in production)
- Actuator health/metrics

## Run

```bash
mvn spring-boot:run
```

Open http://localhost:8080

Demo login: `admin@cgovern.dev` / `admin123`

## Architecture

CGovern is the **control plane**. Remote **agents** are the **data plane**.

1. Users model Feature → Organization → Service → Job → Task.
2. A service pipeline runs jobs in `orderIndex` order and stops on failure.
3. Inside a job, tasks are grouped by `parallelGroup`. PARALLEL tasks in one group run together; groups stay sequential.
4. Each job is pinned to a remote server. The control plane HMAC-signs the task payload (`timestamp.body`) and POSTs to `{baseUrl}/agent/execute`.
5. The agent verifies the signature, checks the capability allowlist, and executes in its own isolation.

Simulated agents skip the HTTP call so the demo works on one machine.

## How many servers?

| Mode | What to run |
|---|---|
| Demo | 1 Spring Boot process |
| Small production | Control plane + database + 1 agent per environment |
| Hardened | 2 control-plane nodes, DB primary/replica, optional queue, metrics, N agents |

Scale **agents** for job throughput. Keep the UI/API tier small.

## Security for remote jobs

- HMAC (implemented) or mTLS between control plane and agents
- Short-lived timestamps
- Per-agent capability allowlists (`BUILD`, `TEST`, `DEPLOY`, `MONITOR`)
- No SSH from CGovern into customer hosts
- Run tasks in containers/ephemeral VMs, not on the agent OS
- Private networking; agents only accept the control plane
