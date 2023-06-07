# ClientesAPI
## Descrição do Projeto
<p> Esta é uma API REST que realiza um CRUD de clientes. </p>

## Funcionalidades
Esta é uma aplicação Spring Boot com interface totalmente REST que permite:
* Inclusão de Clientes
* Exclusão de clientes
* Atualização de dados de clientes
* Consulta de clientes pelo CPF
* Listagem com possibilidade de filtrar por DDD dos Celulares, trazendo todos os clientes que possuem celulares com aquele DDD;
* Listagem com possibilidade de filtrar por uma parte do nome;

## Tecnologias Utilizadas
* java 17
* Banco mysql em container
* Docker
* Spring boot, Spring MVC e Spring Data
* Junit e Mockito

## Como Rodar Localmente
Primeiro é preciso clonar o repositório usando o comando: ``` git clone git@github.com:isabeladutra/ClientesAPI.git ```

É preciso ter instalado na máquina o java 17, segue o link da [página de download do java](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

É Preciso baixar o docker, segue o link da [página do docker](https://docs.docker.com/desktop/)

Com o docker instalado e configurado, você vai entrar na pasta onde clonou o projeto com o comando:
``` cd clientesAPI ```

E vai buildar a imagem do dockerfile com o comando:
 ``` docker build -t clientesApi ```

Depois é só subir o container com a imagem do projeto:
``` docker run -p 8080:8080 clientesApi ```
 

A aplicação ficará disponibilizada no endereço: ```http://localhost:8080/```


O projeto dispõe de um swagger que está disponível após subir a aplicação localmente e acessar o endereço no navegador: http://localhost:8080/swagger-ui/index.html#/
![image](https://github.com/isabeladutra/ClientesAPI/assets/39921576/dd7d72ed-63e0-4816-acdd-1eae87a7e42e)

O projeto possui também uma collection do postman na raiz do projeto com o nome: clientes.postman_collection.json


