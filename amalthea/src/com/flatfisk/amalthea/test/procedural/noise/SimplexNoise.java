package com.flatfisk.amalthea.test.procedural.noise;

import java.util.Random;

/*
 * A speed-improved simplex noise algorithm for 2D, 3D and 4D in Java.
 *
 * Based on example code by Stefan Gustavson (stegu@itn.liu.se).
 * Optimisations by Peter Eastman (peastman@drizzle.stanford.edu).
 * Better rank ordering method by Stefan Gustavson in 2012.
 *
 * This could be speeded up even further, but it's useful as it is.
 *
 * Version 2012-03-09
 *
 * This code was placed in the public domain by its original author,
 * Stefan Gustavson. You may use it as you see fit, but
 * attribution is appreciated.
 *
 */

public class SimplexNoise {

    SimplexNoise_octave[] octaves;
    double[] frequencys;
    double[] amplitudes;

    double persistence;
    double scale;
    double allAmplitudes = 0f;
    int seed;


    /**
     * @param numberOfOctaves Number of frequencies we generate noise for
     * @param persistence     a high value gives higher frequencies high amplitude
     * @param scale           the scale to be applied to the x and y values
     * @param seed            the random seed to use
     */
    public SimplexNoise(int numberOfOctaves, double persistence, double scale, int seed) {
        this.persistence = persistence;
        this.seed = seed;
        this.scale = scale;

        //recieves a number (eg 128) and calculates what power of 2 it is (eg 2^7)
        System.out.println(numberOfOctaves);

        octaves = new SimplexNoise_octave[numberOfOctaves];
        frequencys = new double[numberOfOctaves];
        amplitudes = new double[numberOfOctaves];

        Random rnd = new Random(seed);

        for (int i = 0; i < numberOfOctaves; i++) {
            octaves[i] = new SimplexNoise_octave(rnd.nextInt());

            frequencys[i] = Math.pow(2, i);
            System.out.println("F=" + frequencys[i] + " " + i);
            amplitudes[i] = (Math.pow(persistence, octaves.length - i) / 2);
            allAmplitudes += amplitudes[i];
            System.out.println("A=" + amplitudes[i] + " " + i);
        }

    }

    public double getNoise(double x, double y) {
        double result = 0;
        x *= scale;
        y *= scale;
        for (int i = 0; i < octaves.length; i++) {
            result = result + octaves[i].noise(x / frequencys[i], y / frequencys[i]) * amplitudes[i];
        }
        result /= allAmplitudes;
        result += 1;
        result /= 2;
        return result;
    }

    public double getNoise(int x, int y, int z) {
        double result = 0;

        for (int i = 0; i < octaves.length; i++) {
            double frequency = Math.pow(2, i);
            double amplitude = Math.pow(persistence, octaves.length - i);
            result = result + octaves[i].noise(x / frequency, y / frequency, z / frequency) * amplitude;
        }
        return result;
    }

    public double getSingleNoise(int x, int y, int z) {
        double result = 0;
        SimplexNoise_octave myoctave = new SimplexNoise_octave(0);
        result = myoctave.noise(x, y, z);
        return result;
    }

    public double getDoubleNoise(int x, int y, int z) {
        double result = 0;
        SimplexNoise_octave myoctave = new SimplexNoise_octave(0);
        SimplexNoise_octave myoctave2 = new SimplexNoise_octave(1);
        result = (myoctave.noise(x, y, z) +
                (myoctave2.noise(x, y, z) / 2)
        );
        result = result / 2;
        return result;
    }

    public double getQuadNoise(int x, int y, int z) {
        double result = 0;
        SimplexNoise_octave myoctave = new SimplexNoise_octave(0);
        SimplexNoise_octave myoctave2 = new SimplexNoise_octave(10);
        SimplexNoise_octave myoctave3 = new SimplexNoise_octave(20);
        SimplexNoise_octave myoctave4 = new SimplexNoise_octave(40);
        result = (myoctave.noise(x, y, z) +
                (myoctave2.noise(x, y, z) / 2) +
                (myoctave3.noise(x, y, z) / 4) +
                (myoctave4.noise(x, y, z) / 8)
        );
        result = result / 4;
        return result;
    }

    public double getOctNoise(int x, int y, int z) {
        double result = 0;
        SimplexNoise_octave myoctave = new SimplexNoise_octave(0);
        SimplexNoise_octave myoctave2 = new SimplexNoise_octave(10);
        SimplexNoise_octave myoctave3 = new SimplexNoise_octave(20);
        SimplexNoise_octave myoctave4 = new SimplexNoise_octave(40);
        SimplexNoise_octave myoctave5 = new SimplexNoise_octave(80);
        SimplexNoise_octave myoctave6 = new SimplexNoise_octave(100);
        SimplexNoise_octave myoctave7 = new SimplexNoise_octave(120);
        SimplexNoise_octave myoctave8 = new SimplexNoise_octave(140);
        result = (myoctave.noise(x, y, z) +
                (myoctave2.noise(x, y, z) / 2) +
                (myoctave3.noise(x, y, z) / 4) +
                (myoctave4.noise(x, y, z) / 8) +
                (myoctave5.noise(x, y, z) / 16) +
                (myoctave6.noise(x, y, z) / 32) +
                (myoctave7.noise(x, y, z) / 64) +
                (myoctave8.noise(x, y, z) / 128)
        );
        result = result / 8;
        return result;
    }
}