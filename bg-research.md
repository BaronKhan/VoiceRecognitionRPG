Background Research
===============

Hypothesis
------------

Voice interaction is video games is too restricted. On one hand, the user may be restricted in the input they are allowed to say (even for slight variations of the exected input), and this is easier for the developer. On the other hand, the user may be able to say a plethora of commands, making it imporssible for the designer.

Is there a way to use NLP to strike a balance between both of these tradeoffs?

Existing Voice Recognition (in games or otherwise)
---------------------

#### [Verbis Virtus](https://en.wikipedia.org/wiki/In_Verbis_Virtus)
- Indie game
- Like Skyrim, but activate spells with voice
- "Let there be light", "Beam of Light", "follow my will"; spell phrases are hard-coded.
- Looks like you can't vary the phrases
- Still have to press buttons to sprint, read inscriptions, etc.

#### [Star Trek VR game](https://www.engadget.com/2017/05/11/ibm-watson-voice-commands-to-star-trek-bridge-crew/)
- Integrates IBM's Watson to issue commands to crew members

#### [Speech Recognition for PC Games](http://www.tazti.com/speech-recognition-software-for-pc-games.html)
- Create profiles for keystrokes and trigger with speech command.

#### Houndify

- Used this in my own projects (Pascal, Cisco internship)
- Define custom commands with a regex-style structure
- To cover a lot of ways someone can say an intent, the 'regex' structure can get really long and complicated

E.g. asking someone what their name is:

```"what" . "is" . "your" . "name"```
- weak variation (only "what is your name")

```("what" . "is" | "what\'s") . "your" . "name" | "who" . "are" . "you"```
- slightly better ("who are you", "what's your name")

```("what" . "is" | "what\'s" | ["can" . "you"] . ["please"] . "tell" . "me") . "your" . "name" | "who" . "are" . "you"```
- getting really long, still doesn't cover a lot

We could go on and on, and this has to be done for *every* custom command.

Limitations of Zork
-----------------

- open mailbox - O
- open the small mailbox - O
- I would like to open the mailbox - X
- Can I open the mailbox? - X
- check the mailbox - X
- find out what's in the mailbox - X


- open the mailbox. read the leaflet -O (chaining actions)
- open the mailbox and read the leaflet -X (chaining actions)
- "You can't use multiple objects with that verb."


- close mailbox - O
- shut mailbox - X
- closing mailbox - X
- close the red mailbox - X


- open mailbox. close mailbox. - O
- open mailbox and then close mailbox - X

Potential Issues with any implementation
------------------------

- Action conflicts with homophones (voice rec software detects wrong word)?

Scribblenauts
------------------------

http://www.liquisearch.com/scribblenauts/development/engine

Uses Objectnaut engine. Each object is given properties to determine how they interact with each other. Also a hieracrchy of items (e.g. wood can burn, a boat is made of wood, therfore can also burn). Developers went word-by-word. Five people worked for six months to create a large database for Objectnaut.


POS tagger
-------------------

- Using Stanford POS tagger
- Using left-3-words model as it is __much__ faster than bi-drecrtional model.


Hypernym trees
---------------------------

sword



TODO
--------------------------
- [X] Find out how scribblenauts works with its word dictionary
- [X] Build skeleton project in Android testing I/O
- [X] More research on NLP and Wordnet API
- [ ] Explore hypernyms and synonyms trees for verbs and nouns for an RPG using WordNet (e.g. If I say sword, shield, hammer, etc, do they have "Weapon" or "Object" in their hierarchy?)