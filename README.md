
## Install

To run this program with TornadoVM, you need to have a tornado installation in your machine.

See [INSTALL](https://github.com/beehive-lab/TornadoVM/blob/master/INSTALL.md) from TornadoVM


After the TornadoVM installation, you need to set the following variables in your system:


```bash
export JAVA_HOME=/path/to/Java-JVMCI-used-in-Tornado   ## Change this line
export TORNADO_ROOT=/path/to/tornado/root              ## Change this line
export PATH="${TORNADO_ROOT}/bin/bin/"
export TORNADO_SDK=${TORNADO_ROOT}/bin/sdk
```


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

