import java.util.*;
class Y{
Map m;List<ArrayList<Integer>>g;int N;
String s(boolean p,String I){
if(!p)return"Merry Christmas!";
m=new HashMap();g=new ArrayList();N=0;
for(String L:I.split("\\R")){String[]a=L.split(":? ");int x=i(a[0]);for(int j=1;j<a.length;){int y=i(a[j++]);g.get(x).add(y);g.get(y).add(x);}}
int a=f(0),b=f(a),n=c(a,b);return""+n*(N-n);
}
int i(String s){Integer x=(Integer)m.get(s);if(x!=null)return x;m.put(s,N);g.add(new ArrayList());return N++;}
int f(int s){int[]q=new int[N];boolean[]v=new boolean[N];int h=0,t=0,l=s;v[q[t++]=s]=1>0;while(h<t){l=q[h++];for(int n:g.get(l))if(!v[n])v[q[t++]=n]=1>0;}return l;}
int c(int s,int e){
boolean[][]u=new boolean[N][N];int z=0;
for(int p=0;p<4;p++){int[]q=new int[N],P=new int[N];int h=0,t=0;P[s]=s+1;q[t++]=s;z=0;
while(h<t){int v=q[h++];z++;if(v==e){for(int x=e;x!=s;x=P[x]-1)u[x][P[x]-1]=u[P[x]-1][x]=1>0;break;}for(int n:g.get(v))if(P[n]<1&&!u[v][n]){P[n]=v+1;q[t++]=n;}}}
return z;
}
}
