package me.melontini.dark_matter.impl.content;

import lombok.experimental.UtilityClass;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@UtilityClass
@ApiStatus.Internal
public class TextFormattingInternals {

    public static @NotNull TextColor addTextColor(@NotNull Formatting formatting) {
        var tc = new TextColor(formatting.getColorValue(), formatting.getName());
        TextColor.FORMATTING_TO_COLOR.put(formatting, tc);
        TextColor.BY_NAME.put(tc.name, tc);
        return tc;
    }

    static {
        TextColor.FORMATTING_TO_COLOR = new HashMap<>(TextColor.FORMATTING_TO_COLOR);
        TextColor.BY_NAME = new HashMap<>(TextColor.BY_NAME);
    }
}
