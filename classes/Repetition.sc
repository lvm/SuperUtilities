/*
        Repetition

        (c)opyleft 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        *Heavily* inspired by TidalCycles. Consider this a (tiny) dialect that implements some of its features.

        So far, i've implemented only these possibilities:
        * Polyrhythms: "a | b"
        * Groups: "a+b"
        * Accents: "a@"
        * Repetition: "a!"
        * Multiplication: "a*N" (N -> Int)

        All of this is "chainable".

        Examples:

        A fairly complex pattern:
        "bd*3 | hq@+sn rm@! cp@".parseRepetitionPattern;

        Is converted to:
        -> [
            ( 'pattern': [ bd, bd, bd ],
              'accent': [ 0, 0, 0 ],
              'time': [ 0.33333333333333, 0.33333333333333, 0.33333333333333 ]
            ),
            ( 'pattern': [ hq, sn, rm, rm, cp ],
              'accent': [ 0.25, 0, 0.25, 0.25, 0.25 ],
              'time': [ 0.125, 0.125, 0.25, 0.25, 0.25 ]
            )
           ]

        A polymeter:
        x = "5@ x*4 | 4@ x*3".parseRepetitionPattern
        -> [
            ( 'pattern': [ 5, x, x, x, x ],
              'accent': [ 0.25, 0, 0, 0, 0 ],
              'time': [ 0.2, 0.2, 0.2, 0.2, 0.2 ]
            ),
            ( 'pattern': [ 4, x, x, x ],
              'accent': [ 0.25, 0, 0, 0 ],
              'time': [ 0.25, 0.25, 0.25, 0.25 ]
            )
           ]

        To "see" what's going on, it's possible to:
        x[0].pattern.dup(4).flat;
        x[1].pattern.dup(5).flat;
        -> [ 5, x, x, x, x, 5, x, x, x, x, 5, x, x, x, x, 5, x, x, x, x ]
        -> [ 4, x, x, x, 4, x, x, x, 4, x, x, x, 4, x, x, x, 4, x, x, x ]

        A much more simple Pbind:
        (
        var notes = "0 0+3 7".parseRepetitionPattern.pop;
        ~test = Pbind(
          \tempo, 60/60,
          \type, \md,
          \amp, Pseq(notes.amp, inf) + 0.75,
          \dur, Pseq(notes.time, inf),
          \midinote, Pseq(notes.pattern.collect(_.asInt), inf) + 60,
          \sustain, Pkey(\dur),
          \chan, 2,
        );
        )

        That is equivalent to (it always returns a list, hence .pop):
        -> [
            ( 'pattern': [ 0, 0, 3, 7 ],
              'accent': [ 0, 0, 0, 0 ],
              'time': [ 0.33333333333333, 0.16666666666667, 0.16666666666667, 0.33333333333333 ]
            )
           ]

*/

Repetition { }

+ Symbol {

  maybeRepeat {
    var item = this.asString;

    if (item.contains("!")) {
      item = item.split($ ).collect{
        |i|
        if (i.contains("!")) {
          i.replace("!", "").dup.join(" ")
        } {
          i
        }
      }.join(" ")
    };

    if (item.contains("*")) {
      item = item.split($ ).collect{
        |i|
        if (i.contains("*")) {
          i = i.split($*);
          i[0].dup(i[1].asInt).join(" ");
        } {
          i
        }
      }.join(" ")
    };

    ^item.asSymbol;
  }


  maybeSplit {
    |sep|
    var item = this;

    if (item.isKindOf(String)) {
      item = item.split(sep);
    };

    ^item;
  }

  distributeInTime {
    var acc, size, dur, time;
    var pattern = this.asString;

    pattern = pattern.split($ ).reject { |x| x.asString.size < 1 };
    size = pattern.size;
    dur = 1/size;
    pattern = pattern.collect(_.maybeSubdivide);
    time = pattern.collect{ |sub| if (sub.isKindOf(String)) { dur; } { (dur/sub.size).dup(sub.size); } };
    acc = pattern.collect{ |sub| if (sub.isKindOf(String)) { sub.maybeAccent; } { sub.collect(_.maybeAccent) } };
    pattern = pattern.collect{ |sub| if (sub.isKindOf(String)) { sub.maybeCleanUp; } { sub.collect(_.maybeCleanUp) } };

    ^(
      accent: acc.flat.asStream,
      time: time.flat.asStream,
      pattern: pattern.flat.asStream,
    )
  }

}

+ String {

  maybeCleanUp {
    ^this.replace("@", "").asSymbol;
  }

  maybeAccent {
    ^if (this.contains("@")) { 0.2 } { 0 }
  }

  maybeSubdivide {
    var item = this;

    if (item.contains("+")) {
      item = item.split($+);
    };

    ^item;
  }

  parseRepetitionPattern {
    var regexp = "([\\w!?@?\\+?(\\*\d+)? ]+)";

    ^this
    .asString
    .findRegexp(regexp)
    .collect(_[1])
    .collect(_.stripWhiteSpace)
    .uniq
    .collect(_.flat)
    .collect(_.asSymbol)
    .collect(_.maybeRepeat)
    .collect(_.maybeSplit)
    .collect(_.distributeInTime)
    ;
  }

}

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

  parseRepetitionCollection {
    ^this.collect(_.parseRepetitionPattern);
  }

}
