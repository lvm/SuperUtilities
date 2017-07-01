/*
        Tidal class

        (c) 2017 by Mauro <mauro@sdf.org>, Damián <munshkr@gmail.com>
        http://cyberpunk.com.ar/

        Another TidalCycles interface.
        Reference:
        * https://tidalcycles.org/

        Note: Requires `ghci` and TidalCycles installed.
*/


Tidal {
  classvar ghci, ghciOutput;

  *new {
    ^this.start();
  }

  *start {
    if ( currentEnvironment.at(\tidal).isNil ){
      ~tidal = this;
      ghci = Pipe.new("ghci -XOverloadedStrings", "w");
      ghciOutput = Routine {
        var line = ghci.getLine;
        while(
          { line.notNil },
          {
            line.postln;
            line = ghci.getLine;
          }
        );
        ghci.close;
      };
    };
  }

  *send {
    |message|

    ghci.write("%\n".format(message));
    ghci.flush;
  }

  *stop {
    ghci.close;
    ~tidal = nil;
  }

}