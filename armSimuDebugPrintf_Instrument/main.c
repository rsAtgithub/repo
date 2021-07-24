
#include "stdio.h"
#include "EventRecorder.h" 

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
		//EventRecord2 (1+EventLevelAPI, 1, 0); 
		foo();
		bar();
		while(1) {
		}
}
