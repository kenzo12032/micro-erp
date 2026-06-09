# 🚀 Micro ERP

Sistema de gestão empresarial desenvolvido utilizando **Java, Spring Boot, Thymeleaf e MySQL**, com foco no gerenciamento de produtos, clientes, fornecedores, compras, vendas e estoque.

---

## 📋 Sobre o Projeto

O **Micro ERP** foi desenvolvido como projeto acadêmico com o objetivo de simular um sistema de gestão empresarial utilizado por pequenas e médias empresas.

A aplicação centraliza informações importantes do negócio, permitindo o controle de operações comerciais, movimentação de estoque e gerenciamento de cadastros de forma simples e eficiente.

O sistema foi construído seguindo o padrão de arquitetura **MVC (Model-View-Controller)**, proporcionando melhor organização do código, manutenção facilitada e escalabilidade.

---

## 🎯 Objetivos do Projeto

* 📦 Controlar produtos e estoque
* 👥 Gerenciar clientes
* 🏢 Gerenciar fornecedores
* 🛒 Registrar compras
* 💰 Registrar vendas
* 📊 Organizar informações financeiras básicas
* 📈 Fornecer consultas e relatórios para apoio à tomada de decisão

---

## ✨ Funcionalidades

### 🔐 Autenticação

* Login de usuários

### 📦 Gestão de Produtos

* Cadastro de produtos
* Edição de produtos
* Exclusão de produtos
* Controle de estoque
* Geração automática de SKU

### 👥 Gestão de Clientes

* Cadastro de clientes
* Alteração de clientes
* Exclusão de clientes

### 🏢 Gestão de Fornecedores

* Cadastro de fornecedores
* Alteração de fornecedores
* Exclusão de fornecedores

### 🛒 Gestão de Compras

* Registro de compras
* Entrada automática de produtos no estoque
* Histórico de compras

### 💰 Gestão de Vendas

* Registro de vendas
* Baixa automática de estoque
* Emissão de comprovante em PDF

### 📊 Financeiro

* Plano de contas
* Controle financeiro básico

### 📈 Dashboard

* Visualização rápida das informações do sistema

---

## 🏗️ Arquitetura do Sistema

O projeto segue o padrão **MVC (Model-View-Controller)**:

### 📂 Model

Responsável pelas entidades e regras de negócio.

### 🎨 View

Interfaces desenvolvidas utilizando Thymeleaf e Bootstrap.

### ⚙️ Controller

Responsável por receber as requisições dos usuários e integrar as regras de negócio com a interface.

---

## 🗄️ Banco de Dados

O sistema utiliza **MySQL** para persistência dos dados.

### Principais Entidades

* 📦 Produto
* 👥 Cliente
* 🏢 Fornecedor
* 🛒 Compra
* 📋 CompraItem
* 💰 Venda
* 📋 VendaItem

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia          | Finalidade                    |
| ------------------- | ----------------------------- |
| ☕ Java              | Linguagem principal           |
| 🍃 Spring Boot      | Framework backend             |
| 🗄️ Spring Data JPA | Persistência de dados         |
| 🎨 Thymeleaf        | Templates HTML                |
| 📱 Bootstrap        | Interface responsiva          |
| 🐬 MySQL            | Banco de dados                |
| 🔧 Maven / Gradle   | Gerenciamento de dependências |
| 🌐 Git              | Controle de versão            |
| 🐙 GitHub           | Hospedagem do código          |

---

## ▶️ Como Executar o Projeto

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/kenzo12032/micro-erp.git
```

### 2️⃣ Configurar o Banco de Dados

Criar um banco MySQL e ajustar as credenciais no arquivo:

```properties
application.properties
```

### 3️⃣ Executar a Aplicação

Executar o projeto através da IDE ou utilizando Maven/Gradle.

### 4️⃣ Acessar o Sistema

Após iniciar a aplicação, acessar pelo navegador:

```text
http://localhost:8080
```

---

## 📚 Conhecimentos Aplicados

Durante o desenvolvimento deste projeto foram aplicados conceitos de:

* Programação Orientada a Objetos
* Engenharia de Software
* Arquitetura MVC
* Banco de Dados Relacional
* Spring Boot
* Desenvolvimento Web
* Controle de Versão com Git
* Versionamento de Projetos com GitHub

---

## 🎓 Projeto Acadêmico

Projeto desenvolvido para fins acadêmicos no curso de **Sistemas de Informação**, visando aplicar na prática os conhecimentos adquiridos durante a graduação.

---

## 👨‍💻 Desenvolvedor

### Kenzo Ramos Otaguiri

🎓 Estudante de Sistemas de Informação

🏫 Universidade de Uberaba (UNIUBE)

📚 4º Período

☕ Desenvolvedor Java

🌱 Estudando Spring Boot, Banco de Dados e Desenvolvimento Web

🚀 Apaixonado por tecnologia, desenvolvimento de software e soluções empresariais

---

⭐ Se este projeto foi útil para você, deixe uma estrela no repositório!
