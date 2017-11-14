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
        var notes = "0 0+3 7".parseRepetitionPattern.pop;
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


        Another example using `ChordProg`. Patterns can be built from arrays aswell.
        (
        var chord = (
          \c: \min,
          \gs: \maj,
          \a: \min,
        );
        var p = [\c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a].collect{
          |n|
          ChordProg.getChord(n, chord[n])
        }.flat.join(" ");
        var pbd = (tempo: 60/60, cb: \asInt, chan: 4, stretch: 26, type: \md, amp: 0.8);
        var polc = p.parseRepetitionPattern;
        ~poly = polc.at(0).asPbind(pbd);
        )


        Other features:
        Callbacks

        It affects the current event (from pattern) and applies a certain function:

        * \asInt, converts to integer
        * \asPerc, converts to midi note. See PercSymbol for more info
        * \asChord, which takes an additional argument \chord
        * \asSynth, which should be used with `\type, \dirt`, otherwise the value will be passed as `\note`
        * \asFn, which takes an additiona arg \fn (which should be a function)

        Examples:
        (
        var pbd = (tempo: 60/60, octave: 5, type: \md, amp: 0.5);
        var ccc = "c d e".parseRepetitionPattern;
        ~ccc = ccc.at(0).asPbind(pbd.blend((chan: 4, cb: \asChord, chord: Pseq([\min,\maj,\maj7],inf))));
        )

        Which will be rendered as:
        Cmin, DMaj, EMaj7

        (
        var pbd = (tempo: 60/60, octave: 5, type: \md, amp: 0.5);
        var fun = "bd".parseRepetitionPattern;
        ~fun = fun.at(0).asPbind(pbd.blend((chan: 9, cb: \asFn, fn: {|x| [x].asGMPerc + (12..24).choose }, octave: 0)));
        )

        Which will add a number between 12 and 24 to the current midinote". Also notice `octave: 0`. That is because in this particular case, since it's a Event type MIDI,
        it'll add automatically `octave: 5`, so 36 (bd) instead of ending between 48 and 60, it would end between 108 and 120 (`octave: 5` equals to current-note + 12*Pkey(\octave)).


        Bjorklund / Euclidean Rhythm:

        Repetition isn't flexible as Tidal itself but taking advantage of the `Bjorklund` Quark, we are able
        to generate Strings that represent the same rhythm. The valid args are `k` which represents the amount of
        notes distributed in `n` places, and `rotate` which will shift positions.
        For example:
        "bd".asBjorklund(4, 16).quote;
        -> "bd r r r bd r r r bd r r r bd r r r"

        "bd".asBjorklund(4, 16, 2).quote;
        -> "r r bd r r r bd r r r bd r r r bd r"

        Additionally, it's possible to pass more than one 'symbol' as a Bjorklund pattern, such as "sn rm", which
        is converted to a group, "sn+rm", therefore creating a _longer_ pattern but maintaining the same duration.
        For example:
        "bd sn".asBjorklund(1,4).parseRepetitionPattern
        -> "bd+sn r r r"
        -> [
            ( 'pattern': [ bd, sn, r, r, r ],
              'time': [ 0.125, 0.125, 0.25, 0.25, 0.25 ],
              'accent': [ 0, 0, 0, 0, 0 ]
            )
           ]

        So, instead of dividing each value by the total amount (1/5), it's divided by 1/4 and the group by the amount
        of items in it (1/2). This can be seen clearly in the 'time' Array.

        An example chaining Bjorklund/Euclidean rhythms:
        (
        var pbd = (tempo: 60/60, type: \md, amp: 0.75, chan: 9, cb: \asPerc);
        var pat = ("bd".asBjorklund(3,8))+"| r r r sn r r | r ch ch@ ch |"+("rm".asBjorklund(5,8))+"|cp";
        var t8 = pat.parseRepetitionPattern;
        ~t80 = t8.at(0).asPbind(pbd);
        ~t81 = t8.at(1).asPbind(pbd);
        ~t82 = t8.at(2).asPbind(pbd);
        ~t83 = t8.at(3).asPbind(pbd);
        ~t84 = t8.at(4).asPbind(pbd);
        )

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
        var isSynth = false;

        if (evt[\cb].notNil) {
          current = current.applyCallback(evt[\cb], evt);
          isPerc = evt[\cb].asSymbol == \asPerc;
          isSynth = evt[\cb].asSymbol == \asSynth;
        };

        if (evt[\octave].isNil) {
          evt[\octave] = 5;
        };

        if (evt[\stut].isNil) {
          evt[\stut] = 1;
        };

        evt[\dur] = evt[\time].at(idx);
        evt[\amp] = evt[\amp] + evt[\accent].at(idx);

        if ((evt[\type].asSymbol == \midi) || (evt[\type].asSymbol == \md)) {
          evt[\midinote] = current + (if(isPerc.asBoolean == false) { 12*evt[\octave] } { 0 });
        } {
          if (evt[\type].asSymbol == \dirt) {
            if (isSynth.asBoolean) {
              evt[\s] = current;
            } {
              evt[\n] = current;
            }
          } {
            evt[\note] = current;
          }
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
        if (sym.asSymbol.isRest) {
          sym = \rest;
        } {
          sym = [sym].asChord(evt[\chord] ?? \maj).flat;
        }
      },
      // Requires `PercSymbol`
      \asPerc, {
        sym = [sym].asGMPerc;
      },
      \asFn, {
        if (evt[\fn].notNil) {
          sym = evt[\fn].value(sym);
        }
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

  // requires `Bjorklund` Quark.
  asBjorklund {
    |k, n, rotate=0|
    ^Bjorklund(k, n)
    .rotate(rotate)
    .collect { |p| if (p.asBoolean) { this.replace(" ", "+").asSymbol } { \r } }
    .flat.join(" ")
    ;
  }

  maybeCleanUp {
    ^this.replace("@", "").asSymbol;
  }

  maybeAccent {
    ^if (this.contains("@")) { 0.25 } { 0 }
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
