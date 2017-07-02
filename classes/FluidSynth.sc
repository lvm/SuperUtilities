/*
        FluidSynth class

        (c) 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        A very basic fluidsynth "front-end".
        Reference:
        * https://sourceforge.net/p/fluidsynth/wiki/FluidSettings/

        Note: Requires `fluidsynth` installed in the system.
*/

FluidSynth {
  classvar err_msg, fluidsynth, fluidOutput;
  classvar fluidsynth_bin, fluidsynth_args;
  classvar valid_audio_servers;
  classvar audio_server, channels, commands_file;

  *new {
    |audio_server channels commands_file|
    ^this.boot(audio_server, channels, commands_file);
  }

  *initClass {
    fluidsynth_bin = "which fluidsynth".unixCmdGetStdOut.replace(Char.nl, "").asString;
    valid_audio_servers = ["alsa", "file", "jack", "oss", "pulseaudio"];
    err_msg = "TODO: Complete with a proper error message.";
  }

  *boot {
    |audio_server channels commands_file|

    var audioServer, chan, cmds;

    if ( currentEnvironment.at(\fluidsynth).isNil ){
      ~fluidsynth = this;

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

      cmds = if (
        commands_file.isNil.not,
        {
          if (
            File.exists(commands_file.standardizePath),
            { " -f " ++ commands_file.standardizePath },
            { "" });
        },
        { "" });


      fluidsynth_args = " -sl" ++ audioServer.asString ++ chan.asString ++ " " ++ cmds.asString;
      fluidsynth = Pipe.new("% %".format(fluidsynth_bin, fluidsynth_args.asString), "w");
      fluidOutput = Routine {
        var line = fluidsynth.getLine;
        while(
          { line.notNil },
          {
            line.postln;
            line = fluidsynth.getLine;
          }
        );
        fluidsynth.close;
      };
      ^"FluidSynth is running!";
    }
  }

  *send {
    |message|

    fluidsynth.write("%\n".format(message));
    fluidsynth.flush;
  }

  *stop {

    fluidsynth.close;
    ~fluidsynth = nil;
    ^"FluidSynth is stopped!"
  }


}

FluidCommands {
  classvar err_msg;

  *initClass {
    err_msg = "TODO: Complete with a proper error message.";
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
      Error(err_msg).throw;
    };

    ^format("\nload %", soundfont);
  }

  *unloadSoundfont {
    |soundfont|

    if (soundfont.isNil) {
      Error(err_msg).throw;
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
        Error(err_msg).throw;
      }
    );
    ^"FluidSynth Commands Saved";
  }
}