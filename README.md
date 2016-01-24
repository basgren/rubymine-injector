Injector
========

Injector is a simple plugin for RubyMine that provides fast ruby code
injections into erb templates. The plugin expands templates as fast as you
type shortcut text:

    '%%' --> <% | %>
    '%=' --> <%= | %>
    '%-' --> <%- | -%>
    '##' --> <%# | %>

Templates are not inserted if cursor is already in Ruby-code block. You can
change existing templates of define your own templates in settings dialog which
is accessible in IDE Settings or by right-click on injector icon in status bar.
Injector provides insertion of '#{}' into Ruby strings. When cursor is placed
in Ruby string, shortcut (default '##') is expanded into '#{}' and if string
is non-interpolating (single-quoted), it's automatically converted to
interpolating (double-quoted) string. This behavior and shortcut can be
customized in settings.
To enable or disable Injector you can click on injector icon in the status bar
or press default shortcut Ctrl+Comma(,).


## Development Environment

1. Fork Injector repository and clone it to your local machine.

2. Open the project in IntelliJ IDEA

3. Set up a JDK if it's not set (File > Project Structure > SDKs > Add New JDK).
   It's highly recommended to use JDK 1.6 to make plugin compatible with wider
   range of systems.

4. Set up IntelliJ plugin SDK. You should have RubyMine 8 installed.
   Go to File > Project Structure > SDKs > Add new IntelliJ IDEA Plugin SDK,
   name it "RubyMine 8 SDK" and select path to your RubyMine 8 installation.
   When you are asked to select JDK, specify JDK from previous step.

5. Select a project SDK for your project using "File > Project Structure >
   Project > Project SDK". Choose the plugin SDK you have created in the
   previous step.

6. Use "Run > Run 'Injector plugin'" menu to test plugin inside RubyMine.


## Building the Plugin

1. Copy and rename file `build.properties.example` to `build.properties`.

2. Edit this file and edit properties to match your environment (details are
   provided in comments in `build.properties` file).

3. Open Ant tool window in IntelliJ IDEA and run 'all' task. Plugin jars will
   be placed into `release/injector-[version]/` directory.
