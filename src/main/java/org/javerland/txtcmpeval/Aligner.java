package org.javerland.txtcmpeval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Package private helper for pairing of two lists of parts (words or
 * sentences), when similarity of every possible pair is already calculated.
 * Score of pairing is normalized to result from 0.0 to 1.0, unpaired parts of
 * longer list decrease it.
 *
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
final class Aligner {

    private Aligner() {
    }

    /**
     * Pairs parts with adhering of their order, it means pairs can't cross each
     * other. It's classic dynamic programming alignment, every part can be
     * skipped without penalty, but only paired parts increase the score.
     *
     * @param similarities Matrix of similarities, first index is index of part
     * of first list, second index is index of part of compared list.
     * @return Result from 0.0 to 1.0.
     */
    static double orderedScore(double[][] similarities) {
        int rows = similarities.length;
        int cols = similarities[0].length;

        double[] previousRow = new double[cols + 1];
        double[] actualRow = new double[cols + 1];

        for (int i = 1; i <= rows; i++) {
            actualRow[0] = 0.0d;
            for (int j = 1; j <= cols; j++) {
                double paired = previousRow[j - 1] + similarities[i - 1][j - 1];
                double skipped = Math.max(previousRow[j], actualRow[j - 1]);
                actualRow[j] = Math.max(paired, skipped);
            }
            double[] swap = previousRow;
            previousRow = actualRow;
            actualRow = swap;
        }

        return previousRow[cols] / Math.max(rows, cols);
    }

    /**
     * Pairs parts without adhering of their order, the most similar pairs are
     * taken first and every part can be used only once.
     *
     * @param similarities Matrix of similarities, first index is index of part
     * of first list, second index is index of part of compared list.
     * @return Result from 0.0 to 1.0.
     */
    static double unorderedScore(double[][] similarities) {
        int rows = similarities.length;
        int cols = similarities[0].length;

        List<Pair> pairs = new ArrayList<Pair>(rows * cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (similarities[i][j] > 0.0d) {
                    pairs.add(new Pair(i, j, similarities[i][j]));
                }
            }
        }
        Collections.sort(pairs);

        boolean[] usedRows = new boolean[rows];
        boolean[] usedCols = new boolean[cols];
        double score = 0.0d;
        for (Pair pair : pairs) {
            if (usedRows[pair.row] || usedCols[pair.col]) {
                continue;
            }
            usedRows[pair.row] = true;
            usedCols[pair.col] = true;
            score += pair.similarity;
        }

        return score / Math.max(rows, cols);
    }

    /**
     * One possible pair of parts, ordered from the most similar one.
     */
    private static final class Pair implements Comparable<Pair> {

        private final int row;
        private final int col;
        private final double similarity;

        private Pair(int row, int col, double similarity) {
            this.row = row;
            this.col = col;
            this.similarity = similarity;
        }

        @Override
        public int compareTo(Pair other) {
            int result = Double.compare(other.similarity, similarity);
            if (result == 0) {
                result = Integer.compare(row, other.row);
            }
            if (result == 0) {
                result = Integer.compare(col, other.col);
            }
            return result;
        }

    }

}
