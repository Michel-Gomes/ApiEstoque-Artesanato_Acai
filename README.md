# ApiEstoque-Artesanato_Acai

API REST para gerenciamento de estoque e movimentações de produtos, desenvolvida com **Java 21 e Spring Boot**, com foco no controle de estoque de fábrica e loja, gerenciamento de lotes de fabricação e rastreabilidade das movimentações.

O projeto faz parte de uma solução de gestão de estoque e possui uma arquitetura organizada em camadas, com separação entre controllers, services, repositories, DTOs e entidades.

## 📌 Sobre o projeto

A aplicação foi desenvolvida para apoiar o controle de produtos e estoques, permitindo registrar e acompanhar movimentações relacionadas à fábrica e à loja.

Entre as principais funcionalidades estão:

* Cadastro e gerenciamento de produtos;
* Controle de estoque da fábrica;
* Controle de estoque da loja;
* Gerenciamento de lotes de fabricação;
* Registro de movimentações de estoque;
* Rastreabilidade de lotes nas movimentações;
* Integração entre diferentes processos relacionados ao estoque;
* Validação de dados;
* Tratamento centralizado de exceções;
* Persistência de dados em PostgreSQL;
* Documentação da API com OpenAPI/Swagger.

O projeto também passou por correções de integração e evolução das regras de negócio, incluindo a implementação do rastreamento de lotes nas movimentações.

## 🏗️ Arquitetura

A aplicação utiliza uma arquitetura em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Além das camadas principais, o projeto possui estruturas específicas para DTOs, entidades, validações, exceções, handlers, enums e scripts SQL.

### Principais componentes

| Componente       | Responsabilidade                                           |
| ---------------- | ---------------------------------------------------------- |
| `controller`     | Exposição dos endpoints REST                               |
| `service`        | Regras de negócio e processamento das operações            |
| `repository`     | Acesso e persistência dos dados                            |
| `dto`            | Transferência e representação dos dados da API             |
| `entities`       | Entidades persistidas no banco                             |
| `enums`          | Representação de estados e tipos utilizados pela aplicação |
| `exception`      | Exceções específicas da aplicação                          |
| `handler`        | Tratamento das exceções e respostas da API                 |
| `configurations` | Configurações da aplicação                                 |
| `sql`            | Scripts relacionados ao banco de dados                     |

## 📦 Módulos principais

### Produtos

Responsável pelo gerenciamento dos produtos utilizados no controle de estoque.

### Estoque da fábrica

Permite controlar a disponibilidade e as movimentações dos produtos relacionados ao estoque da fábrica.

### Estoque da loja

Responsável pelo controle dos produtos disponíveis no estoque da loja.

### Lotes de fabricação

Gerenciamento dos lotes utilizados no processo de fabricação, permitindo relacionar os produtos e suas respectivas movimentações.

### Movimentações da fábrica

Registro e controle das movimentações realizadas no estoque da fábrica.

### Movimentações da loja

Registro e controle das movimentações realizadas no estoque da loja.

### Rastreabilidade de lotes

Funcionalidade adicionada para permitir o acompanhamento dos lotes relacionados às movimentações de estoque, aumentando a rastreabilidade das operações realizadas no sistema.

## 🛠️ Tecnologias utilizadas

### Backend

* Java 21
* Spring Boot 3.5.7
* Spring Web
* Spring Data JPA
* Hibernate
* Spring Validation
* Lombok
* MapStruct
* ModelMapper
* PostgreSQL 16
* Maven

### Documentação e testes

* OpenAPI
* Swagger UI
* Spring Boot Test

### Infraestrutura

* Docker
* Docker Compose
* PostgreSQL
* Variáveis de ambiente

## 🗄️ Banco de dados

O projeto utiliza **PostgreSQL 16** como banco de dados.

A configuração do ambiente utiliza variáveis de ambiente para as credenciais do banco:

```env
DB_USERNAME=seu_usuario_aqui
DB_PASSWORD=sua_senha_aqui
```

O Docker Compose disponibiliza o PostgreSQL na porta:

```text
localhost:5439
```

Enquanto a API é executada na porta:

```text
localhost:8080
```

## 🐳 Docker

O projeto possui configuração com Docker Compose para facilitar a execução da API e do banco de dados.

Serviços utilizados:

```text
┌─────────────────────────┐
│      Estoque API        │
│     Spring Boot         │
│       :8080             │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│      PostgreSQL 16      │
│       :5439             │
└─────────────────────────┘
```

Os serviços utilizam uma rede Docker dedicada:

```text
estoque_network
```

E os dados do PostgreSQL são persistidos por meio do volume:

```text
postgres_data
```

O projeto também utiliza scripts de inicialização do PostgreSQL através do diretório:

```text
docker/postgres-init
```

## ⚙️ Configuração

### 1. Clonar o projeto

```bash
git clone https://github.com/Michel-Gomes/ApiEstoque-Artesanato_Acai.git
cd ApiEstoque-Artesanato_Acai
```

### 2. Configurar as variáveis de ambiente

Crie um arquivo `.env` baseado no `.env.example`:

```env
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
```

### 3. Executar com Docker Compose

```bash
docker compose up --build
```

Após a inicialização, a API estará disponível em:

```text
http://localhost:8080
```

### 4. Executar sem Docker

Também é possível executar a aplicação diretamente através do Maven:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Nesse cenário, é necessário garantir que o PostgreSQL esteja disponível e configurado corretamente.

## 📖 Documentação da API

A aplicação utiliza **SpringDoc OpenAPI** para geração da documentação dos endpoints.

Com a aplicação em execução, a interface Swagger pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```

A documentação OpenAPI pode ser utilizada para consultar e testar os endpoints disponibilizados pela aplicação.

## 📁 Estrutura do projeto

```text
src/
└── main/
    └── java/
        └── br/
            └── ...
                ├── configurations/
                ├── controller/
                ├── dto/
                ├── entities/
                ├── enums/
                ├── exception/
                ├── handler/
                ├── repository/
                ├── service/
                ├── sql/
                └── EstoqueApiApplication.java
```

## 🔄 Evolução do projeto

O projeto possui histórico de manutenção e evolução das regras de negócio.

Entre as alterações recentes estão:

* Correção de problemas relacionados à integração;
* Implementação do rastreamento de lotes nas movimentações;
* Evolução dos processos de estoque;
* Ajustes nos módulos de fábrica e loja.

Essas alterações fazem parte da evolução contínua da aplicação e de suas regras de negócio.

Documentação e testes

* OpenAPI
* Swagger UI
* Spring Boot Test
* **JUnit — testes automatizados planejados para as regras de negócio e funcionalidades da aplicação**

A implementação dos testes automatizados com **JUnit** faz parte da evolução do projeto, com foco em validar as principais regras de negócio e reduzir regressões durante futuras alterações.


## 🚀 Evoluções futuras

Entre as funcionalidades planejadas para futuras versões está a possibilidade de integração com **recursos de Inteligência Artificial**, ampliando as possibilidades de análise e automação do sistema.

> A integração com IA está planejada e ainda não faz parte da implementação atual.

## 🎯 Objetivos técnicos

O projeto demonstra a aplicação prática de conceitos como:

* Desenvolvimento de APIs REST;
* Arquitetura em camadas;
* Separação de responsabilidades;
* Desenvolvimento de regras de negócio;
* Persistência com JPA/Hibernate;
* Validação de dados;
* Tratamento de exceções;
* Mapeamento entre DTOs e entidades;
* Integração com PostgreSQL;
* Containerização com Docker;
* Configuração por variáveis de ambiente;
* Documentação de APIs;
* Evolução e manutenção de uma aplicação existente.

## 🔗 Frontend

O projeto possui também uma aplicação frontend desenvolvida em **Angular**, responsável pela interface de gerenciamento do estoque.

O repositório do frontend será disponibilizado separadamente.

## 👨‍💻 Autor

**Michel da Silva Gomes**

Analista de Sistemas | Java | Spring Boot | Angular | REST APIs | AWS

[GitHub](https://github.com/Michel-Gomes)
