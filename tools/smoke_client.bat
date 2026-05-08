@echo off
REM 冒烟：编译并启动 Fabric 开发客户端（需已配置 JDK）
cd /d "%~dp0.."
call gradlew.bat compileJava --no-daemon || exit /b 1
call gradlew.bat runClient --no-daemon
