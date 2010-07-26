package zephyr.plugin.core.api.monitoring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface DataLogged {
  String label() default "";

  String id() default "";

  boolean skipLabel() default false;

  int level() default 0;
}
