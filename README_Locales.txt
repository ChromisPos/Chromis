

Acknowledgement: The content of the language/Locale files are compiled from the efforts of
Openbravo, Openbravo POS Community Members and we appreciate and 
acknowledge everyone who has contributed to making this distribution possible.

All files, including these Locale files, are made available under the GPL v3 License and within those terms must be passed on to any person who requests them
*******************************************************************************
Please refer to Locales Guide for installation details.

Chromis POS supports 15 languages.

English UK is the default language set.

Not all locales are completely translated

The locale files shipped with this version of Chromis POS are the latest at the date of this release.


Application Locales (Default):
beans_messages.properties is the Java generic message file
data_messages.properties is relevant to the data interface 
erp_messages.properties - no longer used (ex-Openbravo POS)
pos_messages.properties is the MAIN application label and Messages file

Reports:
Each report has a .properties file associated with it.
You will find the files in the  Chromis POS installation folder in the
\reports\com\openbravo\reports

HOW TO CHANGE YOUR LANGUAGE
1. Copy the locale you need from the language sub-folder \locales\languagename
   into the \chromispos\locales parent folder
2. Copy the content of reports folder from the language sub-folder
   \locales\languagename\reports folder into the \chromispos\reports folder 
3. Set the Chromis POS Configuration>Locale to your required language