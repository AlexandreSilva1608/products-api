# Product API - API de Catálogo e Pedidos

Esta API de backend, desenvolvida com Java e Spring Boot para um teste técnico, gere um catálogo de produtos e processa pedidos atomicamente, garantindo a consistência do stock.

## Pré-requisitos

* Java (versão 21 ou superior)
* Maven (versão 3.8 ou superior)
* Servidor de banco de dados MySQL a correr localmente.

## Como Rodar a Aplicação

A aplicação está configurada para criar e popular o banco de dados automaticamente na primeira inicialização.

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/AlexandreSilva1608/products-api
    cd products-api
    ```

2.  **Configure o Banco de Dados:**
    -   Certifique-se de que o seu serviço MySQL está ativo.
    -   Copie `src/main/resources/application.properties.example` para `application.properties`.
    -   No novo ficheiro, preencha as suas credenciais do MySQL (`spring.datasource.username` e `spring.datasource.password`).

3.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

Ao iniciar, o Hibernate criará e populará as tabelas automaticamente. A API estará pronta para uso em `http://localhost:8080`.

## Endpoints da API

### Listar Produtos

-   **URL:** `/products`
-   **Método:** `GET`
-   **Parâmetros:** `search` (filtro parcial por nome), `page` (padrão `0`), `size` (padrão `10`).

### Criar um Pedido (Checkout)

-   **URL:** `/orders`
-   **Método:** `POST`
-   **Corpo (JSON):**
    ```json
    {
      "items": [
        { "productId": 1, "quantity": 2 }
      ]
    }
    ```
**Respostas:** **201** (Sucesso), **409** (Stock insuficiente), **400** (Requisição inválida) **404** (Endpoint não encontrado ou produto não encontrado).

---

## Estratégia de Atomicidade e Rollback

A atomicidade do checkout é garantida pela anotação `@Transactional` no `OrderService`. Qualquer falha durante a validação ou atualização de stock resulta num **rollback** completo da transação, mantendo a consistência dos dados.

---

## Consulta SQL: Top 3 Produtos Mais Vendidos

A consulta abaixo identifica os três produtos mais vendidos pela soma das quantidades.

```sql
SELECT
    p.id,
    p.name,
    SUM(oi.quantity) AS total_sold
FROM
    products p
JOIN
    order_items oi ON p.id = oi.product_id
GROUP BY
    p.id, p.name
ORDER BY
    total_sold DESC
LIMIT 3;