#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define M 1
#define TABLE_SIZE 200
#define MAX_CHAR 10000

struct cell{
  char *id;
  int info;
  struct cell *next;
};
struct cell *hashtable[M];

struct cell table[TABLE_SIZE];
int next_cell;

struct cell *deleted_address[TABLE_SIZE];
int next_address=0;

int char_top=0;
char char_heap[MAX_CHAR];

char *save_string(char *s)
{
  int result;
  result=char_top;
  while(1){
    if(char_top>=MAX_CHAR){
      printf("string buffer overflow\n");
      exit(1);
    }
    char_heap[char_top]=*s;
    char_top++;
    if(*s==0){
      break;
    }
    s++;
  }
  return (&char_heap[result]);
}

int hash(char *v)
{
  int x;
  x=0;
  while(*v!=0){
    x=256*x+(*v++);
  }
  x=x%M;
  if(x<0){
    x=-x;
  }
  return x;
}

void initialize_table(void)
{
  int i;
  for(i=0;i<M;i=i+1){
    hashtable[i]=NULL;
  }
  next_cell=0;
}

struct cell *new_cell(void)
{
  struct cell *p;
  if(next_cell>=TABLE_SIZE){
    printf("table overflow\n");
    exit(1);
  }
  p=&table[next_cell];
  next_cell++;
  return p;
}

void print_table(void)
{
  int i;
  struct cell *p;
  for(i=0;i<M;i=i+1){
    p=hashtable[i];
    while(p!=NULL){
      printf("%s %d i=%d p=%p\n",p->id,p->info,i,p);
      p=p->next;
    }
  }
}

void print_address(struct cell *p)
{
  int i;
  struct cell *q;
  printf("\n");
  for(i=next_address;i>=0;i=i-1){
    printf("deleted_address[%d]=%p\n",i,deleted_address[i]);
  }
  printf("\n");
}

void insert_table(char *id,int info)
{
  struct cell *p;
  //printf("id=%d\n",hash(id));
  printf("insert before:\n");
  print_table();
  p=NULL;
  if(next_address>0){
    if(deleted_address[next_address-1]!=NULL){
      p=deleted_address[next_address-1];
      next_address=next_address-1;
      print_address(p);
      if(next_address==0){
        deleted_address[next_address]=NULL;
      }
    }
  }
  if(p==NULL){
    p=new_cell();
  }
  p->id=save_string(id);
  p->info=info;
  p->next=hashtable[hash(id)];
  hashtable[hash(id)]=p;
  printf("insert after:\n");
  print_table();
  printf("\n");
}

struct cell *search_table(char *id)
{
  int h;
  struct cell *p;
  h=hash(id);
  p=hashtable[h];
   while(p!=NULL){
    if(strcmp(id,p->id)==0){
      return p;
    }
    p=p->next;
  }
  return NULL;
}

void delete_table(char *id)
{
  int h;
  struct cell *p,*savep;
  h=hash(id);
  p=hashtable[h];
  if(strcmp(p->id,id)==0){
    hashtable[h]=p->next;
    deleted_address[next_address]=p;
    print_address(p);
    next_address=next_address+1;
    return ;
  }
  else{
    while(p!=NULL){
      savep=p;
      p=p->next;
      if(strcmp(p->id,id)==0){
        savep->next=p->next;
        deleted_address[next_address]=p;
        print_address(p);
        next_address=next_address+1;
        return ;
      }
    }
  }
}

int main(void)
{
  char command[100],name[100];
  int year;
  initialize_table();
  while(scanf("%s %s",command,name)!=EOF){
    if(command[0]=='I'){
      scanf("%d",&year);
      if(search_table(name)==NULL){
        insert_table(name,year);
      }
      else{
        printf("over insert\n\n");
      }
    }
    if(command[0]=='S'){
      if(search_table(name)==NULL){
        printf("not insert\n\n");
      }
      else{
        printf("%d\n\n",search_table(name)->info);
      }
    }
    if(command[0]=='D'){
      if(search_table(name)==NULL){
        printf("not insert\n\n");
      }
      else{
        printf("delete before:\n");
        print_table();
        delete_table(name);
        printf("delete after:\n");
        print_table();
        printf("\n");
      }
    }
  }
  return 0;
}