# 🔗 LinkUp - Encurtador de URLs

O **LinkUp** é uma API REST desenvolvida para encurtamento de URLs. O projeto utiliza uma estratégia de cache em duas camadas (Redis + PostgreSQL) para garantir redirecionamentos ultra-rápidos e consistência de dados.



## Decisões de Técnicas

### 1. Algoritmo de Encurtamento (Base62)
Para evitar colisões e garantir identificadores únicos, o sistema utiliza a conversão de **ID Incremental para Base62**.
- **Vantagem:** Diferente de UUIDs ou hashes aleatórios, o Base62 gera a menor string possível para um ID numérico, garantindo URLs curtas e determinísticas.

### 2. Cache-Aside com Redis
Implementação do padrão de cache para otimizar a latência de leitura:
- O sistema busca a URL original primeiramente no **Redis**.
- Caso não exista (Cache Miss), a busca é feita no **PostgreSQL** e o cache é alimentado com TTL (Time to Live) de 24 horas.

### 3. Analytics em Tempo Real (Redis INCR)
A contagem de cliques utiliza operações atômicas do Redis para evitar concorrência:
- **Alta Performance:** Incrementos feitos em memória via comando `INCR`.
- **Sincronização Agendada:** Uma **Scheduled Task** (@Scheduled) descarrega esses contadores para o banco de dados relacional periodicamente, protegendo a integridade dos dados sem sobrecarregar o banco principal com updates constantes.

### 4. Validação Ativa de Links (WebClient)
Antes de encurtar, o sistema utiliza o **Spring WebFlux (WebClient)** para realizar uma requisição `HEAD` na URL original.
- **Resiliência:** Configuração de timeouts para garantir que sites externos lentos não causem o esgotamento do pool de threads do servidor (*Thread Pool Exhaustion*).



## Tecnologias Utilizadas

- **Java 21** & **Spring Boot**
- **Spring Data JPA** (PostgreSQL)
- **Spring Data Redis** (Cache)
- **Spring WebFlux** (WebClient para validação de links)
- **Docker & Docker Compose** (Containerização da infraestrutura)

## Como Executar

**Pré-requisitos:** Docker e Docker Compose instalados.

1. Clone o repositório:
```bash
git clone https://github.com/Henrique-Barbosaa/LinkUp.git
```
2. Na pasta raiz, suba os serviços de banco de dados e cache:
```bash
docker-compose up -d
```
3. Execute a aplicação via Maven:
```bash
./mvnw spring-boot:run
```
