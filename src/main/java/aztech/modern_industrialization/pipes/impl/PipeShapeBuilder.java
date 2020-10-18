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
package aztech.modern_industrialization.pipes.impl;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * Reusing the PipePartBuilder to generate shapes for the pipe parts.
 */
public class PipeShapeBuilder extends PipePartBuilder {
    private VoxelShape shape;

    PipeShapeBuilder(int slotPos, Direction direction) {
        super(slotPos, direction);
        shape = VoxelShapes.empty();
    }

    @Override
    protected void drawPipe(float length, Intent intent, boolean end) {
        Vec3d up = up();
        addShape(pos.add(up.multiply(SIDE / 2)).add(right.multiply(SIDE / 2)),
                pos.subtract(up.multiply(SIDE / 2)).subtract(right.multiply(SIDE / 2)).add(facing.multiply(length)));
    }

    /**
     * Add a shape to the current shape using two corners.
     */
    private void addShape(Vec3d c1, Vec3d c2) {
        double x = Math.min(c1.x, c2.x), y = Math.min(c1.y, c2.y), z = Math.min(c1.z, c2.z);
        double X = Math.max(c1.x, c2.x), Y = Math.max(c1.y, c2.y), Z = Math.max(c1.z, c2.z);
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(x, y, z, X, Y, Z));
    }

    /**
     * Retrieve the built shape.
     */
    VoxelShape getShape() {
        return shape;
    }

    /**
     * Draw the center connector (starting from whatever direction).
     */
    public void centerConnector() {
        moveForward(-SIDE);
        drawPipe(SIDE, null);
    }
}
