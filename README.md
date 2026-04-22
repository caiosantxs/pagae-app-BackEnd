# PagaAê - Back-End

Repositório do Front-End: https://github.com/caiosantxs/pagae-app-FrontEnd

## 📌 Introdução

Este documento descreve a arquitetura e os requisitos do **PagaAê**, um software para **gerenciamento de despesas compartilhadas em rolês entre amigos**. O Back-End é desenvolvido em **Java Spring Boot**, o Front-End em **Angular** e o Banco de Dados em **PostgreSQL**. O objetivo é criar uma solução prática e eficiente para dividir gastos de forma justa, gerenciar dívidas e facilitar pagamentos entre participantes de um grupo.

---

## 📂 Arquitetura e Organização

### 🔹 Ponto de Entrada da Aplicação

`PagaeAppApplication.java` → Classe principal do Spring Boot que inicia a aplicação.

### 🔹 Módulos Funcionais

#### 📌 Autenticação (`controllers/` + `services/`)

| Arquivo | Descrição |
|---|---|
| `AuthenticationController.java` | Controla endpoints de login, registro, recuperação e redefinição de senha, e login com Google OAuth2. |
| `AuthService.java` | Contém a lógica de autenticação via Google, verificando o token do Google e criando/vinculando a conta automaticamente. |
| `AuthorizationService.java` | Serviço auxiliar de autorização para o Spring Security. |

#### 📌 Hangouts (Rolês)

| Arquivo | Descrição |
|---|---|
| `domain/hangout/` | Possui a entidade `HangOut`, DTOs de request/response e o enum `StatusHangOut` (ATIVO, FINALIZADO). |
| `domain/hangout_member/` | Entidade `HangOutMember` com chave composta e DTO para adição de membros. |
| `HangOutController.java` | Gerencia o CRUD de hangouts, adição/remoção de membros, criação de despesas e pagamentos dentro de um hangout, e finalização/reabertura. |
| `HangOutService.java` | Contém a lógica de negócios para manipular hangouts: criação com membros, validação de permissões do criador, entrada e saída de membros. |
| `HangOutRepository.java` | Interface que interage com o banco de dados para salvar e recuperar hangouts. |
| `HangOutMemberRepository.java` | Repositório de persistência dos membros de cada hangout. |

#### 📌 Despesas (Expenses)

| Arquivo | Descrição |
|---|---|
| `domain/expense/` | Possui a entidade `Expense`, DTOs de request/response e DTOs de atualização de descrição. |
| `domain/expense_participants/` | Entidade `ExpenseParticipant` com chave composta para vincular participantes a despesas específicas. |
| `ExpenseController.java` | Gerencia atualização de despesas e cálculo/aplicação de descontos mútuos entre usuários. |
| `ExpenseService.java` | Processa dados de despesas, divide valores entre participantes, registra pagamentos e calcula descontos cruzados. |
| `ExpenseRepository.java` | Interface para acesso ao banco de dados das despesas. |
| `ExpenseParticipantRepository.java` | Repositório de persistência dos participantes de cada despesa. |

#### 📌 Divisão de Despesas (Expense Shares)

| Arquivo | Descrição |
|---|---|
| `domain/expense_shares/` | Possui a entidade `ExpenseShare` e DTOs para representar dívidas individuais e descontos mútuos. |
| `ExpenseShareController.java` | Permite marcar dívidas como pagas, listar todas as dívidas do usuário e listar dívidas por hangout. |
| `ExpenseShareService.java` | Lida com a lógica de consulta e atualização de dívidas, com validação de permissões. |
| `ExpenseShareRepository.java` | Repositório de persistência das dívidas de cada despesa, com queries customizadas para relatórios. |

#### 📌 Pagamentos (Payments)

| Arquivo | Descrição |
|---|---|
| `domain/payment/` | Possui a entidade `Payment` e DTOs de request/response para registrar pagamentos parciais ou totais. |

#### 📌 Dashboard

| Arquivo | Descrição |
|---|---|
| `domain/dashboard/` | DTOs para estatísticas: `DashboardStatsDTO`, `RecentHangOutDTO`, `ParticipantBadgeDTO`. |
| `DashboardController.java` | Endpoint para retornar estatísticas do dashboard do usuário autenticado. |
| `DashboardService.java` | Agrega dados de hangouts, dívidas, valores a receber e rolês recentes para o dashboard. |

#### 📌 Usuários

| Arquivo | Descrição |
|---|---|
| `domain/user/` | Possui a entidade `User` (implementa `UserDetails`), DTOs de autenticação, registro, resposta, recuperação de senha e o enum `UserRole` (USER, ADMIN). |
| `UserController.java` | Gerencia deleção de usuários (admin), listagem e busca de usuários por nome/login. |
| `UserService.java` | Lida com criação, deleção, recuperação/redefinição de senha, alteração de senha e busca de usuários. |
| `UserRepository.java` | Repositório de persistência dos dados dos usuários, com buscas por login e email. |
| `TokenRepository.java` | Repositório para tokens de recuperação de senha. |

#### 📌 Segurança

| Arquivo | Descrição |
|---|---|
| `SecurityConfigurations.java` | Configura JWT, permissões de acesso (endpoints públicos/privados), CORS e sessão stateless. |
| `SecurityFilter.java` | Filtro que intercepta requisições, extrai e valida o token JWT do header `Authorization`. |
| `TokenService.java` | Responsável por gerar (HMAC256) e validar tokens JWT com expiração de 2 horas. |
| `CustomUserDetailsService.java` | Implementa `UserDetailsService` para carregar usuários do banco durante a autenticação. |
| `InvalidTokenException.java` | Exceção customizada para tokens inválidos ou expirados na recuperação de senha. |

#### 📌 Infraestrutura e Utilitários

| Arquivo | Descrição |
|---|---|
| `application.properties` | Arquivo de configuração contendo informações sobre banco de dados, JWT, Google OAuth2, SMTP de e-mail e URL do front-end. |
| `CorsConfig.java` | Configuração de CORS (atualmente desativada em favor da configuração no `SecurityConfigurations`). |
| `OpenApiConfig.java` | Configuração do Swagger/OpenAPI com autenticação Bearer JWT. |
| `EmailService.java` | Serviço para envio de e-mails automáticos, incluindo recuperação de senha e notificações de alteração. |

---

## 📋 Requisitos do Sistema

### ✅ Requisitos Funcionais

- **Autenticação e autorização:** O sistema possui autenticação de usuários via JWT e login com Google OAuth2.
- **Cadastro e gerenciamento de usuários:**
  - Criar um novo usuário manualmente pelo sistema.
  - Login via Google cria a conta automaticamente caso não exista.
  - Recuperar a senha utilizando um token enviado por e-mail.
  - Alterar a senha com base no token de recuperação.
  - Notificação por e-mail ao alterar senha.
- **Gerenciamento de Hangouts (Rolês):**
  - Criar um hangout e convidar membros na criação.
  - Adicionar novos membros a um hangout existente.
  - Entrar em um hangout via link/convite.
  - Sair de um hangout (apenas se não houver despesas pendentes).
  - Finalizar e reabrir um hangout (somente criador).
  - Deletar um hangout (somente criador).
- **Gerenciamento de Despesas:**
  - Criar despesas vinculadas a um hangout, com divisão automática entre todos os membros ou participantes específicos.
  - Definir um pagador diferente do criador.
  - Editar descrição e valores de uma despesa.
  - Deletar uma despesa (somente criador ou pagador da despesa).
- **Divisão e Pagamento de Dívidas:**
  - Dividir automaticamente o valor da despesa entre os participantes.
  - Registrar pagamentos parciais ou totais em uma dívida.
  - Marcar dívidas como pagas (devedor ou criador da despesa).
  - Calcular e aplicar descontos mútuos entre dois usuários com dívidas cruzadas.
- **Dashboard:** Gerar estatísticas com total de hangouts, valores devidos, valores a receber, dívidas pendentes e rolês recentes.
- **Tratamento de exceções:** O sistema possui tratamento de exceções com respostas personalizadas.

### 🔒 Requisitos Não Funcionais

- **Linguagem:** O sistema é feito em **Java 17** utilizando o framework **Spring Boot 3.5.6**.
- **Banco de dados:** O banco de dados é em **PostgreSQL**.
- **Desempenho:** O sistema responde rapidamente às solicitações dos usuários, com paginação nos endpoints de listagem.
- **Segurança:**
  - Uso de **JWT (HMAC256)** para autenticação stateless com expiração de 2 horas.
  - Login social via **Google OAuth2**.
  - Senhas armazenadas criptografadas utilizando **BCrypt**.
  - Configuração de **CORS** para permitir apenas origens autorizadas.
- **Documentação:** API documentada automaticamente via **Swagger/OpenAPI**.
- **CI/CD:** Pipeline automatizado via **GitHub Actions** para build de imagem Docker e deploy automático na **AWS** via SSH.

---

## 🚀 Como Executar

### 🔧 Pré-requisitos

- Java 17+
- Maven
- PostgreSQL
- Docker (opcional, para execução via container)

### 📌 Passos

1. **Configure as variáveis de ambiente** criando um arquivo `.env` na raiz do projeto ou definindo as variáveis de ambiente do sistema:

```env
# Configuração do Banco de Dados
DB_URL=jdbc:postgresql://localhost:5432/pagae
DB_USER=postgres
DB_PASSWORD=sua_senha

# Chave secreta para geração de tokens JWT
JWT_SECRET=sua_chave_secreta

# Configuração do OAuth2 (Login com Google)
GOOGLE_CLIENT_ID=seu_google_client_id

# Configuração de E-mail (Envio de notificações)
MAIL_USER=seu_email@gmail.com
MAIL_PASSWORD=sua_senha_de_app

# URL do Front-End (para CORS e links em e-mails)
FRONTEND_URL=http://localhost:4200
```

2. **Execute localmente:**

```bash
./mvnw spring-boot:run
```

3. **Ou execute via Docker Compose:**

```bash
docker compose up -d
```

---

## 🌐 Endpoints Principais

### 🔹 Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Autentica um usuário e retorna um token JWT |
| POST | `/api/auth/register` | Registra um novo usuário |
| POST | `/api/auth/google` | Login via Google OAuth2 |
| POST | `/api/auth/forgot-password` | Solicita redefinição de senha por e-mail |
| POST | `/api/auth/reset-password` | Redefine a senha com token recebido por e-mail |

### 🔹 Hangouts (Rolês)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/hangouts` | Cria um novo hangout |
| GET | `/api/hangouts` | Lista os hangouts do usuário autenticado (paginado) |
| GET | `/api/hangouts/{id}` | Retorna um hangout específico |
| PUT | `/api/hangouts/{id}` | Edita o título de um hangout |
| DELETE | `/api/hangouts/{id}` | Deleta um hangout |
| POST | `/api/hangouts/{id}/new-member` | Adiciona um membro ao hangout |
| POST | `/api/hangouts/{id}/join` | Usuário entra em um hangout |
| DELETE | `/api/hangouts/{id}/leave` | Usuário sai de um hangout |
| PATCH | `/api/hangouts/{id}/finalize` | Finaliza um hangout |
| PATCH | `/api/hangouts/{id}/open` | Reabre um hangout finalizado |
| GET | `/api/hangouts/{id}/members` | Lista membros de um hangout |

### 🔹 Despesas (Expenses)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/hangouts/{hangOutId}/expenses` | Cria uma despesa em um hangout |
| GET | `/api/hangouts/{hangOutId}/expenses` | Lista despesas de um hangout (paginado) |
| PUT | `/api/expenses/{expenseId}` | Atualiza uma despesa |
| PATCH | `/api/hangouts/{hangOutId}/expenses/{expenseId}/description` | Atualiza a descrição de uma despesa |
| DELETE | `/api/hangouts/expense/{expenseId}` | Deleta uma despesa |

### 🔹 Pagamentos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/hangouts/expenses/{expenseId}/payments` | Registra um pagamento em uma despesa |

### 🔹 Divisão de Despesas (Expense Shares)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| PATCH | `/api/shares/{shareId}/pay` | Marca uma dívida como paga |
| GET | `/api/shares/my-shares` | Lista todas as dívidas do usuário (paginado) |
| GET | `/api/shares/hangouts/{hangOutId}/expense-shares` | Lista dívidas de um hangout (paginado) |
| GET | `/api/hangouts/{hangOutId}/expense-shares` | Lista dívidas do usuário em um hangout |

### 🔹 Descontos Mútuos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/expenses/descontos` | Lista possíveis descontos mútuos |
| POST | `/api/expenses/descontos/aplicar/{targetUserId}` | Aplica desconto mútuo com outro usuário |

### 🔹 Dashboard

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/dashboard/stats` | Retorna estatísticas do dashboard do usuário |

### 🔹 Usuários

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/user/users` | Lista todos os usuários |
| GET | `/api/user/search?query=` | Busca usuários por nome ou login |
| DELETE | `/api/user/{userId}` | Deleta um usuário (somente admin) |

---

## 📜 Tecnologias Utilizadas

### 🔹 Back-End

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.5.6
- **Gerenciamento de Dependências:** Maven

### 🔹 Banco de Dados

- **Banco de Dados Relacional:** PostgreSQL
- **ORM:** Spring Data JPA (Hibernate)

### 🔹 Segurança

- **Autenticação:** Spring Security
- **Autenticação via Terceiros:** Google OAuth2 (via Google API Client)
- **Token de Segurança:** JWT (Auth0 java-jwt + JJWT)
- **Criptografia de Senhas:** BCrypt

### 🔹 Documentação da API

- **Swagger UI:** SpringDoc OpenAPI (springdoc-openapi-starter-webmvc-ui)
- **Anotações:** Swagger Annotations (io.swagger.core.v3)

### 🔹 Infraestrutura e DevOps

- **Containerização:** Docker
- **Orquestração de Containers:** Docker Compose
- **CI/CD:** GitHub Actions (build, push para Docker Hub e deploy automático na AWS via SSH)
- **Cloud:** AWS (EC2)

### 🔹 Outras Bibliotecas e Utilitários

- **Validação de Dados:** Spring Boot Starter Validation (Jakarta Validation)
- **Envio de E-mails:** Spring Mail
- **Manipulação de JSON:** Jackson
- **Logs:** SLF4J

---

## 📜 Licença

Este projeto é de código aberto e pode ser utilizado conforme necessário.
