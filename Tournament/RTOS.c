

/*
	Uses task struct and gcd of task periods to execute two tasks with different periods using the same timer.
	The tasks are the 3 led and blinking led tasks from before.
*/

#include "rims.h"


/*This code will be shared between state machines.*/
typedef struct task {
   int state;
   unsigned long period;
   unsigned long elapsedTime;
   int (*TickFct)(int);
} task;

task tasks[2];

int j=0;

const unsigned char tasksNum = 2;
const unsigned long periodBlinkLed = 1500;
const unsigned long periodThreeLeds = 500;

const unsigned long tasksPeriodGCD = 500;

int TickFct_BlinkLed(int state);
int TickFct_ThreeLeds(int state);

unsigned char processingRdyTasks = 0;

void TimerISR() {

   printf("%i\n",j++);
   unsigned char i;
   if (processingRdyTasks) {
      printf("Period too short to complete tasks\n");
   }
   processingRdyTasks = 1;
   for (i = 0; i < tasksNum; ++i) { // Heart of scheduler code
      if ( tasks[i].elapsedTime >= tasks[i].period ) { // Ready
         tasks[i].state = tasks[i].TickFct(tasks[i].state);
         tasks[i].elapsedTime = 0;
      }
      tasks[i].elapsedTime += tasksPeriodGCD;
   }
   processingRdyTasks = 0;
}

int main() {
   // Priority assigned to lower position tasks in array
   unsigned char i=0;
   tasks[i].state = -1;
   tasks[i].period = periodBlinkLed;
   tasks[i].elapsedTime = tasks[i].period;
   tasks[i].TickFct = &TickFct_BlinkLed;

   ++i;
   tasks[i].state = -1;
   tasks[i].period = periodThreeLeds;
   tasks[i].elapsedTime = tasks[i].period;
   tasks[i].TickFct = &TickFct_ThreeLeds;

   ++i;
   TimerSet(tasksPeriodGCD);
   TimerOn();
   
   while(1) { Sleep(); }

   return 0;
}

enum BL_States { BL_LedOff, BL_LedOn } BL_State;
int TickFct_BlinkLed(int state) {
   /*VARIABLES MUST BE DECLARED STATIC*/
/*e.g., static int x = 0;*/
/*Define user variables for this state machine here. No functions; make them global.*/
   switch(state) { // Transitions
      case -1:
         state = BL_LedOff;
         break;
      case BL_LedOff:
            state = BL_LedOn;
        
         break;
      case BL_LedOn:
            state = BL_LedOff;
         break;
      default:
         state = -1;
      } // Transitions

   switch(state) { // State actions
      case BL_LedOff:
         B0=0;
         break;
      case BL_LedOn:
         B0=1;
         break;
      default: // ADD default behaviour below
         break;
   } // State actions
   BL_State = state;
   return state;
}


enum TL_States { TL_T0, TL_T1, TL_T2 } TL_State;
int TickFct_ThreeLeds(int state) {
   /*VARIABLES MUST BE DECLARED STATIC*/
/*e.g., static int x = 0;*/
/*Define user variables for this state machine here. No functions; make them global.*/
   switch(state) { // Transitions
      case -1:
         state = TL_T0;
         break;
      case TL_T0:
            state = TL_T1;
        
         break;
      case TL_T1:
            state = TL_T2;
        
         break;
      case TL_T2:
            state = TL_T0;
        
         break;
      default:
         state = -1;
      } // Transitions

   switch(state) { // State actions
      case TL_T0:
         B5=1;

         B6=0;

         B7=0;
         break;
      case TL_T1:
         B5=0;

         B6=1;

         B7=0;
         break;
      case TL_T2:
         B5=0;

         B6=0;

         B7=1;
         break;
      default: // ADD default behaviour below
         break;
   } // State actions
   TL_State = state;
   return state;
}

