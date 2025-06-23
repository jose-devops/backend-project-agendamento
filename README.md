# 🏡 Backend - Sistema de Agendamentos

Este é um projeto backend desenvolvido com **Spring Boot** para gerenciar um sistema de agendamentos entre **proprietários** e **moradores**. O sistema permite login de usuários, cadastro de agendamentos, controle de perfis e listagem de dados com base no tipo de usuário autenticado.

## 📁 Estrutura do Projeto

Project-main/
├── src/main/java/com/api/app/
│ ├── config/ # Configurações (CORS, etc)
│ ├── controllers/ # Controladores REST
│ ├── dtos/ # Objetos de transferência de dados
│ ├── models/ # Entidades JPA
│ │ └── enums/ # Enumerações (Status, Tipo de Agendamento)
│ ├── repositories/ # Interfaces JPA Repository
│ └── AppApplication.java # Classe principal do Spring Boot
├── pom.xml # Configuração Maven
└── .mvn/ # Wrapper Maven



## 🚀 Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security (com autenticação JWT)
- H2 / PostgreSQL / MySQL (dependendo da configuração)
- Maven

## 🔐 Funcionalidades

- **Autenticação com JWT**
- **CRUD completo de usuários (proprietário, morador)**
- **Cadastro e gerenciamento de agendamentos**
- **Enumerações para status e tipos**
- **Permissões baseadas no tipo de usuário**
- **CORS configurado**

## 📦 Endpoints Principais

### Autenticação
- `POST /auth/login` - Realiza login e retorna o token

### Usuários
- `GET /usuario/{id}` - Buscar usuário
- `POST /usuario` - Criar usuário

### Proprietário
- `GET /proprietario/me` - Dados do proprietário logado

### Morador
- `GET /morador` - Lista moradores vinculados

### Agendamento
- `POST /agendamento` - Criar agendamento
- `GET /agendamento` - Listar agendamentos
- `PUT /agendamento/{id}` - Atualizar agendamento
- `DELETE /agendamento/{id}` - Deletar agendamento

## 📌 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Passos

```bash
# Clone o projeto
git clone https://github.com/seuusuario/backend-agendamento.git
cd backend-agendamento/Project-main

# Compile o projeto
./mvnw clean install

# Rode o projeto
./mvnw spring-boot:run
