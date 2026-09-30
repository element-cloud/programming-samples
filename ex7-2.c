#include <stdio.h>
#include <stdlib.h>

#define STACK_SIZE 100
int stack[STACK_SIZE];
int sp,over=0,error=0,c;

void initialize_stack(void)
{
  sp=-1;
}

void push(int x)
{
  if(sp<STACK_SIZE-1){
    stack[++sp]=x;
  }
  else{
    printf("入力する数字を%d個まで減らしてください。\n",STACK_SIZE);
    error=1;
    while(c!='\n'){
      c=getchar();
    }
    initialize_stack();
  }
}

int pop(void)
{
  if(sp>=0){
    return stack[sp--];
  }
  else{
    printf("1 2 + 3 - のように後置式の形で入力してください。\n");
    error=1;
    while(c!='\n'){
      c=getchar();
    }
  }
}

int overflow(int p1,int p2)
{
  int savep1=p1,savep2=p2,i=0,sum=0;
  if(p1<0){
    p1=-savep1;
  }
  if(p2<0){
    p2=-savep2;
  }
  for(i=0;i<p2;i=i+1){
    sum=sum+p1;
    if(sum<0){
      printf("おーばーふろー\n");
      error=1;
      while(c!='\n'){
	c=getchar();
      }
      return 1;
    }
  }
  return 0;
}


typedef enum {NUMBER,PLUS,MINUS,MULT,DIV,REM,OVER,OTHER,END} ttyp;
ttyp token;

int num;

ttyp get_token(void)
{
  while(c==' '||c=='\n'||c=='\t'){
    c=getchar();
  }
  switch(c){
  case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8':  case '9':{
    num=c-'0';
    while(1){
      c=getchar();
      if('0'<=c&&c<='9'){
	      num=10*num+c-'0';
	      if(num<0){
	        printf("おーばーふろー\n");
	        error=1;
	        while(c!='\n'){
	          c=getchar();
	        }
	        return OVER;
	      }
      }
      else{
        break;
      }
    }
    return NUMBER;
  }
  case '+':{
    c=getchar();
    return PLUS;
  }
  case '-':{
    c=getchar();
    return MINUS;
  }
  case '*':{
    c=getchar();
    return MULT;
  }
  case '/':{
    c=getchar();
    return DIV;
  }
  case '%':{
    c=getchar();
    return REM;
  }
  case EOF:{
    return END;
  }
  default:{
    c=getchar();
    return OTHER;
  }
  }
}

int main(void)
{
  int x=1;
  c=getchar();
  token=get_token();
  initialize_stack();
  while(token!=END){
    x=0;
    error=0;
    switch(token){
    case NUMBER:{
      if(error==1){
        break;
      }
      push(num);
      break;
    }
    case PLUS:{
      int p1=pop();
      if(error==1){
        break;
      }
      int p2=pop();
      if(error==1){
        break;
      }
      if(overflow(p1,p2)==1){
        break;
      }
      push(p2+p1);
      error=0;
      break;
    }
    case MINUS:{
      int p1=pop();
      if(error==1){
        break;
      }
      int p2=pop();
      if(error==1){
        break;
      }
      if(overflow(p1,p2)==1){
        break;
      }
      push(p2-p1);
      error=0;
      break;
    }
    case MULT:{
      int p1=pop();
      if(error==1){
        break;
      }
      int p2=pop();
      if(error==1){
        break;
      }
      if(overflow(p1,p2)==1){
        break;
      }
      push(p2*p1);
      error=0;
      break;
    }
    case DIV:{
      int p1=pop();
      if(error==1){
        break;
      }
      int p2=pop();
      if(error==1){
        break;
      }
      if(p1==0){
        printf("0で割っています。\n");
        error=1;
        break;
      }
      if(overflow(p1,p2)==1){
        break;
      }
      if(p1==-1&&p2==-2147483648){
        printf("おーばーふろー\n");
        error=1;
        while(c!='\n'){
          c=getchar();
        }
        break;
      }
      push(p2/p1);
      error=0;
      break;
    }
    case REM:{
      int p1=pop();
      if(error==1){
        break;
      }
      int p2=pop();
      if(error==1){
        break;
      }
      if(p1==0){
        printf("0で割っています。\n");
        error=1;
        break;
      }
      if(overflow(p1,p2)==1){
        break;
      }
      if(p1==-1&&p2==-2147483648){
        printf("おーばーふろー\n");
        error=1;
        while(c!='\n'){
          c=getchar();
        }
        break;
      }
      push(p2%p1);
      error=0;
      break;
    }
    case OVER:{
      error=1;
      break;
    }
    default:{
      printf("使える文字は数字 + - * / %のみです。\n");
      error=1;
      while(c!='\n'){
        c=getchar();
      }
      initialize_stack();
    }
    }
    if(sp==0&&c=='\n'&&error==0){
      printf("%10d\n",pop());
      x=1;
    }
    if(sp<0&&x!=1&&error==0){
      printf("数字 + - * / %を使った後置式を入力してください。\n");
      while(c!='\n'){
        c=getchar();
      }
      error=1;
      initialize_stack();
    }
    if(token!=END){
      c=getchar();
    }
    token=get_token();
  }
  return 0;
}
