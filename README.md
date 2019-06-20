BetterBeds
==========
### paper-api 1.13.2 update

Paper plugin to let you sleep better in Minecraft's survival multiplayer.

The original plugin can be found here:
http://www.spigotmc.org/resources/better-beds.1824/

## What is this?
This is an update to a much beloved and popular bukkit / spigot plugin to the newer builds of paper, the superior Minecraft server environment which provides maintained High-Performance patch-updates to Bukkit and Spigot -> https://papermc.io/

This plugin will allow you to provide a more convenient way for large multiplayer servers to roll over to the next day by allowing a certain percentage of people who choose to sleep at night roll the server over to the next day automatically.

Getting tired of your chat's "Go sleep !" spam at night? This plugin can resolve all that for you by making nights a more pleasent opt-in/opt-out experience for your players.

Are your buddies AFK? Is one of 'em stuck down a strip mine or 400 blocks away from their beds? No worries, as long as a certain number of players choose to sleep, your night rollover is covered thanks to BetterBeds!

[Back to top](#top)

## Features
- Allows a set percentage of players to roll the server over to the next day if they choose to sleep.
- Properly configured permissions nodes for LuckPerms / Vault.
- Allows fully customizable notifications for the server when night rolls around and when players choose to sleep.
- Ignores players who are AFK or offline, they won't count toward your needed sleep percentage!

[Back to top](#top)

## Improvements
- Uses the new paper/spigot api to provide a better experience.
- Complete, more professional code rebase!
- Custom bed handler that fixes / patches up vanilla's message notifications and elliminates the issues of the old plugin.

[Back to top](#top)

## Releases

Want to download the new and improved version of the plugin?  Head on over to the [Releases](https://github.com/loopyd/BetterBeds/releases) section

[Back to top](#top)

## Building from source

You will need:  [openJDK 8](https://adoptopenjdk.net/) and [Maven](https://maven.apache.org/)
Also recommended:  git 

1. **Clone** or **Download** the contents of this repository.

2. Maven pom.xml and a Jetbrains [Intellij IDEA](https://www.jetbrains.com/idea/) .iml project file has been provided for you.  If you don't fancy using a paid IDE, you can run the following commands from the repository root with maven in your ``PATH`` and the JDK in both your ``PATH`` and ``JAVA_PATH`` environment veriables:

```
    cd ./BetterBeds
    mvn package
```

The contents of this repository as-is should __never__ be considered to be in a **production-ready** state.  If you would like production-ready builds, please check the [Releases](https://github.com/loopyd/BetterBeds/releases) section.

[Back to top](#top)

## Contributing

This project is free and open source, and always open to new ideas.  Follow these instructions to contribute:

1.  **fork** this repository.
2.  ``clone`` your new repository to create a local copy.
3.  Configure my repository as your ``origin/upstream`` provider.
4.  Make your **changes** and **test** them with a [paper 1.13.2 server jar](https://papermc.io/downloads#Paper-1.13).  If you need help building the plugin during testing, follow the **Building from Source** guide.
5.  ``commit`` & ``push`` your changes to your repository when you are finished.
5.  Submit a **merge pull request** with an **adiquite** description of your changes here.
6.  Wait __patiently__!

*Plugin development not your thing, but you love the plugin and what it's done for your Minecraft community?  Donations are welcome: 
[Buy me a BetterDrink](https://www.paypal.com/paypalme2/snowflowerwolf) :-)*

[Back to top](#top)

# Bugs or Issues ?

They happen.  You can report them by opening an issue ticket.
