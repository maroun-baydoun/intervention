Intervention
=====

Intercept your Java calls with dynamic JVM languages.

Introduction
------------

With Intervention, you can separate the changing aspects of your Java application (business logic, validation rules...) from the rest of your codebase and place them in scripts files that can be edited without having to recompile the whole application.

Intervention intercepts the calls to your object methods, and executes the corresponding script functions prior to and after the Java method has been executed. This enables you to do necessary checks, validations and logging, before the Java method is executed, and to stop that method from being executed if needed.  After the Java method has finished execution, your script can clean up resources, log/persist the results or  modify the application flow by invoking other methods. 




License
-------

Intervention is licensed under the [Apache license 2][] terms.

  [Apache license 2]:http://www.apache.org/licenses/LICENSE-2.0


