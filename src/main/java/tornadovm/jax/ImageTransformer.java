package tornadovm.jax;

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.annotations.Parallel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Run: tornado -cp target/tornadovm.jax-1.0-SNAPSHOT.jar tornadovm.jax.ImageTransformer
 */
public class ImageTransformer {

    private BufferedImage image;
    private static final boolean PARALLEL_COMPUTATION = Boolean.parseBoolean(System.getProperty("parallel", "False"));
    private static final String IMAGE_FILE = "/tmp/image.jpg";
    private static TaskSchedule tornadoTask;
    private static final int REPETITIONS = 25;

    private void loadImage(String imageFile) {
        try {
            image = ImageIO.read(new File(imageFile));
        } catch (IOException e) {
            throw new RuntimeException("Input file not found: " + imageFile);
        }
    }

    private static void compute(int[] image, final int w, final int s) {
        for (@Parallel int i = 0; i < w; i++) {
            for (@Parallel int j = 0; j < s; j++) {
                int rgb = image[i * s + j];
                int alpha = (rgb >> 24) & 0xff;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb & 0xFF);

                int grayLevel = (red + green + blue) / 3;
                int gray = (alpha << 24) | (grayLevel << 16) | (grayLevel << 8) | grayLevel;

                image[i * s + j] = gray;
            }
        }
    }

    private void writeImage(String fileName) {
        try {
            ImageIO.write(image, "jpg", new File("/tmp/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Input file not found: " + IMAGE_FILE);
        }
    }

    private void parallelComputation() {
        int w = image.getWidth();
        int s = image.getHeight();

        int[] imageRGB = new int[w * s];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < s; j++) {
                int rgb = image.getRGB(i, j);
                imageRGB[i * s + j] = rgb;
            }
        }

        if (tornadoTask == null) {
            tornadoTask = new TaskSchedule("s0");
            tornadoTask.streamIn(imageRGB).task("t0", ImageTransformer::compute, imageRGB, w, s).streamOut(imageRGB);

        }
        long taskStart = 0;
        long taskEnd = 0;
        for (int i  = 0; i < REPETITIONS; i++) {
            taskStart = System.nanoTime();
            tornadoTask.execute();
            taskEnd = System.nanoTime();
            double seconds = ((taskEnd - taskStart) * 1E-9);
            System.out.println("Task time: " + (taskEnd - taskStart) + " (ns) - " +  seconds  + " (s)");
        }

        // unmarshall
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < s; j++) {
                image.setRGB(i, j, imageRGB[i * s + j]);
            }
        }


        writeImage("parallel.jpg");
    }

    private void sequential(int w, int s) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < s; j++) {
                int rgb = image.getRGB(i, j);
                int alpha = (rgb >> 24) & 0xff;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb & 0xFF);
                int grayLevel = (red + green + blue) / 3;
                int gray = (alpha << 24) | (grayLevel << 16) | (grayLevel << 8) | grayLevel;
                image.setRGB(i, j, gray);
            }
        }
    }

    private void sequentialComputation() {
        int w = image.getWidth();
        int s = image.getHeight();
        long start = 0;
        long end = 0;
        for (int i  = 0; i < REPETITIONS; i++) {
            start = System.nanoTime();
            sequential(w, s);
            end = System.nanoTime();
            double seconds = ((end - start) * 1E-9);
            System.out.println("Total time: " + (end - start) + " (ns) - " + seconds  + " (s)");
        }

        writeImage("sequential.jpg");
    }

    public static void main(String[] args) {
        ImageTransformer image = new ImageTransformer();

        String imageFile = IMAGE_FILE;
        if (args.length > 0) {
            imageFile = args[0];
        }

        image.loadImage(imageFile);

        if (PARALLEL_COMPUTATION) {
            image.parallelComputation();
        } else {
            image.sequentialComputation();
        }
    }
}
