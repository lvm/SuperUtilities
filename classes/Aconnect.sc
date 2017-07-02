/*
        Aconnect class

        (c) 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        A very basic aconnect "front-end".
        Reference:
        Copyright (C) 1999-2000 Takashi Iwai
        Usage:
         * Connection/disconnection between two ports
           aconnect [-options] sender receiver
             sender, receiver = client:port pair
             -d,--disconnect     disconnect
             -e,--exclusive      exclusive connection
             -r,--real #         convert real-time-stamp on queue
             -t,--tick #         convert tick-time-stamp on queue
         * List connected ports (no subscription action)
           aconnect -i|-o [-options]
             -i,--input          list input (readable) ports
             -o,--output         list output (writable) ports
             -l,--list           list current connections of each port
         * Remove all exported connections
             -x, --removeall

        Note: Requires `aconnect` installed in the system.
*/

Aconnect {
  classvar <>cmd;

  *new {  }

  *initClass {
    cmd = "which aconnect".unixCmdGetStdOut.replace(Char.nl, "").asString;
  }

  *runCmd {
    |flag|
    ^"% %".format(cmd, flag).unixCmdGetStdOutLines.select {
      |line i|
      line.containsi("client");
    }.collect {
      |line|
      line = line.split($:);
      (
        \port: line.at(0).replace("client ", "").stripWhiteSpace,
        \device: line.at(1).findRegexp("'.*'").pop().at(1).replace("'","").stripWhiteSpace
      );
    };
  }

  *runCmdByName {
    |flag, name|
    ^this.runCmd(flag).select {
      |each i|
      each.device.containsi(name)
    }.pop();
  }

  *out {
    ^this.runCmd("-o");
  }

  *outByName {
    |name|
    ^this.runCmdByName("-o", name);
  }

  *in {
    ^this.runCmd("-o");
  }

  *inByName {
    |name|
    ^this.runCmdByName("-o", name);
  }

  *connect {
    |sender receiver flag=""|
    ^"% % % %".format(cmd, flag, sender, receiver).unixCmd(postOutput:false);
  }

  *disconnect {
    |sender receiver|
    ^this.connect(sender, receiver, "-d");
  }

}