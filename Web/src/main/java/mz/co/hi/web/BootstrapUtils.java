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


                        String urlStr = classURL.toString();
                        if(urlStr.length()<=6)
                            continue;

                        String extension = urlStr.substring(urlStr.length()-6,urlStr.length());


                        if(!extension.equals(".class"))
                            continue;

                        indexer.index(classURL.openStream());

                    }catch (Exception ex){

                        continue;

                    }

                }

                indexSet.add(indexer.complete());

            }

            Set<URL> libraries = getLibraries(servletContext);
            if(libraries!=null){

                for(URL libURL : libraries){

                    try {


                        String urlStr = libURL.toString();
                        if(urlStr.length()<=4)
                            continue;

                        String extension = urlStr.substring(urlStr.length()-4,urlStr.length());

                        if(!extension.equals(".jar"))
                            continue;

                        File realFile = new File(libURL.getFile());
                        File tempFile = File.createTempFile(realFile.getName(),"jandex");
                        Index index = JarIndexer.createJarIndex(realFile, indexer,tempFile,false, false, false).getIndex();
                        indexSet.add(index);

                        try {

                            tempFile.delete();


                        }catch (Exception ex){

                            ex.printStackTrace();

                        }


                    }catch (Exception ex){

                        continue;

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
