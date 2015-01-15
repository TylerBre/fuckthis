# Fuck This by Build This.

Fuck This is a version of Evernote for privacy minded programmers. It indexes snippets of code using Apache Lucene and Clojure.

Keep snippets of code in text files in the ./files/ folder.

Then, run 

    $ lein ring server

to start the server.

Then, bind a shortcut to open http://localhost:3000/ on your desktop. 

I bound the fuck key. Using alfred, I type "fuck <blank>" to search for what I remember about blank. So, by typing "fuck javascript" I can refer back to something I don't remember but probably learned 10 times over. You can search by fucking specific things. For example "fuck javascript canvas" or "fuck javascript input selectors".

MORE README TO COME!

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

