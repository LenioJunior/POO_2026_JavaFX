@echo off
setlocal

echo ========================================
echo CONFIGURANDO AMBIENTE JAVA FX
echo ========================================

REM Pasta base
set BASE_DIR=%USERPROFILE%\Downloads\Tools

mkdir "%BASE_DIR%"

REM =========================
REM MAVEN
REM =========================

echo.
echo Baixando Maven...

powershell -Command "Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip' -OutFile '%BASE_DIR%\maven.zip'"

echo Extraindo Maven...

powershell -Command "Expand-Archive -Path '%BASE_DIR%\maven.zip' -DestinationPath '%BASE_DIR%' -Force"

set MAVEN_HOME=%BASE_DIR%\apache-maven-3.9.16

REM =========================
REM SCENE BUILDER
REM =========================

echo.
echo Baixando Scene Builder...

powershell -Command "Invoke-WebRequest -Uri 'https://download2.gluonhq.com/scenebuilder/26.0.0/install/win/SceneBuilder-26.0.0.msi' -OutFile '%BASE_DIR%\SceneBuilder.msi'"

echo.
echo Instalando Scene Builder para usuario atual...

msiexec /i "%BASE_DIR%\SceneBuilder.msi" /quiet ALLUSERS=2 MSIINSTALLPERUSER=1

REM =========================
REM PATH USUARIO
REM =========================

echo.
echo Configurando PATH do usuario...

setx MAVEN_HOME "%MAVEN_HOME%"

setx PATH "%PATH%;%MAVEN_HOME%\bin"

echo.
echo ========================================
echo INSTALACAO FINALIZADA
echo ========================================
echo.

echo Feche e abra o terminal novamente.

pause
