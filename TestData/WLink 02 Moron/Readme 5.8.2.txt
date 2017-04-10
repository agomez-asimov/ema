README Summary 10-14-2008
WeatherLink 5.8.2 for Vantage Pro, VantagePro 2, 
Envoy, Monitor, Wizard, and Perception stations

Version 5.8.2 
- Updated the Silicon Lab's USB direct driver and VCP driver to version 5.30.

Version 5.8.1 Bug Fixed
- Updated USB driver DLL files, CP210xManufacturing.dll and SiUSBXp.dll
- Fixed problem with using direct USB driver with Vista.
- Fixed CRC error when using direct USB communication.

Version 5.8.0 Features
Support has been added for the new WeatherLinkIP data logger (# 6555). 
The Communications Port now supports both a direct USB connection  and multiple connection
methods for WeatherLinkIP.
The Station Walkthrough has been modified to accommodate the addition of a web station.

Version 5.8.0 Bugs Fixed
WeatherLink will crash if it cannot access database files over a network.

Version 5.7.1 Features
- Updated new daylight savings dates for North America for years after 2006.
- Calculated hourly rain and last 24-hour rain for APRS.
- Changed APRS default server to "rotate.aprs.net".
- Changed Console Diagnostic screen to display console or Envoy firmware version and/or date.

Version 5.7.1 Bugs Fixed
- Fixed problem with yearly rain fall not showing up correctly on the "Set Yearly Rain" dialog box when the unit was "mm" instead of "inch".

Version 5.7 Features 
- APRS Feature changes - Option of APRS vs. NOAA report has been removed.   The standard is   NOAA.   WeatherLink software now sends 2 minute average wind speed and 10 minute gust into   the APRS string for NOAA requirement.   For Vantage Pro console, altimeter setting is   reported instead of barometer in the APRS string.   WeatherLink now supports rotating APRS   server.   The default APRS server is "rotatewx.aprs2.net"
- Bright Sunshine Hours Report has been added to the reports menu
- Number of COM ports have been increased to 18 ports

Version 5.7 Bugs Fixed
- WeatherLink will no longer lock up if database files are readonly.
- Bulletin bar no longer shows -32.767 if bar value is missing
- Sunrise/Sunset calculation for Monitor/Wizard does not always default to Pacific Time Zone   settings
- WeatherLink now allows Palm .pdb and capitalized .PDB files to be imported into the - WeatherLink software
- Saving the plot templates now works correctly


Version 5.6 Bugs Fixed
- Fixed memory leak generating historical upload graphs
- Fixed some anomolus High wind speed issues with internet uploads.
- The Forecast HTML tag works for Monitor stations.
- A new help file exists.
- Fixed problems with reporting negative ISS reception percentages.
- Added Inside Dew and Inside Heat to the Summary Window
- Fixed APRS reporting of the new average and high wind values in non-MPH units.
- Added "Low Console Batteries" warning message to the Bulletin and Summary windows
  and added the current console battery voltage to the Alarm & Battery Status window.
- Modified the Wind Chill and Heat warning messages in the Bulletin and Summary to
  reflect the messages that appear on the Vantage console when the particular weather
  data is selected on the console, instead of the general message if another variable
  is selected.
- Fixed a problem with viewing log files in different directories. 
- NOAA Year reports blank out rain data for months with no data
- Historical gifs with Week and 2 Week time spans use Windows date format for 
  MM/DD vs DD/MM. 
- Fixed issue with default strip chart templates not being stored in the correct folder.

Known Bugs in 5.6:
- Will not print in color on a Tectonics Phaser 850 Printer
- Weather Underground upload is not interruptible.
- Occasionally WeatherLink tries to send the same record twice to Weather Underground
  (i.e. the last record of one upload is sent as the first record for the next upload).
  This generates a "Duplicate key" warning message which can be ignored.
- When a Plot or Strip chart starts at midnight, the first midnight record is not displayed.
  This is because it "belongs" with the previous day's data.
- The progress dialogs for downloads and internet uploads can not be hidden or 
  minimized, even when the WeatherLink program has been minimized.
- If you maintain your weather station on Standard time, but Windows sets your PC to
  Daylight savings time (or if for some other reason your station time is different from
  your PC time), the barometer graph in the Bulletin will have a 1-hour gap between the
  last data record and the current time. For Monitor and Perception stations, this means
  that a forecast can not be generated until the Bulletin has been running continuously
  for 3 hours.
- 64-bit drivers are not available for the USB datalogger. Until they can be provided by
  our supplier,

Version 5.5 Bugs Fixed
- The "Use 00:00" option for exporting data is fixed.
- Fix Memory leaks in internet upload. 
- Set Transceivers dialog correctly sets Temp Only station on Vantage and Envoy systems.
- Fix Bulletin barometer graph freezing when a month rolls over.
- Fixed a bug in the allowed range of ET entry in the Edit record dialog. (0.254 inches is
  allowed).
- Valid Barometer values for data entry and calibration are 16.00 to 32.50 inches. Values
  outside of this range are dashed.
- Fixed Summary display of High/Low values with inappropriate large negative values.
  Specifically, soil moisture values greater than 127 or missing temperature sensors.
 
  THE USB DATALOGGER DOES NOT SUPPORT 64-BIT VERSIONS OF WINDOWS.

TO INSTALL THE PROGRAM

From the Web:
Download and run the file WeatherLink 56 Install.exe to begin the installation.
Follow the on screen prompts.

From the CD:
Place the Install Disk in your CD ROM drive
The install program should start automatically. If the install program does not start, 
choose Run from the Start menu, type D:SETUP (or E:SETUP, substituting the correct 
drive letter for D or E), and choose OK to begin the installation.
Follow the on screen prompts.

  PLEASE SEE THE README FILE FOR MORE INFORMATION ABOUT THE 
  FOLLOWING TOPICS:

- Installing the new USB drivers
- Uninstalling WeatherLink
- The new Internet Upload configuration file
- Requirements for VantagePro 2 repeaters
- Converting or transferring data from earlier WeatherLink installations
- WeatherLink database file formats 
