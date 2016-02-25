Zeppa-Smartfollow

When pulling from this branch you download src/main/resources/signatures, a library of semantic signatures used for ADW word comparison. This is so you can run ADW locally by setting properties in src/main/config/adw.properties.

In Eclipse, right-click on the signatures folder and select Properties. Expand Resources, select Resource Filters, Add... Set the parameters to Exclude all, Files and Folders, and check All children (recursive). Click OK out of the prompts. You will still be able to access the files even though they do not appear in the Eclipse workspace.

This module automatically follows tags based on calculated interest
These are the implemented instances when smartfollow is invoked:
	- CreateInitialTagFollows: Two users connect and interesting tags should be followed

This library makes use of Princeton's Natural Language Processing Library, Wordnet

WordNet 3.0 Copyright 2006 by Princeton University. All rights reserved. THIS SOFTWARE AND DATABASE IS PROVIDED "AS IS" AND PRINCETON UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED. BY WAY OF EXAMPLE, BUT NOT LIMITATION, PRINCETON UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES OF MERCHANT- ABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT THE USE OF THE LICENSED SOFTWARE, DATABASE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.

