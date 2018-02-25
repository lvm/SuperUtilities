/*
        JackConnect class

        (c) 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        A very simple `jack_lsp`, `jack_connect` and `jack_disconnect` interface for SuperCollider.
        Note: Requires `jackd2` installed in the system.
*/

JackConenct {
  classvar lsp, conn, disconn;
  classvar jack;

  *initClass {
    lsp = "which jack_lsp".unixCmdGetStdOut.replace(Char.nl, "").asString;
    conn = "which jack_connect".unixCmdGetStdOut.replace(Char.nl, "").asString;
    disconn = "which jack_disconnect".unixCmdGetStdOut.replace(Char.nl, "").asString;
  }

  *new {
    if(jack.isNil){
      jack = super.new;
    }
    ^jack;
  }

  listClients {
    |filter_by|
    var output = lsp.unixCmdGetStdOutLines;

    if (filter_by.notNil) { output = output.select {|line i| line.containsi(filter_by); }; };
    ^output;
  }

  connectClients {
    |out, in|
    "% % %".format(conn, out.quote, in.quote).unixCmdGetStdOutLines;
  }

  disconnectClients {
    |out, in|
    "% % %".format(disconn, out.quote, in.quote).unixCmdGetStdOutLines;
  }

}