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

/*
Swing lifted from Pattern Guide Cookbook 08: Swing
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
            ev[\dur] = ev.use { ~dur.value } - adjust;
          };
        } {
          // if next note will swing, this note needs to be lengthened
          if(nextShouldSwing) {
            ev[\dur] = ev.use { ~dur.value } + adjust;
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


PCoin : Pattern {
  var	<>condition, <>iftrue, <>iffalse, <>default;
  *new { |condition, iftrue, iffalse, default|
    ^super.newCopyArgs(condition, iftrue, iffalse, default)
  }
  storeArgs { ^[condition, iftrue, iffalse,default] }
  asStream {
    var	trueStream = iftrue.asStream,
    falseStream = iffalse.asStream;

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

PNever : PCoin {
  *new { |iftrue, iffalse, default|	^super.newCopyArgs(0.0, iftrue, iffalse, default) }
}
PRarely : PCoin {
  *new { |iftrue, iffalse, default|	^super.newCopyArgs(0.25, iftrue, iffalse, default) }
}
PSometimes : PCoin {
  *new { |iftrue, iffalse, default| ^super.newCopyArgs(0.5, iftrue, iffalse, default) }
}
PRegularly : PCoin {
  *new { |iftrue, iffalse, default| ^super.newCopyArgs(0.75, iftrue, iffalse, default) }
}
PAlways : PCoin {
  *new { |iftrue, iffalse, default|	^super.newCopyArgs(1.0, iftrue, iffalse, default) }
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