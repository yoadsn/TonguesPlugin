#Tongues - Translation plugin for Bukkit MC server

Tongues implements real time translation support for messages between players and extends the basic chat services to include other useful features. Currently only the Bing translation API as the translation service.

This is quite a stable version, any issues or feature requests are welcome!

##Main features
- Translates on the fly chat messages
- Provides a translated "WhisperWithinRadius" command
- Uses the Bing Translation service (Requires an active account)
- Allows each player to configure its own language
- Allows pre-configuratino of player languages in a JSON data file
- Redirects global chat to the **talk** command by default
- Allows to "talk" to a specific player or all players (requires permissions)
- Allows definition of player groups, and sending chat messages to these groups
- Alloes listening to all chats taking place on the server
- Logs chat messages to files and/or [LogEntries](logentries.com)
- Configuration store files use the player UUID since version 1.3

##Installation
- Drop the JAR in the plugins folder
- Reload plugins or start the server
- Edit the plygin.yml created in the plugin's data folder to set the Bing translation API ClientID and SecretKey
- Possibly create languages and groups configuration files
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
/talk [groupName] [message] - Will send the message only to the players in group 'groupName'.

permissions: Requires permission **tongues.talk**

/talk * [message] - Will send the message to all players

permissions: Requires permission **tongues.talk.all**

aliases: talk, t

###tongues.listen###
/tl [on/off] - Enables or disables the global chat listening.

permissions: Requires permission **tongues.listen**

##Whisper Radius Configuration
The plugin allows configuration of the radius to consider for whispering to players (same in all directions x/y/z) with the config.yml key:
* *whisper.radius* (int) - Sets the whispering radius - default is 10

##Translation Logic
When a message is sent, the language of the player receiving the message is used to translate the message.

If no language is setup for this player, no translation is attempted or sent.
The translation is sent in addition to the original message a few moments later.

Currently, regardless of the sending player's configured language, an auto-detection of the message source language is done.

So for example, a "French" configured player might still send some "English" text and expect it to be translated correctly to a "Russian" configured player.

##Language Configuration
Language settings per player are stored in the file **langStore.json** in the plugin's data folder.
This file contains a single JSON object. The keys of this object are the player UUID and the values are the language.
Configuration of the language is stored in the file when the plugin is disabled and read when the plugin is enabled.
It is possible to edit the configuration with a normal text editor but please note:
- The file must contain a single valid JSON object in the format described above.
- A restart of the plugin is required to read configuration which was edited directly in the file.

Example of a valid language configuration JSON object:
```JSON
{
  "player1UUID": "arabic",
  "player2UUID": "hebrew"
}
```

##Groups Configuration
A Player can belong to zero or more groups.
To define the groups create a file called **groupsStore.json** in the plugin's data folder.
The file contains a single JSON object. The keys of this object are the group name and the values are the group member players' UUID's.
Each value is a JSON Array with string values for each player in the group.

- The file must contain a single valid JSON object in the format described above.
- A restart of the plugin is required to read configuration which was edited directly in the file.

Example of a valid groups configuration JSON object:
```JSON
{
	"blueGroup" : [
		"player1UUID",
		"player2UUID"
	]
}
```

The server would load the JSON file groups when started.
During server run time, the groups can be modified using the `add` and `remove` commands:

/tg add [group name] [player UUID]
group name - Any string or * for all groups
player UUID - The UUID of the player

/tg remove [group name] [player UUID]
group name - Any string or * for all groups
player UUID - The UUID of the player or * for all players

Currently - Saving the changes to the JSON file or reloading from it is not supported.

##Chat Logging
The plugin logs chat messages, their sender, receiver, and the message that was sent as it was formatted.
When a message is sent to multiple receivers (such as a public message, or a whisper with more than one player around) it will log one row for each delivered message.
When logging to files, chat logs are appended to a log file per server startup, organized in a month/day folder structure.
When logging to [LogEntries](logentries.com), it appends all messages using the token-based input with the configured token.

The server logs the following information for each chat message:
- Message send time - UTC
- Sender player name
- Sender player display name
- Receiver player name
- Receiver player display name
- Message as it was displayed to the client (with names, languages, and every other formatting applied)

Local log files are stored under the plugin data folder in the path: `logs/chat/<month>/<day>/chat.<TIMESTAMP>.log`

The config.yml file can configure the following for chat logging:
* *chatLogging.file.enabled* (true/false) - Sets if logging to files is enabled - default is true
* *chatLogging.logentries.enabled* (true/false) - Sets if logging to logentries is enabled - default is false
* *chatLogging.logentries.token* (string) - The token to use for logentries logging
* *chatLogging.logentries.debug* (true/false) - Enable or disable logentries debug messages to minecraft console - default false


**Note:** The logger will not log whisper commands which arrived at no one.

##Latest Changes

Since 1.4:
- Groups administration commands partially implemented (missing list, save, reload commands)

Since 1.3:
- Groups and Languages store files use UUID. Old configuration file are not compatible and needs to be recreated. (No migration tool is available)

Since 1.2:
- Added chat logging support using a file logger and/or logentries logger.
- Added a configuration key for the whispering radius

Since 1.1:
- Added the **listen** to allow listening to all chats on the server.

Since 1.0:
- Added player groups feature stored in a json file
- Sending chat messages now supports also sending to groups

Since 0.9:
- Added the **talk** command
- Blocked global chat by default and redirected it to the **talk** command

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
