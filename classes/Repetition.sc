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


        A simple Pbind:
        (
        var notes = "0 0+3 7".parseRepetitionPattern;
        ~x = notes.at(0).asPbind((tempo: 60/60, type: \md, chan: 2, amp: 0.75));
        )

        That is equivalent to:
        (
        var notes = "0 0+3 7".parseRepetitionPattern;
        ~x = Pbind(
          \tempo, 60/60,
          \type, \md,
          \amp, Pseq(notes.amp, inf) + 0.75,
          \dur, Pseq(notes.time, inf),
          \midinote, Pseq(notes.pattern.collect(_.asInt), inf) + 60,
          \sustain, Pkey(\dur),
          \chan, 2,
        );
        )

        Another a bit more complex Pbind example:
        (
        var pbd = (tempo: 60/60, type: \md, amp: 0.5);
        var drum = pbd.blend((chan: 9, cb: \asPerc));
        var test = "bd sn | ch*3 | 0@ 4 7 0 9 0".parseRepetitionPattern;
        ~q = test.at(0).asPbind(drum.blend((stut: 2)));
        ~w = test.at(1).asPbind(drum.blend((stretch: Pseq([1,1/4,1/2,2].stutter(4),inf))));
        ~e = test.at(2).asPbind(pbd.blend((chan: 4, octave: Pseq([3,4,5],inf), cb: \asInt, amp: 0.7)));
        )

        In which I defined a dict, `pbd`, which later is blended with `drum` another dict which defines a
        midi channel. Later on, `drum` is blended once again with different settings, once using `stut`
        that internally is translated to a `Pstutter` and the other uses `stretch` that modifies the value
        of `dur` (0.3, 0.075, 0.15, 0.6) repeated 4 times each.
        Also, a _bass_ midi-synth is defined with `octave` which cycles twice in the same pattern:
        0/3, 4/4, 7/5, 0/3, 9/4, 0/5.
        Finally, each dict has `cb`, which is basically a _callback_ over the current note being played.


        Callbacks

        It affects the current event (from pattern) and applies a certain function:

        * \asInt, converts to integer
        * \asPerc, converts to midi note. See PercSymbol for more info
        * \asChord, which takes an additional argument \chord

        Example:
        (
        var pbd = (tempo: 60/60, octave: 5, type: \md, amp: 0.5);
        var ccc = "c d e".parseRepetitionPattern;
        ~ccc = ccc.at(0).asPbind(pbd.blend((chan: 4, cb: \asChord, chord: Pseq([\min,\maj,\maj7],inf))));
        )

        Which will be rendered as:
        Cmin, DMaj, EMaj7

*/

Prepetition {
  *new {
    ^Prout({
      |evt|
      var idx = 0;
      var len = evt[\pattern].size;

      while { evt.notNil } {
        var current = evt[\pattern].at(idx).asSymbol;
        var isPerc = false;

        if (evt[\cb].notNil) {
          current = current.applyCallback(evt[\cb], evt);
          isPerc = evt[\cb].asSymbol == \asPerc;
        };

        if (evt[\octave].isNil) {
          evt[\octave] = 5;
        };

        if (evt[\stut].isNil) {
          evt[\stut] = 1;
        };

        evt[\dur] = evt[\time].at(idx);
        evt[\amp] = evt[\amp] + evt[\accent].at(idx);

        if ((evt[\type].notNil == \midi) || (evt[\type].asSymbol == \md)) {
          evt[\midinote] = current + (if(isPerc.asBoolean == false) { 12*evt[\octave] } { 0 });
        } {
          evt[\note] = current;
        };

        if (idx+1 < len) {
          idx = idx + 1;
        } {
          idx = 0;
        };

        evt = evt.yield;
      }
    }).stutter(Pkey(\stut));
  }
}


+ Symbol {

  // Requires `PercSymbol`;
  asPerc {
    ^this
    .collect {
      |item|
      if(item.isEmpty) {
        \rest;
      } {
        item;
      }
    }
    .collect {
      |item|
      if (item.isRest) {
        item;
      } {
        item.asGMPerc;
      }
    }
  }

  applyCallback {
    |cb evt|
    var sym = this;

    switch (cb.asSymbol,
      \asInt, {
        sym = sym.asInt;
      },
      // Requires `ChordProg`
      \asChord, {
        sym = [sym].asChord(evt[\chord] ?? \maj).flat;
      },
      // Requires `PercSymbol`
      \asPerc, {
        sym = [sym].asGMPerc;
      }
    );

    ^sym;
  }

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
      accent: acc.flat,
      time: time.flat,
      pattern: pattern.flat,
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

+ Dictionary {

  asPbind {
    |dict|
    dict.postln;
    ^Pchain(Prepetition(), Pbind(*this.blend(dict).getPairs));
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

}
