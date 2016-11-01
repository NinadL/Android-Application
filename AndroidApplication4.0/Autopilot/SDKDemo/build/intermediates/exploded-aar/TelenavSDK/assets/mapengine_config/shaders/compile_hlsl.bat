@echo off
setlocal

del /Q /S *.hlslo

@for /f %%f IN ('dir /b *.vert.hlsl') do (

..\..\util\hlslc\hlslc.exe %%~nf.hlsl %1

)

@for /f %%f IN ('dir /b *.frag.hlsl') do (

..\..\util\hlslc\hlslc.exe %%~nf.hlsl %1

)

endlocal