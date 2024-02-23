package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class kmeans {

    public static void main(String[] args) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("path/to/your/image.jpg"));
            BufferedImage kMeansImage = applyKMeans(originalImage, 16);
            ImageIO.write(kMeansImage, "jpg", new File("path/to/your/output.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage applyKMeans(BufferedImage originalImage, int k) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        int[] pixels = originalImage.getRGB(0, 0, w, h, null, 0, w);

        // Initialize clusters
        List<Cluster> clusters = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < k; i++) {
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            Cluster cluster = new Cluster(r, g, b);
            clusters.add(cluster);
        }

        boolean pixelChangedCluster;
        do {
            pixelChangedCluster = false;
            for (int pixel : pixels) {
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                Cluster closestCluster = null;
                double minDistance = Double.MAX_VALUE;
                for (Cluster cluster : clusters) {
                    double distance = cluster.calculateDistance(r, g, b);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestCluster = cluster;
                    }
                }
                if (closestCluster != null && closestCluster.addPixel(pixel)) {
                    pixelChangedCluster = true;
                }
            }

            for (Cluster cluster : clusters) {
                cluster.updateCluster();
            }
        } while (pixelChangedCluster);

        // Reconstruct the image with clustered colors
        BufferedImage kMeansImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixelIndex = y * w + x;
                int closestClusterIndex = -1;
                double minDistance = Double.MAX_VALUE;
                for (int i = 0; i < clusters.size(); i++) {
                    double distance = clusters.get(i).calculateDistance(pixels[pixelIndex]);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestClusterIndex = i;
                    }
                }
                kMeansImage.setRGB(x, y, clusters.get(closestClusterIndex).getRGB());
            }
        }
        return kMeansImage;
    }

    static class Cluster {
        int r, g, b;
        List<Integer> pixels = new ArrayList<>();

        Cluster(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        boolean addPixel(int color) {
            if (!pixels.contains(color)) {
                pixels.add(color);
                return true;
            }
            return false;
        }

        void updateCluster() {
            int sumR = 0, sumG = 0, sumB = 0;
            for (int pixel : pixels) {
                sumR += (pixel >> 16) & 0xFF;
                sumG += (pixel >> 8) & 0xFF;
                sumB += pixel & 0xFF;
            }
            int n = pixels.size();
            if (n > 0) {
                r = sumR / n;
                g = sumG / n;
                b = sumB / n;
            }
            pixels.clear();
        }

        double calculateDistance(int color) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            return Math.sqrt(Math.pow(this.r - r, 2) + Math.pow(this.g - g, 2) + Math.pow(this.b - b, 2));
        }

        double calculateDistance(int r, int g, int b) {
            return Math.sqrt(Math.pow(this.r - r, 2) + Math.pow(this.g - g, 2) + Math.pow(this.b - b, 2));
        }

        int getRGB() {
            return (r << 16) | (g << 8) | b;
        }
    }
}