## Documentation for this project

### Conditional Instrumentation:
> It is important to determine which all files will need instrumentation (definitely user code and not the library :D)
> Thus only apply `-finstrument-functions` to selected files.

#### Entry and exit functions
When `-finstrument-functions` is applied to a file and for every function and exit, following two functions are called:
`__cyg_profile_func_enter` and `__cyg_profile_func_exit`.
Arguments for these two functions integers `current_func` to which call is placed and `callsite` which one called it. These are addresses.
Thus a post processing is needed for human readable format.

### Debug.ini explanation:
```C
signal void DWT_CYCCNT (void) {
  while (1) {
    // 0x20000800 is some RAM address, which is accessible in Simulator and mapped correctly.
    // rwatch waits till some code tries to read this specific address.
    // Once code tries to read, Keil interrupts the read operation of code, and executes this signal
    // function.
    // So, when LDR opcode is executed for 0x20000800, following two lines execute!! Superb!
    rwatch(0x20000800);
    // states is the instruction cycle counter for Simulator without any memory read overheads.
    _WWORD(0x20000800, states);
  }
}
 
DWT_CYCCNT()
```

### References:
> information about `states`: https://www.keil.com/support/man/docs/uv4/uv4_db_dbg_cpuregs.htm

> rwatch documentation: https://www.keil.com/support/man/docs/uv4/uv4_rwatch.htm

> Event recorder documentation: https://www.keil.com/pack/doc/compiler/EventRecorder/html/er_theory.html#simulation

> ARM options for instrumentation: https://developer.arm.com/documentation/ka004400/latest

> Tips for instrumentation: https://www.valtrix.in/programming/a-lightweight-function-profiler

