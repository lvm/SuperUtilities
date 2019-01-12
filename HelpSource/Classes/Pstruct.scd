TITLE:: Pstruct
summary:: Pattern Structure
related:: Classes/Pdilla, Classes/Pcoin, Classes/Pnever, Classes/Psometimes, Classes/PifRest, Classes/PifEqual, Classes/Plsys, Classes/Pdur, Classes/Psec, Classes/Pmidi, Classes/Pcc, Classes/Pdirt, Classes/Pregularly
categories:: Streams-Patterns-Events>Patterns

description::

Gives a structure to a Pattern

Examples::

code::
(
Pstruct(
  Pseq([\bd, \sn], inf),  // a Pattern
  Pseq([3, 5], inf),  // a SimpleNumber or a Pattern
  8,  // a SimpleNumber or a Pattern
  0,  // a SimpleNumber or a Pattern
  inf // a SimpleNumber or inf
).asStream.nextN(16);
)
-> [ 36, r, r, 38, r, r, 36, r, 38, r, 36, 38, r, 36, 38, r ]
::