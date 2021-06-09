/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package aztech.modern_industrialization.inventory;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack.ConfigurableFluidSlot;
import aztech.modern_industrialization.inventory.ConfigurableItemStack.ConfigurableItemSlot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ConfigurableInventoryPacketHandlers {
    public static class S2C {
        // sync id, slot id, slot tag
        public static final ClientPlayNetworking.PlayChannelHandler UPDATE_ITEM_SLOT = (mc, handler, buf, sender) -> {
            int syncId = buf.readInt();
            int stackId = buf.readInt();
            NbtCompound tag = buf.readNbt();
            mc.execute(() -> {
                ScreenHandler sh = mc.player.currentScreenHandler;
                if (sh.syncId == syncId) {
                    ConfigurableScreenHandler csh = (ConfigurableScreenHandler) sh;
                    ConfigurableItemStack oldStack = csh.inventory.getItemStacks().get(stackId);
                    // update stack
                    ConfigurableItemStack newStack = ConfigurableItemStack.fromNbt(tag);
                    csh.inventory.getItemStacks().set(stackId, newStack);
                    // update slot
                    for (int i = 0; i < csh.slots.size(); ++i) {
                        Slot slot = csh.slots.get(i);
                        if (slot instanceof ConfigurableItemSlot) {
                            ConfigurableItemSlot is = (ConfigurableItemSlot) slot;
                            if (is.getConfStack() == oldStack) {
                                csh.slots.set(i, newStack.new ConfigurableItemSlot(is));
                                return;
                            }
                        }
                    }
                    throw new RuntimeException("Could not find slot to replace!");
                }
            });
        };
        // sync id, slot id, slot tag
        public static final ClientPlayNetworking.PlayChannelHandler UPDATE_FLUID_SLOT = (mc, handler, buf, sender) -> {
            int syncId = buf.readInt();
            int stackId = buf.readInt();
            NbtCompound tag = buf.readNbt();
            mc.execute(() -> {
                ScreenHandler sh = mc.player.currentScreenHandler;
                if (sh.syncId == syncId) {
                    ConfigurableScreenHandler csh = (ConfigurableScreenHandler) sh;
                    ConfigurableFluidStack oldStack = csh.inventory.getFluidStacks().get(stackId);
                    // update stack
                    ConfigurableFluidStack newStack = ConfigurableFluidStack.fromNbt(tag);
                    csh.inventory.getFluidStacks().set(stackId, newStack);
                    // update slot
                    for (int i = 0; i < csh.slots.size(); ++i) {
                        Slot slot = csh.slots.get(i);
                        if (slot instanceof ConfigurableFluidSlot) {
                            ConfigurableFluidSlot fs = (ConfigurableFluidSlot) slot;
                            if (fs.getConfStack() == oldStack) {
                                csh.slots.set(i, newStack.new ConfigurableFluidSlot(fs));
                                return;
                            }
                        }
                    }
                    throw new RuntimeException("Could not find slot to replace!");
                }
            });
        };
    }

    public static class C2S {
        // sync id, new locking mode
        public static final ServerPlayNetworking.PlayChannelHandler SET_LOCKING_MODE = (ms, player, handler, buf, sender) -> {
            int syncId = buf.readInt();
            boolean lockingMode = buf.readBoolean();
            ms.execute(() -> {
                ScreenHandler sh = player.currentScreenHandler;
                if (sh.syncId == syncId) {
                    ConfigurableScreenHandler csh = (ConfigurableScreenHandler) sh;
                    csh.lockingMode = lockingMode;
                }
            });
        };
    }
}
