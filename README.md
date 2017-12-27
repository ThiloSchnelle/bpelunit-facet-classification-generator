## BPELUnit facet classification generator

The BPELUnit facet classification generator takes a classification (tree) and fragments of code as inputs. It outputs a BPELUnit test suite.

### Files overview

Example files for the following descriptions are found in "files/template" and the corresponding schema is found in "files/schema". There are two folders ending with "Test" there, too, that contain working codefragments as a further example.

The generator expects the codefragments and the classification to be in the folder "files/suite/". This can be changed in the Executor.java. The resulting test suite is placed in this folder as well.

### Classification

The classification should be based on the specification of the process. It contains of a classification tree, that divides the specification in categories. For example in image recognition these could be "form" and "color". These categories subdivide into values, that are the leaves of the tree. Leaves of the "color"-category could be "black", "red" or "yellow".

A value may be flagged as a fault, so this value needs special treatment in test design.

In addition to this tree there can be conditions that rule out certain combinations of values. Like form:triangle AND NOT color:red which forbids test cases which test triangles that are not red.

The third part is the definition of test cases by selecting which values should be combined into one case. There may be only one value per category per test case.

#### Format
The classification is stored in an .xls-File (Microsoft Excel) as there is no graphical editor as of now.

### Codefragments

Codefragments are the pieces that the test suite is assembled of. Simple codefragments are 
* PartnerTrackSequence - A sequence of slots. Messages can be inserted in one of these slots. The order of these slots is respected during generation.
* MessageExchanges - The template for a sendReceive-style exchange. May contain variableSlot-Elements whose names are tied to variables.
* Variables - Variables have a name (tied to one or more variableSlots). For every variable there are VariableInstances from which one is chosen and inserted into all variableSlots of that name.
	
How the codefragments are assembled is definied in special codefragments - the mappings. A mapping corresponds to a leaf in the classification tree. It decides which messages to use at a certain slot in a PartnerTrack and what VariableInstances to use for a certain type of VariableSlot. Also it can deactivate MessageSlots and complete PartnerTracks.

Another special codefragment is the basefile.xml. This file contains the basic setup of the test suite. The generator adds its test cases inside the testCases-Element. Everything else that was inside this element remains untouched.