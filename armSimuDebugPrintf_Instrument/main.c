
#include "stdio.h"
#include "EventRecorder.h" 

/* DWT (Data Watchpoint and Trace) registers, only exists on ARM Cortex with a DWT unit */

/*!< DWT Control register */
#define KIN1_DWT_CONTROL             (*((volatile uint32_t*)0xE0001000))

/*!< CYCCNTENA bit in DWT_CONTROL register */
#define KIN1_DWT_CYCCNTENA_BIT       (1UL<<0)

/*!< DWT Cycle Counter register */
#define KIN1_DWT_CYCCNT              (*((volatile uint32_t*)0xE0001004))

/*!< DEMCR: Debug Exception and Monitor Control Register */
#define KIN1_DEMCR                   (*((volatile uint32_t*)0xE000EDFC))

/*!< Trace enable bit in DEMCR register */
#define KIN1_TRCENA_BIT              (1UL<<24)

/*!< TRCENA: Enable trace and debug block DEMCR (Debug Exception and Monitor Control Register */
#define KIN1_InitCycleCounter() \
  KIN1_DEMCR |= KIN1_TRCENA_BIT

/*!< Reset cycle counter */
#define KIN1_ResetCycleCounter() \
  KIN1_DWT_CYCCNT = 0

/*!< Enable cycle counter */
#define KIN1_EnableCycleCounter() \
  KIN1_DWT_CONTROL |= KIN1_DWT_CYCCNTENA_BIT

/*!< Disable cycle counter */
#define KIN1_DisableCycleCounter() \
  KIN1_DWT_CONTROL &= ~KIN1_DWT_CYCCNTENA_BIT

/*!< Read cycle counter register */
#define KIN1_GetCycleCounter() \
  KIN1_DWT_CYCCNT


void bar(void) {
	printf("in bar\n");
}

void foo(void) {
	printf("in foo\n");
	bar();
}

int main() {
	  EventRecorderInitialize (EventRecordAll, 1); 
		KIN1_InitCycleCounter(); /* enable DWT hardware */
		KIN1_ResetCycleCounter(); /* reset cycle counter */
		KIN1_EnableCycleCounter(); /* start counting */
	
		printf("Hello World!\n");
		printf("\nHello World Again: %08x\n!", KIN1_GetCycleCounter());
		//EventRecord2 (1+EventLevelAPI, 1, 0); 
		foo();
		while(1) {
		}
}
