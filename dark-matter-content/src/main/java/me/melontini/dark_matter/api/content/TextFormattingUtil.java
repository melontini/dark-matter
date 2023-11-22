package me.melontini.dark_matter.api.content;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.content.TextFormattingInternals;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class TextFormattingUtil {

    /**
     * Registers a new color for an existing {@link Formatting} element.
     *
     * <p>
     * This method allows adding a color to an existing {@link Formatting} element at runtime.
     * In particular, if the {@link Formatting} enum is modified after the {@link TextColor} class has been initialized, the existing {@link Formatting} elements may not have their associated colors updated correctly.
     * This method can be used to register new colors for existing {@link Formatting} elements.
     * </p>
     *
     * @param formatting the {@link Formatting} element to add a color to
     * @return the newly created {@link TextColor} instance
     */
    public static @NotNull TextColor addTextColor(@NotNull Formatting formatting) {
        return TextFormattingInternals.addTextColor(formatting);
    }
}
