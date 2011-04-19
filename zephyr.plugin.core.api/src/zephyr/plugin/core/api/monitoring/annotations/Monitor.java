package zephyr.plugin.core.api.monitoring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Monitor {
  String label() default "";

  String id() default "";

  boolean emptyLabel() default false;

  int level() default 0;

  String[] wrappers() default {};

  boolean arrayIndexLabeled() default true;
}
