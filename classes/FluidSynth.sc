/*
        FluidSynth class

        (c) 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        A very basic fluidsynth "front-end".
        Reference:
        * https://sourceforge.net/p/fluidsynth/wiki/FluidSettings/

        Note: Requires `fluidsynth`, `netcat`, `procps`, `grep` installed in the system.
*/

FluidSynth {
  classvar err_no_file;
  var <>audio_server, <>channels, <>port, <>commands_file, <>pid;

  *new {
    |audio_server channels port commands_file|
    ^super.newCopyArgs(audio_server, channels, port, commands_file).init;
  }

  *initClass {
    err_no_file = "File doesn't exists or isn't readable";
  }

  init {
    this.audio_server = audio_server ? "jack";
    this.channels = channels ? 16;
    this.port = port ? 9800;
    this.commands_file = commands_file;
    this.pid = FluidServer.boot(audio_server, channels, port, commands_file);
  }
}

FluidServer {
  classvar <>fluidpid;

  *new {
    |audio_server channels port commands_file|
    ^this.boot(audio_server, channels, port, commands_file);
  }

  *boot {
    |audio_server channels port commands_file|

    var sf, audioServer, tcpPort, chan, cmds, fluidpid;
    var fluidsynth = "which fluidsynth".unixCmdGetStdOut.replace("\n", "").asString;
    var valid_audio_servers = ["alsa", "file", "jack", "oss", "pulseaudio"];
    var fluidsynth_args;

    audioServer = if (
      audio_server.isNil.not and:
      (valid_audio_servers.atIdentityHash(audio_server) == 0),
      { audio_server },
      { "jack" });
    // also, if audioServer is jack autoconnect.
    audioServer = if (
      audioServer == "jack",
      { format(" -j -a %", audioServer) },
      { format(" -a %", audioServer) });

    chan = if (
      channels.isNil.not and: (channels.isKindOf(Integer)),
        { format(" -K %", channels.asInt.clip(16, 256)) },
        { " -K 16" });

    tcpPort = if (
      port.isNil.not and: (port.isKindOf(Integer)),
      { format(" -o 'shell.port=%' ", port.asInt) },
      { "" });

    cmds = if (
      commands_file.isNil.not,
      {
        if (
          File.exists(commands_file.standardizePath),
          { " -f " ++ commands_file.standardizePath },
          { "" });
      },
      { "" });

    fluidsynth_args = " -sil" ++ audioServer.asString ++ chan.asString ++ tcpPort.asString;

    fluidpid = (fluidsynth ++ fluidsynth_args.asString ++ " " ++ cmds.asString).unixCmd;
    "FluidSynth is running!".postln;
    ^fluidpid;
  }

  *stop {
    |ppid|
    var cpid = "ps --ppid % -o pid|grep -v '^ '".format(ppid).unixCmdGetStdOut;
    "kill -9 % %".format(ppid, cpid).unixCmd(postOutput: false);
    ^"FluidSynth is stopped!"
  }
}

FluidCommands {
  classvar err_no_file;

  *initClass {
    err_no_file = "File doesn't exists or isn't readable";
  }

  *setGain {
    |gain|

    ^format("\ngain %", gain.asFloat.clip(0.01, 4.99));
  }

  *listChannels {
    ^"\nchannels";
  }

  *listSoundfonts {
    ^"\nfonts";
  }

  *listInstruments {
    |soundfont|

    ^format("\ninst %", soundfont);
  }

  *loadSoundfont {
    |soundfont|

    if (soundfont.isNil) {
      Error(soundfont ++ ": " ++ err_no_file).throw;
    };

    ^format("\nload %", soundfont);
  }

  *unloadSoundfont {
    |soundfont|

    if (soundfont.isNil) {
      Error(soundfont ++ ": " ++ err_no_file).throw;
    };

    ^format("\nunload %", soundfont);
  }

  *selectInstruments {
    |instruments|
    var select_cmd = "";
    var values;

    if (instruments.isNil.not and: (instruments.isKindOf(Array))) {
      instruments.collect {
        |inst|
        if (inst.isKindOf(Dictionary)) {
          values = [inst.at(\chan), inst.at(\sfont), inst.at(\bank), inst.at(\prog)];
          select_cmd = select_cmd ++ format("\nselect % % % %", *values);
        }
      };
    }

    ^select_cmd;
  }

  *send {
    |port, commands|
    var nc;

    if (
      port.isNil.not and: (commands.isNil.not),
      {
        commands = format("'%\nquit'", commands);
        nc = format("echo % | nc localhost %", commands, port);
        nc.unixCmdGetStdOut.postln;
      },
      {
        Error("Missing port or commands").throw;
      }
    );
    ^"FluidSynth Commands Sent";
  }

  *save {
    |filename, commands|
    var f;

    if (
      filename.isNil.not and: (commands.isNil.not),
      {
        f = File(filename.standardizePath, "w");
        f.write(commands ++ "\n");
        f.close;
      },
      {
        Error("Missing filename or commands").throw;
      }
    );
    ^"FluidSynth Commands Saved";
  }
}