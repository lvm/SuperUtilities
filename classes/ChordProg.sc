ChordProg {
  classvar chromatic, progression, circleof5th, chords, functions;
  classvar majorChords, minorChords;
  classvar cMaj, cmin;

  *new {  }

  *initClass {
    chromatic = [\c, \cs, \d, \ds, \e, \f, \fs, \g, \gs, \a, \as, \b];

    progression = (
      \eleven: [1,0,1,4],
      \elevenb: [1,4,1,0],
      \elevenc: [1,4,0],
      \elevend: [5,1,4,0],
      \sad: [0,3,4,4],
      \ballad: [0,0,3,5],
      \balladb: [0,3,5,4],
      \rockplus: [0,3,0,4],
      \rebel: [3,4,3],
      \nrg: [0,2,3,5],
      \creepy: [0,5,3,4],
      \creepyb: [0,5,1,4],
      \rock: [0,3,4],
      \gral: [0,3,4,0],
      \gralb: [0,3,1,4],
      \blues: [0,3,0,4,0],
      \pop: [0,4,5,3],
      \roll: [0,3,4,3],
      \unresolved: [3,0,3,4],
      \bluesplus: [0,0,0,0,3,3,0,0,4,3,0,0,],
    );

    circleof5th = (
      \c: [\f, \g, \a],
      \d: [\g, \a, \b],
      \e: [\a, \b],
      \f: [\c, \d],
      \g: [\c, \d, \e],
      \a: [\d, \e],
      \b: [\e],
    );

    chords = (
      \maj: [0, 4, 7],
      \min: [0, 3, 7],
      \dim: [0, 3, 6],
      \dim7: [0, 3, 6, 9],
      \five: [0, 7],
      \dom7: [0, 4, 7, 10],
      \maj7: [0, 4, 7, 11],
      \m7: [0, 3, 7, 10],
      \mMaj7: [0, 3, 7, 11],
      \sus4: [0, 5, 7],
      \sus2: [0, 2, 7],
      \six: [0, 4, 7, 9],
      \m6: [0, 3, 7, 9],
      \nine: [0, 4, 7, 10, 14],
      \m9: [0, 3, 7, 10, 14],
      \maj9: [0, 4, 7, 11, 14],
      \mMaj9: [0, 3, 7, 11, 14],
      \eleven: [0, 4, 7, 10, 14, 17],
      \m11: [0, 3, 7, 10, 14, 17],
      \maj11: [0, 4, 7, 11, 14, 17],
      \mMaj11: [0, 3, 7, 11, 14, 17],
      \thirteen: [0, 4, 7, 10, 14, 21],
      \m13: [0, 3, 7, 10, 14, 21],
      \maj13: [0, 4, 7, 11, 14, 21],
      \mMaj13: [0, 3, 7, 11, 14, 21],
      \add9: [0, 4, 7, 14],
      \madd9: [0, 3, 7, 14],
      \sixadd9: [0, 4, 7, 9, 14],
      \m6add9: [0, 3, 7, 9, 14],
      \add11: [0, 4, 7, 10, 17],
      \majAdd11: [0, 4, 7, 11, 17],
      \mAdd11: [0, 3, 7, 10, 17],
      \mMajAdd11: [0, 3, 7, 11, 17],
      \add13: [0, 4, 7, 10, 21],
      \majAdd13: [0, 4, 7, 11, 21],
      \mAdd13: [0, 3, 7, 10, 21],
      \mMajAdd13: [0, 3, 7, 11, 21],
      \sevenFlat5: [0, 4, 6, 10],
      \sevenSharp5: [0, 4, 8, 10],
      \sevenFlat9: [0, 4, 7, 10, 13],
      \sevenSharp9: [0, 4, 7, 10, 15],
      \sevenSharp5Flat9: [0, 4, 8, 10, 13],
      \m7Flat5: [0, 3, 6, 10],
      \m7dim: [0, 3, 6, 9],
      \m7Sharp5: [0, 3, 8, 10],
      \m7Flat9: [0, 3, 7, 10, 13],
      \nineSharp11: [0, 4, 7, 10, 14, 18],
      \nineFlat13: [0, 4, 7, 10, 14, 20],
      \sixSus4: [0, 5, 7, 9],
      \sevenSus4: [0, 5, 7, 10],
      \maj7Sus4: [0, 5, 7, 11],
      \nineSus4: [0, 5, 7, 10, 14],
      \maj9Sus4: [0, 5, 7, 11, 14]
    );

    functions = (
      \tonic: [0,2,4,5,6], // 1,3,5,6,7
      \subdominant: [0,1,2,3,5], // 1,2,3,4,6
      \dominant: [1,3,4,5,6], // 2,4,5,6,7
    );

    // Scale.major
    cMaj = [0+chords[\maj], 2+chords[\min], 4+chords[\min], 5+chords[\maj], 7+chords[\maj], 9+chords[\min], 11+chords[\dim]];
    majorChords = (\c: cMaj, \cs: cMaj+1, \d: cMaj+2, \ds: cMaj+3, \e: cMaj+4, \f: cMaj+5, \fs: cMaj+6, \g: cMaj+7, \gs: cMaj+8, \a: cMaj+9, \as: cMaj+10, \b: cMaj+11);

    // Scale.minor
    cmin = [0+chords[\min], 2+chords[\dim], 3+chords[\maj], 5+chords[\min], 7+chords[\min], 8+chords[\maj], 10+chords[\maj]];
    minorChords = (\c: cmin, \cs: cmin+1, \d: cmin+2, \ds: cmin+3, \e: cmin+4, \f: cmin+5, \fs: cmin+6, \g: cmin+7, \gs: cmin+8, \a: cmin+9, \as: cmin+10, \b: cmin+11);


  }

  /***
  *buildScale {
    |key, scale, formula|
    var i, x;
    i = chromatic.indexOfEqual(key);
    x = 0;

    {scale.size < 6}.while ({

      if(i + formula[x] < 12,
        {
          i = i + formula[x];
          x = x + 1;
        },
        {
          i = 12 - (i + formula[x]);
          x = x + 1;
        }
      );
      scale = scale.add(chromatic[i]);
    });

    ^scale;
  }

  *pickChords {
    |key, scale, n_chords|
    var chords = [];
    var k = 0;

    ^Array.fill(n_chords, { scale.choose });
  }

  *randomMajorChords {
    |key, n_chords|
    var scale = [key.asSymbol];
    var formula = [2,2,1,2,2,2,1];
    ^this.pickChords(key, this.buildScale(key, scale, formula), n_chords);
  }

  *randomMinorChords {
    |key, n_chords|
    var scale = [key.asSymbol];
    var formula = [2,1,2,2,1,2,2];
    ^this.pickChords(key, this.buildScale(key, scale, formula), n_chords);
  }
  ***/

  *getMajorProg {
    |key, prog|
    ^Array.fill(progression[prog.asSymbol].size, {|i| majorChords[key.asSymbol][progression[prog.asSymbol][i]] });
  }

  *getMinorProg {
    |key, prog|
    ^Array.fill(progression[prog.asSymbol].size, {|i| minorChords[key.asSymbol][progression[prog.asSymbol][i]] });
  }

  *getHarmonicFunc {
    |key, scale, func, deg|
    var scaleChords = majorChords;

    if (scale == \minor) {
      scaleChords = minorChords;
    };

    if ( functions[func].isNil ) {
      Error("Wrong function!").throw;
    };

    if ( functions[func].indexOfEqual(deg).isNil ) {
      "Wrong degree! return Rest instead".postln;
      ^\r;
    }{
      ^scaleChords[key.asSymbol][ functions[func].indexOfEqual(deg) ];
    };
  }

  *getCircle {
    |key, as_notes=nil|
    /*
    if as_notes, return position relative to root key
    -> \f = \c, \d
    -> \f = 12, 14
    -> \f = 5th, 6th
    */
    var notes = circleof5th[key.asSymbol];
    ^if(as_notes.isNil.not, {
      notes
    }, {
      Array.fill(notes.size, {
        |i|
        var root = chromatic.indexOfEqual(key);
        var rel = chromatic.indexOfEqual(notes[i]);
        var n = 0;
        if(rel < root, {
          // n = (root - rel) * -1;
          n = root + (12 - root + rel);
        }, {
          // n = rel - root;
          n = rel; // - root;
        });
        n;
      });
    });
  }

  *getChord {
    |key, chord, as_notes=nil|
    var notes = (chromatic.indexOfEqual(key.asSymbol)+chords[chord.asSymbol]);
    ^if (as_notes.isNil.not) {
     notes.collect{|c|
        var n = if ( c > 11 ) {
          c - 12;
        } {
          c
        };
        chromatic[n];
      };
    } {
      notes;
    }
  }

  *getInversion {
    |key, chord, as_notes=nil|

    var basechord = this.getChord(key, chord, true);
    var st = chromatic.indexOfEqual(key);
    var inv = Array.fill(basechord.size, { |x| basechord.rotate(basechord.size-x); });
    ^if (as_notes.isNil) {
      Array.fill(inv.size, {
        |x|
        var bass_note = basechord[x];
        var curr_chord = inv[x];
        var curr_chrom = chromatic.rotate(-1*chromatic.indexOfEqual(bass_note));
        var curr_st = chromatic.indexOfEqual( bass_note );
        var octave_plus = if (st > curr_st) { 12 } { 0 }; // weird
        /*
        bass_note -> indicates the "lowest" note in the chord (aka the "new root")
        curr_chord -> is the current chord, only really relevant from the 2nd towards the end
        curr_chrom -> chromatic scale starting from the bass_note (rotates/shifts positions)
        curr_st -> semitone index in the original chromatic scale (starting with C)
        octave_plus -> adds 12 st or 0 st if the curr_st is lower than the original \key argument st
        */

        Array.fill(curr_chord.size, {
          |y|
          // st index of the bass_note + st index of the current chrome note + 12 or 0;
          chromatic.indexOfEqual(bass_note) + curr_chrom.indexOfEqual( curr_chord[y] ) + octave_plus;
        });
      });
    } {
      // just return the notes in ABC notation
      inv;
    };
}

  *getProgList {
    ^progression.keys();
  }
}


+ SequenceableCollection {

  asChord {
    |chord|
    ^this.collect{ |note| ChordProg.getChord(note, chord); };
  }

  asProgression {
    |scale=\maj prog|
    ^this.collect{
      |note|
      if (scale.asSymbol == \maj ) {
        ChordProg.getMajorProg(note, prog);
      } {
        ChordProg.getMinProg(note, prog);
      };
    };
  }

  asChordInversion {
    |chord|
    ^this.collect{ |note| ChordProg.getInversion(note, chord); };
  }

  asHarmonicFunc {
    |scale func deg|
    ^this.collect{ |note| ChordProg.getHarmonicFunc(note, scale, func, deg); };
  }

  asCircleOf5 {
    ^this.collect{ |note| ChordProg.getCircle(note); };
  }

}