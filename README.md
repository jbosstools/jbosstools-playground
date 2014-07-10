# Easier things for Eclipse IDE

This repository contains some plugins adding nice functionality for day to day usage
of Eclipse IDE. Namely:

## Easy Open/Import of a project

Aren't you sick of this messy "Import" wizard you see in Eclipse? Can't the IDE figure out what kind of project you're importing by itself? Yes, it does. This plugins include a new *File > Open Project/Folder* menu which will just ask you for a directory and will do its best to figure out what kind of project it is and run the most relevant import tricks.

Currently supported projects for easy import are:
* Eclipse projects: directories that are already Eclipse projects will be imported "as it" and will reuse existing configuration
* Java projects: will setup nature, detect and configure source folders
* JavaScript projects: will setup nature, detect source and configure source folders
* Maven projects: will import project as Maven project, and delegate its configuration to m2e according to the content of the pom.xml file
* JEE Projects: will enable the WebTools nature, and
  * Web projects: will enable Servlet facet
  * JPA projects: will enable JPA facet
  * Jax-rs projects: will enable Jax-rs facet
* PDE projects:
  * Plugin project: will enable Plug-in Nature
  * Feature project: will enable Feature Nature
* and by default, Plain folders: will be imported as new Eclipse projects, without additional configuration

This contains an extension point, so if you want to add support for other types of projects, it's just a matter of adding an extension and implementing a class.

Of course, this may not always give you the perfect result. It is recommended after such an import to have a look at your project properties and see whether there are some things that need more specific configuration.

Reference Eclipse bug: http://bugs.eclipse.org/421930

## Edit project natures

There are cases where you really want to hack your project, and are ready to take all the risks that go with it. So these plugins add a Project Property page that allow to add or remove project natures. Beware that this is a risky operation, so do it only when you're sure of what you're doing. Usually, enabling a nature is only 1 step in the configuration, so after you enabled a new nature, you'll most likely want to open (again) the Project Settings page and tweak the new pages as desired.

Reference Eclipse bug: https://bugs.eclipse.org/102527

## View Nested Projects

TODO: this is not yet part of these plugins

Reference Eclipse bug: https://bugs.eclipse.org/427768
