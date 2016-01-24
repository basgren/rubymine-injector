Injector for Ruby Mine - Change Notes
=====================================


#### Version 1.04 (2016-01-25)

* Fix saving plugin options when it's opened in IDE settings (Settings > Tools >
  Injector)
* Update default templates


#### Version 1.03

* Update plugin to work with RubyMine 8.0


#### Version 1.02

* Fixed Injector exception thrown when typing characters in editors that are not linked to any project (for example, in Settings dialog).


#### Version 1.01

* Fixed throwing exception by Injector when task is changed in Task Manager with switching current context


#### Version 1.0

* Added Injector settings dialog, where you can specify own templates. The dialog accessible by right-click on Injector icon in status bar (opens very fast).
* Added ignore shortcut option
* Added ability to insert '#{}' into ruby strings, converting them to interpolated if necessary
* Code optimization
* Usage statistics added


#### Version 0.95b

* Changed injection method. Now character sequence should be typed instead of
  one char to be replaced by template. This makes Injector not so annoying in
  some situations as it was before.
* Added some new templates.


#### Version 0.91

* Fixed issue when Injector occasionally inserted ruby injections if replace
  key is hold.
* Minor injection rules fix.


#### Version 0.9

* Injector have become much more unobtrusive. It injects code only within html
  text and attributes value, within &lt;script&gt; tag it's automatically
  disabled.
* Performance improved.
* Plugin state now persists between IDE restarts.
* Minor bugs fixed.


#### Version 0.8

* Initital release
