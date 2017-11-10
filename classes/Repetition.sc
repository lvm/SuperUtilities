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

    if (pat.isKindOf(String)) {
      pat = [pat];
    };

    pattern = pat;
  }

  *initClass {
    regexp = "([\\w!?@?(\\*\d+)? ]+|[\\[\\w!?@?(\\*\d+)? ]+\\]+)"
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
    var curr = item.asArray; // in case item doesn't have *N or !
    item = item.asString;

    if (item.contains("!")) {
      curr = item.split($ ).collect{
        |i|
        if (i.contains("!")) {
          i.replace("!", "").dup.join(" ")
        } {
          i
        }
      }
    };

    if (item.contains("*")) {
      curr = item.split($ ).collect{
        |i|
        if (i.contains("*")) {
          i = i.split($*);
          i[0].dup(i[1].asInt).join(" ");
        } {
          i
        }
      }
    };
    ^curr.join(" ").asSymbol;
  }

  *split {
    |pat|
    pat = pat.asString.findRegexp(regexp)
    .collect(_[1])
    .collect(_.stripWhiteSpace)
    .asList
    ;

    parsed = this
    .uniq(pat)
    .collect {
      |x|
      if (x.includesAny("[]").not) {
        x.split($ ).collect(_.asSymbol);
      } {
        x.replace("[", "").replace("]", "").asSymbol;
      }
    }
    .flat
    .collect {
      |x, i|
      this.maybeRepeatLast(x, i);
    }
    ;

    ^parsed.flat;
  }

  *parse {
    ^pattern.collect {
      |pat|
      this.split(pat).collect {
        |x|
        var amp, size;
        x = x.asString.split($ );
        size = x.size;
        amp = x.collect { |y| if (y.contains("@")) { 0.5 } { 0 } };
        (amp: (amp + 0.5), time: ((1/size)).dup(size), pattern: x.collect { |y| y.replace("@", ""); })
      }
    }
  }

  *timePattern {
    var amp, time, patas;
    ^this.parse.collect{
      |pat|
      amp = List.new;
      time = List.new;
      patas = List.new;
      pat.collect {
        |x|
        x.amp.collect { |a| amp.add (a); } ;
        x.time.collect { |t| time.add (t); } ;
        x.pattern.collect { |p| patas.add (p) };
      };

      (amp: amp.asArray, time: time.asArray.normalizeSum, pattern: patas.asArray);
    }
  }

}
