# Assinador Digital

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0.43-blue)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)

## Descrição

O **Assinador Digital** é uma aplicação em Java com Spring Boot para gerenciar assinaturas digitais.  
As configurações sensíveis (banco de dados, e-mail e JWT) são carregadas via arquivo `.env`.

---

## Tecnologias

- Java 21  
- Spring Boot  
- MySQL  
- Maven  
- JWT (JSON Web Token) para autenticação  
- JavaMail para envio de e-mails  

---

## Pré-requisitos

- Java 21 
- MySQL instalado e rodando

---

## Configuração

### Banco de Dados

Crie o banco de dados MySQL:

```sql
CREATE DATABASE assinadordb;
```

## Arquivo .env

Na raiz do projeto, crie um arquivo .env com as seguintes variáveis:
```env
# Banco de dados
DB_HOST=localhost
DB_PORTA=3306
DB_NOME=assinadordb
DB_USUARIO=root
DB_SENHA=root

# Configurações de e-mail
EMAIL_LOGIN=email@remetente.com
EMAIL_SENHA=senhaemail

# JWT
JWT_SEGREDO=umaChaveSuperSecretaComPeloMenos32Caracteres!!!
JWT_EXPIRACAO=86400000
```
