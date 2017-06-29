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

  *enable {
    |key snippets|
    key = key.asString;
    keyCode = key.ascii.pop();
    snippets = ().putAll((), snippets);

    keyFunc = {
      |doc, char, mod, uni, kc, k|
      var code;
      if (mod.isCtrl and: {kc == keyCode}) {
        code = snippets.at(doc.selectedString.asSymbol);
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
