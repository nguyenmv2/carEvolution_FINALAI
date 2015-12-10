/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.jbox2d.common;

import java.util.Random;

/**
 * A few math methods that don't fit very well anywhere else.
 */

public class MathUtils extends PlatformMathUtils {
    public static final float PI = 3.1415927F;
    public static final float TWOPI = 6.2831855F;
    public static final float INV_PI = 0.31830987F;
    public static final float HALF_PI = 1.5707964F;
    public static final float QUARTER_PI = 0.7853982F;
    public static final float THREE_HALVES_PI = 4.712389F;
    public static final float DEG2RAD = 0.017453292F;
    public static final float RAD2DEG = 57.295776F;
    public static final float[] sinLUT;

    public MathUtils() {
    }

    public static final float sin(float x) {
        return Settings.SINCOS_LUT_ENABLED ? sinLUT(x) : (float) StrictMath.sin((double) x);
    }

    public static final float sinLUT(float x) {
        x %= 6.2831855F;
        if (x < 0.0F) {
            x += 6.2831855F;
        }

        if (Settings.SINCOS_LUT_LERP) {
            x /= 1.1E-4F;
            int index = (int) x;
            if (index != 0) {
                x %= (float) index;
            }

            return index == Settings.SINCOS_LUT_LENGTH - 1 ? (1.0F - x) * sinLUT[index] + x * sinLUT[0] : (1.0F - x) * sinLUT[index] + x * sinLUT[index + 1];
        } else {
            return sinLUT[round(x / 1.1E-4F) % Settings.SINCOS_LUT_LENGTH];
        }
    }

    public static final float cos(float x) {
        return Settings.SINCOS_LUT_ENABLED ? sinLUT(1.5707964F - x) : (float) StrictMath.cos((double) x);
    }

    public static final float abs(float x) {
        return Settings.FAST_ABS ? (x > 0.0F ? x : -x) : Math.abs(x);
    }

    public static final int abs(int x) {
        int y = x >> 31;
        return (x ^ y) - y;
    }

    public static final int floor(float x) {
        if (Settings.FAST_FLOOR) {
            int y = (int) x;
            return x < 0.0F && x != (float) y ? y - 1 : y;
        } else {
            return (int) Math.floor((double) x);
        }
    }

    public static final int ceil(float x) {
        if (Settings.FAST_CEIL) {
            int y = (int) x;
            return x > 0.0F && x != (float) y ? y + 1 : y;
        } else {
            return (int) Math.ceil((double) x);
        }
    }

    public static final int round(float x) {
        return Settings.FAST_ROUND ? floor(x + 0.5F) : StrictMath.round(x);
    }

    public static final int ceilPowerOf2(int x) {
        int pow2;
        for (pow2 = 1; pow2 < x; pow2 <<= 1) {
            ;
        }

        return pow2;
    }

    public static final float max(float a, float b) {
        return a > b ? a : b;
    }

    public static final int max(int a, int b) {
        return a > b ? a : b;
    }

    public static final float min(float a, float b) {
        return a < b ? a : b;
    }

    public static final int min(int a, int b) {
        return a < b ? a : b;
    }

    public static final float map(float val, float fromMin, float fromMax, float toMin, float toMax) {
        float mult = (val - fromMin) / (fromMax - fromMin);
        float res = toMin + mult * (toMax - toMin);
        return res;
    }

    public static final float clamp(float a, float low, float high) {
        return max(low, min(a, high));
    }

    public static final Vec2 clamp(Vec2 a, Vec2 low, Vec2 high) {
        Vec2 min = new Vec2();
        min.x = a.x < high.x ? a.x : high.x;
        min.y = a.y < high.y ? a.y : high.y;
        min.x = low.x > min.x ? low.x : min.x;
        min.y = low.y > min.y ? low.y : min.y;
        return min;
    }

    public static final void clampToOut(Vec2 a, Vec2 low, Vec2 high, Vec2 dest) {
        dest.x = a.x < high.x ? a.x : high.x;
        dest.y = a.y < high.y ? a.y : high.y;
        dest.x = low.x > dest.x ? low.x : dest.x;
        dest.y = low.y > dest.y ? low.y : dest.y;
    }

    public static final int nextPowerOfTwo(int x) {
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    public static final boolean isPowerOfTwo(int x) {
        return x > 0 && (x & x - 1) == 0;
    }

    public static final float atan2(float y, float x) {
        return Settings.FAST_ATAN2 ? fastAtan2(y, x) : (float) StrictMath.atan2((double) y, (double) x);
    }

    public static final float fastAtan2(float y, float x) {
        if (x == 0.0F) {
            return y > 0.0F ? 1.5707964F : (y == 0.0F ? 0.0F : -1.5707964F);
        } else {
            float z = y / x;
            float atan;
            if (abs(z) < 1.0F) {
                atan = z / (1.0F + 0.28F * z * z);
                if (x < 0.0F) {
                    if (y < 0.0F) {
                        return atan - 3.1415927F;
                    }

                    return atan + 3.1415927F;
                }
            } else {
                atan = 1.5707964F - z / (z * z + 0.28F);
                if (y < 0.0F) {
                    return atan - 3.1415927F;
                }
            }

            return atan;
        }
    }

    public static final float reduceAngle(float theta) {
        theta %= 6.2831855F;
        if (abs(theta) > 3.1415927F) {
            theta -= 6.2831855F;
        }

        if (abs(theta) > 1.5707964F) {
            theta = 3.1415927F - theta;
        }

        return theta;
    }

    public static final float randomFloat(float argLow, float argHigh) {
        return (float) Math.random() * (argHigh - argLow) + argLow;
    }

    public static final float randomFloat(Random r, float argLow, float argHigh) {
        return r.nextFloat() * (argHigh - argLow) + argLow;
    }

    public static final float sqrt(float x) {
        return (float) StrictMath.sqrt((double) x);
    }

    public static final float distanceSquared(Vec2 v1, Vec2 v2) {
        float dx = v1.x - v2.x;
        float dy = v1.y - v2.y;
        return dx * dx + dy * dy;
    }

    public static final float distance(Vec2 v1, Vec2 v2) {
        return sqrt(distanceSquared(v1, v2));
    }

    static {
        sinLUT = new float[Settings.SINCOS_LUT_LENGTH];

        for (int i = 0; i < Settings.SINCOS_LUT_LENGTH; ++i) {
            sinLUT[i] = (float) Math.sin((double) ((float) i * 1.1E-4F));
        }

    }
}