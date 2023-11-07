package edu.unh.cs.cs619.bulletzone.repository;

import java.util.Random;

public class Noise{
    int rows;
    int cols;
    int min;
    int max;
    int seed;

    double[][] data;

    public Noise(int rows, int cols, int min, int max, int seed){
        this.rows = rows;
        this.cols = cols;
        this.min = min;
        this.max = max;
        this.seed = seed;
        this.data = new double[rows][cols];
    }

    public Noise(int rows, int cols, int min, int max){
        this.rows = rows;
        this.cols = cols;
        this.min = min;
        this.max = max;
        this.seed = new Random().nextInt();
        this.data = new double[rows][cols];
    }

    public double[][] generate(){
        Random generator = new Random(seed);

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                data[i][j] = generator.nextDouble() * (max - min) + min;
            }
        }
        return data;
    }

    /**
     * Smooths the noise by averaging the values of the surrounding cells
     * @param smoothness the number of times to smooth
     */
    public double[][] smooth(int smoothness){
        for (int i = 0; i < smoothness; i++){
            for (int j = 0; j < data.length; j++){
                for (int k = 0; k < data[0].length; k++){
                    double sum = 0;
                    int count = 0;
                    for (int l = -1; l <= 1; l++){
                        for (int m = -1; m <= 1; m++){
                            if (j + l >= 0 && j + l < data.length && k + m >= 0 && k + m < data[0].length){ // if in bounds
                                sum += data[j + l][k + m];
                                count++;
                            }
                        }
                    }
                    data[j][k] = sum / count;
                }
            }
        }
        return data;
    }

    /**
     * Adds peaks and troughs to the noise based on the min and max values
     * @param fluctuation the amount of fluctuation (higher is more fluctuation)
     * @return the fluctuated noise
     */
    public double[][] fluctuate(double fluctuation){
        Random generator = new Random(seed);
        for (int i = 0; i < data.length; i++){
            for (int j = 0; j < data[0].length; j++){
                if (data[i][j] < (max + min) / 2){
                    data[i][j] -= generator.nextDouble() * fluctuation;
                    data[i][j] = clamp(data[i][j], min, max);
                } else {
                    data[i][j] += generator.nextDouble() * fluctuation;
                    data[i][j] = clamp(data[i][j], min, max);
                }
            }
        }
        return data;
    }

    public double[][] changeHeight(double height){
        for (int i = 0; i < data.length; i++){
            for (int j = 0; j < data[0].length; j++){
                data[i][j] += height;
                data[i][j] = clamp(data[i][j], min, max);
            }
        }
        return data;
    }

    public double[][] raiseFloor(double height){
        min += height;
        for (int i = 0; i < data.length; i++){
            for (int j = 0; j < data[0].length; j++){
                if (data[i][j] < (max + min) / 2){
                    data[i][j] = clamp(data[i][j], min, max);
                }
            }
        }
        return data;
    }

    public static double clamp(double value, double min, double max){
        if (value < min){
            return min;
        } else if (value > max){
            return max;
        } else {
            return value;
        }
    }
}