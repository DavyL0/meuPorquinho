@echo off
rem Sobe o frontend Angular na porta 4200 e abre o navegador
cd /d %~dp0frontend
npx ng serve --port 4200 --open
