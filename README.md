# 💸 PagaAê - Back-end API

> API RESTful desenvolvida para o aplicativo PagaAê, um sistema de gestão e divisão de despesas. Este projeto é parte integrante do Trabalho de Conclusão de Curso (TCC).

O back-end do PagaAê é responsável por gerenciar a regra de negócios, autenticação segura de usuários via Google, persistência de dados em nuvem e cálculos de divisão de contas, fornecendo dados estruturados para o front-end em Angular.

## 🛠️ Tecnologias Utilizadas

A arquitetura foi projetada visando escalabilidade, segurança e boas práticas de mercado:

* **Linguagem:** Java (Versão 17/21)
* **Framework:** Spring Boot 3.x
* **Segurança:** Spring Security, JWT (JSON Web Tokens) e OAuth2 (Google Login)
* **Banco de Dados:** PostgreSQL (Hospedado via Supabase)
* **ORM:** Hibernate / Spring Data JPA
* **Infraestrutura/Deploy:** Docker, GitHub Actions (CI/CD) e AWS EC2 *(em andamento)*

## ⚙️ Arquitetura e Recursos Principais

* **Autenticação via Google:** Login fluido utilizando a API oficial do Google sem necessidade de criação de senhas locais.
* **Banco de Dados em Nuvem:** Conexão otimizada com Supabase utilizando Connection Pooler e criptografia SSL.
* **Variáveis de Ambiente:** Proteção total de dados sensíveis (credenciais e chaves de API) fora do código-fonte.
* **Fallback de Banco de Dados:** Configuração inteligente que utiliza o PostgreSQL em produção ou um banco em memória (H2) localmente caso as variáveis de ambiente não sejam informadas.

## 🚀 Como rodar o projeto localmente

### 1. Pré-requisitos
Certifique-se de ter instalado em sua máquina:
* [Java JDK](https://adoptium.net/) (17 ou superior)
* [Maven](https://maven.apache.org/)
* Uma IDE de sua preferência (IntelliJ IDEA, VS Code, Eclipse)

### 2. Clonando o repositório
```bash
git clone [https://github.com/seu-usuario/pagaae-backend.git](https://github.com/seu-usuario/pagaae-backend.git)
cd pagaae-backend
