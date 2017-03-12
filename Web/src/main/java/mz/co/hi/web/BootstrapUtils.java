package mz.co.hi.web;

import mz.co.hi.web.internal.Logging;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;
import org.slf4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mário Júnior
 */
public class BootstrapUtils {

    private static Set<URL> libraries = null;
    private static Set<URL> classFiles = null;
    private static Set<Index> indexSet = null;
    private static Logger log = Logging.getInstance().getLogger(BootstrapUtils.class);
    public static boolean DISABLE_SERVLET_CONTEXT_SCANNING = false;

    private static void indexClassURLs(Set<URL> classes, Indexer indexer){

        for(URL classURL : classes)
            indexClassURL(classURL,indexer);

    }

    private static void indexClassURL(URL classURL, Indexer indexer){

        try {

            if(!isClassFile(classURL))
                return;

            indexer.index(classURL.openStream());

        }catch (Throwable ex){

            log.error(String.format("Error indexing class: %s",classURL.toString()),ex);
            return;

        }

    }


    private static boolean isDirectory(URL url){

        return url.toString().endsWith("/")&&url.getProtocol().equals("file");

    }


    private static void indexDirectory(File directory, Indexer indexer){

        File[] files = directory.listFiles();
        if(files==null)
            return;


        for(File file : files){

            if(file.isDirectory()) {
                indexDirectory(file, indexer);
                continue;
            }

            try {

                indexClassURL(file.toURI().toURL(),indexer);

            }catch (IOException ex){



            }

        }

    }

    private static void indexDirectoryURL(URL url, Indexer indexer){

        if(!isDirectory(url))
            return;


        File directory = new File(url.getFile());
        if(!directory.isDirectory())
            return;

        indexDirectory(directory,indexer);


    }


    private static boolean isClassFile(URL url){

        String urlStr = url.toString();
        if(urlStr.length()<=6)
            return false;

        String extension = urlStr.substring(urlStr.length()-6,urlStr.length());
        if(!extension.equals(".class"))
            return false;

        return true;

    }

    private static boolean isJarFile(URL url){


        String urlStr = url.toString();
        if(urlStr.length()<=4)
            return false;

        String extension = urlStr.substring(urlStr.length()-4,urlStr.length());
        if(!extension.equals(".jar"))
            return false;

        return true;

    }

    private static void indexClassLoader(URLClassLoader classLoader, Indexer indexer){

        for(URL url : classLoader.getURLs()) {

            log.debug(String.format("Indexing URL : %s",url.toString()));
            indexClassURL(url, indexer);
            indexJarURL(url, indexer);
            indexDirectoryURL(url,indexer);

        }

    }


    private static void indexJarURL(URL jarURL, Indexer indexer){

        try {

            if(!isJarFile(jarURL))
                return;

            File realFile = new File(jarURL.getFile());
            File tempFile = File.createTempFile(realFile.getName(),"jandex");
            Index index = JarIndexer.createJarIndex(realFile, indexer,tempFile,false, false, false).getIndex();
            indexSet.add(index);

            try {

                tempFile.delete();

            }catch (Exception ex){
                log.error("Failed to deleted Jandex temporary index file",ex);
            }

        }catch (Throwable ex){

            log.error(String.format("Error indexing lib %s",jarURL.toString()),ex);
            return;

        }


    }




    private static Set<URL> getFilesContext(ServletContext context,String path,Set<URL> set){

        Set<String> libs = context.getResourcePaths(path);

        if(libs==null)
            return set;

        for(String filePath : libs) {

            try {

                //Directory
                if(filePath.substring(filePath.length()-1,filePath.length()).equals("/")){

                    getFilesContext(context,filePath,set);
                    continue;

                }


                URL resource = context.getResource(filePath);
                if(resource!=null)
                    set.add(resource);

            }catch (Exception ex){

                log.error(String.format("Error getting files in path %s",path),ex);

            }
        }

        return set;


    }

    private static Set<URL> getFiles(ServletContext context,String path){

        Set<URL> set = new HashSet<>();
        getFilesContext(context,path,set);
        return set;

    }


    public static Set<Index> getIndexes(ServletContext servletContext){

        if(indexSet==null){

            Indexer indexer = new Indexer();
            indexSet = new HashSet<>();

            ClassLoader classLoader = DispatcherServlet.class.getClassLoader();

            if( classLoader instanceof URLClassLoader && DISABLE_SERVLET_CONTEXT_SCANNING ){

                log.info("Indexing beans based in ClassLoader : Servlet context scanning disabled");
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                indexClassLoader(urlClassLoader,indexer);
                indexSet.add(indexer.complete());
                return indexSet;

            }

            Set<URL> classes = getClassFiles(servletContext);
            if(classes!=null)
                indexClassURLs(classes,indexer);

            Set<URL> libraries = getLibraries(servletContext);

            if(libraries!=null){
                for(URL libURL : libraries)
                    indexJarURL(libURL,indexer);

            }

            indexSet.add(indexer.complete());

        }

        return indexSet;


    }

    public static Set<URL> getLibraries(ServletContext context){

        if(libraries==null)
            libraries = getFiles(context,"/WEB-INF/lib/");
        return libraries;

    }

    public static Set<URL> getClassFiles(ServletContext context){

        if(classFiles==null)
            classFiles = getFiles(context,"/WEB-INF/classes/");

        return  classFiles;

    }


}
