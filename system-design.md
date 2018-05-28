System Design
========================

Contributions
-----

- Allow a developer to add voice recognition to their game with little effort.
- Create a simple natural language processing system that can work offline.
- Explore NLP usages in RPGs (for text generation, item descriptions, etc).
- No deep learning required. Only uses WordNet.
- User can also easily create and adapt new voice commands.

Milestones
----------

- Create an RPG with exploration and battle gameplay using the systems.
- Create a library that wraps around everything.
- Wrap around a video conferencing app demo.


Sentence Structure
---------------------

Always imply 'I' as the subject. Therefore, only concerned with the main verb of
 the sentence, and the noun.

Using Slot-filling technique.

- For verbs, first use the semantic similarity. If > 0.7/1, then probably the
same thing. If not, then check if any of the primitive verbs are in the word tree.

- Commands in battle structure: ... `verb` ... [`target` ... with ... `context`]
- Commands in overworld structure: ... `verb` ... [`target` ... with ...
`context`]

### Starting with "use"/"with"

If the sentence starts with use/with, then the structure will be different
- First search for action, if best action is after a use/with, then choose that
structure
- ... use/with ... `context` ... `verb` ... `target`

### Sentence Deciphering

- As a test, first search for first verb, then search for a verb that works with
 the noun. Search for "with" or "using" for specific item.

Battle Systems
---------------------

- Turn-based

### Actions

- Attack ("attack")
- Defend ("prevent") (Add later)
- Heal ("better")

- Actions should be assigned default action if no other information is specified
 (e.g. attack with random move, heal with best item, etc.)

Items
---------

Types:
- Weapons
-- sharp
-- blunt
- Shields (add later)
- Healing Items
-- potion
-- hi potion
- Key Items

Items have default action.

Inventory System
--------------------

- Just an array of all the items the user has.
- Items have properties. Items is an abstract class.
- Item has a type, assigned to enum.

Create a lookup-table, mapping items to verbs, and the functions that are called


Objects
------------------

- Item
- Inventory
- Enemy
- Troll (Enemy)
- Action
- ItemActionMap


Pipeline
---------------

input -> get verb -> find noun, if no noun, use default

For battle mode:
- Tag input
- Assert that both lists are equal
- Get candidate action indices
- Find the best action, remove its index from words and tags
- Get candidate target indices
- Find best target, remove it from words and tags
- Get candidate contexts (items)
- Find best context (no need to remove)

For exploration mode:
- Tag input
- Assert that both lists are equal
- Get candidate action indices
- Find the best action, remove its index from words and tags
- Get candidate contexts (items)
- Find best context (no need to remove)

ContextActionMap
---------------

- This is an abstract class that the user overloads to specify the mapping of
contexts to actions.
- public Map<Integer, Map<String, Action>> mMap = new HashMap<>();
- But how to know whether an item or enemy matches a context?
- Contains the current target (also a Context), the actions specify what to do
for each target.

### Finding the best context

- Go through candidate context words
- If word matches object in inventory or environment, select it
- Each "Context" instance should have a string representing the context that it
is
- Map context string to hash table (indexed by that type of string).
- Need to have sources (list objects) from where to get the context, pass as
parameter


POS tagger
----------------

- VERB = v
- NOUN = n
- ADVERB = r
- ADJECTIVE = a
- CAll getTag function to get this letter

Synset IDs have SID- infront, must remove (and set tag to lowercase)


Recursive action calls
------------------

- Execute several actions in one input
- Segment phrases with "and" as a delimiter?
- Separate input across "and", "then"
- Without and or let: use leftovers?
- If intent is ambiguous? Have a partial queue and look for confirmation
- Feed in yes or no each time until partial queue finished


Confirmation
------------------

Problem: Semantic similarity messes up sometimes
"smash the table with a hammer"
"regenerate with a potion"
- Did you mean? If so, then create synonym mapping
- either action, target or context ambiguous
- Note: only update if user says same ambiguous phrase twice
- Have list of suggestions and go through them
- "show my troll", "obliterate the troll" are ambiguous
- "grab the utensil"
- Add attempts timeout
- Need to have a weighting for the synonyms that are added
- Example: pick up the utensil
- There is a fork in the room
- "did you mean fork?"
- Now utensil maps to fork
- Next room: knife
- "did you mean knife"
- utensil now maps to knife
- this will overwrite mapping of utensil to fork
- Next room contains fork and knife
- This should be ambiguous and ask which one they meant
- Can do this by mapping synonym to list of words

Action Replies
-----------------

- Action Class should have interface to ask for reply from system, and an
abstract function to execute the reply.
- "Are you sure you want to leave this room."


Performance Issues
------------------

- Problem: command, "break the table" was taking too long, this is due to
"break" being the last action checked, so all the previous actions are
using semantic similarity.


- Solution: use concurrency? Also separate for loop into two separate for loops,
if a direct match is found then don't have to run semantic similarity.

- Con: MT could stall app
- Java 8 streams
- current: "crack" = 7s (5s when cached)


Generate room based on description
-----------------

- Developer gives string input and then game generates room.
- Use methods which take as input the string text
- Have conditional methods for whether to display one text or another (e.g.
  if knife in room)
- addDescription(), addDescription(..., cond), etc.
- This should be considered an extension
- addDescriptionWithObject should add a sentence to a list, print if object exists
- Use Predicate for conditional storing
- Advantage: don't have to write a bunch of if statements (give example in repo)
- Want to do better room generation (just from a description)
- Something similar to RSpec
- Create a program that takes as input a text file of a room description
- The program will create a .java file for the room
- The user can fill in the gaps by themselves
- Program build an AST after parsing the grammar
- Make a Java program which uses semantic similarity library!
- https://github.com/knowitall/reverb
- 

Multiple targets
-----------------

- "pick up the knife and the potion"
- In the MultipleCommandProcess class
- Check the next statement if it contains only a possible target
- In VoiceProcess: if multiple commands and expecting more input toggles
- There is currently a bug when first command has context
- "attack the troll and the goblin with the sword"
- Only attack goblin with sword
- Must say "attack the troll with the sword and the goblin with the sword"
- This is a minor downside of the systems

Multiple Context
----------------

- "attack the troll with a sword and hammer"

Sentence similarity
-----------------

- Idea: let developer define pre-made phrases that map to intents
- Calculate semantic similarity of each word pair and give sentence score
- Have SentenceMapper class
- Run when intent is not understood and before first suggestion
- Let developer add example sentences for to map to an Action
- For each example sentence, specify target and context
- When executing intent, check if specified target is in possible targets list
- How to check: check string names of targets
- Example: "what is in my bag?", "what is inside my inventory",
"what items do I have?"
- Use cosine similarity calculation of similarity?
- Using soft cosine similarity measure ()

CustomWordNet
----------------

- Create a Github gist of CustomWordNet

Good examples
-----------------

- "pick up the cutlery" --> grab the knife

Lesk
-----------------

- Current time: 12min 27s
- Remove replaceAlls: 3min 4s
- Single replaceAll: 2min 13s

Library
-----------------

- Create script to automatically generate JAR from source files
- Replace com.khan.baron.voicerecrpg with package name in files
- Voice Commands with WordNet (VCW, voice-commands-with-wordnet)
- Add countless voice commands with little effort
- Map countless voice commands with ease
- Create separate repo with source files (copy them from main repo)
- Remove Log, Toast; add Pair, Triple classes
- Add main executable demo class to JAR

Video Conferencing example
-----------------

- "call dan"
- "phone dan"
- "contact dan"
- "call dan with video"
- "use audio to phone dan"
- "make a call to dan" - sentence mapping
- "make an audio call to dan" - sentence mapping
- "call dan and fred with video" - needs multiple targets (won't work)
- Add call to possible targets during call
- "stop call"
- "finish the call"
- "hang up [the call]"
- "hang up the call with dan"
- "Are you sure you want to hang up?"
- "mute my video"
- "silence my audio"

TODO
-----------------

- Need to add weightings to the synonyms
- Add match ignore code to target and context
- Use different similarity methods for action, target and context
- Better room generation from description