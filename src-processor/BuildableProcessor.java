import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.tools.*;

@SupportedAnnotationTypes("Buildable")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public final class BuildableProcessor extends AbstractProcessor {

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element e : roundEnv.getElementsAnnotatedWith(Buildable.class)) {
            final ExecutableElement ctor = (ExecutableElement) e;

            debug("Found ctor: " + ctor);
            for (VariableElement v : ctor.getParameters()) {
                debug("arg: " + v.getSimpleName() + " => " + v.asType());
            }
        }

        return true;
    }

    private void debug(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }
}
