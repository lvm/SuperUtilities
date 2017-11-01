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
