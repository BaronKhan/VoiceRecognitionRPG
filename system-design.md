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

Always imply 'I' as the subject. Therefore, only concerned with the main verb of the sentence, and the noun.

Using Slot-filling technique.

- For verbs, first use the semantic similarity. If > 0.7/1, then probably the same thing. If not, then check if any of the primitive verbs are in the word tree.

- Commands in battle structure: ... `verb` ... [`target` ... with ... `context`]
- Commands in overworld structure: ... `verb` ... [`target` ... with ... `context`]

### Starting with "use"/"with"

If the sentence starts with use/with, then the structure will be different
- First search for action, if best action is after a use/with, then choose that structure
- ... use/with ... `context` ... `verb` ... `target`

### Sentence Deciphering

- As a test, first search for first verb, then search for a verb that works with the noun. Search for "with" or "using" for specific item.

Battle Systems
---------------------

- Turn-based

### Actions

- Attack ("attack")
- Defend ("prevent") (Add later)
- Heal ("better")

- Actions should be assigned default action if no other information is specified (e.g. attack with random move, heal with best item, etc.)

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

- This is an abstract class that the user overloads to specify the mapping of contexts to actions.
- public Map<Integer, Map<String, Action>> mMap = new HashMap<>();
- But how to know whether an item or enemy matches a context?
- Contains the current target (also a Context), the actions specify what to do for each target.

### Finding the best context

- Go through candidate context words
- If word matches object in inventory or environment, select it
- Each "Context" instance should have a string representing the context that it is
- Map context string to hash table (indexed by that type of string).
- Need to have sources (list objects) from where to get the context, pass as parameter


POS tagger
----------------

- VERB = v
- NOUN = n
- ADVERB = r
- ADJECTIVE = a
- CAll getTag function to get this letter

Synset IDs have SID- infront, must remove


Recursive action calls
------------------

- Execute several actions in one input
- Segment phrases with "and" as a delimiter?
- Separate input across "and", "then"


Confirmation
------------------

Problem: Semantic similarity messes up sometimes
"smash the table with a hammer"
"regenerate with a potion"
- Did you mean? If so, then create synonym mapping
- either action, target or context ambiguous
- Note: only update if user says same ambiguous phrase twice