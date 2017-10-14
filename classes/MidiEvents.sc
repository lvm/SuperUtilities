/*
        MidiEvents class

        (c) 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        Events types for MIDIOut Patterns.
        (\type, \md, \midinote, Pseq((60..72),inf).play;
        (\type, \cc, \control, Pseq((0..127),inf).play;
*/

MidiEvents {
  classvar <outmidi;

  *new {
    |midiout|
    outmidi = midiout;
  }

  *initClass {
    Event.addEventType(\md, {
      |server|
      ~type = \midi;
      ~midiout = outmidi;
      ~chan = ~chan ? 9;
      ~amp = ~amp ? 0.9;
      currentEnvironment.play;
    });

    Event.addEventType(\cc, {
      |server|
      ~type = \midi;
      ~midicmd = \control;
      ~midiout = outmidi;
      ~ctlNum = ~ctlNum ? 23;
      currentEnvironment.play;
    });
  }

}
