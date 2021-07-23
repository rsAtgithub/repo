
#include "stdio.h"
#include "EventRecorder.h" 

int main() {
	EventRecorderInitialize (EventRecordAll, 1); 
	
		printf("Hello World!\n");
		printf("\nHello World Again\n!");
		EventRecord2 (1+EventLevelAPI, 1, 0); 
		printf("A");
		while(1) {
		}
}
