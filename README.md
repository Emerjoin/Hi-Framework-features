# Hi-Framework
Java EE 7 Framework that integrates Angular JS with CDI.

#Need some guide?
Please read the documentation [here](https://emerjoin.github.io/Hi-Framework/docs/Getting_started/Introduction.html "Hi-Framework")


#Changes-Log
1. View-modes support added
2. Exceptions thrown during Frontier invocations are now serialized and sent back
3. Configuration is now abstracted allow to integrate bean-based configuration in the future and not Just Hi.xml.
4. Authorization based in roles and permission introduced via AuthComponent: compatible with spring-security, apache-shiro and others