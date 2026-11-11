@echo off
rem =====================================================
rem  Porquinho - sobe PostgreSQL + backend + frontend
rem  Uso: duplo clique ou "iniciar.bat" no terminal
rem =====================================================
cd /d %~dp0

echo [1/3] Subindo PostgreSQL (Docker)...
docker start financas-db 2>nul || docker-compose up -d postgres
if errorlevel 1 (
    echo [ERRO] Nao foi possivel subir o PostgreSQL. O Docker Desktop esta rodando?
    pause
    exit /b 1
)

echo [2/3] Subindo backend em nova janela (porta 8080)...
start "Porquinho - Backend" cmd /k "%~dp0iniciar-backend.bat"

echo Aguardando o backend iniciar...
timeout /t 15 /nobreak >nul

echo [3/3] Subindo frontend em nova janela (porta 4200)...
start "Porquinho - Frontend" cmd /k "%~dp0iniciar-frontend.bat"

echo.
echo Pronto! Acesse: http://localhost:4200
echo Para parar, feche as janelas "Porquinho - Backend" e "Porquinho - Frontend".
