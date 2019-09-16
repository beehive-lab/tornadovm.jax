package tornadovm.jax;

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.annotations.Reduce;

import java.util.stream.IntStream;

/**
 * An example to play with TornadoVM (not intended for acceleration, since operations are too simple).
 *
 * Run:
 * $ tornado --printKernel -cp target/tornadovm.jax-1.0-SNAPSHOT.jar tornadovm.jax.MapReduceSample
 *
 */
public class MapReduceSample {

    private TaskSchedule ts;
    private static final int SIZE = 512;

    private static void map(float[] a,float[] b,float[] c) {
        for (@Parallel int i = 0; i < c.length; i++) {
            c[i] = a[i] + b[i];
        }
    }

    private static void reduce(float[] input,@Reduce float[] out) {
        for (@Parallel int i = 0; i < input.length; i++) {
            out[0] += input[i];
        }
    }

    private void compute(float[] a,float[] b,float[] c,float[] output) {
        ts = new TaskSchedule("s0")
                .task("map", MapReduceSample::map,a,b,c)
                .task("reduce", MapReduceSample::reduce,c,output)
                .streamOut(output);
        ts.execute();
    }

    public static void main(String[] args) {

        float[] a = new float[SIZE];
        float[] b = new float[SIZE];
        float[] c = new float[SIZE];
        float[] result = new float[1];

        IntStream.range(0, SIZE).forEach(i -> {
            a[i] = 2;
            b[i] = 2;
        });

        new MapReduceSample().compute(a, b, c, result);

        System.out.println("Result: " + result[0]);
    }
}
