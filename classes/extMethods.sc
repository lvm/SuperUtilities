/*
External Methods

(c) 2020 by Mauro <mauro@sdf.org>
http://cyberpunk.com.ar/

*/


+ SequenceableCollection {

  uniq {
    var result = List.new;
    this.do{
      |item|
      if (result.indexOfEqual(item).isNil) {
        result.add( item );
      }
    };
    ^result.asArray;
  }

  // alias because String.shuffle
  shuffle { ^this.scramble; }
}

+ Float {
  midirange {
    ^(127 * this).round;
  }
}

+ Integer {
  rangemidi {
    ^(this/127).asStringPrec(2);
  }
}