SeqColExtras {

  *new {
    |input|
    ^this.asMidiRange(input);
  }

  *asMidiRange {
    |input|

    // return...
    // rercusive funkyness
    if(input.isKindOf(Array) or: (input.isKindOf(List))) {
      ^input.collect({ |i| this.asMidiRange(i) });
    };

    // or the value
    ^(127 * input).floor;
  }

}


+ SequenceableCollection {

  midiRange {
    ^SeqColExtras.asMidiRange(this);
  }

}