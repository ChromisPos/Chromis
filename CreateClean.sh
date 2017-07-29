#!/bin/sh


DIRNAME=`dirname $0`
CP=$DIRNAME/chromispos.jar
CP=$CP:$DIRNAME/reports/
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

# Select the library folder
case "`uname -s`" in
    Linux)
    case "`uname -m`" in
    i686) LIBRARYPATH=/lib/Linux/i686-unknown-linux-gnu;;
    ia64) LIBRARYPATH=/lib/Linux/ia64-unknown-linux-gnu;;
    x86_64|amd64) LIBRARYPATH=/lib/Linux/x86_64-unknown-linux-gnu;;
    esac;;
    SunOS)
    case "`uname -m`" in
    sparc32) LIBRARYPATH=/Solaris/sparc-solaris/sparc32-sun-solaris2.8;;
    sparc64) LIBRARYPATH=/Solaris/sparc-solaris/sparc64-sun-solaris2.8;;
    esac;;
Darwin) LIBRARYPATH=/lib/Mac_OS_X;;
CYGWIN*|MINGW32*) LIBRARYPATH=/lib/Windows/i368-mingw32;;
esacr

java -cp $CP -Djava.library.path=$DIRNAME/lib/Linux -Ddirname.path="$DIRNAME" uk.chromis.pos.cleandb.JFrmCreateClean $1