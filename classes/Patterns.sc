Pstruct : Pattern {
  var <>pattern, <>k= 3, <>n= 8, offset= 0, <>length= inf;
  *new { |pattern, k, n, offset= 0, length= inf|
    ^super.newCopyArgs(pattern, k, n, offset, length);
  }
  storeArgs {^[pattern, k, n, offset, length]}
  embedInStream { |inval|
    var pStr = pattern.asStream;
    var kStr = k.asStream;
    var nStr = n.asStream;
    var oStr = offset.asStream;
    var pVal, kVal, nVal, oVal;
    length.value(inval).do{
      var outval, b;
      kVal = kStr.next(inval);
      nVal = nStr.next(inval);
      oVal = oStr.next(inval);
      if(kVal.notNil and:{nVal.notNil}, {
        b = Pseq(Bjorklund(kVal, nVal), 1, oVal).asStream;
        while({outval = b.next; outval.notNil}, {
          inval = (if (outval == 0) { \r } { pStr.next(inval) }).yield;
        });
      }, {
        inval = nil.yield;
      });
    };
    ^inval;
  }
}

Pdilla : FilterPattern {
  /*
  Adapted from:
  -> Pattern Guide Cookbook 08: Swing
  */
  var <>swingAmount, <>swingBase, <>swingThreshold;
  *new { arg pattern, swingAmount=0, swingBase=0, swingThreshold=0;
    ^super.newCopyArgs(pattern, swingAmount, swingBase, swingThreshold);
  }
  storeArgs { ^[pattern, swingAmount, swingBase, swingThreshold] }

  embedInStream {  arg inevent;
    var cleanup = EventStreamCleanup.new;
    var now, adjust, thisShouldSwing, nextTime = 0, nextShouldSwing = false;
    var event, swAmount, swBase, swThreshold;
    var evtStream = pattern.asStream;
    var amtStream = swingAmount.asStream;
    var baseStream = swingBase.asStream;
    var thresholdStream = swingThreshold.asStream;

    loop {
      event = evtStream.next(inevent).asEvent;
      if (event.isNil) { ^cleanup.exit(inevent) };
      swAmount = amtStream.next(event);
      swBase = baseStream.next(event);
      swThreshold = thresholdStream.next(event);
      if (swAmount.isNil || swBase.isNil || swThreshold.isNil) {
        ^cleanup.exit(inevent)
      };

      // current time is what was "next" last time
      now = nextTime;
      nextTime = now + event.delta;
      thisShouldSwing = nextShouldSwing;
      // should next beat swing?
      nextShouldSwing =
      ((nextTime absdif: nextTime.round(swBase))
        <= (swThreshold ? 0)) and: {
        (nextTime / swBase).round.asInteger.odd
      };
      adjust = swBase * swAmount;
      // an odd number here means we're on an off-beat
      if(thisShouldSwing) {
        event[\timingOffset] = (event[\timingOffset] ? 0) + adjust;
        // if next note will not swing, this note needs to be shortened
        if(nextShouldSwing.not) {
          event[\dur] = event.use { ~dur.value } - adjust;
        };
      } {
        // if next note will swing, this note needs to be lengthened
        if(nextShouldSwing) {
          event[\dur] = event.use { ~dur.value } + adjust;
        };
      };
      cleanup.update(event);
      inevent = yield(event);
    }
  }
}

Pswing : Pdilla { }

Pcoin : Pattern {
  var	<>condition, <>doThis, <>doThat, <>default;
  *new { |condition, doThis, doThat, default|
    ^super.newCopyArgs(condition, doThis, doThat, default)
  }
  storeArgs { ^[condition, doThis, doThat,default] }
  asStream {
    var	trueStream = doThis.asStream,
    falseStream = doThat.asStream;

    ^FuncStream({ |inval|
      var test;
      if((test = {condition.asFloat.coin}.()).isNil) {
        nil
      } {
        if(test) {
          trueStream.next(inval) ? default
        } {
          falseStream.next(inval) ? default
        };
      };
    }, {		// reset func
      trueStream.reset;
      falseStream.reset;
    })
  }
}

Pnever : Pcoin {
  *new { |doThis, doThat, default|	^super.newCopyArgs(0.0, doThis, doThat, default) }
}
Prarely : Pcoin {
  *new { |doThis, doThat, default|	^super.newCopyArgs(0.25, doThis, doThat, default) }
}
Psometimes : Pcoin {
  *new { |doThis, doThat, default| ^super.newCopyArgs(0.5, doThis, doThat, default) }
}
Pregularly : Pcoin {
  *new { |doThis, doThat, default| ^super.newCopyArgs(0.75, doThis, doThat, default) }
}
Palways : Pcoin {
  *new { |doThis, doThat, default|	^super.newCopyArgs(1.0, doThis, doThat, default) }
}

PifRest : Pattern {
  var	<>key, <>iftrue, <>iffalse, <>default;
  *new { |key, iftrue, iffalse, default|
    ^super.newCopyArgs(key, iftrue, iffalse, default)
  }
  storeArgs { ^[key, iftrue, iffalse,default] }
  asStream {
    var	trueStream = iftrue.asStream,
    falseStream = iffalse.asStream;

    ^FuncStream({ |inval|
      var test;
      if((test = (inval.at(key) == \rest).next(inval)).isNil) {
        nil
      } {
        if(test) {
          trueStream.next(inval) ? default
        } {
          falseStream.next(inval) ? default
        };
      };
    }, {
      trueStream.reset;
      falseStream.reset;
    })
  }
}

PifEqual : Pattern {
  var	<>key, <>key2, <>iftrue, <>iffalse, <>default;
  *new { |key, key2, iftrue, iffalse, default|
    ^super.newCopyArgs(key, key2, iftrue, iffalse, default)
  }
  storeArgs { ^[key, key2, iftrue, iffalse,default] }
  asStream {
    var	trueStream = iftrue.asStream,
    falseStream = iffalse.asStream;

    ^FuncStream({ |inval|
      var test;
      if((test = (inval.at(key) == inval.at(key2)).next(inval)).isNil) {
        nil
      } {
        if(test) {
          trueStream.next(inval) ? default
        } {
          falseStream.next(inval) ? default
        };
      };
    }, {
      trueStream.reset;
      falseStream.reset;
    })
  }
}

Plsys : Pattern {
  var <>pattern, <>dict, <>levels;
  *new { |pattern, dict, levels=1|
    ^super.newCopyArgs(pattern, dict, levels);
  }
  storeArgs { ^[pattern, dict, levels] }
  embedInStream { |inval|
    var stream, outval;
    stream = Prewrite(pattern, dict, levels).asStream;
    loop {
      outval = stream.next(inval);
      if (outval.isNil) { ^inval };
      inval = outval.yield;
    }
  }
}

Pdur : Pattern {
  var <>proportion, <>pattern, <>default;
  *new { |proportion, pattern=1, default=1|
    ^super.newCopyArgs(proportion, pattern, default);
  }
  storeArgs { ^[proportion, pattern, default] }
  embedInStream { |inval|
    var stream, outval;
    stream = Prorate(proportion, pattern).asStream;
    loop {
      outval = stream.next(inval);
      if (outval.isNil) { outval = default; };
      inval = outval.clip(0.03125, 256).yield; // to avoid negative values
    }
  }
}

Psec : Pattern {
  var <>seconds, <>default;
  *new { |seconds, default=1|
    ^super.newCopyArgs(seconds, default);
  }
  storeArgs { ^[seconds, default] }
  embedInStream { |inval|
    var stream, outval, tempo;
    tempo = TempoClock.default.tempo;
    stream = seconds.asStream;
    loop {
      outval = stream.next(inval);
      if (outval.isNil) { outval = default; };
      inval = (outval.clip(0.1, 256) / tempo ).yield;
    }
  }
}

Psampler {
  *new { |... args|
    ^Pchain(
      Prout({ |evt|
        var clock = TempoClock.default;
        while { evt.notNil } {
          evt[\instrument] = "sampler_%".format(evt.buf.numChannels);
          evt[\fps] = evt.buf.numFrames/evt.beats;
          evt[\startPos] = (evt[\pos] ?? 0 ) * evt[\fps];
          evt[\dur] = evt[\dur] ?? clock.beatDur;
          evt = evt.yield;
        }
      }),
      Pbind(*args)
    );
  }
}


/*
These classes are the worst thing ever.
DO NOT use them if you have some self respect >:(
*/
Pbase {
  *new { |...pattern|
    ^Pchain(Pbind(*pattern), Pbind(
      \tempo, PatternProxy(Pfunc{ TempoClock.default.tempo }),
      \amp, PatternProxy(0.9),
      \dur, PatternProxy(1),
      \fast, PatternProxy(1), // ??
      \slow, PatternProxy(1), // ??
      \stut, PatternProxy(1),
      \stretch, PatternProxy(1) / (Pkey(\fast) / Pkey(\slow)),
      \seq, PatternProxy(0),
      \sequence, PatternProxy(0),
    ));
  }
}

Pmidi {
  *new { |...pattern|
    ^Pchain(Pbase(*pattern), Pbind(
      \type, \midi,
      \midiout, Pfunc{currentEnvironment.at(\midi) ?? nil},
      \midinote, PatternProxy(0),
      \chan, PatternProxy(9),
    ));
  }
}

Pcc {
  *new { |...pattern|
    ^Pchain(Pbase(*pattern), Pbind(
      \type, \control,
      \midiout, Pfunc{currentEnvironment.at(\cc) ?? nil},
      \control, PatternProxy(0),
      \ctlNum, PatternProxy(23),
      \chan, PatternProxy(9),
    ));
  }
}

Pdirt {
  *new { |...pattern|
    ^Pchain(Pbase(*pattern), Pbind(
      \type, \dirt,
      \orbit, 0,
      \gain, 0.9,
    ));
  }
}

Pdrum {
  *new { |loop ... pattern|
    ^Ppar(
      loop.collect{ |sequence|
        Pmidi(
          *[
            \pattern, Pseq(sequence, inf),
            \dir, Psometimes(1, PifRest(\pattern, 1, Pwrand([1, Pxrand([2, 4, -2, -4])], [0.9, 0.1], inf))),
            \stut, Pcoin(0.9, Pregularly(8,Psometimes(2,4)), PifRest(\pattern, 1, Psometimes(2, Psometimes(3,4)))),
            \sequence, Pscratch(Pkey(\pattern), PifRest(\pattern, 1, Pkey(\dir))),
            \midinote, Pstutter(Pkey(\stut), Pkey(\sequence)),
          ].addAll(pattern)
        )
      }, inf
    )
  }
}

Pdrumx {
  *new { |loop ... pattern|
    ^Ppar(
      loop.collect{ |sequence|
        Pmidi(
          *[
            \pattern, Pxrand(sequence, inf),
            \dir, Psometimes(1, PifRest(\pattern, 1, Pwrand([1, Pxrand([2, 4, -2, -4])], [0.9, 0.1], inf))),
            \stut, Pcoin(0.9, Pregularly(8,Psometimes(2,4)), PifRest(\pattern, 1, Psometimes(2, Psometimes(3,4)))),
            \sequence, Pscratch(Pkey(\pattern), PifRest(\pattern, 1, Pkey(\dir))),
            \midinote, Pstutter(Pkey(\stut), Pkey(\sequence)),
          ].addAll(pattern)
        )
      }, inf
    )
  }
}