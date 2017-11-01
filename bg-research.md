Background Research
===============

Hypothesis
------------

Voice interaction is video games is too restricted. On one hand, the user may be restricted in the input they are allowed to say (even for slight variations of the exected input), and this is easier for the developer. On the other hand, the user may be able to say a plethora of commands, making it imporssible for the designer.

Is there a way to use NLP to strike a balance between both of these tradeoffs?

Existing Voice Recognition Games
---------------------

#### [Verbis Virtus](https://en.wikipedia.org/wiki/In_Verbis_Virtus)
- Indie game
- Like Skyrim, but activate spells with voice
- "Let there be light", "Beam of Light", "follow my will"; spell phrases are hard-coded.
- Looks like you can't vary the phrases
- Still have to press buttons to sprint, read inscriptions, etc.

#### [Star Trek VR game](https://www.engadget.com/2017/05/11/ibm-watson-voice-commands-to-star-trek-bridge-crew/)
- Integrates IBM's Watson to issue commands to crew members

#### [Speech Recognition for PC Games](http://www.tazti.com/speech-recognition-software-for-pc-games.html) (Not a game)
- Create profiles for keystrokes and trigger with speech command.

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

TODO
--------------------------
- [ ] Find out how scribblenauts works with its word dictionary
- [ ] Build skeleton project in Android testing I/O
- [ ] More research on NLP and Wordnet API