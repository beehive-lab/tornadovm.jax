

## Compile and run


```bash
$ mvn clean package
$ tornado -Dparallel=True -cp target/tornadovm.jax-1.0-SNAPSHOT.jar tornadovm.jax.ImageTransformer /path/to/image
```


Run the sequential code

```bash
$ tornado -Dparallel=False -cp target/tornadovm.jax-1.0-SNAPSHOT.jar tornadovm.jax.ImageTransformer /path/to/imag
```


Print the OpenCL generated kernel:

```bash
$ tornado --printKernel -Dparallel=True -cp target/tornadovm.jax-1.0-SNAPSHOT.jar tornadovm.jax.ImageTransformer
```


Use debug information to track devices:

```bash
$ tornado --printKernel --debug -Dparallel=True -cp target/tornadovm.jax-1.0-SNAPSHOT.jar tornadovm.jax.ImageTransformer
```

