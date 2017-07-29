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


