

volatile unsigned int *cycleCounter = (volatile unsigned int *)0x20000800;

int printf(const char * __restrict /*format*/, ...) __attribute__((no_instrument_function));

void __cyg_profile_func_enter(void *current_func, void *callsite) __attribute__((no_instrument_function));

void __cyg_profile_func_exit(void *current_func, void *callsite) __attribute__((no_instrument_function));

void __cyg_profile_func_enter(void *current_func, void *callsite) {
		printf("Entry,%08x,%d\n", (int)current_func,*(volatile unsigned int *)0x20000800);
		(void) callsite;
}

void __cyg_profile_func_exit(void *current_func, void *callsite) {
	printf("Exit,%08x,%d\n", (int)current_func,*(volatile unsigned int *)0x20000800);
	(void) callsite;
}
