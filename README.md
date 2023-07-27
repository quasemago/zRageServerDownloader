# zRageServerDownloader
Server Downloader is a tool created to automate the mass downloading of servers assets lists (maps, sounds, etc). It eases the process by automatically figuring out what assets from the servers list aren't downloaded, and downloading/extracting them to your games assets folder.

The application is based off a tool published by Vauff (https://github.com/Vauff/MapDownloader).

## Can I Use This For My Server?

To use it on your server, you will need to fork this repository and change the [servers.json](https://github.com/ZombieRage/public/blob/master/ServerDownloaderApp/servers.json) query URL to your own. You can then build the program and distribute it to your players. The program will automatically update the servers list for anyone using it, so you can add/remove servers at any time.

## How To Use
The map/assets list must be an online hosted URL (to support the dynamically updating nature of the program), where you choose to host it is up to you.

The format for any custom map list just needs to be a CSV (comma separated values) map list with a $ symbol prefixing any map not using .bz2 format in the FastDL (likely maps > 150 MB). You can view the map list for ZRAGE BR ZE as an example [here](https://github.com/ZombieRage/public/blob/master/ServerDownloaderApp/maps_csgoze.csv).

The format for any custom asset list just needs to be a TXT  (semicolon separated values) asset list. You can view the asset list for ZRAGE BR ZE as an example [here](https://github.com/ZombieRage/public/blob/master/ServerDownloaderApp/assets_csgoze.txt).