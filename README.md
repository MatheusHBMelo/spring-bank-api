# **💸 SpringBank - Sistema Bancário com Spring Boot**

## **Descrição do Projeto**
Esta é uma API Rest que fornece um CRUD para gerenciamento de um sistema bancário completo desenvolvido em **Java** utilizando o **Spring Boot**. A aplicação oferece funcionalidades como criação de usuários e contas, operações financeiras (transferência, depósito, saque e extrato), funções administrativas, envio de e-mails e autenticação segura baseada em **JWT (JSON Web Token)**.

O objetivo deste projeto é demonstrar habilidades em desenvolvimento backend moderno, incluindo segurança, persistência de dados, documentação e testes.

---

## **Índice**
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Funcionalidades](#funcionalidades)
- [Como Executar](#como-executar)
    - [Pré-requisitos](#pré-requisitos)
    - [Instalação](#instalação)
    - [Executando o Projeto](#executando-o-projeto)
- [Commits Semânticos](#commits-semânticos)
- [Endpoints da API](#endpoints-da-api)
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## **Tecnologias Utilizadas**
- **Java 17**
- **Spring Boot 3+**
    - Spring Web
    - Spring Data JPA
    - Spring Security
    - Spring Validation
    - Flyway
- **PostgreSQL** como banco de dados relacional
- **JWT** para autenticação
- **JUnit 5** e **Mockito** para testes
- **Springdoc OpenAPI** para documentação da API (Swagger UI)
- **Java Mail Sender** (envio de e-mails)
- **Maven** como gerenciador de dependências
- **Lombok** para reduzir boilerplate de código
- **SL4J** para controle dos logs da aplicação

---

## **Funcionalidades**
1. **Conta**
    - Cadastro.
    - Inativação de conta.
    - Gerenciamento administrativo.

2. **Usuário**
    - Cadastro.
    - Login com autenticação baseada em token JWT.
    - Gerenciamento de perfis (usuário ou administrador).

3. **Transação**
    - Transferência entre contas.
    - Deposito em conta.
    - Saque em conta.
    - Extrato de transações.

4. **Segurança e logs (Em Desenvolvimento)**
    - Proteção de endpoints com autenticação e autorização.
    - Controle de acesso baseado em perfis de acesso (**ROLE_USER**, **ROLE_ADMIN**).
    - Logs com SL4J.

5. **Emails**
    - Envio de e-mails para confirmação de usuário e de transferências.
    - JavaMailSender com mock para mailtrap.

6. **Otimização da DB (Em Desenvolvimento)**
    - Paginação.
    - Simplificação de consultas.
    - Cache.

7. **Gestão de tokens (Em Desenvolvimento)**
    - Redis.

8. **Conteinerização (Em Desenvolvimento)**
    - Dockerfile.
    - Docker-Compose.

9. **Documentação da API (Em desenvolvimento)**
    - Documentação gerada pelo Springdoc OpenAPI, acessível via Swagger UI.
    - README do projeto no GitHub.

10. **Testes (Em desenvolvimento)**
    - Testes unitários para validação das regras de negócio.
    - Testes de integração para os endpoints e o banco de dados.

---

## **Como Executar**

### **Pré-requisitos**
Antes de começar, certifique-se de ter instalado:
- **Java 17**
- **Maven**
- **Docker** (opcional, para executar o banco de dados PostgreSQL via container)
- Uma IDE como **IntelliJ IDEA** ou **Eclipse**

### **Instalação**
1. Clone este repositório:
```bash
   git clone https://github.com/MatheusHBMelo/spring-bank-api
   cd spring-bank-api
```
2. Configure o banco de dados PostgreSQL:

-   Certifique-se de que o PostgreSQL esteja rodando localmente.
-   Crie um banco de dados chamado `springbankdb`.
-   Atualize as credenciais no arquivo `application.properties`.

```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/springbankdb 
   spring.datasource.username=seu-usuario 
   spring.datasource.password=sua-senha
```
3. Configure o emissor de emails:

-   Crie uma conta no Mailtrap (recomendado).
-   Selecione a conta gratuita. 
-   Entre na sua caixa de entrada e copie as credênciais.
-   Atualize as credenciais no arquivo `application.properties`, conforme os dados do Mailtrap.

```properties
	spring.mail.host={host do provedor de email}  
	spring.mail.port={porta do provedor de email}  
	spring.mail.username={aqui seu username}  
	spring.mail.password={aqui sua senha de acesso}
```
4. Configure as variáveis de ambiente:

-   Atualize as credenciais no arquivo `application.properties`.

```properties
	spring.app.security.secret=${JWT_SECRET}  
	spring.app.security.issuer=${JWT_ISSUER}  
	spring.app.email=${EMAIL_APP}
```

5. Compile o projeto:

```bash
   mvn clean install
```

### Executando o Projeto

- Inicie o servidor Spring Boot:

```bash
   mvn spring-boot:run
```
- A aplicação estará disponível em: http://localhost:8080


## **Commits Semânticos**

Adotei a convenção de **commits semânticos** para manter o histórico do repositório organizado e facilitar o entendimento das mudanças realizadas. Utilize o seguinte padrão para mensagens de commit:

### **Estrutura**
```properties
<tipo>: <descrição breve>
```

### **Tipos de Commit**

-   **feat**: Adição de uma nova funcionalidade.
    -   Exemplo: `feat: adiciona endpoint para criação de contas`
-   **fix**: Correção de bugs.
    -   Exemplo: `fix: corrige erro de autenticação no login`
-   **refactor**: Refatoração de código sem alterar funcionalidades.
    -   Exemplo: `refactor: melhora organização do serviço de transações`
-   **chore**: Alterações menores ou tarefas que não alteram o comportamento do código (e.g., atualizações de dependências).
    -   Exemplo: `chore: atualiza versão do Spring Boot`
-   **docs**: Alterações na documentação.
    -   Exemplo: `docs: adiciona seção sobre commits semânticos no README`
-   **test**: Adição ou modificação de testes.
    -   Exemplo: `test: adiciona teste unitário para o serviço de usuários`
-   **style**: Alterações relacionadas à formatação de código, semântica ou ajustes visuais.
    -   Exemplo: `style: aplica formatação ao código do controller`

## **Endpoints da API**

### Exemplos de Endpoints

#### **Autenticação**

-   `POST /auth/register`: Criação de usuários (salva no banco de dados).
-   `POST /auth/login`: Autenticação de usuários (retorna token JWT).

#### **Transação**

-   `POST /transaction/transfer`: Fazer transação entre contas.
-   `POST /transaction/deposit`: Fazer deposito em conta.
-   `POST /transaction/withdraw`: Fazer saque em conta.
-   `GET /transaction/statement`: Buscar extrato de transações.

#### **Admin**

-   `GET /admin/users`: Retorna todos os usuários.
-   `GET /admin/user?username=""`: Retorna um usuário por username.
-   `PATCH /admin/user?username=""`: Desativa um usuário.
-   `GET /admin/transactions`: Retorna todas as transações.
-   `GET /admin/accounts`: Retorna todas as contas.
-   `GET /admin/account?numberAccount=""`: Retorna uma conta por número.

## **Contribuição**

Contribuições são bem-vindas! Siga os passos abaixo:

1.  Faça um fork deste repositório.
2.  Crie uma branch para sua feature ou correção:
```bash
git checkout -b feature/nova-feature 
``` 
3.  Faça o commit de suas alterações:
```bash
git commit -m "feat: detalhe da nova funcionalidade"
``` 
4.  Envie suas mudanças:

```bash
git push origin feature/nova-feature
```  
5.  Abra um Pull Request.

## **Licença**

Este projeto está licenciado sob a Licença MIT.

----------

### **Autor**

**Matheus Barbosa**

-   [LinkedIn](https://www.linkedin.com/in/matheushbmelo)
-   [GitHub](https://github.com/MatheusHBMelo)