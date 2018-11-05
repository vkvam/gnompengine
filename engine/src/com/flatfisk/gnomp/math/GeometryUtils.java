/** Copyright 2013 Robin Stumm (serverkorken@googlemail.com, http://dermetfan.net/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. */

package com.flatfisk.gnomp.math;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

/**
 * provides some useful methods for geometric calculations
 *
 * @author dermetfan
 */
public abstract class GeometryUtils {

    /**
     * a {@link com.badlogic.gdx.math.Vector2} for temporary usage
     */
    public static final Vector2 vec2_0 = new Vector2(), vec2_1 = new Vector2();

    /**
     * a temporary steerings
     */
    private static Vector2[] tmpVecArr;

    /**
     * a temporary steerings
     */
    private static float[] tmpFloatArr;

    /**
     * @return a Vector2 representing the size of a rectangle containing all given vertices
     */
    public static Vector2 size(Vector2[] vertices, Vector2 output) {
        return output.set(MathUtils.amplitude(filterX(vertices)), MathUtils.amplitude(filterY(vertices)));
    }

    /**
     * @see #size(com.badlogic.gdx.math.Vector2[], com.badlogic.gdx.math.Vector2)
     */
    public static Vector2 size(Vector2[] vertices) {
        return size(vertices, vec2_0);
    }

    /**
     * @return the x values of the given vertices
     */
    public static float[] filterX(Vector2[] vertices, float[] output) {
        if (output == null || output.length != vertices.length)
            output = new float[vertices.length];
        for (int i = 0; i < output.length; i++)
            output[i] = vertices[i].x;
        return output;
    }

    /**
     * @see #filterX(com.badlogic.gdx.math.Vector2[], float[])
     */
    public static float[] filterX(Vector2[] vertices) {
        return filterX(vertices, tmpFloatArr);
    }

    /**
     * @return the y values of the given vertices
     */
    public static float[] filterY(Vector2[] vertices, float[] output) {
        if (output == null || output.length != vertices.length)
            output = new float[vertices.length];
        for (int i = 0; i < output.length; i++)
            output[i] = vertices[i].y;
        return output;
    }

    /**
     * @see #filterY(com.badlogic.gdx.math.Vector2[], float[])
     */
    public static float[] filterY(Vector2[] vertices) {
        return filterY(vertices, tmpFloatArr);
    }

    /**
     * rotates {@code point} by {@code radians} around [0:0] (local rotation)
     *
     * @param point   the point to rotate
     * @param radians the rotation
     * @return the given {@code point} rotated by {@code radians}
     */
    public static Vector2 rotate(Vector2 point, float radians) {
        // http://stackoverflow.com/questions/1469149/calculating-vertices-of-a-rotated-rectangle
        float xx = point.x, xy = point.y, yx = point.x, yy = point.y;
        xx = xx * cos(radians) - xy * sin(radians);
        yy = yx * sin(radians) + yy * cos(radians);
        return point.set(xx, yy);
    }

    /**
     * rotates a {@code point} around {@code center}
     *
     * @param point   the point to rotate
     * @param center  the point around which to rotate {@code point}
     * @param radians the rotation
     * @return the given {@code point} rotated around {@code center} by {@code radians}
     * @see #rotate(com.badlogic.gdx.math.Vector2, float)
     */
    public static Vector2 rotate(Vector2 point, Vector2 center, float radians) {
        return rotate(point, radians).add(center);
    }

    /**
     * @param vector2s the Vector2[] to convert to a float[]
     * @return the float[] converted from the given Vector2[]
     */
    public static float[] toFloatArray(Vector2[] vector2s, float[] output) {
        if (output == null || output.length != vector2s.length * 2)
            output = new float[vector2s.length * 2];

        for (int i = 0, vi = -1; i < output.length; i++)
            if (i % 2 == 0)
                output[i] = vector2s[++vi].x;
            else
                output[i] = vector2s[vi].y;

        return output;
    }

    /**
     * @see #toFloatArray(com.badlogic.gdx.math.Vector2[], float[])
     */
    public static float[] toFloatArray(Vector2[] vector2s) {
        return toFloatArray(vector2s, tmpFloatArr);
    }

    /**
     * @param floats the float[] to convert to a Vector2[]
     * @return the Vector2[] converted from the given float[]
     */
    public static Vector2[] toVector2Array(float[] floats, Vector2[] output) {
        if (floats.length % 2 != 0)
            throw new IllegalArgumentException("the float steerings's length is not dividable by two, so it won't make up a Vector2 steerings: " + floats.length);

        if (output == null || output.length != floats.length / 2) {
            output = new Vector2[floats.length / 2];
            for (int i = 0; i < output.length; i++)
                output[i] = new Vector2();
        }

        for (int i = 0, fi = -1; i < output.length; i++)
            output[i].set(floats[++fi], floats[++fi]);

        return output;
    }

    /**
     * @see #toVector2Array(float[], com.badlogic.gdx.math.Vector2[])
     */
    public static Vector2[] toVector2Array(float[] floats) {
        return toVector2Array(floats, tmpVecArr);
    }

    /**
     * @param vertexCount the number of vertices for each {@link com.badlogic.gdx.math.Polygon}
     * @see #toPolygonArray(com.badlogic.gdx.math.Vector2[], int[])
     */
    public static Polygon[] toPolygonArray(Vector2[] vertices, int vertexCount) {
        int[] vertexCounts = new int[vertices.length / vertexCount];
        for (int i = 0; i < vertexCounts.length; i++)
            vertexCounts[i] = vertexCount;
        return toPolygonArray(vertices, vertexCounts);
    }

    /**
     * @param vertices     the vertices which should be split into a {@link com.badlogic.gdx.math.Polygon} steerings
     * @param vertexCounts the number of vertices of each {@link com.badlogic.gdx.math.Polygon}
     * @return the {@link com.badlogic.gdx.math.Polygon} steerings extracted from the vertices
     */
    public static Polygon[] toPolygonArray(Vector2[] vertices, int[] vertexCounts) {
        Polygon[] polygons = new Polygon[vertexCounts.length];

        int vertice = -1;
        for (int i = 0; i < polygons.length; i++) {
            tmpVecArr = new Vector2[vertexCounts[i]];
            for (int i2 = 0; i2 < tmpVecArr.length; i2++)
                tmpVecArr[i2] = vertices[++vertice];
            polygons[i] = new Polygon(toFloatArray(tmpVecArr));
        }

        return polygons;
    }

    /**
     * @param polygon the polygon, assumed to be simple
     * @return if the vertices are in clockwise order
     */
    public static boolean areVerticesClockwise(Polygon polygon) {
        return polygon.area() < 0;
    }

    /**
     * @see #areVerticesClockwise(com.badlogic.gdx.math.Polygon)
     */
    public static boolean areVerticesClockwise(float[] vertices) {
        if (vertices.length <= 4)
            return true;
        return area(vertices) < 0;
    }

    /**
     * @see #isConvex(com.badlogic.gdx.math.Vector2[])
     */
    public static boolean isConvex(float[] vertices) {
        return isConvex(toVector2Array(vertices));
    }

    /**
     * @see #isConvex(com.badlogic.gdx.math.Vector2[])
     */
    public static boolean isConvex(Polygon polygon) {
        return isConvex(polygon.getVertices());
    }

    /**
     * @return the area of the polygon
     */
    public static float area(float[] vertices) {
        // from com.badlogic.gdx.math.Polygon#area()
        float area = 0;

        int x1, y1, x2, y2;
        for (int i = 0; i < vertices.length; i += 2) {
            x1 = i;
            y1 = i + 1;
            x2 = (i + 2) % vertices.length;
            y2 = (i + 3) % vertices.length;

            area += vertices[x1] * vertices[y2];
            area -= vertices[x2] * vertices[y1];
        }

        return area /= 2;
    }

    /**
     * @param vertices the vertices of the polygon to examine for convexity
     * @return if the polygon is convex
     */
    public static boolean isConvex(Vector2[] vertices) {
        // http://www.sunshine2k.de/coding/java/Polygon/Convex/polygon.htm
        Vector2 p, v = vec2_1, u;
        float res = 0;
        for (int i = 0; i < vertices.length; i++) {
            p = vertices[i];
            vec2_0.set(vertices[(i + 1) % vertices.length]);
            v.x = vec2_0.x - p.x;
            v.y = vec2_0.y - p.y;
            u = vertices[(i + 2) % vertices.length];

            if (i == 0) // in first loop direction is unknown, so save it in res
                res = u.x * v.y - u.y * v.x + v.x * p.y - v.y * p.x;
            else {
                float newres = u.x * v.y - u.y * v.x + v.x * p.y - v.y * p.x;
                if (newres > 0 && res < 0 || newres < 0 && res > 0)
                    return false;
            }
        }

        return true;
    }

    /**
     * @param concave the concave polygon to triangulate
     * @return an steerings of triangles representing the given concave polygon
     * @see com.badlogic.gdx.math.EarClippingTriangulator#computeTriangles(float[])
     */
    public static Polygon[] triangulate(Polygon concave) {
        Vector2[] polygonVertices = toVector2Array(concave.getTransformedVertices());
        ShortArray indices = new EarClippingTriangulator().computeTriangles(toFloatArray(polygonVertices));
        Vector2[] vertices = new Vector2[indices.size];
        for (int i = 0; i < indices.size; i++)
            vertices[i] = polygonVertices[indices.get(i)];
        return toPolygonArray(vertices, 3);
    }

    /**
     * @param concave the concave polygon to to decompose
     * @return an steerings of convex polygons representing the given concave polygon
     * @see BayazitDecomposer#convexPartition(com.badlogic.gdx.utils.Array)
     */
    public static Polygon[] decomposeIntoConvex(Polygon concave) {
        Array<Array<Vector2>> convexPolys = BayazitDecomposer.convexPartition(new Array<Vector2>(toVector2Array(concave.getTransformedVertices())));
        Polygon[] convexPolygons = new Polygon[convexPolys.size];
        for (int i = 0; i < convexPolygons.length; i++)
            convexPolygons[i] = new Polygon(toFloatArray((Vector2[]) convexPolys.get(i).toArray(Vector2.class)));
        return convexPolygons;
    }

    /**
     * Keeps the first described rectangle in the second described rectangle. If the second rectangle is smaller than the first one, the first will be centered on the second one.
     *
     * @param position the vector of the first rectangle
     * @param width    the width of the first rectangle
     * @param height   the height of the first rectangle
     * @param x2       the x of the second rectangle
     * @param y2       the y of the second rectangle
     * @param width2   the width of the second rectangle
     * @param height2  the height of the second rectangle
     * @return the vector of the first rectangle
     */
    public static Vector2 keepWithin(Vector2 position, float width, float height, float x2, float y2, float width2, float height2) {
        if (width2 < width)
            position.x = x2 + width2 / 2 - width / 2;
        else if (position.x < x2)
            position.x = x2;
        else if (position.x + width > x2 + width2)
            position.x = x2 + width2 - width;
        if (height2 < height)
            position.y = y2 + height2 / 2 - height / 2;
        else if (position.y < y2)
            position.y = y2;
        else if (position.y + height > y2 + height2)
            position.y = y2 + height2 - height;
        return position;
    }

    /**
     * @see #keepWithin(com.badlogic.gdx.math.Vector2, float, float, float, float, float, float)
     */
    public static Vector2 keepWithin(float x, float y, float width, float height, float rectX, float rectY, float rectWidth, float rectHeight) {
        return keepWithin(vec2_0.set(x, y), width, height, rectX, rectY, rectWidth, rectHeight);
    }

    /**
     * @see #keepWithin(float, float, float, float, float, float, float, float)
     */
    public static Rectangle keepWithin(Rectangle rect, Rectangle other) {
        return rect.setPosition(keepWithin(rect.x, rect.y, rect.width, rect.height, other.x, other.y, other.width, other.height));
    }

    /**
     * Keeps the given {@link com.badlogic.gdx.graphics.OrthographicCamera} in the given rectangle. If the rectangle is smaller than the camera viewport times the camera zoom, the camera will be centered on the rectangle.<br/>
     * Note that the camera will not be {@link com.badlogic.gdx.graphics.OrthographicCamera#update() updated}.
     *
     * @param camera the camera to keep in the rectangle
     * @see #keepWithin(float, float, float, float, float, float, float, float)
     */
    public static void keepWithin(OrthographicCamera camera, float x, float y, float width, float height) {
        Vector2 pos = keepWithin(camera.position.x - camera.viewportWidth / 2 * camera.zoom, camera.position.y - camera.viewportHeight / 2 * camera.zoom, camera.viewportWidth * camera.zoom, camera.viewportHeight * camera.zoom, x, y, width, height).scl(camera.zoom);
        camera.position.x = pos.x + camera.viewportWidth / 2;
        camera.position.y = pos.y + camera.viewportHeight / 2;
    }

}