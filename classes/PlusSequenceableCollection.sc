PlusSequenceableCollection {

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

  // requires ChordSymbol: https://github.com/triss/ChordSymbol
  *asMidiOctave {
    |array|
    ^array.collect {
      |note|
      var n, noct, oct;
      noct = if(note.isInteger, { note }, { note.asNote});
      n = if(noct.size == 0, { noct }, { noct[0] });
      oct = if(noct.size == 0, { 5 }, { noct[1] });
      12*oct+n;
    };
  }

}


+ SequenceableCollection {

  midiRange {
    ^PlusSequenceableCollection.asMidiRange(this);
  }

  midiOctave {
    ^PlusSequenceableCollection.asMidiOctave(this);
  }

}


/*
Based on Steven Yi's Hex Beats.
http://kunstmusik.com/2017/10/20/hex-beats/
*/

+ SequenceableCollection {

  hexBeat {
    // ^this.collect{ |hex| hex.asString.asList.collect{ |h| h.digit.asBinaryDigits(4) }.flat }.flat;
    ^this.collect{ |hex| hex.asString.asList.reject{ |c| ["0123456789abcdef".asList].flat.indexOfEqual(c).isNil }.collect{ |hex| hex.asString.asList.collect{ |h| h.digit.asBinaryDigits(4) }.flat; }.flat; }.flat;

  }

}
