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

## Repetition.sc

Has its own quark now: https://github.com/lvm/Repetition.sc

```
Quarks.install("https://github.com/lvm/Repetition.sc");
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

## LICENSE

See [LICENSE](LICENSE)
