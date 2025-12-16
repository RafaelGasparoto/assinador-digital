# Assinador Digital

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0.43-blue)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)

# Documento de Instalação – Assinador Digital

## 1. Descrição

O **Assinador Digital** é uma aplicação desenvolvida em **Java com Spring Boot** para gerenciamento de assinaturas digitais.
As configurações sensíveis, como banco de dados, e-mail e JWT, são carregadas a partir de um arquivo `.env` localizado na raiz do projeto.

---

## 2. Tecnologias Utilizadas

* Java 21
* Spring Boot
* MySQL
* Maven
* JWT (JSON Web Token) para autenticação
* JavaMail para envio de e-mails

---

## 3. Pré-requisitos

Antes de iniciar a instalação, certifique-se de que os seguintes softwares estejam instalados:

* **Java 21**
  Verifique a instalação com:

  ```bash
  java -version
  ```

* **MySQL 8.0**
  Download disponível em:
  [https://dev.mysql.com/downloads/mysql/8.0.html](https://dev.mysql.com/downloads/mysql/8.0.html)

* **Eclipse**
  Download disponível em:
  https://www.eclipse.org/downloads/packages/release/2022-06/r/eclipse-ide-enterprise-java-and-web-developers
  
* Caso não queira instalr a IDE do Eclipse, utilizar **Maven**
  Download disponível em:
  https://maven.apache.org/download.cgi
  
  Verifique a instalação com:

  ```bash
  mvn -version
  ```

---

## 4. Configuração do Banco de Dados (via CMD)

### 4.1 Acessar o MySQL pelo CMD

1. Abra o **Prompt de Comando (CMD)**.
2. Navegue até a pasta `bin` do MySQL (caso o MySQL não esteja no PATH):

   ```bash
   cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
   ```
3. Conecte-se ao MySQL utilizando o usuário root:

   ```bash
   mysql -u root -p
   ```
4. Informe a senha do usuário quando solicitado.

---

### 4.2 Criar o Banco de Dados

Após acessar o console do MySQL, execute o comando abaixo:

```sql
CREATE DATABASE assinadordb;
```

Opcionalmente, você pode validar se o banco foi criado:

```sql
SHOW DATABASES;
```

---

## 5. Configuração do Arquivo `.env`
1. Gerar uma senha de app pelo google, seguir o tutorial disponível em https://support.google.com/mail/answer/185833?hl=pt-BR
2. Na raiz do projeto, crie um arquivo chamado **.env** com o seguinte conteúdo:

```env
# Banco de dados
DB_HOST=localhost
DB_PORTA=3306
DB_NOME=assinadordb
DB_USUARIO=root
DB_SENHA=root

# Configurações de e-mail
EMAIL_LOGIN=email@remetente.com
EMAIL_SENHA=senhaemail (senha de app gerada no passo 1)

# JWT
JWT_SEGREDO=umaChaveSuperSecretaComPeloMenos32Caracteres!!!
JWT_EXPIRACAO=86400000
```
---

## 6. Compilação e Execução do Projeto
1. Faça instalação do Maven, disponível em https://maven.apache.org/download.cgi, ou Compile o projeto pela IDE do Eclipse
2. Acesse a pasta raiz do projeto.
3. Compile o projeto com Maven:

   ```bash
   mvn clean install
   ```
4. Cole o .env junto ao local do .jar gerado.
5. Executar o JAR gerado:

```bash
java -jar target/assinador-digital-0.3.1.jar
```

---

## 7. Observações Finais

* Certifique-se de que o MySQL esteja em execução antes de iniciar a aplicação.
* Verifique firewall e portas liberadas (3306 para MySQL e 8080 para a aplicação).
* Ajuste as configurações do `.env` conforme o ambiente.

