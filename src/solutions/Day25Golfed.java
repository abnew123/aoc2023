import java.util.*;
class Y{
Map m;List<ArrayList<Integer>>g;int N;
String s(boolean p,String[]I){
if(!p)return"Merry Christmas!";
m=new HashMap();g=new ArrayList();N=0;
for(var L:I){var a=L.split(":? ");int x=i(a[0]);for(int j=1;j<a.length;){int y=i(a[j++]);g.get(x).add(y);g.get(y).add(x);}}
for(int e=1,a;e<N;e++)if((a=c(e))>0)return""+a;return"";
}
int i(String s){var x=(Integer)m.get(s);if(x!=null)return x;m.put(s,N);g.add(new ArrayList());return N++;}
int c(int e){
var u=new int[N][N];
for(int f=0;f<4;f++){int[]q=new int[N],P=new int[N];int h=0,t=0;P[0]=1;q[t++]=0;
while(h<t&P[e]<1){int v=q[h++];for(int n:g.get(v))if(P[n]<1&&u[v][n]<1){P[n]=v+1;q[t++]=n;}}
if(P[e]<1)return f>2?t*(N-t):0;for(int x=e;x>0;x=P[x]-1){int y=P[x]-1;u[y][x]++;u[x][y]--;}}
return 0;
}
}
