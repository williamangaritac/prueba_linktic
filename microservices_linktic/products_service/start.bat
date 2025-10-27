@echo off
echo ========================================
echo    PRODUCTS SERVICE - LINKTIC TEST
echo ========================================
echo.

echo Verificando Java...
java -version
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java no está instalado o no está en el PATH
    pause
    exit /b 1
)

echo.
echo Verificando Maven...
mvn -version
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven no está instalado o no está en el PATH
    pause
    exit /b 1
)

echo.
echo Compilando el proyecto...
mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo ERROR: Falló la compilación
    pause
    exit /b 1
)

echo.
echo Ejecutando pruebas...
mvn test
if %ERRORLEVEL% neq 0 (
    echo ADVERTENCIA: Algunas pruebas fallaron
    echo Continuando con la ejecución...
)

echo.
echo ========================================
echo Iniciando Products Service...
echo ========================================
echo.
echo La aplicación estará disponible en:
echo - API: http://localhost:8081/api/v1/products
echo - Swagger UI: http://localhost:8081/api/v1/swagger-ui.html
echo - Health Check: http://localhost:8081/api/v1/actuator/health
echo.
echo Presiona Ctrl+C para detener la aplicación
echo.

mvn spring-boot:run

pause
