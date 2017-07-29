@echo off

set DIRNAME=%~dp0
set CP="%DIRNAME%chromispos.jar"
set CP=%CP%;"%DIRNAME%locales/"
set CP=%CP%;"%DIRNAME%locales/Albanian"
set CP=%CP%;"%DIRNAME%locales/American"
set CP=%CP%;"%DIRNAME%locales/Arabic"
set CP=%CP%;"%DIRNAME%locales/Argentinian"
set CP=%CP%;"%DIRNAME%locales/Brazilian"
set CP=%CP%;"%DIRNAME%locales/Croatian"
set CP=%CP%;"%DIRNAME%locales/Dutch"
set CP=%CP%;"%DIRNAME%locales/English"
set CP=%CP%;"%DIRNAME%locales/Estonian"
set CP=%CP%;"%DIRNAME%locales/French"
set CP=%CP%;"%DIRNAME%locales/German"
set CP=%CP%;"%DIRNAME%locales/Italian"
set CP=%CP%;"%DIRNAME%locales/Mexican"
set CP=%CP%;"%DIRNAME%locales/Portuguese"
set CP=%CP%;"%DIRNAME%locales/Spanish"

set CP=%CP%;"%DIRNAME%reports/"

start /B javaw -cp %CP% uk.chromis.pos.sales.JResetPickupID