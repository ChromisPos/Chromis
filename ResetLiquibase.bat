@echo off
REM    Chromis POS  - The New Face of Open Source POS
REM    Copyright (c) (c) 2015-2016 2016 
REM    http://www.chromis.co.uk
REM
REM    This file is part of Chromis POS
REM
REM    Chromis POS is free software: you can redistribute it and/or modify
REM    it under the terms of the GNU General Public License as published by
REM    the Free Software Foundation, either version 3 of the License, or
REM    (at your option) any later version.
REM
REM    Chromis POS is distributed in the hope that it will be useful,
REM    but WITHOUT ANY WARRANTY; without even the implied warranty of
REM    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM    GNU General Public License for more details.
REM
REM    You should have received a copy of the GNU General Public License
REM    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
REM

set DIRNAME=%~dp0
set CP="%DIRNAME%chromispos.jar"
set CP=%CP%;"%DIRNAME%locales/"
set CP=%CP%;"%DIRNAME%lib/"

start /B  java -cp %CP% uk.chromis.pos.liquibase.JResetLiquibase