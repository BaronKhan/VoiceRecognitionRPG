Voice Recognition RPG project
======================

This repository contains the work that has been done for the Voice Recognition
RPG project by Baron Khan, as part of the MEng Final Year Project
assessment in the Department of Computing at Imperial College London.

- [Final Report](https://github.com/BaronKhan/VoiceRecognitionRPG/blob/master/final-report/FinalReport.pdf)
- [Voice Commands with WordNet Library](https://github.com/BaronKhan/voice-commands-with-wordnet)
- [Voice Recognition RPG Demo Download](https://play.google.com/store/apps/details?id=com.khan.baron.voicerecrpg)

Abstract
------

When adding voice commands to a video game, a developer may find themselves
adding each variation of a phrase that a player can say, and mapping them to
intents within the game. It becomes more work to include varying phrases of the
same intent as the developer would have to include every permutation of the
phrase (for which there could be infinitely many).

This project presents a text-based role-playing game (RPG) on Android which
allows the player to issue actions within the game using their voice. The game
uses a new voice recognition system that aims to make it easier for the
developer to add voice commands without hard-coding the phrases, and works
completely offline with no cloud processing required. The system uses the
WordNet database created by Princeton University to process voice commands.

Various techniques for improving the voice recognition system are explored and
evaluated, with the aim of decreasing the developer workload. These include
using semantic similarity methods, learning mechanisms, and more. The voice
recognition system is also applied to other domains such as video conferencing
solutions, and cooking applications.

This project also explores other areas for easing the development of the RPG
using WordNet, such as automatically assigning properties to objects, and room
generation from text descriptions.