package mz.co.hi.web.boot;

import org.jboss.jandex.Index;

import java.util.Set;

/**
 * @author Mário Júnior
 */
public interface BootExtension {

    public void boot(Set<Index> indexes) throws Exception;

}
