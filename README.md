# PeopleBatchLoader
Essa aplicação foi desenvolvida para lidar com o carregamento de dados de um arquivo em um banco de dados. Basicamente a aplicação lê os dados de um arquivo CSV, processa os registros do arquivo e em seguida armazena em um banco de dados.

## Dependências

### Java
Este aplicativo foi desenvolvido para rodar em JDK 1.8. Se você quiser utilizar uma JDK superior, precisará fazer alterações em sua JVM para adicionar módulos, como JAXB, para que as dependências funcionem corretamente.

### Maven
O projeto usa o Maven como ferramenta de construção. Este aplicativo foi desenvolvido e funciona com o Maven versão 3.8.2.

### IDE
O projeto foi construído usando VS Code.

### Spring Boot
O projeto utiliza o Spring Boot versão 2.5.4.

### Spring Batch
O projeto utiliza o Spring Batch versão 4.3.3.

## Instalação
Você pode clonar o repositório por meio do terminal apenas digitando:
```sh
git clone https://github.com/thiagohbhonorato/people-batch-loader
```
## Como usar

### Execução do projeto
Com o projeto aberto em sua IDE, execute a classe PeopleBatchLoaderApplication.java. Caso esteja utilizando o Spring Tools 4 no VS Code, Eclipse ou Theia, utilize o Spring Boot Dashboard para executar a aplicação.
![springbootdashboard](https://github.com/thiagohbhonorato/people-batch-loader/blob/main/doc/sbd.png "Spring Boot Dashboard")

### Banco de Dados
A aplicação utiliza o H2 Database para armazenamento dos dados. Para acessar o banco de dados, abra o navegador e acesse http://localhost:8080/h2-console em seguida preencha o formulário com as informações abaixo e clique em Connect:
```
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:mem:dbbatch
User Name: batch
Password: batch
```
![h2console](https://github.com/thiagohbhonorato/people-batch-loader/blob/main/doc/h2console.png "H2 Console")

Após acessar o console do banco de dados, é possível visualizar à esquerda as tabelas criadas. Localize a tabela PEOPLE e execute um SELECT para visualizar os dados, que inicialmente deverá estar vazia.

![table_empty](https://github.com/thiagohbhonorato/people-batch-loader/blob/main/doc/table_empty.png "Tabela PEOPLE")

### Carregamento dos dados
Para fins de testes, a aplicação possui um endpoint para execução do carregamento dos dados. Você pode utilizar o próprio navegador para executar o processo de carregamento dos dados. Abra o navegador e acesse http://localhost:8080/job/file/people.csv.

![execute_job](https://github.com/thiagohbhonorato/people-batch-loader/blob/main/doc/execute_job.png "Execução do carregamento dos dados")

Em seguida acesse novamente o console do H2 e execute o SELECT na tabela PEOPLE. Com a execução do processo, a tabela agora possui os dados que foram carregados do arquivo CSV.

![table_loaded](https://github.com/thiagohbhonorato/people-batch-loader/blob/main/doc/table_loaded.png "Tabela PEOPLE")
