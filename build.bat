@echo off
echo Building Recipe Application...

echo.
echo Building Frontend...
cd recipe-frontend
call npm install
call npm run build
cd ..

echo.
echo Building Backend...
call mvn clean package

echo.
echo Build complete! 
echo To run the application:
echo 1. Start the backend: mvn spring-boot:run
echo 2. For development, start the frontend: cd recipe-frontend ^&^& npm run dev
echo For production, the frontend is already built and served by the backend at http://localhost:8080