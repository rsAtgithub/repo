# Some trials
FROM nvidia/cuda:12.4.1-cudnn-devel-ubuntu22.04
RUN apt-get update && apt-get install -y software-properties-common
RUN apt-get update && apt-get install -y g++-11 make python3 python-is-python3 pip
RUN apt-get install -y git
RUN apt-get install -y vim
RUN pip install torch
RUN pip install NumPy
RUN pip install accelerate==0.21.0 peft==0.4.0 bitsandbytes==0.40.2 transformers==4.31.0 trl==0.4.7

# Cuda.py
This testscript runs a matrix multiplication first on CPU and then on CUDA.
