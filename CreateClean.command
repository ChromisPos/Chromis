#!/bin/sh

DIRNAME=`dirname $0`
CP=$DIRNAME/chromispos.jar
CP=$CP:$DIRNAME/lib/
CP=$CP:$DIRNAME/locales/
CP=$CP:$DIRNAME/locales/Albanian
CP=$CP:$DIRNAME/reports/American
CP=$CP:$DIRNAME/locales/Arabic
CP=$CP:$DIRNAME/locales/Argentinian
CP=$CP:$DIRNAME/locales/Brazilian
CP=$CP:$DIRNAME/locales/Croatian
CP=$CP:$DIRNAME/locales/Dutch
CP=$CP:$DIRNAME/locales/English
CP=$CP:$DIRNAME/locales/Estonian
CP=$CP:$DIRNAME/locales/French
CP=$CP:$DIRNAME/locales/German
CP=$CP:$DIRNAME/locales/Italian
CP=$CP:$DIRNAME/locales/Mexican
CP=$CP:$DIRNAME/locales/Portuguese
CP=$CP:$DIRNAME/locales/Spanish
CP=$CP:$DIRNAME/reports/

java -cp $CP -Djava.library.path=$DIRNAME/lib/Linux -Ddirname.path="$DIRNAME" uk.chromis.pos.cleandb.JFrmCreateClean $1