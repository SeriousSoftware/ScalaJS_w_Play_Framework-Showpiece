# Play Framework with Scala.js Showcase
###Scala: 2.11.7, Scala.js: 0.8.2, Play: 2.4.3, Slick: 3.1.0

This project was inspired by [Todo application](http://lihaoyi.github.io/workbench-example-app/todo.html)

This project contains 4 simple examples:

1. Todo application with backend persistence. (InMemory, Slick, Anorm)
1. Hangman (Inspired by [Yii's demo](http://www.yiiframework.com/demos/hangman/))
  This simple game as a Single Page Application shows you the use of client side change propagation by means of [Scala.Rx](https://github.com/lihaoyi/scala.rx). A general Scala library which also runs on a JS platform (due its Execution Context abstraction).
  _Smart variables_ are embedded in a page `HTMLElement` producing function (`pageGuess`) will auto-update themselves when the values they depend on change and are placed in a `Rx` block.
  With compile-time pickling aka serialisation aka marshalling the servr will communicate with the client using the shared typeclass `Hangman` which its definition is shared with the JVM server and JS client.
1. HTML5 File upload (Modified from [How to Use HTML5 File Drag and Drop](http://www.sitepoint.com/html5-file-drag-and-drop/))
1. Server Push Chat. It supports both Websocket and Server-Sent Event

This project uses project structure from [play-with-scalajs-example](https://github.com/vmunier/play-with-scalajs-example)
## Copyright notice
Serious Software
©2015 by F.W. van den Berg
Licensed under the EUPL V.1.1

This Software is provided to You under the terms of the European Union Public License (the "EUPL") version 1.1 as published by the European Union. Any use of this Software, other than as authorized under this License is strictly prohibited (to the extent such use is covered by a right of the copyright holder of this Software).
 
This Software is provided under the License on an "AS IS" basis and without warranties of any kind concerning the Software, including without limitation merchantability, fitness for a particular purpose, absence of defects or errors, accuracy, and non-infringement of intellectual property rights other than copyright. This disclaimer of warranty is an essential part of the License and a condition for the grant of any rights to this Software.
