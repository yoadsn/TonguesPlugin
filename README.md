#Tongues - Translation plugin for Bukkit MC server

*Warning* - This Plugin is not ready for production use.

Tongues implements real time translation support for messages between players.
Currently Chat and Whisper messages are supported and only the Bing translation API as the translation service.

This is an alpha version, any issues or feature requests are welcome!

##Main features
- Translates on the fly chat messages
- Provides a translated "WhisperWithinRadius" command
- Uses the Bing Translation service (Requires an active account)
- Allows each player to configure its own language

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

##Translation Logic
When a message is sent, the language of the player receiving the message is used to translate the message.

If no language is setup for this player, no translation is attempted or sent.
The translation is sent in addition to the original message a few moments later.

Currently, regardless of the sending player's configured language, an auto-detection of the message source language is done.

So for example, a "French" configured player might still send some "English" text and expect it to be translated correctly to a "Russian" configured player.

#Latest Changes

Since 0.8:
- All identification of a Player is done by it's user name instead of it's UUID.

Since 0.7:
- Permission and command names have changed
- Permission checks were implemented

Since 0.6:
- Supports persistence of player's configured language between plugin restarts

Since 0.3:
- Initial Release
- Bing translation API authentication data read from config file