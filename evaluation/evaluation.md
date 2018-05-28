Evaluation of Semantic Similarity Methods
==========

Note: this Java project requires the repository's library to be generated (go
  to the `library` directory and run `buildJar.sh`).

Goal
---------

- To evaluate how well the system can detect various phrases with no helpers
attached using different semantic similarity methods
- Generate a csv file for the results, and whether the correct intent is
returned
- Have three different domains
- Game commands (ATTACK, HEAL, MOVE | ENEMY, SELF | SWORD, POTION)
- Cooking (MAKE, STIR, BOIL | EGGS, SOUP | SPOON, COOKER)
- Video Conferencing (CALL, STOP, MUTE | PERSON | VIDEO, AUDIO)
- No helpers allowed


Tests
---------

- Game:
- attack the enemy
- charge at the enemy
- hit the enemy
- attack with a sword
-

Test Layout
---------

- Run each method separately (too slow to change on each test)
- Separate csv for each method
- Headers for each domain
