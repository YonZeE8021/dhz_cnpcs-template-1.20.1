@echo off
setlocal
cd /d "%~dp0.."
chcp 65001 >nul
where py >nul 2>&1 && (
  py -3 "%~dp0mixin_accessor_audit.py" %*
  exit /b %ERRORLEVEL%
)
where python >nul 2>&1 && (
  python "%~dp0mixin_accessor_audit.py" %*
  exit /b %ERRORLEVEL%
)
echo 未找到 Python，请安装 Python 3 或使用 Microsoft Store 的 python。
exit /b 1
