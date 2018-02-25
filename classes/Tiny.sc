/*
        TinySnippets class

        (c) 2017 by Mauro <mauro@sdf.org>
        http://cyberpunk.com.ar/

        A class for the lazy-coder.
*/

TinySnippets {
  classvar key, snippets, keyCode, keyFunc;

  *new {
    |key snippets|
    ^this.enable(key, snippets);
  }

  *selectionEnd {
    |doc start = -1|
    var str, end;
    end = start;
    str = doc.string;
    while {
      str[end] !== Char.nl
    } { end = end + 1 };
    ^end;
  }

  *enable {
    |key snippets|
    key = key ? "y";
    keyCode = key.asString.ascii.pop();
    snippets = ().putAll((), snippets);

    keyFunc = {
      |doc, char, mod, uni, kc, k|
      var code, selected, current, start, end;
      if (mod.isCtrl and: {kc == keyCode}) {
        // current = doc.currentLine;
        selected = doc.selectedString;


/*        if (selected.asString.size.booleanValue.not) {
          start = doc.selectionStart;
          end = this.selectionEnd(doc, doc.selectionStart);

          selected = current.replace(Char.nl,"").stripWhiteSpace;
          doc.selectRange(start, end-start);
        };*/

        code = snippets.at(selected.asSymbol);
        if (code.isNil.not) {
          if (doc.editable) {
            doc.selectedString_(code);
          }
        }
      }
    };

    Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(keyFunc);

    ^"% enabled with hotkey Ctrl+%".format(this.asString, key.asString);
  }


  *disable {
    Document.globalKeyDownAction = Document.globalKeyDownAction.removeFunc(keyFunc);

    ^"% disabled".format(this.asString);
  }

}
