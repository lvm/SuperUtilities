
/*
  Swing lifted from Pattern Guide Cookbook 08: Swing
  https://github.com/triss/LiveCollider/blob/dev/patterns/classes/Pswing.sc
*/
Pswing {
    *new { |pattern amt=0.25 base=0.25 threshold=0|
        var swinger = Prout({ |ev|
            var now, nextTime = 0, thisShouldSwing, nextShouldSwing = false, adjust;
            while { ev.notNil } {
                // current time is what was "next" last time
                now = nextTime;

                nextTime = now + ev.delta;

                thisShouldSwing = nextShouldSwing;

                // should next beat swing?
                nextShouldSwing =
                    ((nextTime absdif: nextTime.round(ev[\swingBase]))
                    <= (ev[\swingThreshold] ? 0)) and: {
                        (nextTime / ev[\swingBase]).round.asInteger.odd
                    };

                adjust = ev[\swingBase] * ev[\swingAmount];

                // an odd number here means we're on an off-beat
                if(thisShouldSwing) {
                    ev[\timingOffset] = (ev[\timingOffset] ? 0) + adjust;
                    // if next note will not swing, this note needs to be shortened
                    if(nextShouldSwing.not) {
                        ev[\sustain] = ev.use { ~sustain.value } - adjust;
                    };
                } {
                    // if next note will swing, this note needs to be lengthened
                    if(nextShouldSwing) {
                        ev[\sustain] = ev.use { ~sustain.value } + adjust;
                    };
                };
                ev = ev.yield;
            };
        });

        ^Pchain(swinger, pattern, (
            swingAmount: amt, swingBase: base, swingThreshold: threshold
        ))
    }
}


/*
"Merge" a Pbind with a Prewrite
*/

Pbindenmayer {
  *new {
    |lsystem basepattern|
    var lsys = lsystem.asStream;
    var lindenmayer = Prout({
      |evt|
       while { evt.notNil } {
        var current = lsys.next;
        var isPerc = false;

        if (evt[\isPerc].isNil.not && evt[\isPerc].asBoolean) {
          isPerc = true;
          current = [current].asGMPerc;
        };

        if (evt[\octave].isNil.not) {
          evt[\octave] = 5;
        };

        if (evt[\stut].isNil) {
          evt[\stut] = 1;
        };

        if ((evt[\type].asSymbol == \midi) || (evt[\type].asSymbol == \md)) {
          evt[\midinote] = current + (if(isPerc.asBoolean == false) { 12*evt[\octave] } { 0 });
        } {
          evt[\note] = current;
        };

        evt = evt.yield;
      }
    }).stutter(Pkey(\stut));

    ^Pchain(lindenmayer, basepattern);
  }
}
