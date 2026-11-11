@echo off
rem Sobe o backend Spring Boot carregando as variaveis do .env
cd /d %~dp0

rem Carrega o .env (formato CHAVE=VALOR, sem segredos neste script)
for /f "usebackq tokens=1,* delims==" %%a in (".env") do set "%%a=%%~b"

rem Java 21+ necessario; usa o JDK local se JAVA_HOME nao estiver definido
if not defined JAVA_HOME set "JAVA_HOME=C:\Users\Davy\.jdks\openjdk-26.0.1"

backend\porquinho\mvnw.cmd -f backend\porquinho\pom.xml spring-boot:run
