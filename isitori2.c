#include <stdio.h>

void game(int num){
  int i,n,take;
  char turn;
  if(num%2==0){
    turn='c';
    printf("Now, let me start.\n");
  }
  else{
    turn='h';
    printf("You start.\n");
  }

  while(num>0){
    printf("The number of stones is %d.\n",num);
    if(turn=='c'){
      switch(num%4){
      case 0:
        take=3;
        break;
      case 2:
        take=1;
        break;
      case 3:
        take=2;
        break;
      }
      printf("Take %d stones.\n\n",take);
      num=num-take;
      turn='h';
    }
    else{
      printf("What numbers do you take stones ? (1~3) ");
      scanf("%d",&n);
      if(1<=n&&n<=3){
        num=num-n;
      }
      else{
        printf("Please enter a number from 1 to 3.\n");
      }
      printf("\n");
      turn='c';
      if(n<1||n>3){
        turn='h';
      }
    }
  }
  if(num==0&&turn=='h'){
    printf("You wins!\n");
  }
  if(num<0&&turn=='c'){
    printf("The number of stones is %d.\n",num);
    printf("You lost by foul play.\n");
  }
  if(num==0&&turn=='c'){
    printf("CPU wins!\n");
  }
  if(num<0&&turn=='h'){
    printf("The number of stones is %d.\n",num);
    printf("CPU lost by foul play.\n");
  }
}

int main(void){
  int num;
  num=0;
  while(num<10){
    printf("Please enter the number of stones (greater than or equal to 10): ");
    scanf("%d",&num);
    if(num<10){
      printf("\nPlease enter a number greater than or equal to 10.\n");
    }
  }
  game(num);
  return 0;
}