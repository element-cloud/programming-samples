#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define size 100

int a1[size][size],a2[size][size],res[size][size];

//void mul(int a1[m][n],int a2[n][l],int res[m][l],int m,int n,int l){

void InitializeMatrix(int a[][size],int m,int n){
  int i,j;
  for(i=0;i<m;i=i+1){
    for(j=0;j<n;j=j+1){
      scanf("%d",&a[i][j]);
      //printf("a[%d][%d]=%d\n",i,j,a1[i][j]);
    }
  }
}

void mul(int a1[][size],int a2[][size],int res[][size],int m,int n,int l){
  int i,j,k;
  for(i=0;i<m;i=i+1){
    for(j=0;j<l;j=j+1){
      for(k=0;k<n;k=k+1){
        res[i][j]=res[i][j]+a1[i][k]*a2[k][j];
        //printf("res[%d][%d]=%d a1[%d][%d]=%d a2[%d][%d]=%d i=%d j=%d k=%d\n",i,j,res[i][j],i,k,a1[i][k],k,j,a2[k][j],i,j,k);
      }
    }
  }
}

void add(int a1[][size],int a2[][size],int res[][size],int m,int n){
  int i,j;
  for(i=0;i<m;i=i+1){
    for(j=0;j<n;j=j+1){
      res[i][j]=a1[i][j]+a2[i][j];
      //printf("res[%d][%d]=%d a1[%d][%d]=%d a2[%d][%d]=%d i=%d j=%d k=%d\n",i,j,res[i][j],i,k,a1[i][k],k,j,a2[k][j],i,j,k);
    }
  }
}

void sub(int a1[][size],int a2[][size],int res[][size],int m,int n){
  int i,j;
  for(i=0;i<m;i=i+1){
    for(j=0;j<n;j=j+1){
      res[i][j]=a1[i][j]-a2[i][j];
      //printf("res[%d][%d]=%d a1[%d][%d]=%d a2[%d][%d]=%d i=%d j=%d k=%d\n",i,j,res[i][j],i,k,a1[i][k],k,j,a2[k][j],i,j,k);
    }
  }
}


int main(void){
  int i,j,mm,nn,ll;
  char s[4];
  printf("What operations do you want to do?\n");
  printf("Choose add (Addition), sub (Substraction), or mul (Multiplication): ");
  scanf("%s",&s);
  /*for(i=0;i<5;i=i+1){
    printf("%d ",s[i]);
  }*/
  /*printf("%s",s);
  printf("\n");*/
  if(strcmp(s,"add")!=0&&strcmp(s,"sub")!=0&&strcmp(s,"mul")!=0){
    printf("Choose add, sub, or mul. Please do again!\n");
    exit(-1);
  }
  if(strcmp(s,"mul")==0){
    printf("m:");
    scanf("%d",&mm);
    printf("n:");
    scanf("%d",&nn);
    printf("l:");
    scanf("%d",&ll);

    printf("Initialize matrix1\n");
    InitializeMatrix(a1,mm,nn);

    printf("Initialize matrix2\n");
    InitializeMatrix(a2,nn,ll);

    mul(a1,a2,res,mm,nn,ll);

    printf("result:\n");
    for(i=0;i<mm;i=i+1){
      for(j=0;j<ll;j=j+1){
        printf("%d ",res[i][j]);
      }
      printf("\n");
    }
  }
  else{
    printf("m:");
    scanf("%d",&mm);
    printf("n:");
    scanf("%d",&nn);

    printf("Initialize matrix1\n");
    InitializeMatrix(a1,mm,nn);

    printf("Initialize matrix2\n");
    InitializeMatrix(a2,mm,nn);

    if(strcmp(s,"add")==0){
      add(a1,a2,res,mm,nn);
    }
    else{
      sub(a1,a2,res,mm,nn);
    }
    printf("result:\n");
    for(i=0;i<mm;i=i+1){
      for(j=0;j<nn;j=j+1){
        printf("%d ",res[i][j]);
      }
      printf("\n");
    }
  }

  /*printf("a1\n");
    for(i=0;i<m;i=i+1){
      for(j=0;j<n;j=j+1){
        printf("%d ",a1[i][j]);
      }
      printf("\n");
    }

  printf("a2\n");
  for(i=0;i<n;i=i+1){
    for(j=0;j<l;j=j+1){
      printf("%d ",a2[i][j]);
    }
    printf("\n");
  }*/


  return 0;
}