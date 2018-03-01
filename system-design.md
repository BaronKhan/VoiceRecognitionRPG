System Design
========================

Contributions
-----
• Allow a developer to add voice recognition to their game with little effort.
• Create a simple natural language processing system that can work offline.
• Explore NLP usages in RPGs (for text generation, item descriptions, etc).


Sentence Structure
---------------------

Always imply 'I' as the subject. Therefore, only concerned with the main verb of the sentence, and the noun.

- For verbs, first use the semantic similarity. If > 0.7/1, then probably the same thing. If not, then check if any of the primitive verbs are in the word tree.

- Commands in battle structure: `verb` `target` with `item`
-- Exceptions: "use"
- Commands in overworld structure: `verb` `object` with `item`

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

POS tagger
----------------

VERB = v
NOUN = n
ADVERB = r
ADJECTIVE = a
CAll getTag function to get this letter

Synset IDs have SID- infront, must remove