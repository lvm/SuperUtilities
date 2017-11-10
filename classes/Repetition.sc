/*
        Repetition

        (c)opyleft 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        A class that returns (time + pattern) sequences, heavily inspired by TidalCycles.

        Example:
        Repetition("[bd sn rm] cp").timePattern;
        -> ( 'pattern': List[ bd, sn, rm, cp ], 'time': List[ 0.16666666666667, 0.16666666666667, 0.16666666666667, 0.5 ] );

        (
        var notes = Repetition("0 [0 3] 7").timePattern;
        ~fczr = Pbind(
          \tempo, 55/60,
          \type, \md,
          \amp, 0.666,
          \dur, Pseq(notes.time, inf),
          \repnote, Pseq(notes.pattern, inf) + 60,
          \midinote, Prout({ |e| loop { e = e[\repnote].asInt.yield } }) + 60,
          \sustain, Pkey(\dur),
          \chan, 2,
        );
        )
*/

Repetition {
  classvar <pattern;
  classvar <regexp;
  classvar <parsed;

  *new {
    |pat|
    pattern = pat;
  }

  *initClass {
    regexp = "([\\w!?(\\*\d+)? ]+|[\\[\\w!?(\\*\d+)? ]+\\]+)"
  }

  *uniq {
    |arr|
    var result = List.new;
    arr.do{
      |item|
      if (result.indexOfEqual(item).isNil) {
        result.add( item );
      }
    };
    ^result.asArray;
  }

  *maybeRepeatLast {
    |item idx|
    var bang;
    item = item.asString.asList.collect(_.asString);
    bang = item.indexOfEqual("!");

    if (bang.isNil.not) {
      item
      .collect {
        |chr, i|
        if (chr == "!") {
          item[i] = " " ++ item[i-1];
        }
      };
      parsed.put(idx, item.join.asSymbol);
    };

    if (item.indexOfEqual("*").isNil.not) {
      item = item.join.split($*);
      parsed.put(idx, item[0].dup(item[1].asInt).collect(_.asSymbol));
    }

  }

  *split {
    var pat = pattern.asString.findRegexp(regexp)
    .collect(_[1])
    .collect(_.stripWhiteSpace)
    .asList
    ;

    parsed = this
    .uniq(pat)
    .collect {
      |x|
      if (x.asList.collect(_.asString).indexOfEqual("[").isNil) {
        x.split($ ).collect(_.asSymbol);
      } {
        x.replace("[", "").replace("]", "").asSymbol;
      }
    }
    .flat
    ;

    parsed.collect {
      |x, i|
      this.maybeRepeatLast(x, i);
    };

    ^parsed.flat;
  }

  *parse {
    var p = this.split;
    var pat, time;

    ^p.collect {
      |x|
      x = x.asString.split($ );
      (time: ((1 / p.size) / x.size).dup(x.size), pattern: x)
    }
  }

  *timePattern {
    var time = List.new, patas = List.new;
    this.parse.collect {
      |x|
      x.time.collect { |t| time.add (t); } ;
      x.pattern.collect { |p| patas.add (p) };
    };

    ^(time: time, pattern: patas);
  }

}
