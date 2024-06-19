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
