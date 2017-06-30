# SuperUtilities

SuperCollider utilities to ease the job of the everyday coder.

## Install

Simply clone this repository in the `Extension` directory.
The path can be found by evaluating `Platform.userExtensionDir` or `Platform.systemExtensionDir`.


## FluidSynth.sc

Not quite a `fluidsynth` implementation but some sort of _front-end_.  

### Usage

```
f = FluidSynth.new;
// or
// f = FluidSynth.new(audio_server: "jack", channels: 32, port: 9801, commands_file: "~/sf_commands");

var gain = FluidCommands.setGain(1);
var load = FluidCommands.loadSoundfont("~/filename.sf2");
var list = FluidCommands.listSoundfonts;
var chans = FluidCommands.listChannels;
FluidCommands.send(f.port, gain++load++list);

var inst = FluidCommands.listInstruments(1);
FluidCommands.send(f.port, inst);

var select = FluidCommands.selectInstruments([
  (\chan: 9, \sfont: 1, \bank: 128, \prog: 2),
  (\chan: 10, \sfont: 1, \bank: 128, \prog: 1),
  (\chan: 2, \sfont: 1, \bank: 2, \prog: 4),
]);
FluidCommands.send(f.port, select++chans);

var unload = FluidCommands.unloadSoundfont(1);
FluidCommands.send(f.port, unload);


FluidServer.stop(f.pid);
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

## LICENSE

See [LICENSE](LICENSE)
