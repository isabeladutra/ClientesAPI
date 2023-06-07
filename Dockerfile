# Define a imagem base com a versão do Java 17
FROM eclipse-temurin:17

# Define o diretório de trabalho dentro do container
WORKDIR /app


# Copia o arquivo JAR da aplicação para o container
COPY clientesAPI-1.0.0.jar app.jar

# Expõe a porta que a aplicação utiliza
EXPOSE 8080

# Comando a ser executado quando o container for iniciado
CMD ["java", "-jar", "app.jar"]