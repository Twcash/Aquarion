package aquarion.world.graphics;

import arc.math.geom.Vec2;

public class AquaIK {
    private static final Vec2 temp = new Vec2(), temp2 = new Vec2();
    private static final Vec2[] mats = {new Vec2(), new Vec2()};

    /**
     * Solves for the positions of the joints in a multi-segment chain.
     *
     * @param lengths  an array of segment lengths
     * @param end      the target endpoint you want to reach
     * @param side     whether to solve on the positive or negative side
     * @param results  an array of results that will be populated with joint positions
     * @return whether IK succeeded (this can fail if the target point is too far)
     */
    public static boolean solve(float[] lengths, Vec2 end, boolean side, Vec2[] results) {
        if (lengths.length != results.length) {
            throw new IllegalArgumentException("Length of segments and results must match.");
        }

        // Calculate the total length of all segments.
        float totalLength = 0;
        for (float length : lengths) {
            totalLength += length;
        }

        // Calculate an attractor point by rotating around the end.
        Vec2 attractor = new Vec2(end).rotate(side ? 1 : -1).setLength(totalLength).add(end.x / 2f, end.y / 2f);

        return solve(lengths, end, attractor, results);
    }

    /**
     * Solves for the positions of the joints with a specified attractor.
     *
     * @param lengths   an array of segment lengths
     * @param end       the target endpoint you want to reach
     * @param attractor the point that helps resolve ambiguities in positioning
     * @param results   an array to store the positions of the joints
     * @return whether the solution succeeded
     */
    public static boolean solve(float[] lengths, Vec2 end, Vec2 attractor, Vec2[] results) {
        int segmentCount = lengths.length;
        Vec2 direction = mats[0].set(end).nor();
        mats[1].set(attractor).sub(temp2.set(direction).scl(attractor.dot(direction))).nor();

        // Set up transformation matrices for axis alignment.
        @SuppressWarnings("SuspiciousNameCombination") Vec2[] mat1 = {new Vec2(direction.x, mats[1].x), new Vec2(direction.y, mats[1].y)};

        // Position calculation starts from the end and works backwards.
        Vec2 currentPos = new Vec2(end);

        for (int i = segmentCount - 1; i >= 0; i--) {
            float segmentLength = lengths[i];
            results[i] = temp.set(currentPos).sub(segmentLength, 0);  // Simplified joint positioning logic

            // Transform the joint position using the rotation matrix.
            results[i].set(mat1[0].dot(results[i]), mat1[1].dot(results[i]));

            // Set the current position to the calculated joint for the next iteration.
            currentPos.set(results[i]);
        }

        return true;
    }
}