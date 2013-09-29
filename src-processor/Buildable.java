import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.*;

@Documented
@Retention(SOURCE)
@Target(CONSTRUCTOR)
public @interface Buildable {
}
