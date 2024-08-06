@echo off
setlocal

:: Defina o caminho para o seu arquivo JAR
set JAR_PATH="C:\caminho\para\seu\arquivo.jar"

:: Executa o arquivo JAR
java -jar %JAR_PATH%

:: Pausa o script para que você possa ver a saída
pause

endlocal
