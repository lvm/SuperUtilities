# SuperUtilities

SuperCollider utilities to ease the job of the everyday coder.

## Install

Simply clone this repository in the `Extension` directory.
The path can be found by evaluating `Platform.userExtensionDir` or `Platform.systemExtensionDir`.


## FluidSynth.sc

Has its own quark now: https://github.com/lvm/FluidSynth.sc

```
Quarks.install("https://github.com/lvm/FluidSynth.sc");
```

## ChordProg.sc

A simple Chord Progression class.

### Usage

```
ChordProg.getCircle(\c);

ChordProg.getChord(\c, \maj);

ChordProg.getInversion(\c, \maj);

ChordProg.getMajorProg(\c, \pop);

ChordProg.getMinorProg(\c, \blues);

(
// 1, 3, 5, 6, 7
[0,2,4,5,6].do {
  |x|
  ChordProg.getHarmonicFunc(\c, \major, \tonic, x).postln;
}
)

// classic track
(
var chord = (
  \c: \min,
  \gs: \maj,
  \a: \min,
);
[\c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \gs, \c, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a, \gs, \a].collect{
  |n|
  ChordProg.getChord(n, chord[n])
}.flat;
)

```

## Repetition.sc

*Heavily* inspired by TidalCycles. Consider this a (tiny) dialect that implements some of its features.

So far, i've implemented only these possibilities:

* Polyrhythms: `a | b`
* Groups: `a+b`
* Accents: `a@`
* Repetition: `a!`
* Multiplication: `a*N` (`N` -> Int)

All of this is "chainable".

### Usage

A fairly complex pattern:
```
        "bd*3 | hq@+sn rm@! cp@".parseRepetitionPattern;
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
```

A polymeter:
```
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
```

To "see" what's going on, it's possible to:
```
        x[0].pattern.dup(4).flat;
        x[1].pattern.dup(5).flat;
        -> [ 5, x, x, x, x, 5, x, x, x, x, 5, x, x, x, x, 5, x, x, x, x ]
        -> [ 4, x, x, x, 4, x, x, x, 4, x, x, x, 4, x, x, x, 4, x, x, x ]
```

A much more simple Pbind:
```
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
```

That is equivalent to this:
Note: it always returns a list, hence `.pop` :-)
```
        -> [
            ( 'pattern': [ 0, 0, 3, 7 ],
              'accent': [ 0, 0, 0, 0 ],
              'time': [ 0.33333333333333, 0.16666666666667, 0.16666666666667, 0.33333333333333 ]
            )
           ]
```


## Aconnect.sc

A simple `aconnect` front end. Useful when you need to connect _other_ MIDI clients.

### Usage

```
a = Aconnect.new;

a.in.postln;
a.out.postln;

i = a.inByName("midi through");
o = a.outByName("amsynth");

a.connect(i.port, o.port);
a.disconnect(i.port, o.port);
```


## Tiny.sc

A class for the lazy-coder.

### Usage

```
var sni = (
  \gg: "GG WP",
  \hf: "HF GL",
);
```

```
TinySnippets.enable("y", sni);
-> TinySnippets enabled with hotkey Ctrl+t
```

```
TinySnippets.disable;
-> TinySnippets disabled
```

## MidiEvents.sc
Events types for MIDIOut Patterns.

### Usage

```
~midiOut = MIDIOut.newByName("...", "...");
MidiEvents(~midiOut);

(\type, \md, \midinote, Pseq((60..72),inf).play;
(\type, \cc, \control, Pseq((0..127),inf).play;
```

## Tidal.sc

Another TidalCycles interface.

### Usage

```
Tidal.start;

Tidal.send(":module Sound.Tidal.Context");
Tidal.send("(cps, getNow) <- bpsUtils");
Tidal.send("(d1,t1) <- superDirtSetters getNow");
Tidal.send(":set prompt ".format("tidal> ".quote));

Tidal.send("d1 $ sound % # release 0.25".format("kick".quote));
Tidal.send("d1 silence");

Tidal.stop;
```

## LICENSE

See [LICENSE](LICENSE)
