# Trial to fine-tune llama
```
Reference: https://youtu.be/Vg3dS-NLUT4?feature=shared
```

### Notes
##### Prompt template for llama2
```
<s> [INST] <<SYS>>
System prompt
<</SYS>>

User prompt [/INST] Model answer </s>
```
##### Dataset to use
- Orignal Dataset: https://huggingface.co/datasets/timdettmers/openassistant-guanaco
- Reformat Dataset following the Llama 2 template with 1k sample: https://huggingface.co/datasets/mlabonne/guanaco-llama2-1k
- Complete Reformat Dataset following the Llama 2 template: https://huggingface.co/datasets/mlabonne/guanaco-llama2

###### Example from openassistant-guanaco (original)
```
### Human: Compose a professional email with the following points: Me chinese cook 10 years Good good cook People love my food Can you hire me?
### Assistant: Thanks for the information. Unfortunately, your initial message does not contain enough details or structure to compose a compelling professional email to a potential employer. Here are some suggestions for how we can improve the message: 1) Let's first clarify the type of cooking position you are interested in. Are you looking for a job as a line cook, sous chef, executive chef, or something else? This will help determine how to emphasize relevant experience and skills. 2) What specific experience do you have? For example, "Over 10 years of experience preparing authentic Sichuan and Cantonese cuisine." Highlight any relevant skills, like knife skills, cooking techniques, menu development, etc. Mention the types of cuisine you specialize in. 3) Have you worked in a professional kitchen before? If so, mention the name and type of restaurant (upscale, casual dining, etc.). Emphasize relevant responsibilities and accomplishments. If not, that's fine too, just be clear that you're looking to transition into a professional kitchen and highlight related skills and passion for cooking. 4) Do you have any certifications or training? Note any culinary degrees, certificates, or other credentials. Even informal training can show your dedication to the craft. 5) Are you looking for full-time, part-time, temporary, or contract work? Be specific about the type of opportunity you're seeking. With the answers to these questions, we can craft an email in a standard business letter format. The opening can express your interest in the position and summarize your relevant experience and qualifications. The body can go into more depth on your experience, skills, and goals. The closing can reiterate your interest and mention that you'd welcome the opportunity to further discuss the role. Does this help? Let me know if you would like help crafting the full email.
I'm happy to assist you in composing a professional and compelling message to potential employers.
```
###### Example from openassistant-guanaco (guanaco-llama2-1k)
```
<s>[INST] Compose a professional email with the following points: Me chinese cook 10 years Good good cook People love my food Can you hire me?
[/INST] Thanks for the information. Unfortunately, your initial message does not contain enough details or structure to compose a compelling professional email to a potential employer. Here are some suggestions for how we can improve the message: 1) Let's first clarify the type of cooking position you are interested in. Are you looking for a job as a line cook, sous chef, executive chef, or something else? This will help determine how to emphasize relevant experience and skills. 2) What specific experience do you have? For example, "Over 10 years of experience preparing authentic Sichuan and Cantonese cuisine." Highlight any relevant skills, like knife skills, cooking techniques, menu development, etc. Mention the types of cuisine you specialize in. 3) Have you worked in a professional kitchen before? If so, mention the name and type of restaurant (upscale, casual dining, etc.). Emphasize relevant responsibilities and accomplishments. If not, that's fine too, just be clear that you're looking to transition into a professional kitchen and highlight related skills and passion for cooking. 4) Do you have any certifications or training? Note any culinary degrees, certificates, or other credentials. Even informal training can show your dedication to the craft. 5) Are you looking for full-time, part-time, temporary, or contract work? Be specific about the type of opportunity you're seeking. With the answers to these questions, we can craft an email in a standard business letter format. The opening can express your interest in the position and summarize your relevant experience and qualifications. The body can go into more depth on your experience, skills, and goals. The closing can reiterate your interest and mention that you'd welcome the opportunity to further discuss the role. Does this help? Let me know if you would like help crafting the full email.
I'm happy to assist you in composing a professional and compelling message to potential employers. </s>
```
 
> To know how this dataset was created, you can check this notebook.  
> https://colab.research.google.com/drive/1Ad7a9zMmkxuXTOh1Z7-rNSICA4dybpM2?usp=sharing

#### Extracted script
```Python

#**Step 2: Import All the Required Libraries**
import os
import torch
from datasets import load_dataset
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    BitsAndBytesConfig,
    HfArgumentParser,
    TrainingArguments,
    pipeline,
    logging,
)
from peft import LoraConfig, PeftModel
from trl import SFTTrainer

#**Step 3**

# The model that you want to train from the Hugging Face hub
model_name = "NousResearch/Llama-2-7b-chat-hf"

# The instruction dataset to use
dataset_name = "mlabonne/guanaco-llama2-1k"

# Fine-tuned model name
new_model = "Llama-2-7b-chat-finetune"

################################################################################
# QLoRA parameters
################################################################################

# LoRA attention dimension
lora_r = 64

# Alpha parameter for LoRA scaling
lora_alpha = 16

# Dropout probability for LoRA layers
lora_dropout = 0.1

################################################################################
# bitsandbytes parameters
################################################################################

# Activate 4-bit precision base model loading
use_4bit = True

# Compute dtype for 4-bit base models
bnb_4bit_compute_dtype = "float16"

# Quantization type (fp4 or nf4)
bnb_4bit_quant_type = "nf4"

# Activate nested quantization for 4-bit base models (double quantization)
use_nested_quant = False

################################################################################
# TrainingArguments parameters
################################################################################

# Output directory where the model predictions and checkpoints will be stored
output_dir = "./results"

# Number of training epochs
num_train_epochs = 1

# Enable fp16/bf16 training (set bf16 to True with an A100)
fp16 = False
bf16 = False

# Batch size per GPU for training
per_device_train_batch_size = 4

# Batch size per GPU for evaluation
per_device_eval_batch_size = 4

# Number of update steps to accumulate the gradients for
gradient_accumulation_steps = 1

# Enable gradient checkpointing
gradient_checkpointing = True

# Maximum gradient normal (gradient clipping)
max_grad_norm = 0.3

# Initial learning rate (AdamW optimizer)
learning_rate = 2e-4

# Weight decay to apply to all layers except bias/LayerNorm weights
weight_decay = 0.001

# Optimizer to use
optim = "paged_adamw_32bit"

# Learning rate schedule
lr_scheduler_type = "cosine"

# Number of training steps (overrides num_train_epochs)
max_steps = -1

# Ratio of steps for a linear warmup (from 0 to learning rate)
warmup_ratio = 0.03

# Group sequences into batches with same length
# Saves memory and speeds up training considerably
group_by_length = True

# Save checkpoint every X updates steps
save_steps = 0

# Log every X updates steps
logging_steps = 25

################################################################################
# SFT parameters
################################################################################

# Maximum sequence length to use
max_seq_length = None

# Pack multiple short examples in the same input sequence to increase efficiency
packing = False

# Load the entire model on the GPU 0
device_map = {"": 0}

#**Step 4:Load everything and start the fine-tuning process**

# Load dataset (you can process it here)
dataset = load_dataset(dataset_name, split="train")

# Load tokenizer and model with QLoRA configuration
compute_dtype = getattr(torch, bnb_4bit_compute_dtype)

bnb_config = BitsAndBytesConfig(
    load_in_4bit=use_4bit,
    bnb_4bit_quant_type=bnb_4bit_quant_type,
    bnb_4bit_compute_dtype=compute_dtype,
    bnb_4bit_use_double_quant=use_nested_quant,
)

# Check GPU compatibility with bfloat16
if compute_dtype == torch.float16 and use_4bit:
    major, _ = torch.cuda.get_device_capability()
    if major >= 8:
        print("=" * 80)
        print("Your GPU supports bfloat16: accelerate training with bf16=True")
        print("=" * 80)

# Load base model
model = AutoModelForCausalLM.from_pretrained(
    model_name,
    quantization_config=bnb_config,
    device_map=device_map
)
model.config.use_cache = False
model.config.pretraining_tp = 1

# Load LLaMA tokenizer
tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
tokenizer.pad_token = tokenizer.eos_token
tokenizer.padding_side = "right" # Fix weird overflow issue with fp16 training

# Load LoRA configuration
peft_config = LoraConfig(
    lora_alpha=lora_alpha,
    lora_dropout=lora_dropout,
    r=lora_r,
    bias="none",
    task_type="CAUSAL_LM",
)

# Set training parameters
training_arguments = TrainingArguments(
    output_dir=output_dir,
    num_train_epochs=num_train_epochs,
    per_device_train_batch_size=per_device_train_batch_size,
    gradient_accumulation_steps=gradient_accumulation_steps,
    optim=optim,
    save_steps=save_steps,
    logging_steps=logging_steps,
    learning_rate=learning_rate,
    weight_decay=weight_decay,
    fp16=fp16,
    bf16=bf16,
    max_grad_norm=max_grad_norm,
    max_steps=max_steps,
    warmup_ratio=warmup_ratio,
    group_by_length=group_by_length,
    lr_scheduler_type=lr_scheduler_type,
    report_to="tensorboard"
)

# Set supervised fine-tuning parameters
trainer = SFTTrainer(
    model=model,
    train_dataset=dataset,
    peft_config=peft_config,
    dataset_text_field="text",
    max_seq_length=max_seq_length,
    tokenizer=tokenizer,
    args=training_arguments,
    packing=packing,
)

# Train model
trainer.train()
# Save trained model
trainer.model.save_pretrained(new_model)

##**Step 5: Check the plots on tensorboard, as follows**

###**Step 6:Use the text generation pipeline to ask questions like “What is a large language model?” Note that I’m formatting the input to match Llama 2 prompt template.**

logging.set_verbosity(logging.CRITICAL)

# Run text generation pipeline with our next model
prompt = "What is a large language model?"
pipe = pipeline(task="text-generation", model=model, tokenizer=tokenizer, max_length=200)
result = pipe(f"<s>[INST] {prompt} [/INST]")
print(result[0]['generated_text'])

# Empty VRAM
del model
del pipe
del trainer
import gc
gc.collect()
gc.collect()

#**Step 7: Store New Llama2 Model (Llama-2-7b-chat-finetune)**

# Reload model in FP16 and merge it with LoRA weights
base_model = AutoModelForCausalLM.from_pretrained(
    model_name,
    low_cpu_mem_usage=True,
    return_dict=True,
    torch_dtype=torch.float16,
    device_map=device_map,
)
model = PeftModel.from_pretrained(base_model, new_model)
model = model.merge_and_unload()

# Reload tokenizer to save it
tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
tokenizer.pad_token = tokenizer.eos_token
tokenizer.padding_side = "right"



```
