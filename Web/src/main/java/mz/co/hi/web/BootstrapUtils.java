package mz.co.hi.web;

import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mário Júnior
 */
public class BootstrapUtils {


    private static Set<URL> libraries = null;
    private static Set<URL> classFiles = null;

    private static Set<Index> indexSet = null;

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



    public static Set<Index> getIndexes(ServletContext servletContext){

        if(indexSet==null){

            Indexer indexer = new Indexer();
            indexSet = new HashSet<>();


            Set<URL> classes = getClassFiles(servletContext);
            if(classes!=null){

                for(URL classURL : classes){

                    try {

                        indexer.index(classURL.openStream());

                    }catch (Exception ex){

                        servletContext.log("Failed to index the class file <"+classURL.toString()+"> using Jandex",ex);

                    }

                }

                indexSet.add(indexer.complete());

            }

            Set<URL> libraries = getLibraries(servletContext);
            if(libraries!=null){

                for(URL libURL : libraries){

                    try {

                        File file = new File(libURL.toURI());
                        Index index = JarIndexer.createJarIndex(file, indexer, true, true, false).getIndex();
                        indexSet.add(index);

                    }catch (Exception ex){

                        servletContext.log("Failed to index <"+libURL.toString()+"> using Jandex",ex);

                    }


                }


            }


        }

        return indexSet;


    }


    private static Set<URL> getFiles(ServletContext context,String path){

        Set<URL> set = new HashSet<>();

        Set<String> libs = context.getResourcePaths(path);

        for(String libPath : libs) {

            try {

                //It is a directory
                if(libPath.substring(libPath.length()-1,libPath.length()).equals("/")){

                    set.addAll(getFiles(context,libPath));
                    continue;

                }


                URL resource = context.getResource(libPath);
                if(resource!=null)
                    set.add(resource);


            }catch (Exception ex){

                ex.printStackTrace();

            }

        }

        return set;

    }

}