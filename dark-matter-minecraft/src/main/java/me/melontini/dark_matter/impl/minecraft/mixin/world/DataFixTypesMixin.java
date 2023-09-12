/*
 * Cardinal-Components-API
 * Copyright (C) 2019-2023 LadySnake
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.melontini.dark_matter.impl.minecraft.mixin.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import me.melontini.dark_matter.impl.minecraft.world.PersistentStateInternals;
import net.minecraft.datafixer.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/Ladysnake/Cardinal-Components-API/commit/56dbc9c01b98c67c27f981dff60b98a371646845#diff-083db263f5237b3170617badc4e77fba53d77a7cdc9b06387b9074652a1fc0f2">Link</a>
 */
@Mixin(DataFixTypes.class)
public class DataFixTypesMixin {

    @Inject(at = @At("HEAD"), method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;", cancellable = true)
    private <T> void dark_matter$cancelUpdate(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion, int newVersion, CallbackInfoReturnable<Dynamic<T>> cir) {
        if (PersistentStateInternals.CANCEL_UPDATE.get()) {
            cir.setReturnValue(dynamic);
        }
    }
}
