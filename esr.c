#include <stdio.h>
#include <stdlib.h>

int main(void)
{
  int i=0,x,number[100],j=0,saveNumber,savei,k=1,num,save=0,l,count=0,n,saveSave=0;
  double ans=0,m=0,dp=0;
  for(j=0;j<100;j=j+1){
    number[j]=0;
  }
  printf("x=");
  scanf("%d",&x);
  printf("\n");
  if(x<0){
    printf("x should be more than 0");
    exit(1);
  }
  while(x!=0){
    number[i]=x%10;
    i=i+1;
    x=x/10;
  }
  savei=i;
  j=0;
  while(j!=i/2){
    saveNumber=number[j];
    number[j]=number[savei-1-j];
    number[savei-1-j]=saveNumber;
    j=j+1;
  }
  if(i%2==1){
    num=number[0];
    count=1;
  }
  else{
    num=number[0]*10+number[1];
    count=2;
  }
  while(num!=0&&save%10==0&&save>=0&&save>=saveSave){
    saveSave=save;
    m=0;
    for(l=0;l<=10;l=l+1){
      if(num-save*l<0){
        save=save-1;
        l=l-1;
        break;
      }
      save=save+1;
      m=m+1;
    }
    if(l<0){
      break;
    }
    num=(num-save*l)*100+number[count]*10+number[count+1];
    count=count+2;
    save=save+l;
    save=save*10;
    if(count<=i+2){
      ans=ans*10+m-1;
    }
    else{
      dp=m-1;
      for(n=0;n<k;n=n+1){
        dp=dp/10;
      }
      ans=ans+dp;
      k=k+1;
    }
    if(num==0&&count<=i){
      while(count<=i){
        printf("root x=%10.8f\n",ans);
        ans=ans*10;
        count=count+2;
      }
      printf("root x=%10.8f\n",ans);
      break;
    }
    printf("root x=%10.8f\n",ans);
  }
    printf("\nroot x=%10.8f\n",ans);
  return 0;
}
