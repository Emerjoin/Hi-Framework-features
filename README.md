# Hi-Framework
Java EE 7 Framework that integrates Angular JS with CDI.

#Need some guide?
Please read the documentation [here](https://emerjoin.github.io/Hi-Framework/docs/Getting_started/Introduction.html "Hi-Framework")


#Changes-Log
* View-modes support added
* Exceptions thrown during Frontier invocations are now serialized and sent back
* Configuration is now abstracted and allows to integrate other configuration methods in the future and not Just Hi.xml.
* Authorization based in roles and permission introduced via AuthComponent: compatible with spring-security, apache-shiro and others