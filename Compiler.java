import java.io.*;
import java.util.*;

public class Compiler{

  BufferedReader source;
  int line_number;
  char ch;
  String id_string;
  int literal_value;

  enum token{
    END_PROGRAM,IDENTIFIER,LITERAL,ELSE,IF,WHILE,COMMA,SEMICOLON,LEFT_BRACE,RIGHT_BRACE,LEFT_PAREN,RIGHT_PAREN,EQUAL,OROR,ANDAND,OR,AND,EQEQ,NOTEQ,LE,LT,GE,GT,PLUS,MINUS,STAR,SLASH,PERCENT
  }

  token sy;

  void next_ch(){
    try{
      ch=(char)source.read();
      if(ch=='\n'){
        line_number++;
      }
    }
    catch(Exception e){
      System.out.println("IO error occurred");
      System.exit(1);
    }
  }

  void get_token(){
    //System.out.println("ch="+ch+" "+"sy="+sy);//debug
    while(ch==' '||ch=='\n'||ch=='\t'){
      next_ch();
    }

    if(ch==65535){
      sy=token.END_PROGRAM;
      return;
    }

    if('A'<=ch&&ch<='Z'||'a'<=ch&&ch<='z'||ch=='_'){
      id_string="";
      while('A'<=ch&&ch<='Z'||'a'<=ch&&ch<='z'||'0'<=ch&&ch<='9'||ch=='_'){
        id_string+=ch;
        next_ch();
      }

      if(id_string.equals("else")){
        sy=token.ELSE;
        return;
      }
      else if(id_string.equals("if")){
        sy=token.IF;
        return;
      }
      else if(id_string.equals("while")){
        sy=token.WHILE;
        return;
      }
      else{
        sy=token.IDENTIFIER;
        return;
      }

    }

    else if('0'<=ch&&ch<='9'){
      int v=0;
      int count=0;
      final int co=214748364;
      while('0'<=ch&&ch<='9'){
        v=v*10+ch-'0';
        count=count+1;
        next_ch();
        if(v>co&&'0'<=ch&&ch<='9'){
          error("too large integer literal");
          while(('0'<=ch&&ch<='9')){
            next_ch();
          }
          literal_value=0;
          sy=token.LITERAL;
          return;
        }

        if(v==co&&(ch=='8'||ch=='9')){
          error("too large integer literal");
          while(('0'<=ch&&ch<='9')){
            next_ch();
          }
          literal_value=0;
          sy=token.LITERAL;
          return;
        }
      }
      literal_value=v;
      sy=token.LITERAL;
      return;
    }

    else{
      if(ch==','){
        next_ch();
        sy=token.COMMA;
        return;
      }

      if(ch==';'){
        next_ch();
        sy=token.SEMICOLON;
        return;
      }

      if(ch=='{'){
        next_ch();
        sy=token.LEFT_BRACE;
        return;
      }

      if(ch=='}'){
        next_ch();
        sy=token.RIGHT_BRACE;
        return;
      }

      if(ch=='('){
        next_ch();
        sy=token.LEFT_PAREN;
        return;
      }

      if(ch==')'){
        next_ch();
        sy=token.RIGHT_PAREN;
        return;
      }

      if(ch=='='){
        next_ch();
        if(ch=='='){
          next_ch();
          sy=token.EQEQ;
          return;
        }
        sy=token.EQUAL;
        return;
      }

      if(ch=='|'){
        next_ch();
        if(ch=='|'){
          next_ch();
          sy=token.OROR;
          return;
        }
        sy=token.OR;
        return;
      }

      if(ch=='&'){
        next_ch();
        if(ch=='&'){
          next_ch();
          sy=token.ANDAND;
          return;
        }
        sy=token.AND;
        return;
      }

      if(ch=='!'){
        next_ch();
        if(ch=='='){
          next_ch();
          sy=token.NOTEQ;
          return;
        }
        else{
          error("exclamation mark not followed by equal");
          get_token();
          return;
        }
      }

      if(ch=='<'){
        next_ch();
        if(ch=='='){
          next_ch();
          sy=token.LE;
          return;
        }
        sy=token.LT;
        return;
      }

      if(ch=='>'){
        next_ch();
        if(ch=='='){
          next_ch();
          sy=token.GE;
          return;
        }
        sy=token.GT;
        return;
      }

      if(ch=='+'){
        next_ch();
        sy=token.PLUS;
        return;
      }

      if(ch=='-'){
        next_ch();
        sy=token.MINUS;
        return;
      }

      if(ch=='*'){
        next_ch();
        sy=token.STAR;
        return;
      }

      if(ch=='/'){
        next_ch();
        if(ch=='*'){
          next_ch();
          char ch2=ch;
          while(!(ch2=='*'&&ch=='/')){
            ch2=ch;
            next_ch();
            if(ch==65535){
              line_number=line_number-1;
              error("comment not terminated");
              line_number=line_number+1;
              sy=token.END_PROGRAM;
              return;
            }
          }
          next_ch();
          get_token();
          return;
        }
        sy=token.SLASH;
        return;
      }

      if(ch=='%'){
        next_ch();
        sy=token.PERCENT;
        return;
      }
      error("invalid character");
      next_ch();
      get_token();
      return;
    }
  }

  token status;

  void statement(){
    if(sy==token.SEMICOLON){
      polish("empty statement");
      polish_newline();
      //System.out.println("AAAAA");//debug
      get_token();
    }
    else if(sy==token.IF){
      status=sy;
      polish("if statement:");
      get_token();
      if(sy==token.LEFT_PAREN){
        get_token();
        expression();
        polish_newline();
        int pc_save=pc;
        emit(operation.FJUMP,0);//オペランドはいい加減な値
        //polish(" ");
        if(sy==token.RIGHT_PAREN){
          //status=null;
          get_token();
          statement();
          //System.out.println("!!!!!!status="+status);//debug
          if(sy==token.ELSE){
            code[pc_save].operand=pc+1;
            status=sy;
            int else_start=pc;
            //System.out.println("status="+status);//debug
            polish("else part");
            polish_newline();
            emit(operation.JUMP,0);//オペランドはいい加減な値
            // polish(" ");
            get_token();
            statement();
            code[else_start].operand=pc;
          }
          else{
            code[pc_save].operand=pc;
          }
        }
        else{
          //get_token();
          error("right parenthesis expected");
        }
      }
      else{
        error("left parenthesis expected");
      }
      //status=null;
      polish("end if statement");
      polish_newline();
    }
    else if(sy==token.WHILE){
      status=sy;
      int while_start=pc;
      polish("while statement:");
      get_token();
      if(sy==token.LEFT_PAREN){
        get_token();
        expression();
        polish_newline();
        int pc_save=pc;
        emit(operation.FJUMP,0);//オペランドはいい加減な値
        //polish(" ");
        if(sy==token.RIGHT_PAREN){
          //status=null;
          get_token();
          statement();
          emit(operation.JUMP,while_start);
          code[pc_save].operand=pc;
        }
        else{
          //get_token();
          error("right parenthesis expected");
        }
      }
      else{
        error("left parenthesis expected");
      }
      polish("end while statement");
      polish_newline();
    }
    else if(sy==token.LEFT_BRACE){//複合文
      get_token();
      /*while(true){
        if(sy==token.RIGHT_BRACE){
          get_token();
          break;
        }
        statement();
        if(sy==token.END_PROGRAM){
          return;
        }
      }*/
      while(sy!=token.RIGHT_BRACE){
        statement();
        if(sy==token.END_PROGRAM){
          error("too few right braces at end of statement list");
          return;
        }
      }
      get_token();
    }
    else{
      expression();
      polish_newline();
      emit(operation.POPUP,0);
      //System.out.println("status="+status);//debug
      if(status==token.IF||status==token.ELSE||status==token.WHILE){
        //polish(" ");
      }
      // System.out.println("sy="+sy);//debug
      if(sy==token.SEMICOLON){
        get_token();
        //emit(operation.___,-1);
      }
      else{
        error("semicolon expected");
      }
    }
  }

  void expression(){
    logical_or_expression();
    if(sy==token.EQUAL){
      if(code[pc-1].op_code!=operation.LOAD){
        error("assignment to non-variable");
      }
      int operand_save=code[pc-1].operand;
      pc=pc-1;
      get_token();
      expression();
      polish("=");
      emit(operation.STORE,operand_save);
    }
    else{
    }
  }

  void logical_or_expression(){
    logical_and_expression();
    while(sy==token.OROR){
      get_token();
      int pc_save=pc;
      emit(operation.TJUMP,1);
      logical_and_expression();
      emit(operation.TJUMP,pc+3);
      emit(operation.LCONST,0);
      emit(operation.JUMP,pc+2);
      emit(operation.LCONST,1);
      polish("||");
      code[pc_save].operand=pc-1;
    }
  }

  void logical_and_expression(){
    bit_or_expression();
    while(sy==token.ANDAND){
      get_token();
      int pc_save=pc;
      emit(operation.FJUMP,0);
      bit_or_expression();
      emit(operation.FJUMP,pc+3);
      emit(operation.LCONST,1);
      emit(operation.JUMP,pc+2);
      emit(operation.LCONST,0);
      polish("&&");
      code[pc_save].operand=pc-1;
    }
  }

  void bit_or_expression(){
    bit_and_expression();
    while(sy==token.OR){
      get_token();
      bit_and_expression();
      polish("|");
      emit(operation.OROP,0);
    }
  }

  void bit_and_expression(){
    equality_expression();
    while(sy==token.AND){
      get_token();
      equality_expression();
      polish("&");
      emit(operation.ANDOP,0);
    }
  }

  void equality_expression(){
    relational_expression();
    while(sy==token.EQEQ||sy==token.NOTEQ){
      if(sy==token.EQEQ){
        while(sy==token.EQEQ){
          get_token();
          relational_expression();
          polish("==");
          emit(operation.EQOP,0);
        }
      }
      if(sy==token.NOTEQ){
        while(sy==token.NOTEQ){
          get_token();
          relational_expression();
          polish("!=");
          emit(operation.NEOP,0);
        }
      }
    }
  }

  void relational_expression(){
    additive_expression();
    while(sy==token.LT||sy==token.GT||sy==token.LE||sy==token.GE){
      if(sy==token.LT){
        while(sy==token.LT){
          get_token();
          additive_expression();
          polish("<");
          emit(operation.LTOP,0);
        }
      }
      if(sy==token.GT){
        while(sy==token.GT){
          get_token();
          additive_expression();
          polish(">");
          emit(operation.GTOP,0);

        }
      }
      if(sy==token.LE){
        while(sy==token.LE){
          get_token();
          additive_expression();
          polish("<=");
          emit(operation.LEOP,0);
        }
      }
      if(sy==token.GE){
        while(sy==token.GE){
          get_token();
          additive_expression();
          polish(">=");
          emit(operation.GEOP,0);
        }
      }
    }
  }

  void additive_expression(){
    multiplicative_expression();
    /*while(sy==token.PLUS){
      get_token();
      multiplicative_expression();
      polish("+");
    }
    while(sy==token.MINUS){
      get_token();
      multiplicative_expression();
      polish("-");
    }*/
    //System.out.println("sy="+sy);//debug
    while(sy==token.PLUS||sy==token.MINUS){
      if(sy==token.PLUS){
        while(sy==token.PLUS){
          get_token();
          multiplicative_expression();
          polish("+");
          emit(operation.ADD,0);
        }
      }
      if(sy==token.MINUS){
        while(sy==token.MINUS){
          get_token();
          multiplicative_expression();
          polish("-");
          emit(operation.SUB,0);
        }
      }
    }
    /*if(sy==token.PLUS||sy==token.MINUS){
      get_token();
      multiplicative_expression();
      while(sy==token.PLUS){
        get_token();
        multiplicative_expression();
        polish("+");
      }
      while(sy==token.MINUS){
        get_token();
        multiplicative_expression();
        polish("-");
      }
    }*/
    /*while(sy==token.PLUS||sy==token.MINUS){
      get_token();
      multiplicative_expression();
      if(sy==token.PLUS){
        polish("+");
      }
      if(sy==token.MINUS){
        polish("-");
      }
    }*/
  }

  void multiplicative_expression(){
    unary_expression();
    while(sy==token.STAR||sy==token.SLASH||sy==token.PERCENT){
      if(sy==token.STAR){
        while(sy==token.STAR){
          get_token();
          unary_expression();
          polish("*");
          emit(operation.MULT,0);
        }
      }
      if(sy==token.SLASH){
        while(sy==token.SLASH){
          get_token();
          unary_expression();
          polish("/");
          emit(operation.DIV,0);
        }
      }
      if(sy==token.PERCENT){
        while(sy==token.PERCENT){
          get_token();
          unary_expression();
          polish("%");
          emit(operation.MOD,0);
        }
      }
    }
  }

  void unary_expression(){
    if(sy==token.MINUS){
      get_token();
      emit(operation.LCONST,0);
      unary_expression();
      polish("u-");
      emit(operation.SUB,0);
    }
    else{
      primary_expression();
    }
  }

  void primary_expression(){
    //System.out.println("!!!!!sy="+sy);//debug
    if(sy==token.LITERAL){
      polish(literal_value+"");
      get_token();
      emit(operation.LCONST,literal_value);
    }
    else if(sy==token.IDENTIFIER){
      polish(id_string);
      get_token();
      if(sy==token.LEFT_PAREN){//関数呼び出し
        if(!id_string.equals("getd")&&!id_string.equals("putd")&&!id_string.equals("newline")&&!id_string.equals("putchar")){
          error(id_string+": variable is used as a function");
        }
        int n=0;
        int function_id_save=lookup_function(id_string).function_id;
        int parameter_count_save=lookup_function(id_string).parameter_count;
        String function_name=id_string;
        //emit(operation.CALL,lookup_function(id_string).function_id);
        get_token();
        //emit(operation.CALL,lookup_function(id_string).function_id);
        if(sy==token.RIGHT_PAREN){
          get_token();
          polish("call-"+n);
          emit(operation.CALL,lookup_function(id_string).function_id);
          if(parameter_count_save!=n){
            if(function_id_save==0){
              function_name="getd";
            }
            else if(function_id_save==1){
              function_name="putd";
            }
            else if(function_id_save==2){
              function_name="newline";
            }
            else if(function_id_save==3){
              function_name="putchar";
            }
            error(function_name+": number of parameters mismatch");
          }
        }
        else{//式に行く
          expression();
          n=n+1;
          while(sy==token.COMMA){
            get_token();
            expression();
            n=n+1;
          }
          //System.out.println("sy="+sy);//debug
          emit(operation.CALL,lookup_function(function_name).function_id);
          if(sy==token.RIGHT_PAREN){
            get_token();
            polish("call-"+n);
            //emit(operation.CALL,lookup_function(id_string).function_id);
            if(parameter_count_save!=n){
              if(function_id_save==0){
                function_name="getd";
              }
              else if(function_id_save==1){
                function_name="putd";
              }
              else if(function_id_save==2){
                function_name="newline";
              }
              else if(function_id_save==3){
                function_name="putchar";
              }
              error(function_name+": number of parameters mismatch");
            }
            //emit(operation.CALL,lookup_function(function_name).function_id);
          }
          else{
            error("right parenthesis expected");
          }
        }
      }
      else{ //変数参照
        int address_save=lookup_variable(id_string).address;
        if(id_string.equals("getd")||id_string.equals("putd")||id_string.equals("newline")||id_string.equals("putchar")){
          error(id_string+": function is used as a variable");
        }
        emit(operation.LOAD,lookup_variable(id_string).address);
      }
    }
    else if(sy==token.LEFT_PAREN){
      get_token();
      expression();
      if(sy==token.RIGHT_PAREN){
        get_token();
      }
      else{
        error("right parenthesis expected");
      }
    }
    else{
      error("unrecognized element in expression");
      get_token();
    }
  }

  final boolean debug_parse=false;

  void polish(String s){
    if(debug_parse){
      System.out.print(s+" ");
    }
  }

  void polish_newline(){
    if(debug_parse){
      System.out.println();
    }
  }

  public static void main(String[] args) throws Exception{
    System.out.println("sample C0 compiler-interpreter system");
    Compiler Compiler_instance=new Compiler();
    Compiler_instance.driver(args);
  }

  void driver(String[] args) throws Exception{
    if(args.length==1){
      source=new BufferedReader(new FileReader(new File(args[0])));
    }
    else{
      source=new BufferedReader(new InputStreamReader(System.in));
      if(args.length!=0){
        error("multilple source file is not supported");
      }
    }
    line_number=1;
    ch=' ';

    /*do{
      get_token();
      if(sy==token.IDENTIFIER){
        System.out.println(sy+" "+id_string);
      }
      else if(sy==token.LITERAL){
        System.out.println(sy+" "+literal_value);
      }
      else{
        System.out.println(sy);
      }
    }
    while(sy!=token.END_PROGRAM);*/

    init_symbol_table();
    get_token();
    statement();
    emit(operation.HALT,0);
    //expression();
    if(sy!=token.END_PROGRAM){
      error("extra text at the end of the program");
    }
    if(error_count==0){
      interpret(false);
    }
    //print_code();
  }

  void error(String s){
    error_count=error_count+1;
    System.out.println(String.format("%4d",line_number)+":"+" "+s);
  }

  enum type{
    Variable,Function
  };

  class id_record{
    type id_class;
    int address;
    int function_id;
    int parameter_count;

    id_record(type a,int b,int c,int d){
      this.id_class=a;
      this.address=b;
      this.function_id=c;
      this.parameter_count=d;
    }
  }

  Map<String,id_record> symbol_table;
  int variable_count;

  void init_symbol_table(){
    symbol_table=new TreeMap<String,id_record>();
    variable_count=0;
    symbol_table.put("getd",new id_record(type.Function,-1,0,0));
    symbol_table.put("putd",new id_record(type.Function,-1,1,2));
    symbol_table.put("newline",new id_record(type.Function,-1,2,0));
    symbol_table.put("putchar",new id_record(type.Function,-1,3,1));
  }

  id_record search(String name){
    id_record r=symbol_table.get(name);
    if(r==null){
      r=new id_record(type.Variable,variable_count,-1,-1);
      variable_count=variable_count+1;
      symbol_table.put(name,r);
      //r=symbol_table.get(name);
      //その値を返す（putから返る値は，登録した値ではないので，それを返してはいけない）?
    }
    return r;
  }

  id_record lookup_variable(String name){
    id_record r=search(name);
    if(r.id_class!=type.Variable){
      error("error:id_class!=type.Variable");
    }
    return r;
  }

  id_record lookup_function(String name){
    id_record r=search(name);
    if(r.id_class!=type.Function){
      error("error:id_class!=type.Function");
    }
    return r;
  }

  enum operation{
    LCONST,LOAD,STORE,POPUP,CALL,JUMP,FJUMP,TJUMP,HALT,MULT,DIV,MOD,ADD,SUB,ANDOP,OROP,EQOP,NEOP,LEOP,LTOP,GEOP,GTOP,
  };

  class code_type{
    operation op_code;
    int operand;
  }

  final int CODE_MAX=5000;
  int pc=0;
  code_type code[]=new code_type[CODE_MAX];

  void emit(operation op,int param){
    if(pc>=CODE_MAX){
      System.exit(0);
    }
    code[pc]=new code_type();
    code[pc].op_code=op;
    code[pc].operand=param;
    pc++;
  }

  void print_code(){
    for(int i=0;i<pc;i++){
      System.out.println(String.format("%5d",i)+": "+String.format("%-6s",code[i].op_code)+String.format("%6d",code[i].operand));
    }
  }

  final int Stack_Size=100;
  int memory_size=variable_count+Stack_Size;
  int memory[]=new int[memory_size];
  int sp,ic;
  int error_count=0;

  void interpret(boolean trace){
    Scanner sc=new Scanner(System.in);
    ic=0;
    sp=variable_count;
    while(true){
      //System.out.println();
      operation instruction=code[ic].op_code;
      int argument=code[ic].operand;
      if(trace){
        System.out.print("ic="+String.format("%4d",ic)+", sp="+String.format("%5d",sp)+",  code=("+String.format("%-6s",instruction)+String.format("%6d",argument)+")");
        if(sp>variable_count){
          int val=pop();
          push(val);
          System.out.print(", top="+String.format("%10d",val));
        }
        System.out.println();
      }
      ic++;
      int op1,op2;
      switch(instruction){
        case LCONST:
          push(argument);
          continue;
        case LOAD:
          if(argument<0||argument>=variable_count){
            run_error("argument<0||argument>=variable_count");
          }
          push(memory[argument]);
          continue;
        case STORE:
          if(argument<0||argument>=variable_count){
            run_error("argument<0||argument>=variable_count");
          }
          memory[argument]=pop();
          push(memory[argument]);
          continue;
        case POPUP:
          pop();
          continue;
        case CALL:
          switch(argument){
            case 0://getd
              System.out.print("getd: ");
              push(sc.nextInt());
              break;
            case 1://putd
              int width=pop();
              int val=pop();
              String s=String.format("%d",val);
              int d=width-s.length();
              while(d>0){
                System.out.print(" ");
                d--;
              }
              System.out.print(s);
              push(val);
              break;
            case 2://newline
              System.out.println();
              push(0);
              break;
            case 3://putchar
              int val3=pop();
              char c=(char)val3;
              System.out.print(c);
              push(val3);
              break;
            default:
              run_error("その関数は存在しない");
          }
          continue;
        case JUMP:
          ic=argument;
          continue;
        case FJUMP:
          op1=pop();
          if(op1==0){
            ic=argument;
          }
          continue;
        case TJUMP:
          op1=pop();
          if(op1!=0){
            ic=argument;
          }
          continue;
        case HALT:
          if(sp!=variable_count){
            run_error("sp!=variable_count");
          }
          else{
            return;
          }
          continue;
        case MULT:
          push(pop()*pop());
          continue;
        case ADD:
          push(pop()+pop());
          continue;
        case SUB:
          op1=pop();
          op2=pop();
          push(op2-op1);
          continue;
        case ANDOP:
          push(pop()&pop());
          continue;
        case OROP:
          push(pop()|pop());
          continue;
        case DIV:
          op1=pop();
          op2=pop();
          if(op1==0){
            run_error("division by zero");
          }
          push(op2/op1);
          continue;
        case MOD:
          op1=pop();
          op2=pop();
          if(op1==0){
            run_error("modulus by zero");
          }
          push(op2%op1);
          continue;
        case EQOP:
          op1=pop();
          op2=pop();
          if(op2==op1){
            push(1);
          }
          else{
            push(0);
          }
          continue;
        case NEOP:
          op1=pop();
          op2=pop();
          if(op2!=op1){
            push(1);
          }
          else{
            push(0);
          }
          continue;
        case LEOP:
          op1=pop();
          op2=pop();
          if(op2<=op1){
            push(1);
          }
          else{
            push(0);
          }
          continue;
        case LTOP:
          op1=pop();
          op2=pop();
          if(op2<op1){
            push(1);
          }
          else{
            push(0);
          }
          continue;
        case GEOP:
          op1=pop();
          op2=pop();
          if(op2>=op1){
            push(1);
          }
          else{
            push(0);
          }
          continue;
        case GTOP:
          op1=pop();
          op2=pop();
          if(op2>op1){
            push(1);
          }
          else{
            push(0);
          }
          continue;
        default:
          run_error("system error: undefined op code");
      }
    }
  }

  void push(int x){
    if(sp>=memory_size){
      run_error("stack overflow");
    }
    memory[sp]=x;
    sp++;
  }

  int pop(){
    if(sp<=variable_count){
      run_error("system error: stack underflow");
    }
    sp--;
    return(memory[sp]);
  }

  void run_error(String s){
    System.out.println(s);
    System.exit(1);
  }
}
