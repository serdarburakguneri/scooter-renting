package adapter.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PatchUtil {

    private PatchUtil(){

    }

    public static <T> boolean isChanged(T value, Supplier<T> supplier) {
        Predicate<T> predicate = input -> {
            if (Objects.isNull(input)) {
                return Objects.nonNull(value);
            } else {
                return Objects.nonNull(value) && !input.equals(value);
            }
        };
        return predicate.test(supplier.get());
    }

    public static <T> void updateIfChanged(Consumer<T> consumer, T value, Supplier<T> supplier) {
        if (isChanged(value, supplier)) {
            consumer.accept(value);
        }
    }
}
