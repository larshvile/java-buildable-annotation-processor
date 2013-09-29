import java.io.*;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.tools.*;

@SupportedAnnotationTypes("Buildable")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public final class BuildableProcessor extends AbstractProcessor {

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            // TODO find out wtf is up with this signature
            for (Element e : roundEnv.getElementsAnnotatedWith(Buildable.class)) {
                createBuilder((ExecutableElement) e);
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createBuilder(ExecutableElement ctor) throws IOException {
        final TypeElement clazz = (TypeElement) ctor.getEnclosingElement();
        final PackageElement pkg = (PackageElement) clazz.getEnclosingElement();

        debug("Creating builder for " + pkg.getSimpleName() + "." + clazz.getSimpleName() + "." + ctor);

        final JavaFileObject builderFile = processingEnv
            .getFiler()
            .createSourceFile(clazz.getQualifiedName().toString() + "Builder");

        try (PrintWriter wr = new PrintWriter(builderFile.openWriter())) {
            generateBuilderSource(pkg, clazz, ctor, wr);
        }
    }

    private void generateBuilderSource(PackageElement pkg, TypeElement clazz,
            ExecutableElement ctor, final PrintWriter wr) {
        final String targetClassName = clazz.getQualifiedName().toString();
        final String className = clazz.getSimpleName().toString() + "Builder";

        // TODO pkg
        wr.println("class " + className + " {");

        // fields
        forEachArg(ctor, new ArgHandler() {
            void process(String type, String name) {
                wr.println("private " + type + " " + name + ";");
            }
        });

        // fluent setters
        forEachArg(ctor, new ArgHandler() {
            void process(String type, String name) {
                wr.println("public " + className + " with" + capitalize(name) + "(" + type + " in) {");
                wr.println("this." + name + " = in;");
                wr.println("return this;");
                wr.println("}");
            }
        });

        // build method
        wr.println("public " + targetClassName + " build() {");
        wr.print("return new " + targetClassName + "(");
        forEachArg(ctor, new ArgHandler() {
            void process(String type, String name) {
                if (!first) {
                    wr.print(", ");
                }
                wr.print(name);
            }
        });
        wr.println(");");
        wr.println("}");
        

        wr.println("}");
    }

    private void forEachArg(ExecutableElement ctor, ArgHandler h) {
        for (VariableElement v : ctor.getParameters()) {
            h.process(v.asType().toString(), v.getSimpleName().toString());
            h.first = false;
        }
    }

    private void debug(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    private static abstract class ArgHandler {
        boolean first = true;
        abstract void process(String type, String name);
    }

    private static String capitalize(String in) {
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }
}
