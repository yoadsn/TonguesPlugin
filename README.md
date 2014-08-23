#Tongues - Translation plugin for Bukkit MC server

*Warning* - This Plugin is might be ready for production use.

Tongues implements real time translation support for messages between players.
Currently Chat and Whisper messages are supported and only the Bing translation API as the translation service.

This is an alpha version, any issues or feature requests are welcome!

##Main features
- Translates on the fly chat messages
- Provides a translated "WhisperWithinRadius" command
- Uses the Bing Translation service (Requires an active account)
- Allows each player to configure its own language
- Allows pre-configuratino of player languages in a JSON data file
- Redirects global chat to the **talk** command by default
- Allows to "talk" to a specific player or all players (requires permissions)

##Installation
- Drop the JAR in the plugins folder
- Reload plugins or start the server
- Edit the plygin.yml created in the plugin's data folder to set the Bing translation API ClientID and SecretKey
- Reload again
- Give permissions 

##Commands
###tongues.setlang###
/setlang [language] - Will change the current language of the issuing player.

permissions: Requires permission **tongues.setlang**

/setlang [player] [language] - will change the current language of the specified player. 

permissions: Requires permission **tongues.setlang.others**

When [language] is **none** the command would clear any current setting

When [language] is **?** the command would query and display the current language setting

aliases: setlang, lang 

###tongues.whisper###
/whisper [message] - Whisper a message to every player in radius

permissions: Requires permission **tongues.whisper**

aliases: whisper

###tongues.talk###
/talk [message] - Will whisper the message to any players around you

permissions: Requires permission **tongues.talk** and **tongues.whisper**

/talk [player] [message] - Will send the message only to the specified player

permissions: Requires permission **tongues.talk**

/talk * [message] - Will send the message to all players

permissions: Requires permission **tongues.talk.all**

aliases: talk, t


##Translation Logic
When a message is sent, the language of the player receiving the message is used to translate the message.

If no language is setup for this player, no translation is attempted or sent.
The translation is sent in addition to the original message a few moments later.

Currently, regardless of the sending player's configured language, an auto-detection of the message source language is done.

So for example, a "French" configured player might still send some "English" text and expect it to be translated correctly to a "Russian" configured player.

##Language Configuration
Language settings per player are stored in the file **langStore.json** in the plugin's data folder.
This file contains a single JSON object. The keys of this object are the player name and the values are the language.
Configuration of the language is stored in the file when the plugin is disabled and read then the plugin is enabled.
It is possible to edit the configuration with a normal text editor but please note:
- The file must contain a single valid JSON object in the format described above.
- A restart of the plugin is required to read configuration which was edited directly in the file.

Example of a valid language configuration JSON object:
```JSON
{
  "playerName1": "arabic",
  "playerName2": "hebrew"
}
```

##Latest Changes

Since 0.9:
- Added the **talk** command
- Blocked global chat by default and redirected to it to the **talk** command

Since 0.8:
- All identification of a Player is done by it's user name instead of it's UUID.
- Configuration of player language is persisted in a JSON format

Since 0.7:
- Permission and command names have changed
- Permission checks were implemented

Since 0.6:
- Supports persistence of player's configured language between plugin restarts

Since 0.3:
- Initial Release
- Bing translation API authentication data read from config file