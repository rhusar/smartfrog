@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfStop.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

echo ---------------------------------------------------
echo sfStop is now obsolete and replaced by sfTerminate.
echo Please use sfTerminate from next time.
echo ---------------------------------------------------
call "%SFHOME%\bin\sfTerminate" %1 %2

endlocal
