14th June
v0.94 

Bug fixes
+Resolved refund items not being returned to stock
+Resolved issue with auxillary items
+Fixed spinner issue for default remote screen

+Fixed icon set install that stopped linux running
+Fixed permissions bug 


Deprecated
All card payment methods (except external) have been removed, they are not PCI compliant, other options are being investigated.


New features
+Sales screen shows orders for kitchen using colour lines - this option needs an external library to replace the currently used one. This can only be obtained from 
 http://chromis.co.uk/download/chromis-coloured-ticketlines/ 

+New feature to allow a product to be sent to a selected kitchen printer
+New feature to allow a product to be sent to a selected kitchen\remote screen, No more properties to be set.
+New feature to allow an override printer, if a product has this flag set all order items will be sent to the override printer set in the config file.
+Fixed heap issue created with auto refresh\logoff
+Add picture frame in Sale Panel 
+Average item cost calculated, when delivery added, When a delivery is added the cost of the item is calculated based
	on the old cost * qty and the new cost * qty.
+Cost price of product updated if cost price is changed on delivery page.

Other
+Backend work started on preparing for new version - changed user rights now stored in database as an array rather than a text file. 


There are no changes to the supported database engines in this version.
Always back up your existing database before upgrading to the latest version

*********************************************************************************************************
4th September 2017
v0.81

minor bug fixes
Fixed import issue where you are unable to sell imported product.
Fixed missing image due to retired jar file
Added ability to create debug file by passing /debug as parameter

Always back up your existing database before upgrading to the latest version

*********************************************************************************************************
10/08/2017
Updated Installer to fix exe issue. (no change to version number)
*********************************************************************************************************
Always back up your existing database before upgrading to the latest version

*********************************************************************************************************
6th August
v0.80

Bugs found in beta resolved
Started tidy up process of liquibase scripts responsible for database creation and upgrade

New field added to lineremoved entries, also show line value.
Line removed report adding
Hourly sales report available for the current day in sales reports

Depending upon the size of your existing database, the upgrade can take a while, as this upgrade recreats
all the primary key, foreign keys and indexes

New Git repository created for use from this release. Check source forge for links

*********************************************************************************************************
17th July 2017
v0.72 beta

Resolved issues reported from last beta

Added hourly sales to close cash report
Fixed create Clean database issue
Update Liqibase scripts to support easier build and update routines

*********************************************************************************************************
24th June 2017
v0.71 beta

Fixed some bugs in dbmanager section
Fixed some bugs in configuration panels
Fixed issue causing configuration panel to throw error
Fixed Issue with reports not working
Fixed employee timesheet report
Changes to sync code ready for release
Preperations made ready for changes to liquibase & upgrade process
Added fixes from  TJMChan
Remove Administrator.bat - whilst further dev is done to it

Bug #122 fixed - product order in new sales screen
Bug #118 fixed sale profit report

Started test on Mantis bug reporting, to enable more robust bug tracking (currently invitation only)

*********************************************************************************************************
10th June 2017
v0.70 beta

Implemented new layout screens - Wildfox Coder changes
Implemented some custom javaFX controls, to reduce dev time
Started the implementation of Javafx in to the application, the first stage is the configuration panels
Properties files is now written in alpabetical order, making it easier to read if required.
Phase 1 of the suppliers panel implemented
Implemented new editor screen in the resources
Implemented new db manager screen (javafx based)
Removed migration tools - this will be come an external application

*********************************************************************************************************
18th December 2016
v0.61.4 beta

Simplified product search options from sales panel. removed unnecessary parameters like buy price, sell price
Bug #87  fixed with the release of the cleaner search function above.
Bug #109 fixed bug that caused the customer name to be removed if the reset option was used in customer chooser dialog.


More refinement to primary and foreign keys for sync code

Fixed issue with receipts where person logged in not being recorded.
Added new report to allow the products sold to be viewed before the close cash is run.
Added abilty to save CSV import profile, this saves the users from having to repeat the setup each time the same header 
format is used.

*********************************************************************************************************
5th December 2016
v0.61.2 beta

More changes ready for Sync process.
New Feature - if using sync user can now check stock at other stores
Added update sync library v2.1.0


New report directory structure.
	Single reports_messages.properties file replaces multiple files
	Reports folder contains folder for each supported database type, each contains reports designed to run
	 against  that db engine
	Report bs file changed
		report.setReport("/uk/chromis/reports/mysql/newproducts") must point to the folder for the db engine
		---------------------------------------------------------------------------------------------------- 
		if you have custom reports these must be moved the the correct folder for the database type you are
		using
		----------------------------------------------------------------------------------------------------

Fixed bug in closed cash where the close date is incorrect.
Fixed issues with saleprofit report which was caused by ticket line multiple items
Fixed issues with ean in reports, quick build filters	
Fixed bug #106, this is caused by Oracles changes to MySQL 5.7.7 and above, removed all default options
Changed leaves table to not allow data to be saved with blank dates, as part of bug #106 fix

Added new columns to ticketlines table - taxrate & Taxamount, stores the tax details at time of creation
Added Tony's code to allow new product to be added via stock diary.
Added ability to allow disocunt onm all imported products using csv

Set information dialog now always centre of application frame
Set editline dialog now always centre of application frame
Set product finder dialog now always centre of application frame
Set attribute dialog now always centre of application frame
	
		

*********************************************************************************************************
19th October 2016

Created new installer to resolve reports issues.
Fixed database creation issue.
Added Wildfox coders change for customer debt.
*********************************************************************************************************
10th October 2016
Special Release
This a special release with hot fix included as part of the install.
*********************************************************************************************************
29th June
Release v0.58.5.3

This is a replacement jar to fix a P1 issue, Bug # 75 
Rename original file and replace wthe the file in 'HotFix Files' folder.
*********************************************************************************************************
26th June
Release v0.58.5.2

This is a replacement jar to fix a P1 issue, 'Table lock issue resolved' 
Rename original file and replace wthe the file in 'HotFix Files' folder.

*********************************************************************************************************
16th June
Release v0.58.5.1

This is a replacement jar to fix a P1 issue, where a partial cash payment clears entire customers debt. 
Rename original file and replace wthe the file in 'HotFix Files' folder.

*********************************************************************************************************
1st June 2016
Release v0.58.5

-Bug fix customer discount not saving to database

*********************************************************************************************************
31st May 2016
Release v0.58.4

-Fixed issue in productfinder (Search) now shows in stock for current till location
-New Column added for sync process
-Ability to print the original creator of a ticket if required (printOriginalUser())
-New sync library added (1.06)
-Removed line of debug code
-Added Sync version to info box
-Removed default shift out of table, not needed

** Always backup previous version before upgrading to the latest version

*********************************************************************************************************

23rd May 2016
Release v0.58.2-beta

-Bug fix release to fix upgrade issue, when adding primary key to linerremoved table.
-Service charge not adding to new build now fixed 

*********************************************************************************************************
15th May 2016
Release v0.58.1

Release to resolve upgrade bug reported with Derby database

-Fixed bug reported with Derby upgrade
-Table lock feature added for restaurant operation
-Add cleandb script, missing from last release
-Code changes ready for future work, with currency
-Updated external library
-Updated some template files
-Fixed bug in custom barcodes cehcksums
-Fixed bug in config panels

*********************************************************************************************************
2nd May 2016
Release v0.58

This is a full release and not a beta, All upgrades should be from a full version and not a previous beta version

-New Migration Routine (current solution completely re written).
-Create clean database, users can now create a clean database after testing, ready for production. 
-Users can now restore any of the supplied resource (xml & txt files), should the menu.root get messed up for example
-Liquibase script re written to allow easy control in the future.
-Now only supports Derby, MySQL & PostgreSQL databases
-Updates to sync function ready for release
-updates to some external Libraries files - Sync menu is not enabled by default
-Fixed bug in UPC baecode 1st checksum digit was read as part of the price

- All fixes in previous beta included.

*********************************************************************************************************
25th April 2016
Release v0.57.2-beta

-Phase 1 of sync code added to core. 
-Added maximize window on startup option
-new payment type added
-consolidate cash payments into single payment
-fixed recipes issue


*********************************************************************************************************
17th April 2016
Release:v0.57

-Changed index to allow multiple products to have the same name, but they must still have unique reference and barcode.
-Upgraded to Jasper reports from 6.1.1 to 6.2.1
-Change to locale config for time and date, to allow the user to pick format to be used
-Updated reports to use time & date format selected
-Ability to run a repair script if required, by copying file to Chromis folder, prior to conversion or start up
-Dropped support for oracle and HSQLdb

bug #57 removed ifnull and added coalesce instead
********************************************************************************************************
10th April 2016
Release:v0.57-beta


This is a beta version. It is advised not to use in live until results are in from testing.
It includes some of the features added to github. 


Notes:
Changes to the way the upgrade process works, to reduce the time it takes, with MySQL databases.
Extra table added ready for sync function.
Extra column added to large number of tables ready for sync function.
Restructure of Liquibase changesets.
New QBF filter, using miglayout used in some reports
Added CasPDII scales support
Fixes for kitchen screen supplied by N Deppe
New version of customised Liquibase jar, fixes index checksum
Modified Convert app
Startup & upgrade error handling improved - modified from github code(JB)


***********************************************************************************************************
6th March 2016
Release:v0.56.2

Bug Fix release

Close cash - Derby fixed
Changes to convert routine to fix Openbravo bug
Fixes to upgrade when user has deleted some default items
Fixes to Datas file repairs report errors
Fixes to permission messages


***********************************************************************************************************
1st March 2016

Bug fix for recipe\kit products

***********************************************************************************************************

29th February 2016
Release:v0.56

Moved main locales out of country folders
Fixes to dialogbox when using dark colour themes.
Updated some of the locales files with updates provided by users
Updated Migration routine uses new process and cover the latest version, added progress bar
Added fix from Wildfox coder for CSV import
Allow reset of pickup ID within application
Added custom error sound option, modified from John B code
Added autocomplete to products packproduct
Added John B change to only load products after filter is applied
Source code ability to add menu options without removing existing menu.
Adding fixes from John B for promotions and 
Added date of birth to customer records

***********************************************************************************************************
2nd February 2016
Release: v0.55

Update to the convert routine
Update to create\upgrade dialog boxes
Refactor of columms in Derby database to resolve the foreign key issue, which is caused by different columns 
size, in databases that are converted.

Added recipes (formally product kit).
Improved barcode printign routine to allow the software to use graphics for barcodes not supported by the 
printer.

***********************************************************************************************************
10th January 2016
Release: v0.54.4

Update convert routine, This will allow updates from version 2.50 of Unicenta and Openbravo, the convertor
from these versions will still require user intervention to complete the process.

Bugs fixes
Bug around max change resolved

Features
Long names displayed in products and customer panels
New config panel to separate the restaurant settings
Autorefresh on tables, if enabled will auto refresh tables view every 5 seconds
Added option to allow auto popup of layaways
Added option to allow the table buttons to be located from floors
Allow edit of historic ticket providing the day is not closed
 
***********************************************************************************************************
3rd January 2016
Release: v0.54.3

Bug fixes
#41 Debit Sales locked - if payment is made against customer, does not reflect in layaways
#42 Update from v0.54.1 failing
#43 Exit button on info panel

Features
Moved tiptext messages to pos_messages

***********************************************************************************************************
2nd January 2016
Release: v0.54.2

Release to fix bugs identified in v0.54.1

Also included change limit feature, to prevent barcode number being used for change.

***********************************************************************************************************
28th December 2015
Release: v0.54.1/0

Release to fix 
bug#34, this was caused by a source code merged - only affects creating new tables

***********************************************************************************************************
27th December 2015
Release: v0.54


***********************************************

PLEASE NOTE THAT SHARED TICKETS IS DELETED AS 
PART OF THIS UPGRADE PROCESS. THIS IS REQUIRED
TO ALLOW CHANGES TO WORK WITHOUT ERRORS

***********************************************

Bugs
#24 Sales as refunds fixed
#26 Report error fixed
#27 Multiple auxiliary items fixed
#28 new customers fixed
#29 customer adding fixed
#30 Checkin/out report fixed
#32 printAlias bug fixed allows printing of &


Other errors
- Fixed csv import error
- Customer Display routine updated



Features
- All reports have been moved out of the jar file, making it easier to add your own reports or
  customize existing reports.
- Reports now load dynamically, based upon database type.
- Fixed some reports that did not work with all database version.
- All locales have been moved from the jar file, allowing easier editing.
- Change Icon colours from the configuration panel.
- Added new event for scripts 'ticket.save'
- Added new promotions engines.
- fixed issue with customer display, user can now select which display type in configuration.
- Using '+' key on sales screen for quick sales, now reads name of product in by default.
- merged branches from Github into the main code.
- Only create pickup id if order contains ptoducts.
- Improved the way layaways are handled, The ticket will always retain original owner and ID.
- Pickup ID can be used for layaways id
- csv now able to create categories if required
- csv now has progress bar during import routine


***********************************************************************************************************
2nd December
Release: v0.53.3

This changes the resources, to match new tickettype, which make the scripts more readable. In version v0.32.3 
these scripts are converted automatically.

The conversion can be run manually, if required by running resettickettype from the Chomis program folder.

***********************************************************************************************************
1st December 2015
Release: v0.53.2

New features
Added toggle type switches in place of simple checkboxes.
Improved Autologoff routine
Updated the way config properties are handled ready for later changes
Display Categories using show number, if number is null then displays these in name order.
New set of coloured icons available - Orange

Bug #21 Error in Auxiliary Products caused by incorrect column name - fixed
Bug #20 Historic items sent to kitchen printer - fixed
Bug #19 Incorrect tendered amount in reprint - fixed in templates.

#Fixed issue in config, no longer asks to save when no changes have been made to the configuration.
#Fixed case issue in liquibase script
#Fixed issue with new refund routine
***********************************************************************************************************
14th November 2015
Release: v0.53.1

Fix for derby upgrade fail. Found in v0.53. 
As part of this the product pack, auto refresh implemented on the product pack.
**********************************************************************************************************
11th November 2015
Release: v0.53

Bug #16	variable price, product screen display affected only, now resolved
Bug #15 Delete freshly added products - Thanks Wildfox coder
Bug #14 CSV import updated and resolved
Bug #17 Refund bug inherited from Unicenta and Openbravo, user can refund dame recipt multiple times
Bug #9  CustomerView generates insert & Update on load - thanks tsmi


* Included new CSV import - Thanks Wildfox coder
* Refactor of derby database code, since derby 10.10.20 they boolean function changed. to use true/false
  rather than 1/0. Table changed to allow the new fucntion.
* Addition of pack product feature from John Barrett, including new table ready for stock app import.
* Barcode changed to allow ISBn-13 codes to be recognized.
* Migrate routine updated to allow for new changes.
* New event script added 'ticket.pretotals' this runs before the totals are displayed on the screen.
* Display.consolidation updated  
	display.consolidatedwithoutprice if true does not use price when consolidating on screen tickets 
	only workis if display.consolidated=true, to switch on on screen consolidation
* Option to hide the default product popup
* Added usb to print options, works the same way as raw, except it make it easier to implement

*********************************************************************************************************** 
18th October
Release: v0.52

Bug #6 - Multi install error - fixed
Bug #8 - Ticket.Buttons - fixed error
Bug #10 - Barcode printing, issue printing some barcode type in reports - fixed


Automatic barcode type recognition routine added. When a product is saved the type 
of barcode is calculated using some basic formulas
* if it contains no numeric characters, is is flagged as CODE128
* 7 digits with correct checksum - UPC-E
* 8 digits with correct checksum - EAN-8
* 12 digits with correct checksum - UPC-A
* 13 digits with correct checksum - EAN-13
* 14 digits with correct checksum - GTIN

Any code above with the incorrect checksum if defined as null, this is is required as 
some scanners will reject the code. This is not used for general scanning only when 
printing reports.
Drag'n'drop images into chromis for stock records.
Updated ready for the new version of the Kitchen Screen Application V1.50. 
More general tidy up of the code.
Merged changes from John Barrett in to the main code.

***********************************************************************************************************
21st September 2015
Release: v0.51

Fixed SQL errors in Convert and it now informs the the user of its progress via a progress bar.


* Default Icon colours changed.
* Added the ability for the user to change the colout of the icons. Located in the install folder
  is folder called icon sets, copy the the required jar to the lIb folder to changethe colours
* Identified bug in look and feel, unrequired lib file, now fixed
* Found issue of missing field in products when coming from 3.70  - fixed
* Found issue with rightslevel moving from 3.70 - fixed
* Update ticket.buttons to point to image library for built in buttons. Maintains consistency
* New shortcut icons now in use - thanks Fanzam 
* Text version of permissions now deactivated by default, On custom permissions will need to be added 
  to the database.
* Started to tidy the message dialog boxes.
* The main bug is fix in variable barcodes, these have now been written to comply with GS1 UK & GS1 US. 
  Included is a pdf which explains how these barcodes work and how to set them up correctly in Chromis.
* Plus a number of bug fixes supplied by John Barrett, thanks John.


bug #5
All icons set to use 18x18 size to maintain consistency - fixed


