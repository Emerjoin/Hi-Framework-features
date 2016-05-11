package mz.co.hi.web.annotations.processor;

import com.squareup.javapoet.*;
import mz.co.hi.web.annotations.Frontier;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.*;

/**
 * Created by Mario Junior.
 */
public class Processor extends AbstractProcessor {

    private Filer filer = null;
    private Messager messager = null;
    private Elements elements = null;

    private List<TypeElement> annotedClasses = new ArrayList();

    public Processor(){



    }

    public void init(ProcessingEnvironment ev){
        super.init(ev);

        filer = ev.getFiler();
        messager = ev.getMessager();
        elements = ev.getElementUtils();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        Set<? extends Element> annotedElements = roundEnv.getElementsAnnotatedWith(Frontier.class);
        for(Element element : annotedElements){

            if(element.getKind() == ElementKind.CLASS){

                TypeElement typeElement = (TypeElement) element;
                annotedClasses.add(typeElement);
                messager.printMessage(Diagnostic.Kind.NOTE,"Frontier class found : "+typeElement.getQualifiedName()+"");

            }else{

                messager.printMessage(Diagnostic.Kind.ERROR,"Only classes can be annotated with @Frontier");

            }

        }

        if(annotedClasses.size()==0){

            //messager.printMessage(Diagnostic.Kind.WARNING,"No class found with annotation @JavascriptBean");
            return true;

        }

        try {

            JavaFileObject jfo = filer.createSourceFile("mz.co.hi.web.generated.FrontiersInitializer");
            Writer writer = jfo.openWriter();

            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(List.class, "beans");

            for(TypeElement annotedClass : annotedClasses){

                constructorBuilder.addStatement("beans.add($S)", annotedClass.getQualifiedName());

            }

            MethodSpec constructor = constructorBuilder.build();

            TypeSpec javascriptBeansInitializer = TypeSpec.classBuilder("FrontiersInitializer")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(constructor)
                    .build();

            JavaFile javaFile = JavaFile.builder("com.yayee.generated", javascriptBeansInitializer)
                    .build();

            javaFile.writeTo(writer);
            writer.flush();
            writer.close();

        }catch (Exception ex){

            messager.printMessage(Diagnostic.Kind.ERROR,"Could not generate source file. Error: "+ex.getMessage());

        }finally {

            annotedClasses.clear();

        }



        return true;

    }

    public Set<String> getSupportedAnnotationTypes(){

        Set<String> strings = new HashSet();
        strings.add(Frontier.class.getCanonicalName());
        return strings;

    }

    public SourceVersion getSupportedSourceVersion(){

        return SourceVersion.latestSupported();

    }


}
