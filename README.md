# SuperUtilities

SuperCollider utilities to ease the job of the everyday coder.

## Install

Simply clone this repository in the `Extension` directory.
The path can be found by evaluating `Platform.userExtensionDir` or `Platform.systemExtensionDir`.


## FluidSynth.sc

Not quite a `fluidsynth` implementation but some sort of _front-end_.  

### Usage

```
FluidSynth.boot;
// or
// FluidSynth.boot(audio_server: "jack", channels: 32, commands_file: "~/sf_commands");

var gain = FluidCommands.setGain(1);
var load = FluidCommands.loadSoundfont("~/filename.sf2");
var list = FluidCommands.listSoundfonts;
var chans = FluidCommands.listChannels;
FluidSynth.send(gain++load++list);

var inst = FluidCommands.listInstruments(1);
FluidSynth.send(inst);

var select = FluidCommands.selectInstruments([
  (\chan: 2, \sfont: 1, \bank: 2, \prog: 4),
  (\chan: 9, \sfont: 1, \bank: 128, \prog: 2),
  (\chan: 10, \sfont: 1, \bank: 128, \prog: 1),
]);
FluidSynth.send(f.port, select++chans);

var unload = FluidCommands.unloadSoundfont(1);
FluidSynth.send(f.port, unload);


FluidSynth.stop;
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
TinySnippets.enable("t", sni);
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

## LICENSE

See [LICENSE](LICENSE)
