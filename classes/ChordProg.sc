ChordProg {
  classvar chromatic, progression, circleof5th, maj, min, dim;
  classvar majorChords, minorChords;
  classvar cMaj, cmin;

  *new {  }

  *initClass {
    chromatic = [\c, \cs, \d, \ds, \e, \f, \fs, \g, \gs, \a, \as, \b];
    progression = (
      \eleven: [1,0,1,4],
      \elevenb: [1,4,1,0],
      \elevenc: [1,4,0],
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
      \bluesplus: [0,0,0,0,3,3,0,0,4,3,0,0,]
    );

    maj = [0,4,7];
    min = [0,3,7];
    dim = [0,3,6];

    cMaj = [0+maj, 2+min, 4+min, 5+maj, 7+maj, 9+min, 11+dim];
    majorChords = (\c: cMaj, \cs: cMaj+1, \d: cMaj+2, \ds: cMaj+3, \e: cMaj+4, \f: cMaj+5, \fs: cMaj+6, \g: cMaj+7, \gs: cMaj+8, \a: cMaj+9, \as: cMaj+10, \b: cMaj+11);

    cmin = [0+min, 2+dim, 4+maj, 5+min, 7+min, 9+maj, 11+maj];
    minorChords = (\c: cmin, \cs: cmin+1, \d: cmin+2, \ds: cmin+3, \e: cmin+4, \f: cmin+5, \fs: cmin+6, \g: cmin+7, \gs: cmin+8, \a: cmin+9, \as: cmin+10, \b: cmin+11);

    circleof5th = (
      \c: [\f, \g, \a],
      \d: [\g, \a, \b],
      \e: [\a, \b],
      \f: [\c, \d],
      \g: [\c, \d, \e],
      \a: [\d, \e],
      \b: [\e],
    );
  }

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

  *getMajorProg {
    |key, prog|
    ^Array.fill(progression[prog.asSymbol].size, {|i| majorChords[key.asSymbol][progression[prog.asSymbol][i]] });
  }

  *getMinorProg {
    |key, prog|
    ^Array.fill(progression[prog.asSymbol].size, {|i| minorChords[key.asSymbol][progression[prog.asSymbol][i]] });
  }

  *getCircle {
    |key|
    ^circleof5th[key.asSymbol];
  }

  *getProgList {
    ^progression.keys();
  }
}
