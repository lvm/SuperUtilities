# SuperUtilities

SuperCollider utilities to ease the job of the everyday coder.

## Install

Clone this repository in the `Extension` directory.
The path can be found by evaluating `Platform.userExtensionDir` or `Platform.systemExtensionDir`.


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

## JackConnect.sc

A very simple `jack_lsp`, `jack_connect` and `jack_disconnect` interface for SuperCollider.

### Usage

```
j = JackConnect.new;
j.listClients(_.postln);
j.connectClients("PulseAudio JACK Sink:front-left", "system:playback_1");
j.connectClients("PulseAudio JACK Sink:front-right", "system:playback_2");
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

## DrumLoop.sc

Similar to Scales / Tuning classes but for Drum Loops.

### Usage

```
// Create a new "DrumLoop"
DrumLoop.all.put(\kick, DrumLoop((bd: [1,0,0,0]), tempo: 160/60, name: "Kick"))
-> IdentityDictionary[ (kick -> DrumLoop.kick) ]

DrumLoop.kick;
-> DrumLoop(( 'bd': [ 1, 0, 0, 0 ] ), 2.6666666666667, "Kick")


DrumLoop.jungle.flop;
-> [ [ bd, r, ch, r ], [ r, r, ch, r ], [ r, r, ch, sn ], [ r, oh, ch, r ], [ r, r, ch, r ], [ bd, r, ch, r ], [ r, oh, ch, sn ], [ r, r, ch, r ], [ bd, r, ch, r ], [ r, r, ch, sn ], [ r, r, ch, r ], [ bd, oh, ch, r ], [ r, r, ch, r ], [ r, r, ch, sn ], [ r, oh, ch, r ], [ r, r, ch, sn ] ]


DrumLoop.names;
-> [ afro1a, afro1b, afro1c, afro2a, afro2b, afro2c, afro3a, afro3b, afro3c, afro4a, afro4b, afro4c, afro5a, afro5b, afro5c, afro6a, afro6b, afro6c, amen, amenbrother, ashleysroachclip, ballad1a, ballad1b, ballad1c, ballad2a, ballad2b, ballad2c, ballad3a, ballad3b, ballad3c, ballad4a, ballad4b, ballad4c, ballad5a, ballad5b, ballad5c, bigbeat, billyjean, blues1a, blues2a, bookofmoses, bossa1a, bossa1b, bossa1c, bossa2a, bossa2b, bossa2c, bossanova, cha1a, cha1b, cha1c, chugchugchugalug, cissystrut, cissystrut1,...etc...

DrumLoop.directory;
-> Afro 1a: afro1a -> 16 steps
Afro 1b: afro1b -> 16 steps
Afro 1c: afro1c -> 16 steps
Afro 2a: afro2a -> 16 steps
Afro 2b: afro2b -> 16 steps
Afro 2c: afro2c -> 16 steps
...
```


## LICENSE

See [LICENSE](LICENSE)
