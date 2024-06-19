#https://stackoverflow.com/q/76156557

import torch
import time

def measure_time(device, size=1024*4):
    print(f"Running on {device}")
    
    # Create random matrices
    a = torch.randn(size, size, device=device)
    b = torch.randn(size, size, device=device)
    
    # Warm up the device
    for _ in range(10):
        c = torch.matmul(a, b)

    torch.cuda.synchronize() if device.type == 'cuda' else None

    # Measure the time taken for 1000 iterations
    start_time = time.time()
    for _ in range(100):
        c = torch.matmul(a, b)
    
    torch.cuda.synchronize() if device.type == 'cuda' else None
    end_time = time.time()
    
    elapsed_time = end_time - start_time
    print(f"Time taken: {elapsed_time:.4f} seconds\n")
    return elapsed_time

def main():
    # Check for the availability of GPU
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

    # Perform the computation on CPU
    cpu_time = measure_time(torch.device('cpu'))

    # Perform the computation on GPU if available
    if device.type == 'cuda':
        gpu_time = measure_time(device)
        print(f"Speedup factor (GPU/CPU): {cpu_time / gpu_time:.2f}")
    else:
        print("GPU not available.")

main()
