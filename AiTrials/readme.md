# Docker file for image creation
```
# Note: Cuda 12.5 needs latest drivers, latest docker desktop and may be latest WSL updates
# Thus sticking to old version.
FROM nvidia/cuda:12.4.1-cudnn-devel-ubuntu22.04
RUN apt-get update && apt-get install -y software-properties-common
RUN apt-get update && apt-get install -y g++-11 make python3 python-is-python3 pip
RUN apt-get install -y git
RUN apt-get install -y vim
RUN pip install torch
RUN pip install NumPy
RUN pip install accelerate==0.21.0 peft==0.4.0 bitsandbytes==0.40.2 transformers==4.31.0 trl==0.4.7
```
### Command to create the image
```
docker build -f <dockerFile> -t python_dev:0.1 .
```
### Command to run the container
```
docker run -it --gpus device=0 -v <someLocalPath>:/data --name py python_dev:0.1 bash
```

# Test scripts
## cuda.py
This testscript runs a matrix multiplication first on CPU and then on CUDA.
Good starting point to check if the GPU is made available to the docker container.
### Sample output
```
root@e4e09d63ef7b:/# python cuda.py

Running on cpu
Time taken: 33.4591 seconds

Running on cuda
Time taken: 7.4175 seconds

Speedup factor (GPU/CPU): 4.51
```
